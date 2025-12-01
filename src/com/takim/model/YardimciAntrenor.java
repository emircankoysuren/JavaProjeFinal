package com.takim.model;

import java.time.LocalDate;
import java.time.Month;

/**
 * 1.1: Alt Sinif (2/4). Calisan hiyerarsisi.
 */
public class YardimciAntrenor extends Calisan {

    private String uzmanlikAlani;
    private double sahaIciSure;

    public YardimciAntrenor(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                            double maas, LocalDate iseBaslamaTarihi, String uzmanlikAlani, double sahaIciSure) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi);
        this.uzmanlikAlani = uzmanlikAlani;
        this.sahaIciSure = sahaIciSure;
    }

    @Override public double maasHesapla() { return getMaas() + (hizmetYiliHesapla() * 500); }
    @Override public double primHesapla(int performansPuani) { return performansPuani > 70 ? getMaas() * 0.05 : 0; }

    // MaasHesaplanabilir implementasyonu
    @Override public double yillikMaasArtisiOraniGetir() { return hizmetYiliHesapla() > 3 ? 0.07 : 0.05; }
    @Override public double vergiKesintisiHesapla(Month ay) { return ay.getValue() >= 10 ? maasHesapla() * 0.25 : maasHesapla() * 0.18; }
    @Override public double yillikBrutMaasGetir() { return (maasHesapla() * 12) + (hizmetYiliHesapla() * 500); }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); } // Kisi implementasyonu
}