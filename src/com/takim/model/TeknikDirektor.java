package com.takim.model;

import java.time.LocalDate;
import java.time.Month;

public class TeknikDirektor extends Calisan {

    private int lisansYili;
    private String taktik;
    private double bonusHedefi;
    private String eskiTakim;
    private double puanOrt;
    private int kupaSayisi;

    public TeknikDirektor(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                          double maas, LocalDate iseBaslamaTarihi, int lisansYili, String taktik, double bonusHedefi,
                          String eskiTakim, double puanOrt, int kupaSayisi) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi);
        this.lisansYili = lisansYili;
        this.taktik = taktik;
        this.bonusHedefi = bonusHedefi;
        this.eskiTakim = eskiTakim;
        this.puanOrt = puanOrt;
        this.kupaSayisi = kupaSayisi;
    }

    @Override public double maasHesapla() { return getMaas() + (hizmetYiliHesapla() * 1000); }
    @Override public double primHesapla(int performansPuani) { return performansPuani > 80 ? getMaas() * 0.15 : 0; }

    @Override public double yillikMaasArtisiOraniGetir() { return hizmetYiliHesapla() > 5 ? 0.10 : 0.05; }
    @Override public double vergiKesintisiHesapla(Month ay) { return maasHesapla() * 0.20; }
    @Override public double yillikBrutMaasGetir() { return maasHesapla() * 12; }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); }

    // EKSİK GETTER'LAR BURAYA EKLENDİ (DosyaIslemleri için kritik)
    public int getLisansYili() { return lisansYili; }
    public String getTaktik() { return taktik; }
    public double getBonusHedefi() { return bonusHedefi; }
    public String getEskiTakim() { return eskiTakim; }
    public double getPuanOrt() { return puanOrt; }
    public int getKupaSayisi() { return kupaSayisi; }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " >> " +
                super.toString() +
                ", Taktik: " + taktik +
                ", Eski Takım: " + eskiTakim +
                ", Puan Ort.: " + String.format("%.2f", puanOrt) +
                ", Kupa Sayısı: " + kupaSayisi;
    }
}