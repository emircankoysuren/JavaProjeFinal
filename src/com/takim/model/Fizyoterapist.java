package com.takim.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Fizyoterapist extends Calisan implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- DEĞİŞKENLER (FIELDS) ---
    private String uzmanlikAlani;
    private boolean sporMasajYetkisi;
    private short tecrubeYili;
    private String uyruk;
    private String mezuniyetUniversitesi;
    private double gorevSuresiYil;

    // --- CONSTRUCTOR ---
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

    // --- GETTER METOTLARI ---
    public String getUzmanlikAlani() { return uzmanlikAlani; }
    public boolean isSporMasajYetkisi() { return sporMasajYetkisi; }
    public short getTecrubeYili() { return tecrubeYili; }
    public String getUyruk() { return uyruk; }
    public String getMezuniyetUniversitesi() { return mezuniyetUniversitesi; }
    public double getGorevSuresiYil() { return gorevSuresiYil; }

    // --- MAASHESAPLANABILIR INTERFACE METOTLARI ---
    @Override
    public double maasHesapla() {
        // Senin manuel girdiğin maaşı döndürür
        return getMaas();
    }

    @Override
    public double primHesapla(int gol, int asist) {
        // Fizyoterapistlerin gol/asist primi olmaz
        // Ancak profesyonellik gereği: Eğer masaj yetkisi varsa sabit 2000€ ek prim alabilir
        return this.sporMasajYetkisi ? 2000.0 : 0.0;
    }

    @Override
    public double toplamMaliyetHesapla(int gol, int asist) {
        // Kulüp Gideri = Manuel Maaş + Masaj Primi (varsa)
        // Burada + operatörü ile aritmetik gereksinim sağlanır
        return maasHesapla() + primHesapla(gol, asist);
    }

    // --- OVERRIDE METOTLAR (BILGI YAZDIR VE TOSTRING) ---
    @Override
    public void bilgiYazdir() {
        System.out.println(this.toString());
    }

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