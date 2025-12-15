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
    // Calisan sınıfına taşınacağı için burada maas ve iseBaslamaTarihi kaldırıldı.
    // Ancak alt sınıflar maaş ve tarih parametrelerini Kisi yapısına göre bekleyebilir.

    // Constructor 1
    public Kisi(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo) {
        this.ad = ad;
        this.soyad = soyad;
        this.dogumTarihi = dogumTarihi;
        this.tcKimlikNo = tcKimlikNo;
    }

    // Constructor 2 (Calisan için gerekli olan ancak Kisi'de tutulmayan alanları alır)
    // NOT: Calisan sınıfı bu constructor'ı kullanacaktır.
    public Kisi(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo, double maas, LocalDate iseBaslamaTarihi) {
        this(ad, soyad, dogumTarihi, tcKimlikNo);
        // Bu parametreler Kisi sınıfında tutulmaz, sadece Calisan'ın çağrısını destekler.
    }

    // Abstract metod 1 (mevcut)
    public abstract void bilgiYazdir();

    // Abstract metod 2 (Gereksinim 4.2 için eklenmişti)
    public abstract double genelKatkiHesapla();

    // Getter ve Setterlar (getMaas ve getIseBaslamaTarihi Calisan'a taşındı)
    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public LocalDate getDogumTarihi() { return dogumTarihi; }
    public String getTcKimlikNo() { return tcKimlikNo; }

    // getMaas metodu burada yok. Calisan sınıfına bakın.
    // hizmetYiliHesapla metodu burada yok. Calisan sınıfına bakın.


    @Override
    public String toString() {
        return "Ad: " + ad + ", Soyad: " + soyad + ", Dogum Tarihi: " + dogumTarihi;
    }
}