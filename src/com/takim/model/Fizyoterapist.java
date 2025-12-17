package com.takim.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;

public class Fizyoterapist extends Calisan implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uzmanlikAlani;
    private boolean sporMasajYetkisi;
    private short tecrubeYili;
    private String uyruk;
    private String mezuniyetUniversitesi;
    private double gorevSuresiYil;

    public Fizyoterapist(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                         double maas, LocalDate iseBaslamaTarihi,
                         String uzmanlikAlani, boolean sporMasajYetkisi,
                         String uyruk, String mezuniyetUniversitesi, double gorevSuresiYil) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi);
        this.uzmanlikAlani = uzmanlikAlani;
        this.sporMasajYetkisi = sporMasajYetkisi;
        this.tecrubeYili = (short)hizmetYiliHesapla();
        this.uyruk = uyruk;
        this.mezuniyetUniversitesi = mezuniyetUniversitesi;
        this.gorevSuresiYil = gorevSuresiYil;
    }

    public String getUzmanlikAlani() { return uzmanlikAlani; }
    public boolean isSporMasajYetkisi() { return sporMasajYetkisi; }
    public short getTecrubeYili() { return tecrubeYili; }
    public String getUyruk() { return uyruk; }
    public String getMezuniyetUniversitesi() { return mezuniyetUniversitesi; }
    public double getGorevSuresiYil() { return gorevSuresiYil; }

    @Override public double maasHesapla() { return getMaas() + (this.sporMasajYetkisi ? 2000 : 500); }
    @Override public double primHesapla(int performansPuani) { return (double)tecrubeYili * (performansPuani / 100.0); }
    @Override public double yillikMaasArtisiOraniGetir() { return this.uzmanlikAlani.toLowerCase().contains("spor") ? 0.12 : 0.08; }
    @Override public double vergiKesintisiHesapla(Month ay) { return ay == Month.JANUARY ? maasHesapla() * 0.15 : maasHesapla() * 0.20; }
    @Override public double yillikBrutMaasGetir() { return (maasHesapla() * 12) + ((double)getTecrubeYili() * 1000); }
    @Override
    public double kidemTazminatiHesapla() {
        // Fizyoterapistlerde uzmanlık alanına göre ek tazminat çarpanı
        double ek = getUzmanlikAlani().toLowerCase().contains("spor") ? 5000 : 2000;
        return super.kidemTazminatiHesapla() + ek;
    }

    @Override
    public String butceDurumuGetir() {
        return isSporMasajYetkisi() ? "Kritik Sağlık Personeli (Bütçe Dahili)" : "Destek Personel";
    }

    @Override
    public String maliyetDurumuAnaliziGetir() {
        return "Uzmanlık: " + getUzmanlikAlani() + " - Sağlık Bütçesi Analizi";
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
                        "| %-20s : %s\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %.1f Yıl\n" +
                        "| %-20s : %.2f TL\n" +
                        "==========================================================",
                "ÜNVAN", "FİZYOTERAPİST",
                "Ad Soyad", getAd(), getSoyad(),
                "ID", (getId() != null ? getId() : "N/A"),
                "Uyruk", uyruk,
                "Uzmanlık Alanı", uzmanlikAlani,
                "Mezuniyet", mezuniyetUniversitesi,
                "Masaj Yetkisi", (sporMasajYetkisi ? "VAR" : "YOK"),
                "Görev Süresi", gorevSuresiYil,
                "Maaş", maasHesapla()
        );
    }
}