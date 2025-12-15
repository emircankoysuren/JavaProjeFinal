package com.takim.model;

import java.time.LocalDate;
import java.time.Month;

public class YardimciAntrenor extends Calisan {

    // MEVCUT ALANLAR
    private String uzmanlikAlani; // Görev (Yardımcı, Kaleci vb.) olarak kullanılacak
    private double sahaIciSure; // Görev Süresi (Yıl) olarak kullanılacak

    // YENİ EKLENEN ALANLAR
    private String uyruk;
    private String antrenorlukLisansi;

    private int hucumPuani;
    private int defansPuani;
    private int taktikPuani;
    private int teknikPuani;
    private int disiplinPuani;
    private int uyumlulukPuani;
    // ... (checkPuanRange metodu mevcut)

    private static int checkPuanRange(int puan, String fieldName) {
        if (puan < 1 || puan > 20) {
            System.err.println("HATA: " + fieldName + " puani 1 ile 20 arasinda olmalidir. Deger 1'e ayarlandi.");
            return 1;
        }
        return puan;
    }


    // GÜNCELLENMİŞ YAPICI METOT (16 PARAMETRELİ)
    public YardimciAntrenor(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                            double maas, LocalDate iseBaslamaTarihi, String uzmanlikAlani, double sahaIciSure,
                            String uyruk, String antrenorlukLisansi, // YENİ EKLENENLER
                            int hucumPuani, int defansPuani, int taktikPuani,
                            int teknikPuani, int disiplinPuani, int uyumlulukPuani) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi);
        this.uzmanlikAlani = uzmanlikAlani;
        this.sahaIciSure = sahaIciSure;

        // YENİ ALAN ATAMALARI
        this.uyruk = uyruk;
        this.antrenorlukLisansi = antrenorlukLisansi;

        setHucumPuani(hucumPuani);
        setDefansPuani(defansPuani);
        setTaktikPuani(taktikPuani);
        setTeknikPuani(teknikPuani);
        setDisiplinPuani(disiplinPuani);
        setUyumlulukPuani(uyumlulukPuani);
    }

    // EKSİK GETTER'LAR BURAYA EKLENDİ (DosyaIslemleri için kritik)
    public String getUzmanlikAlani() { return uzmanlikAlani; }
    public double getSahaIciSure() { return sahaIciSure; }
    public int getHucumPuani() { return hucumPuani; }
    public int getDefansPuani() { return defansPuani; }
    public int getTaktikPuani() { return taktikPuani; }
    public int getTeknikPuani() { return teknikPuani; }
    public int getDisiplinPuani() { return disiplinPuani; }
    public int getUyumlulukPuani() { return uyumlulukPuani; }

    // YENİ GETTER METOTLARI
    public String getUyruk() { return uyruk; }
    public String getAntrenorlukLisansi() { return antrenorlukLisansi; }

    public void setHucumPuani(int hucumPuani) { this.hucumPuani = checkPuanRange(hucumPuani, "Hucum"); }
    public void setDefansPuani(int defansPuani) { this.defansPuani = checkPuanRange(defansPuani, "Defans"); }
    public void setTaktikPuani(int taktikPuani) { this.taktikPuani = checkPuanRange(taktikPuani, "Taktik"); }
    public void setTeknikPuani(int teknikPuani) { this.teknikPuani = checkPuanRange(teknikPuani, "Teknik"); }
    public void setDisiplinPuani(int disiplinPuani) { this.disiplinPuani = checkPuanRange(disiplinPuani, "Disiplin"); }
    public void setUyumlulukPuani(int uyumlulukPuani) { this.uyumlulukPuani = checkPuanRange(uyumlulukPuani, "Uyumluluk"); }


    @Override public double maasHesapla() { return getMaas() + (hizmetYiliHesapla() * 500); }
    @Override public double primHesapla(int performansPuani) { return performansPuani > 70 ? getMaas() * 0.05 : 0; }

    @Override public double yillikMaasArtisiOraniGetir() { return hizmetYiliHesapla() > 3 ? 0.07 : 0.05; }
    @Override public double vergiKesintisiHesapla(Month ay) { return ay.getValue() >= 10 ? maasHesapla() * 0.25 : maasHesapla() * 0.18; }
    @Override public double yillikBrutMaasGetir() { return (maasHesapla() * 12) + (hizmetYiliHesapla() * 500); }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); }

    // GÜNCELLENMİŞ toString()
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " >> ID: " + (getId() != null ? getId() : "N/A") + ", " +
                super.toString() +
                ", Görev: " + uzmanlikAlani +
                ", Uyruk: " + uyruk + // YENİ EKLENDİ
                ", Lisans: " + antrenorlukLisansi + // YENİ EKLENDİ
                ", Hucum: " + hucumPuani +
                ", Defans: " + defansPuani +
                ", Taktik: " + taktikPuani;
    }
}