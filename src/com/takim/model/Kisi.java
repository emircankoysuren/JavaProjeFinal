package com.takim.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 1.1: Ust Sinif (Base Class).
 * KRİTİK DÜZELTME: Verilerin kaybolmaması için Serializable eklendi.
 */
public abstract class Kisi implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ad;
    private String soyad;
    private LocalDate dogumTarihi;
    private String tcKimlikNo;
    private double maas;
    private LocalDate iseBaslamaTarihi;

    // Constructor 1
    public Kisi(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo) {
        this.ad = ad;
        this.soyad = soyad;
        this.dogumTarihi = dogumTarihi;
        this.tcKimlikNo = tcKimlikNo;
    }

    // Constructor 2
    public Kisi(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo, double maas, LocalDate iseBaslamaTarihi) {
        this(ad, soyad, dogumTarihi, tcKimlikNo);
        this.maas = maas;
        this.iseBaslamaTarihi = iseBaslamaTarihi;
    }

    // Abstract metod
    public abstract void bilgiYazdir();

    public int hizmetYiliHesapla() {
        if (iseBaslamaTarihi == null) return 0;
        return LocalDate.now().getYear() - iseBaslamaTarihi.getYear();
    }

    // Getter ve Setterlar
    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public LocalDate getDogumTarihi() { return dogumTarihi; }
    public String getTcKimlikNo() { return tcKimlikNo; }
    public double getMaas() { return maas; }

    @Override
    public String toString() {
        return "Ad: " + ad + ", Soyad: " + soyad + ", Dogum Tarihi: " + dogumTarihi;
    }
}