package com.takim.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public abstract class Calisan extends Kisi implements MaasHesaplanabilir, Raporlanabilir {

    // --- DEĞİŞKENLER (FIELDS) ---
    protected String id;
    private double maas;
    private LocalDate iseBaslamaTarihi;

    // --- CONSTRUCTOR ---
    public Calisan(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                   double maas, LocalDate iseBaslamaTarihi) {
        super(ad, soyad, dogumTarihi, tcKimlikNo);
        this.maas = maas;
        this.iseBaslamaTarihi = iseBaslamaTarihi;
    }

    // --- SINIFIN KENDİ ÖZEL METOTLARI (BUSINESS LOGIC) ---
    public int hizmetYiliHesapla() {
        if (this.iseBaslamaTarihi == null) return 0;
        return (int) ChronoUnit.YEARS.between(this.iseBaslamaTarihi, LocalDate.now());
    }

    public int yillikIzinHakki() {
        return hizmetYiliHesapla() < 5 ? 14 : 20;
    }

    // --- MAASHESAPLANABILIR INTERFACE METOTLARI ---
    @Override
    public double maasHesapla() {
        return this.maas;
    }

    @Override
    public double primHesapla(int gol, int asist) {
        return 0.0;
    }

    @Override
    public double toplamMaliyetHesapla(int gol, int asist) {
        return maasHesapla() + primHesapla(gol, asist);
    }

    // --- RAPORLANABILIR INTERFACE METOTLARI ---
    @Override
    public String ozetRaporOlustur() {
        return this.toString();
    }

    @Override
    public String detayliIstatistikGetir(LocalDate baslangic, LocalDate bitis) {
        return "Detaylı rapor.";
    }

    @Override
    public boolean raporDurumuKontrolEt() {
        return true;
    }

    // --- KISI SINIFINDAN GELEN ABSTRACT METOT ---
    @Override
    public double genelKatkiHesapla() {
        return maas / 1000;
    }

    // --- GETTER VE SETTER METOTLARI ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getMaas() {
        return maas;
    }

    public void setMaas(double maas) {
        if (maas < 0) {
            System.err.println("Hata: Maas negatif olamaz. Deger 0'a ayarlandi.");
            this.maas = 0;
        } else {
            this.maas = maas;
        }
    }

    public LocalDate getIseBaslamaTarihi() {
        return iseBaslamaTarihi;
    }

    public void setIseBaslamaTarihi(LocalDate iseBaslamaTarihi) {
        this.iseBaslamaTarihi = iseBaslamaTarihi;
    }

    // --- OVERRIDE METOTLAR (TOSTRING) ---
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " >> ID: " + (id != null ? id : "N/A") + ", " +
                super.toString() +
                ", Maas: " + maas +
                ", Hizmet Yili: " + hizmetYiliHesapla();
    }
}