package com.takim.model;

import java.time.LocalDate;
import java.time.Month;

/**
 * 1.1: Alt Sinif (1/4). Calisan hiyerarsisi.
 */
public class TeknikDirektor extends Calisan {

    private int lisansYili;
    private String taktik;
    private double bonusHedefi;

    public TeknikDirektor(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                          double maas, LocalDate iseBaslamaTarihi, int lisansYili, String taktik, double bonusHedefi) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi);
        this.lisansYili = lisansYili;
        this.taktik = taktik;
        this.bonusHedefi = bonusHedefi;
    }

    @Override public double maasHesapla() { return getMaas() + (hizmetYiliHesapla() * 1000); }
    @Override public double primHesapla(int performansPuani) { return performansPuani > 80 ? getMaas() * 0.15 : 0; }

    // MaasHesaplanabilir implementasyonu
    @Override public double yillikMaasArtisiOraniGetir() { return hizmetYiliHesapla() > 5 ? 0.10 : 0.05; }
    @Override public double vergiKesintisiHesapla(Month ay) { return maasHesapla() * 0.20; }
    @Override public double yillikBrutMaasGetir() { return maasHesapla() * 12; }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); } // Kisi implementasyonu
}