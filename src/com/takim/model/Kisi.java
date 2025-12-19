package com.takim.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 1.1: Ust Sinif (Base Class).
 * KRİTİK DÜZELTME: Verilerin kaybolmaması için Serializable eklendi.
 */
public abstract class Kisi implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- DEĞİŞKENLER (FIELDS) ---
    private String ad;
    private String soyad;
    private LocalDate dogumTarihi;
    private String tcKimlikNo;

    // --- CONSTRUCTORLAR ---

    // Constructor 1: Temel kişi bilgilerini alır
    public Kisi(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo) {
        this.ad = ad;
        this.soyad = soyad;
        this.dogumTarihi = dogumTarihi;
        this.tcKimlikNo = tcKimlikNo;
    }

    // Constructor 2: Calisan alt sınıfının çağrılarını desteklemek için genişletilmiş yapı
    public Kisi(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo, double maas, LocalDate iseBaslamaTarihi) {
        this(ad, soyad, dogumTarihi, tcKimlikNo);
        // Maas ve iseBaslamaTarihi parametreleri bu sınıfta tutulmaz, sadece alt sınıf desteği içindir.
    }

    // --- ABSTRACT METOTLAR ---
    public abstract void bilgiYazdir();
    public abstract double genelKatkiHesapla();

    // --- GETTER METOTLARI ---
    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public LocalDate getDogumTarihi() { return dogumTarihi; }
    public String getTcKimlikNo() { return tcKimlikNo; }

    // --- OVERRIDE METOTLAR (TOSTRING) ---
    @Override
    public String toString() {
        return "Ad: " + ad + ", Soyad: " + soyad + ", Dogum Tarihi: " + dogumTarihi;
    }
}