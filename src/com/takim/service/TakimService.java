package com.takim.service;

import com.takim.exception.KapasiteDolduException;
import com.takim.model.*;
import java.util.*;
import java.time.LocalDate;
import com.takim.util.DosyaIslemleri;
import com.takim.util.Formatlayici;

/**
 * Takim verilerini yonetir, is mantigini uygular ve koleksiyonlari tutar.
 */
public class TakimService {

    private List<Futbolcu> futbolcuKadrosu; // Futbolcular için
    private List<Kisi> kisiListesi; // YENİ: Tüm personel için ana liste
    private Map<Integer, Futbolcu> formaNoHaritasi; // Hızlı Futbolcu arama
    private Map<LocalDate,MacVerisi> macGecmisi;
    private static final int MAX_KADRO_LIMITI = 28;

    // >>> YENİ: ID Sayaçları <<<
    private static int yardimciAntrenorSayaci = 0;
    private static int fizyoterapistSayaci = 0;

    public TakimService() {
        this.futbolcuKadrosu = new ArrayList<>();
        this.kisiListesi = new ArrayList<>();
        this.formaNoHaritasi = new HashMap<>();
        this.macGecmisi = new TreeMap<>();
        orneKVeriYukle();

        // YENİ: Veri yüklendikten sonra sayaçları ayarla
        updatePersonelSayaclari();
    }

    // >>> YENİ: ID Üretim Metotları <<<
    /**
     * Yardimci Antrenor için YANT001, YANT002... şeklinde benzersiz ID üretir.
     */
    public String generateYardimciAntrenorId() {
        yardimciAntrenorSayaci++;
        return String.format("YANT%03d", yardimciAntrenorSayaci);
    }

    /**
     * Fizyoterapist için FİZY001, FİZY002... şeklinde benzersiz ID üretir.
     */
    public String generateFizyoterapistId() {
        fizyoterapistSayaci++;
        return String.format("FİZY%03d", fizyoterapistSayaci);
    }
    // <<< ID Üretim Metotları Sonu >>>

    public void futbolcuEkle(Futbolcu futbolcu) throws KapasiteDolduException {
        try {
            if (futbolcuKadrosu.size() >= MAX_KADRO_LIMITI) {
                throw new KapasiteDolduException("Kadro Limiti asildi.");
            }
            futbolcuKadrosu.add(futbolcu);
            this.kisiListesi.add(futbolcu); // Ana listeye de ekle
            formaNoHaritasi.put(futbolcu.getFormaNo(), futbolcu);
        } catch (KapasiteDolduException e) {
            System.err.println("HATA: " + e.getMessage());
        }
    }

    /**
     * Ad ve soyad ile personel siler (büyük/küçük harf duyarsız). (Eski metot, uyumluluk için tutulur)
     */
    public boolean personelSil(String ad, String soyad) {
        Iterator<Kisi> iterator = kisiListesi.iterator();
        while (iterator.hasNext()) {
            Kisi kisi = iterator.next();
            if (kisi.getAd().equalsIgnoreCase(ad) && kisi.getSoyad().equalsIgnoreCase(soyad)) {
                return kisiSil(kisi); // Yeni kisiSil metodunu çağırır.
            }
        }
        return false;
    }

    // >>> YENİ: Tek bir kişi nesnesini tüm listelerden silen metot <<<
    /**
     * Kisi listesinden verilen Kisi nesnesini siler.
     */
    public boolean kisiSil(Kisi kisi) {
        if (kisi == null) return false;

        // Futbolcu ise ek listelerden de sil
        if (kisi instanceof Futbolcu f) {
            futbolcuKadrosu.remove(f);
            formaNoHaritasi.remove(f.getFormaNo());
        }

        // Ana listeden sil
        return kisiListesi.remove(kisi);
    }
    // <<< kisiSil Metodu Sonu >>>


    public boolean performansGuncelle(int formaNo, int gol, int asist) {
        Futbolcu f = futbolcuyuBul(formaNo);
        if (f != null) {
            f.performansGuncelle(gol, asist);
            return true;
        }
        return false;
    }

    public Futbolcu futbolcuyuBul(int formaNo) {
        return formaNoHaritasi.get(formaNo);
    }

    // >>> YENİ: ID/Role Göre Arama Metotları <<<
    // Teknik Direktör'ü döndüren metot
    public TeknikDirektor getTeknikDirektor() {
        for (Kisi k : kisiListesi) {
            if (k instanceof TeknikDirektor) {
                return (TeknikDirektor) k;
            }
        }
        return null;
    }

    // ID'ye göre Yardımcı Antrenör'ü döndüren metot
    public YardimciAntrenor yardimciAntrenoruBul(String id) {
        for (Kisi k : kisiListesi) {
            // Büyük/küçük harf duyarsız ve boşluksuz karşılaştırma
            if (k instanceof YardimciAntrenor && k.getTcKimlikNo().trim().equalsIgnoreCase(id.trim())) {
                return (YardimciAntrenor) k;
            }
        }
        return null;
    }

    // ID'ye göre Fizyoterapist'i döndüren metot
    public Fizyoterapist fizyoterapistBul(String id) {
        for (Kisi k : kisiListesi) {
            // Büyük/küçük harf duyarsız ve boşluksuz karşılaştırma
            if (k instanceof Fizyoterapist && k.getTcKimlikNo().trim().equalsIgnoreCase(id.trim())) {
                return (Fizyoterapist) k;
            }
        }
        return null;
    }
    // <<< Arama Metotları Sonu >>>

    public <T extends Kisi> void listeYazdir(List<T> liste) {
        for (T eleman : liste) {
            eleman.bilgiYazdir();
        }
    }

    public void skorKatkisiSiralamasiYap() {
        // Skor katkısına (Gol + Asist) göre azalan sırada sıralar.
        Collections.sort(futbolcuKadrosu, (f1, f2) -> f2.getSkorKatkisi() - f1.getSkorKatkisi());
    }

    public List<Futbolcu> getFutbolcuKadrosu() { return futbolcuKadrosu; }
    public List<Kisi> getKisiListesi() { return kisiListesi; } // Tüm personel listesini döndürür

    // >>> YENİ: Sayaç Güncelleme Metodu (Kalıcılık Desteği) <<<
    private void updatePersonelSayaclari() {
        int maxYa = 0;
        int maxFizyo = 0;

        for (Kisi k : kisiListesi) {
            String tcNo = k.getTcKimlikNo();
            if (tcNo == null) continue;

            // Yardımcı Antrenör ID'sini kontrol et (TRA99, YANT001 gibi formatları desteklemek için)
            if (k instanceof YardimciAntrenor) {
                try {
                    // Sadece sayısal kısmı alıp en büyük sayıyı buluruz
                    String numPart = tcNo.replaceAll("[^0-9]", "");
                    if (!numPart.isEmpty()) {
                        int id = Integer.parseInt(numPart);
                        if (id > maxYa) {
                            maxYa = id;
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }

            // Fizyoterapist ID'sini kontrol et (TRF99, FİZY001 gibi formatları desteklemek için)
            else if (k instanceof Fizyoterapist) {
                try {
                    String numPart = tcNo.replaceAll("[^0-9]", "");
                    if (!numPart.isEmpty()) {
                        int id = Integer.parseInt(numPart);
                        if (id > maxFizyo) {
                            maxFizyo = id;
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        // Sayaçları bulunan maksimum değerlere ayarla
        yardimciAntrenorSayaci = maxYa;
        fizyoterapistSayaci = maxFizyo;
    }
    // <<< Sayaç Güncelleme Metodu Sonu >>>


    private void orneKVeriYukle() {
        // KRİTİK DÜZELTME: Tüm personeli tek bir dosyadan yükleriz.
        List<Kisi> yuklenenPersonel = DosyaIslemleri.personelVerileriniYukle();

        if (!yuklenenPersonel.isEmpty()) {
            this.kisiListesi = yuklenenPersonel; // Ana listeyi yüklenen verilerle değiştir.

            // Futbolcuları ana listeden filtrele ve diğer listeleri doldur.
            for (Kisi kisi : this.kisiListesi) {
                if (kisi instanceof Futbolcu f) {
                    futbolcuKadrosu.add(f);
                    formaNoHaritasi.put(f.getFormaNo(), f);
                }
            }
        } else {
            System.out.println(Formatlayici.renklendir("Kayitli personel verisi bulunamadi, ornek kadro oluşturuluyor...", Formatlayici.MAVI));
            try {
                // Ornek TD, YA, Fizyo ekle (Bu veriler de kisiListesi'ne eklenecektir)
                TeknikDirektor td = new TeknikDirektor("Ornek", "Direktor", LocalDate.of(1973, 10, 19), "TRD99", 550000, LocalDate.of(2022, 6, 1), 2005, "4-2-3-1", 100000, "EskiTakim", 1.5, 5);
                YardimciAntrenor ya = new YardimciAntrenor("Ornek", "Antrenor", LocalDate.of(1985, 3, 10), "TRA99", 220000, LocalDate.of(2023, 7, 1), "Hucum", 1000.5, 18, 15, 17, 16, 19, 20);
                Fizyoterapist fizyo = new Fizyoterapist("Ornek", "Fizyoterapist", LocalDate.of(1988, 1, 1), "TRF99", 150000, LocalDate.of(2021, 5, 15), "SERT_X", "Ortopedi", true, 18, 15, 19, 17);

                this.kisiListesi.add(td);
                this.kisiListesi.add(ya);
                this.kisiListesi.add(fizyo);

                // Futbolcu ekleme metodu (Ayrıca kisiListesi'ne de ekler)
                Futbolcu f1 = new Futbolcu("Mauro", "Icardi", LocalDate.of(1993, 2, 19), "TC1", 9, "FORVET", 15, 5);
                futbolcuEkle(f1);

            } catch (Exception e) {
                System.err.println("Ornek veri yuklenirken hata olustu: " + e.getMessage());
            }
        }
    }
}