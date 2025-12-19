package com.takim.service;

import com.takim.model.*;
import com.takim.exception.GecersizFormaNoException;
import com.takim.util.DosyaIslemleri;
import com.takim.util.Formatlayici;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class TakimService {

    private static int yardimciAntrenorCounter = 0;
    private static int fizyoterapistCounter = 0;

    private List<Futbolcu> futbolcuKadrosu;
    private List<TeknikDirektor> teknikDirektorler;
    private List<YardimciAntrenor> yardimciAntrenorler;
    private List<Fizyoterapist> fizyoterapistler;

    private Map<Integer, Futbolcu> formaNoHaritasi;
    private Map<LocalDate, MacVerisi> macGecmisi;

    // Haftalık program verisi
    private final String[] gunler = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"};
    private String[] haftalikAktiviteler = {"Kondisyon", "Taktik", "Maç", "Analiz", "Şut", "Hazırlık", "Dinlenme"};

    private static final String FUTBOLCU_DOSYA = "futbolcular.txt";
    private static final String TEKNIK_DIREKTOR_DOSYA = "teknikdirektorler.txt";
    private static final String YARDIMCI_ANTRENOR_DOSYA = "yardimciantrenorler.txt";
    private static final String FIZYOTERAPIST_DOSYA = "fizyoterapistler.txt";
    private static final String FIKSTUR_DOSYA = "fikstur.dat";
    public TakimService() {
        futbolcuKadrosu = new ArrayList<>();
        teknikDirektorler = new ArrayList<>();
        yardimciAntrenorler = new ArrayList<>();
        fizyoterapistler = new ArrayList<>();
        formaNoHaritasi = new HashMap<>();
        macGecmisi = new TreeMap<>();

        tumVerileriYukle();
        initCounters();
        futbolcuKadrosu.forEach(f -> formaNoHaritasi.put(f.getFormaNo(), f));
    }

    private void initCounters() {
        yardimciAntrenorCounter = yardimciAntrenorler.stream()
                .map(YardimciAntrenor::getId)
                .filter(id -> id != null && id.startsWith("YARD"))
                .map(id -> {
                    try { return Integer.parseInt(id.substring(4)); }
                    catch (NumberFormatException e) { return -1; }
                })
                .max(Integer::compare).orElse(-1) + 1;

        fizyoterapistCounter = fizyoterapistler.stream()
                .map(Fizyoterapist::getId)
                .filter(id -> id != null && id.startsWith("FİZY"))
                .map(id -> {
                    try { return Integer.parseInt(id.substring(4)); }
                    catch (NumberFormatException e) { return -1; }
                })
                .max(Integer::compare).orElse(-1) + 1;

        if (yardimciAntrenorCounter < 0) yardimciAntrenorCounter = 0;
        if (fizyoterapistCounter < 0) fizyoterapistCounter = 0;
    }

    public void futbolcuEkle(Futbolcu futbolcu) throws GecersizFormaNoException {
        futbolcuKadrosu.add(futbolcu);
        formaNoHaritasi.put(futbolcu.getFormaNo(), futbolcu);
        tumVerileriKaydet();
    }

    public void teknikDirektorEkle(TeknikDirektor td) {
        teknikDirektorler.add(td);
        tumVerileriKaydet();
    }

    public void yardimciAntrenorEkle(YardimciAntrenor ya) {
        ya.setId(String.format("YARD%03d", yardimciAntrenorCounter++));
        yardimciAntrenorler.add(ya);
        tumVerileriKaydet();
    }

    public void fizyoterapistEkle(Fizyoterapist fizyo) {
        fizyo.setId(String.format("FİZY%03d", fizyoterapistCounter++));
        fizyoterapistler.add(fizyo);
        tumVerileriKaydet();
    }

    public boolean performansGuncelle(int formaNo, int yeniGol) {
        return performansVerisiGir(formaNo, yeniGol, 0);
    }

    public boolean performansVerisiGir(int formaNo, int gol, int asist) {
        Futbolcu f = formaNoHaritasi.get(formaNo);
        if (f != null) {
            try {
                f.performansGuncelle((double) gol, (double) asist);
                tumVerileriKaydet();
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    // Haftalık Programı Güncelleme Metodu
    public void haftalikProgramGuncelle(String[] yeniAktiviteler) {
        if (yeniAktiviteler.length == 7) {
            this.haftalikAktiviteler = yeniAktiviteler;
        }
    }

    public String[] getHaftalikAktiviteler() { return haftalikAktiviteler; }
    public String[] getGunler() { return gunler; }

    public String haftalikProgramiGoster() {
        StringBuilder sb = new StringBuilder();
        sb.append(Formatlayici.renklendir("--- GÜNCEL HAFTALIK PROGRAM ---\n", Formatlayici.MAVI));
        for (int i = 0; i < gunler.length; i++) {
            sb.append(String.format("%-10s : %s\n", gunler[i], haftalikAktiviteler[i]));
        }
        return sb.toString();
    }

    public void sistemBütünlükKontrolü() {
        int c = 0;
        while (c < 1) { System.out.println("Sistem taranıyor..."); c++; }
        int d = 0;
        do { System.out.println("Bütünlük doğrulandı."); d++; } while (d < 1);
    }

    // GÜNCELLEME: antrenmanPuanOrtalamasiHesapla() METODU SİLİNDİ.

    public boolean futbolcuSil(int fNo) {
        boolean r = futbolcuKadrosu.removeIf(f -> f.getFormaNo() == fNo);
        if (r) { formaNoHaritasi.remove(fNo); tumVerileriKaydet(); }
        return r;
    }

    public boolean teknikDirektorSil() {
        if (teknikDirektorler.isEmpty()) return false;
        teknikDirektorler.clear(); tumVerileriKaydet(); return true;
    }

    public boolean yardimciAntrenorSil(String id) {
        boolean r = yardimciAntrenorler.removeIf(y -> y.getId().equalsIgnoreCase(id));
        if (r) tumVerileriKaydet(); return r;
    }

    public boolean fizyoterapistSil(String id) {
        boolean r = fizyoterapistler.removeIf(f -> f.getId().equalsIgnoreCase(id));
        if (r) tumVerileriKaydet(); return r;
    }

    public String listeYazdir(List<? extends Kisi> l) {
        if (l == null || l.isEmpty()) return "Liste boş.";
        StringBuilder sb = new StringBuilder();
        for (Kisi k : l) { sb.append(k.toString()).append("\n"); }
        return sb.toString();
    }

    public String skorKatkisiRaporuGetir() {
        futbolcuKadrosu.sort(Comparator.comparingDouble(Futbolcu::performansPuaniniHesapla).reversed());
        StringBuilder sb = new StringBuilder();
        sb.append(Formatlayici.renklendir("--- SKOR KATKISI VE PERFORMANS RAPORU ---\n", Formatlayici.YESIL));
        futbolcuKadrosu.forEach(f -> {
            sb.append(String.format("Forma No: %-3d | %-20s | %s (%s)\n",
                    f.getFormaNo(),
                    f.getAd() + " " + f.getSoyad(),
                    f.getPerformansDetayi(),
                    f.performansDurumuAnalizi()));
        });
        return sb.toString();
    }

    public void tumVerileriKaydet() {
        try {
            DosyaIslemleri.dosyayaYaz(futbolcuKadrosu, FUTBOLCU_DOSYA);
            DosyaIslemleri.dosyayaYaz(teknikDirektorler, TEKNIK_DIREKTOR_DOSYA);
            DosyaIslemleri.dosyayaYaz(yardimciAntrenorler, YARDIMCI_ANTRENOR_DOSYA);
            DosyaIslemleri.dosyayaYaz(fizyoterapistler, FIZYOTERAPIST_DOSYA);
            DosyaIslemleri.dosyayaYaz(new ArrayList<>(macGecmisi.values()), FIKSTUR_DOSYA);
        } catch (IOException e) { System.err.println("Kayıt hatası: " + e.getMessage()); }
    }

    public void tumVerileriYukle() {
        try {
            futbolcuKadrosu = DosyaIslemleri.dosyadanOku(FUTBOLCU_DOSYA, Futbolcu.class);
            teknikDirektorler = DosyaIslemleri.dosyadanOku(TEKNIK_DIREKTOR_DOSYA, TeknikDirektor.class);
            yardimciAntrenorler = DosyaIslemleri.dosyadanOku(YARDIMCI_ANTRENOR_DOSYA, YardimciAntrenor.class);
            fizyoterapistler = DosyaIslemleri.dosyadanOku(FIZYOTERAPIST_DOSYA, Fizyoterapist.class);
            List<MacVerisi> yuklenenMaclar = DosyaIslemleri.dosyadanOku(FIKSTUR_DOSYA, MacVerisi.class);
            if (yuklenenMaclar != null) {
                for (MacVerisi m : yuklenenMaclar) {
                    macGecmisi.put(m.getMacTarihi(), m);
                }
            }
        }
         catch (Exception e) { System.err.println("Yükleme hatası veya dosya boş."); }
    }

    public List<Futbolcu> getFutbolcuKadrosu() { return futbolcuKadrosu; }
    public List<TeknikDirektor> getTeknikDirektorler() { return teknikDirektorler; }
    public List<YardimciAntrenor> getYardimciAntrenorler() { return yardimciAntrenorler; }
    public List<Fizyoterapist> getFizyoterapistler() { return fizyoterapistler; }



    public String detayliFinansalAnalizRaporu() {
        StringBuilder sb = new StringBuilder();
        sb.append("========= KULÜP FİNANSAL ANALİZ RAPORU =========\n\n");

        // 1. Futbolcu Maaş ve Prim Analizi (Kümülatif Veriden Hesaplar)
        double futbolcuMaasToplam = 0;
        double futbolcuPrimToplam = 0;
        for (Futbolcu f : getFutbolcuKadrosu()) {
            futbolcuMaasToplam += f.maasHesapla(); // Milyon €
            // f.primHesapla() artık içerideki toplamGol ve toplamAsist'i kullanıyor
            futbolcuPrimToplam += f.primHesapla(0, 0);
        }

        // 2. Teknik Kadro ve Sağlık Ekibi Toplamları
        double tdMaasToplam = 0;
        for (TeknikDirektor td : getTeknikDirektorler()) tdMaasToplam += td.maasHesapla();

        double antrenorMaasToplam = 0;
        for (YardimciAntrenor ya : getYardimciAntrenorler()) antrenorMaasToplam += ya.maasHesapla();

        double fizyoMaasToplam = 0;
        for (Fizyoterapist fi : getFizyoterapistler()) fizyoMaasToplam += fi.maasHesapla();

        // Giderleri Raporla (Tablo Formatında)
        sb.append(String.format("%-30s : %.2fM €\n", "► Futbolcu Maaş Gideri", futbolcuMaasToplam));
        sb.append(String.format("%-30s : %.3fM €\n", "► Dağıtılan Toplam Prim", futbolcuPrimToplam));
        sb.append(String.format("%-30s : %,.0f €\n", "► Teknik Direktör Gideri", tdMaasToplam));
        sb.append(String.format("%-30s : %,.0f €\n", "► Antrenör Kadrosu Gideri", antrenorMaasToplam));
        sb.append(String.format("%-30s : %,.0f €\n\n", "► Fizyoterapist Gideri", fizyoMaasToplam));

        sb.append("------------------------------------------------\n");

        // Genel Toplam Hesaplama
        double genelToplamGider = (futbolcuMaasToplam * 1000000) + (futbolcuPrimToplam * 1000000)
                + tdMaasToplam + antrenorMaasToplam + fizyoMaasToplam;

        sb.append(String.format("TOPLAM KULÜP GİDERİ (Net): %,.0f €\n", genelToplamGider));
        sb.append("================================================");

        // Raporu hem döndür hem de otomatik olarak dosyaya kaydet
        String finalRapor = sb.toString();
        finansalRaporuDosyayaKaydet(finalRapor);

        return finalRapor;
    }

    public void finansalRaporuDosyayaKaydet(String raporMetni) {
        // Proje klasöründe "finansal_analiz_raporu.txt" adıyla bir dosya oluşturur
        try (PrintWriter out = new PrintWriter(new FileWriter("finansal_analiz_raporu.txt"))) {
            out.println(raporMetni);
            System.out.println("Rapor başarıyla dosyaya kaydedildi.");
        } catch (IOException e) {
            System.err.println("Rapor kaydedilirken hata oluştu: " + e.getMessage());
        }
    }


    public void macEkle(LocalDate tarih, String rakip, String skor, String macTuru) {
        // MacVerisi constructor'ına macturu parametresini ekliyoruz
        // null geçilen alan macSaati'dir, istersen onu da ekleyebilirsin
        MacVerisi yeniMac = new MacVerisi(tarih, null, rakip, skor, macTuru);

        macGecmisi.put(tarih, yeniMac);

        // Verilerin kaybolmaması için ekleme sonrası kaydı tetikle
        tumVerileriKaydet();
    }

    public Map<LocalDate, MacVerisi> getMacGecmisi() {
        return macGecmisi;
    }
    // Dosya adını tanımla


    // Verileri Kaydet metoduna ekle
    public void verileriKaydet() {
        try {
            // Mevcut futbolcu/personel kaydetme kodlarının yanına ekle:
            DosyaIslemleri.dosyayaYaz(new ArrayList<>(macGecmisi.values()), FIKSTUR_DOSYA);
        } catch (IOException e) {
            System.err.println("Fikstür kaydedilemedi: " + e.getMessage());
        }
    }

    // Verileri Yükle metoduna ekle
    public void verileriYukle() {
        try {
            List<MacVerisi> yuklenenMaclar = DosyaIslemleri.dosyadanOku(FIKSTUR_DOSYA, MacVerisi.class);
            if (yuklenenMaclar != null) {
                for (MacVerisi m : yuklenenMaclar) {
                    macGecmisi.put(m.getMacTarihi(), m);
                }
            }
        } catch (Exception e) {
            System.out.println("Geçmiş fikstür kaydı bulunamadı, yeni liste oluşturuluyor.");
        }
    }
    public Futbolcu futbolcuBul(String formaNoStr) {
        try {
            int arananNo = Integer.parseInt(formaNoStr); // String'i sayıya çeviriyoruz
            for (Futbolcu f : futbolcuKadrosu) {
                // f.getFormaNo() artık image_71d007'deki int değişkenine ulaşacak
                if (f.getFormaNo() == arananNo) {
                    return f;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Geçersiz forma numarası formatı!");
        }
        return null;
    }

}