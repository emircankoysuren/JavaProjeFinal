package com.takim.util;

import com.takim.model.*;
import com.takim.exception.GecersizFormaNoException;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 1.1: Utility sinifi (1/2). Metin tabanlı personel kalıcılığı için güncellendi.
 */
public class DosyaIslemleri {

    private static final String RAPOR_DOSYASI = "takim_rapor.txt";
    private static final String VERI_DOSYASI = "futbolcu_data.txt"; // Eski CSV dosyası
    private static final String PERSONEL_VERI_DOSYASI = "personel.txt"; // YENİ: Tüm personel dosyası
    private static final String PATH_AYIRICI = System.getProperty("file.separator");
    private static final String AYIRICI = "|"; // Veri alanlarını ayırmak için

    // Formatlayıcı sınıfındaki statik alanları kopyalıyoruz
    public static final DateTimeFormatter TARİH_FORMATI = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    // ----------------------------------------------------------------------------------
    // TÜM PERSONELİ KAYDETME METODU (Text Tabanlı)
    // ----------------------------------------------------------------------------------
    public static void personelVerileriniKaydet(List<Kisi> tumPersonel) {
        File dosya = new File("." + PATH_AYIRICI + PERSONEL_VERI_DOSYASI);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dosya))) {

            for (Kisi kisi : tumPersonel) {
                String satir = kisiToSaveString(kisi);
                writer.write(satir + "\n");
            }

            System.out.println(Formatlayici.renklendir("Tüm personel verileri (" + tumPersonel.size() + ") dosyaya basariyla kaydedildi: " + dosya.getAbsolutePath(), Formatlayici.YESIL));

        } catch (IOException e) {
            System.err.println(Formatlayici.renklendir("HATA: Personel veri kaydetme sirasinda sorun olustu: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }


    // ----------------------------------------------------------------------------------
    // TÜM PERSONELİ YÜKLEME METODU (Text Tabanlı)
    // ----------------------------------------------------------------------------------
    public static List<Kisi> personelVerileriniYukle() {
        List<Kisi> yuklenenPersonel = new ArrayList<>();
        File dosya = new File("." + PATH_AYIRICI + PERSONEL_VERI_DOSYASI);

        if (!dosya.exists()) {
            System.out.println(Formatlayici.renklendir("Personel kayit dosyasi bulunamadi. Yeni listeler baslatiliyor.", Formatlayici.MAVI));
            return yuklenenPersonel;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dosya))) {
            String satir;
            int satirSayisi = 0;
            while ((satir = reader.readLine()) != null) {
                satirSayisi++;
                if (satir.trim().isEmpty()) continue;

                // Bölme işlemi sırasında ayırıcının regex karakteri olarak algılanmaması için escape edilir.
                String[] alanlar = satir.split("\\" + AYIRICI);

                if (alanlar.length < 1) continue;

                String sinifAdi = alanlar[0];
                try {
                    Kisi yeniKisi = parseKisi(sinifAdi, alanlar);
                    if (yeniKisi != null) {
                        yuklenenPersonel.add(yeniKisi);
                    }
                } catch (Exception e) {
                    System.err.println(Formatlayici.renklendir("HATA: Satır " + satirSayisi + " (Tipi: " + sinifAdi + ") yüklenemedi. Hata: " + e.getMessage(), Formatlayici.KIRMİZİ));
                }
            }
            System.out.println(Formatlayici.renklendir(yuklenenPersonel.size() + " personel verisi dosyadan basariyla yuklendi.", Formatlayici.YESIL));


        } catch (FileNotFoundException e) {
            System.err.println(Formatlayici.renklendir("UYARI: Personel veri dosyası bulunamadı, yeni kadro başlatılıyor.", Formatlayici.MAVI));
        } catch (IOException e) {
            System.err.println(Formatlayici.renklendir("HATA: Dosya okuma hatası: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
        return yuklenenPersonel;
    }


    // ----------------------------------------------------------------------------------
    // YARDIMCI KAYDETME/YÜKLEME METOTLARI
    // ----------------------------------------------------------------------------------

    private static String kisiToSaveString(Kisi kisi) {
        StringBuilder sb = new StringBuilder();
        // Sınıf adını ekle (Tipi geri yüklemek için kritik)
        sb.append(kisi.getClass().getSimpleName()).append(AYIRICI);

        // Ortak Kisi Alanları
        sb.append(kisi.getAd()).append(AYIRICI);
        sb.append(kisi.getSoyad()).append(AYIRICI);
        sb.append(kisi.getDogumTarihi().format(TARİH_FORMATI)).append(AYIRICI);
        sb.append(kisi.getTcKimlikNo()).append(AYIRICI);

        if (kisi instanceof Futbolcu f) {
            sb.append(f.getFormaNo()).append(AYIRICI);
            sb.append(f.getMevki()).append(AYIRICI);
            sb.append(f.getGolSayisi()).append(AYIRICI);
            sb.append(f.getAsistSayisi());
        } else if (kisi instanceof Calisan c) {
            // Calisan Alanları
            sb.append(c.getMaas()).append(AYIRICI);
            sb.append(c.getIseBaslamaTarihi().format(TARİH_FORMATI)).append(AYIRICI);

            if (kisi instanceof TeknikDirektor td) {
                sb.append(td.getLisansYili()).append(AYIRICI);
                sb.append(td.getTaktik()).append(AYIRICI);
                sb.append(td.getBonusHedefi()).append(AYIRICI);
                // YENİ ALANLAR
                sb.append(td.getEskiTakim()).append(AYIRICI);
                sb.append(td.getPuanOrt()).append(AYIRICI);
                sb.append(td.getKupaSayisi());
            } else if (kisi instanceof YardimciAntrenor ya) {
                sb.append(ya.getUzmanlikAlani()).append(AYIRICI);
                sb.append(ya.getSahaIciSure()).append(AYIRICI);
                // YENİ PUAN ALANLARI
                sb.append(ya.getHucumPuani()).append(AYIRICI);
                sb.append(ya.getDefansPuani()).append(AYIRICI);
                sb.append(ya.getTaktikPuani()).append(AYIRICI);
                sb.append(ya.getTeknikPuani()).append(AYIRICI);
                sb.append(ya.getDisiplinPuani()).append(AYIRICI);
                sb.append(ya.getUyumlulukPuani());
            } else if (kisi instanceof Fizyoterapist fizyo) {
                sb.append(fizyo.getSertifikaNo()).append(AYIRICI);
                sb.append(fizyo.getUzmanlikAlani()).append(AYIRICI);
                sb.append(fizyo.isSporMasajYetkisi()).append(AYIRICI);
                // YENİ PUAN ALANLARI
                sb.append(fizyo.getFizyoterapiPuani()).append(AYIRICI);
                sb.append(fizyo.getSporBilimiPuani()).append(AYIRICI);
                sb.append(fizyo.getUyumlulukPuani()).append(AYIRICI);
                sb.append(fizyo.getDisiplinPuani());
            }
        }
        return sb.toString();
    }

    private static Kisi parseKisi(String sinifAdi, String[] alanlar) throws GecersizFormaNoException, NumberFormatException, DateTimeParseException {
        // Ortak Kisi Alanları (Index 1-4)
        String ad = alanlar[1];
        String soyad = alanlar[2];
        LocalDate dogumTarihi = LocalDate.parse(alanlar[3], TARİH_FORMATI);
        String tcKimlikNo = alanlar[4];

        switch (sinifAdi) {
            case "Futbolcu": {
                int formaNo = Integer.parseInt(alanlar[5]);
                String mevki = alanlar[6];
                int golSayisi = Integer.parseInt(alanlar[7]);
                int asistSayisi = Integer.parseInt(alanlar[8]);
                return new Futbolcu(ad, soyad, dogumTarihi, tcKimlikNo, formaNo, mevki, golSayisi, asistSayisi);
            }
            case "TeknikDirektor": {
                // Calisan Alanları (Index 5-6)
                double maas = Double.parseDouble(alanlar[5]);
                LocalDate iseBaslamaTarihi = LocalDate.parse(alanlar[6], TARİH_FORMATI);
                // TeknikDirektor Alanları (Index 7+)
                int lisansYili = Integer.parseInt(alanlar[7]);
                String taktik = alanlar[8];
                double bonusHedefi = Double.parseDouble(alanlar[9]);
                String eskiTakim = alanlar[10];
                double puanOrt = Double.parseDouble(alanlar[11]);
                int kupaSayisi = Integer.parseInt(alanlar[12]);
                return new TeknikDirektor(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi, lisansYili, taktik, bonusHedefi, eskiTakim, puanOrt, kupaSayisi);
            }
            case "YardimciAntrenor": {
                double maas = Double.parseDouble(alanlar[5]);
                LocalDate iseBaslamaTarihi = LocalDate.parse(alanlar[6], TARİH_FORMATI);
                String uzmanlikAlani = alanlar[7];
                double sahaIciSure = Double.parseDouble(alanlar[8]);
                int hucumPuani = Integer.parseInt(alanlar[9]);
                int defansPuani = Integer.parseInt(alanlar[10]);
                int taktikPuani = Integer.parseInt(alanlar[11]);
                int teknikPuani = Integer.parseInt(alanlar[12]);
                int disiplinPuani = Integer.parseInt(alanlar[13]);
                int uyumlulukPuani = Integer.parseInt(alanlar[14]);
                return new YardimciAntrenor(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi, uzmanlikAlani, sahaIciSure, hucumPuani, defansPuani, taktikPuani, teknikPuani, disiplinPuani, uyumlulukPuani);
            }
            case "Fizyoterapist": {
                double maas = Double.parseDouble(alanlar[5]);
                LocalDate iseBaslamaTarihi = LocalDate.parse(alanlar[6], TARİH_FORMATI);
                String sertifikaNo = alanlar[7];
                String uzmanlikAlani = alanlar[8];
                boolean sporMasajYetkisi = Boolean.parseBoolean(alanlar[9]);
                int fizyoterapiPuani = Integer.parseInt(alanlar[10]);
                int sporBilimiPuani = Integer.parseInt(alanlar[11]);
                int uyumlulukPuani = Integer.parseInt(alanlar[12]);
                int disiplinPuani = Integer.parseInt(alanlar[13]);
                return new Fizyoterapist(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi, sertifikaNo, uzmanlikAlani, sporMasajYetkisi, fizyoterapiPuani, sporBilimiPuani, uyumlulukPuani, disiplinPuani);
            }
            default:
                throw new IllegalArgumentException("Bilinmeyen personel tipi: " + sinifAdi);
        }
    }

    // --- MEVCUT UYUMLULUK METOTLARI ---

    // Raporlama Metodu
    public static void raporuDosyayaYaz(java.util.List<com.takim.model.Futbolcu> kadroListesi) throws java.io.IOException {
        java.io.File dosya = new java.io.File("." + PATH_AYIRICI + RAPOR_DOSYASI);
        java.io.BufferedWriter writer = null;
        try {
            writer = new java.io.BufferedWriter(new java.io.FileWriter(dosya));
            writer.write("Takim Kadro Sayisi: " + kadroListesi.size() + "\n\n");
            for (com.takim.model.Futbolcu futbolcu : kadroListesi) {
                writer.write(futbolcu.ozetRaporOlustur().toUpperCase() + "\n");
            }
            System.out.println("Rapor basariyla dosyaya yazildi: " + dosya.getAbsolutePath());
        } catch (java.io.IOException e) {
            throw new java.io.IOException("Dosya yazma hatasi: " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException ignored) {}
            }
        }
    }

    // Eski Futbolcu CSV metotları (Sadece uyumluluk için tutulur, artık kullanılmaz)
    public static void kadroVerileriniKaydet(List<Futbolcu> kadroListesi) { /* ... */ }
    public static List<Futbolcu> kadroVerileriniYukle() { return new ArrayList<>(); }
}