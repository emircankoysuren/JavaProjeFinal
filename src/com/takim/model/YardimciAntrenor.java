package com.takim.model;

import java.time.LocalDate;
import java.time.Month;

public class YardimciAntrenor extends Calisan {

    private String uzmanlikAlani; // Görev (Yardımcı, Kaleci vb.)
    private double sahaIciSure;   // Görev Süresi (Yıl)
    private String uyruk;
    private String antrenorlukLisansi;

    public YardimciAntrenor(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                            double maas, LocalDate iseBaslamaTarihi, String uzmanlikAlani,
                            double sahaIciSure, String uyruk, String antrenorlukLisansi) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi);
        this.uzmanlikAlani = uzmanlikAlani;
        this.sahaIciSure = sahaIciSure;
        this.uyruk = uyruk;
        this.antrenorlukLisansi = antrenorlukLisansi;
    }

    public String getUzmanlikAlani() { return uzmanlikAlani; }
    public double getSahaIciSure() { return sahaIciSure; }
    public String getUyruk() { return uyruk; }
    public String getAntrenorlukLisansi() { return antrenorlukLisansi; }

    @Override
    public double maasHesapla() {
        // Senin manuel girdiğin temel maaşı döndürür
        return getMaas();
    }

    @Override
    public double primHesapla(int gol, int asist) {
        // Yardımcı antrenörler için prim hesaplamıyoruz (0 döner)
        return 0.0;
    }

    @Override
    public double toplamMaliyetHesapla(int gol, int asist) {
        // Kulüp Gideri = Temel Maaş + Prim (0)
        // Aritmetik işlem (+ operatörü) kullanımı
        return maasHesapla() + primHesapla(gol, asist);
    }


    @Override public void bilgiYazdir() { System.out.println(this.toString()); }

    @Override
    public String toString() {
        return String.format(
                "==========================================================\n" +
                        "| %-20s : %s\n" +
                        "----------------------------------------------------------\n" +
                        "| %-20s : %s %s\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %.1f Yıl\n" +
                        "| %-20s : %.2f TL\n" +
                        "==========================================================",
                "ÜNVAN", "YARDIMCI ANTRENÖR",
                "Ad Soyad", getAd(), getSoyad(),
                "ID", (getId() != null ? getId() : "N/A"),
                "Görev alanı", uzmanlikAlani,
                "Lisans", antrenorlukLisansi,
                "Tecrübe", sahaIciSure,
                "Maaş", maasHesapla()
        );
    }
}