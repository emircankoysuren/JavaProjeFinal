package com.takim.model;

import java.time.LocalDate;
import java.time.Month;

/**
 * 1.1: Alt Sinif (3/4). Calisan hiyerarsisi.
 */
public class Fizyoterapist extends Calisan {

    private String sertifikaNo;
    private String uzmanlikAlani;
    private boolean sporMasajYetkisi;
    private short tecrubeYili; // 2.1: short primitive tipi

    // 3.5: Constructor Overloading (2. sinif)
    public Fizyoterapist(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                         double maas, LocalDate iseBaslamaTarihi,
                         String sertifikaNo, String uzmanlikAlani, boolean sporMasajYetkisi) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi);
        this.sertifikaNo = sertifikaNo;
        this.uzmanlikAlani = uzmanlikAlani;
        this.sporMasajYetkisi = sporMasajYetkisi;
        this.tecrubeYili = (short)hizmetYiliHesapla(); // 2.2: Explicit tip donusumu
    }

    public Fizyoterapist(String ad, String soyad, double maas, String uzmanlikAlani) {
        super(ad, soyad, null, null, maas, LocalDate.now());
        this.uzmanlikAlani = uzmanlikAlani;
    }

    public short getTecrubeYili() {
        return tecrubeYili;
    }

    @Override public double maasHesapla() { return getMaas() + (this.sporMasajYetkisi ? 2000 : 500); } // 9. Bolum: Koşullu İşleç
    @Override public double primHesapla(int performansPuani) { return (double)tecrubeYili * (performansPuani / 100.0); }

    // MaasHesaplanabilir implementasyonu
    @Override public double yillikMaasArtisiOraniGetir() { return this.uzmanlikAlani.toLowerCase().contains("spor") ? 0.12 : 0.08; }
    @Override public double vergiKesintisiHesapla(Month ay) { return ay == Month.JANUARY ? maasHesapla() * 0.15 : maasHesapla() * 0.20; }
    @Override public double yillikBrutMaasGetir() { return (maasHesapla() * 12) + ((double)getTecrubeYili() * 1000); }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); } // Kisi implementasyonu
}