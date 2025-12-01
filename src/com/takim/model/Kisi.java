package com.takim.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * 4.2: Abstract Sınıf (1/2). Tum insan varliklari icin temel sinif.
 */
public abstract class Kisi {

    private String ad;
    private String soyad;
    private LocalDate dogumTarihi; // 6: LocalDate kullanimi
    private String tcKimlikNo;

    public Kisi(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo) {
        this.ad = ad;
        this.soyad = soyad;
        this.dogumTarihi = dogumTarihi;
        this.tcKimlikNo = tcKimlikNo;
    }

    // Yalnızca alt sınıflar için basit constructor
    public Kisi() {}

    // 4.2: Somut metot (1/2)
    public int yasHesapla() {
        if (dogumTarihi == null) return 0;
        return Period.between(dogumTarihi, LocalDate.now()).getYears();
    }

    public abstract void bilgiYazdir(); // 4.2: Abstract metot (1/2)

    // Getter/Setter metodlari
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }
    public LocalDate getDogumTarihi() { return dogumTarihi; }
    public void setDogumTarihi(LocalDate dogumTarihi) { this.dogumTarihi = dogumTarihi; }
    public String getTcKimlikNo() { return tcKimlikNo; }
    public void setTcKimlikNo(String tcKimlikNo) { this.tcKimlikNo = tcKimlikNo; }

    @Override
    public String toString() { // 4.4: Override (1/5)
        return "Ad: " + ad + ", Soyad: " + soyad + ", Dogum Tarihi: " + dogumTarihi;
    }
}