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

    @Override public double maasHesapla() { return getMaas() + (hizmetYiliHesapla() * 500); }
    @Override public double primHesapla(int performansPuani) { return performansPuani > 70 ? getMaas() * 0.05 : 0; }
    @Override public double yillikMaasArtisiOraniGetir() { return hizmetYiliHesapla() > 3 ? 0.07 : 0.05; }
    @Override public double vergiKesintisiHesapla(Month ay) { return ay.getValue() >= 10 ? maasHesapla() * 0.25 : maasHesapla() * 0.18; }
    @Override public double yillikBrutMaasGetir() { return (maasHesapla() * 12) + (hizmetYiliHesapla() * 500); }
    @Override
    public double verimlilikPuaniHesapla(int performans) {
        // Yardımcı antrenörlerde saha içi tecrübe çarpanı kullanılır
        return super.verimlilikPuaniHesapla(performans) * (1 + (getSahaIciSure() / 20));
    }

    @Override
    public String butceDurumuGetir() {
        return getSahaIciSure() > 10 ? "Kıdemli Antrenör Bütçesi" : "Gelişim Antrenörü";
    }

    @Override
    public String maliyetDurumuAnaliziGetir() {
        return "Lisans Tipi: " + getAntrenorlukLisansi() + " Analizi";
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