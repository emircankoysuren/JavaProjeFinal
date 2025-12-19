package com.takim.model;

import com.takim.exception.GecersizFormaNoException;
import java.io.Serializable;
import java.time.LocalDate;

public class Futbolcu extends Kisi implements Serializable, Raporlanabilir, Performans, MaasHesaplanabilir {

    private static final long serialVersionUID = 1L;

    // --- DEĞİŞKENLER (FIELDS) ---
    private int formaNo;
    private String mevki;
    private int golSayisi;
    private int asistSayisi;
    private String ulke;
    private double maas;
    private char mevkisizKarakter = 'A';
    private int toplamGol = 0;
    private int toplamAsist = 0;

    // --- CONSTRUCTORLAR ---

    public Futbolcu(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                    int formaNo, String mevki, int golSayisi, int asistSayisi, String ulke, double maas) throws GecersizFormaNoException {
        super(ad, soyad, dogumTarihi, tcKimlikNo);
        setFormaNo(formaNo);
        this.mevki = mevki;
        setGolSayisi(golSayisi);
        setAsistSayisi(asistSayisi);
        this.ulke = ulke;
        this.maas = maas;
    }

    public Futbolcu(String ad, String soyad, int formaNo, String mevki) {
        super(ad, soyad, null, null);
        this.formaNo = formaNo;
        this.mevki = mevki;
        this.golSayisi = 0;
        this.asistSayisi = 0;
        this.ulke = "Bilinmiyor";
        this.maas = 0.0;
    }

    // --- SINIFIN KENDİ ÖZEL METOTLARI (BUSINESS LOGIC) ---
    public int skorKatkisiHesapla() {
        return golSayisi + asistSayisi;
    }

    public void performansEkle(int yeniGol, int yeniAsist) {
        this.toplamGol += yeniGol;
        this.toplamAsist += yeniAsist;
    }

    public void performansGuncelle(int gol, int asist) {
        setGolSayisi(this.golSayisi + gol);
        setAsistSayisi(this.asistSayisi + asist);
    }

    public void performansGuncelle(int gol) {
        setGolSayisi(this.golSayisi + gol);
    }

    // --- PERFORMANS INTERFACE METOTLARI ---
    @Override
    public double performansPuaniniHesapla() {
        return (getGolSayisi() * 10.0) + (getAsistSayisi() * 7.0);
    }

    @Override
    public void performansGuncelle(double... veriler) {
        if (veriler.length >= 1) this.performansGuncelle((int)veriler[0]);
        if (veriler.length >= 2) this.setAsistSayisi(this.getAsistSayisi() + (int)veriler[1]);
    }

    @Override
    public String performansDurumuAnalizi() {
        double puan = performansPuaniniHesapla();
        if (puan > 150) return "SÜPERSTAR";
        if (puan > 100) return "YILDIZ";
        if (puan > 50) return "FORMDA";
        if (puan > 20) return "STANDART";
        return "GELİŞİM GEREKİYOR";
    }

    @Override
    public String getPerformansDetayi() {
        return String.format("%d Gol - %d Asist", golSayisi, asistSayisi);
    }

    // --- MAASHESAPLANABILIR INTERFACE METOTLARI ---
    @Override
    public double maasHesapla() {
        return this.maas;
    }

    @Override
    public double primHesapla(int g, int a) {
        // Kümülatif sezon verisi kullanılıyor
        return (this.toplamGol * 0.01) + (this.toplamAsist * 0.005);
    }

    @Override
    public double toplamMaliyetHesapla(int gol, int asist) {
        return maasHesapla() + primHesapla(gol, asist);
    }

    // --- RAPORLANABILIR INTERFACE METOTLARI ---
    @Override public String ozetRaporOlustur() { return this.toString(); }
    @Override public String detayliIstatistikGetir(LocalDate baslangic, LocalDate bitis) { return "Rapor"; }
    @Override public boolean raporDurumuKontrolEt() { return true; }

    // --- KISI SINIFINDAN GELEN ABSTRACT METOTLAR ---
    @Override public void bilgiYazdir() { System.out.println(this.toString()); }
    @Override public double genelKatkiHesapla() { return (double)skorKatkisiHesapla(); }

    // --- GETTER VE SETTER METOTLARI ---
    public int getFormaNo() { return formaNo; }
    public void setFormaNo(int formaNo) throws GecersizFormaNoException {
        if (formaNo < 1 || formaNo > 99) throw new GecersizFormaNoException("Hata: 1-99 arası olmalı: " + formaNo);
        this.formaNo = formaNo;
    }

    public int getGolSayisi() { return golSayisi; }
    public void setGolSayisi(int golSayisi) {
        if (golSayisi < 0) throw new IllegalArgumentException("Negatif olamaz");
        this.golSayisi = golSayisi;
    }

    public int getAsistSayisi() { return asistSayisi; }
    public void setAsistSayisi(int asistSayisi) {
        if (asistSayisi < 0) throw new IllegalArgumentException("Negatif olamaz");
        this.asistSayisi = asistSayisi;
    }

    public int getToplamAsist() { return toplamAsist; }
    public int getToplamGol() { return toplamGol; }
    public String getMevki() { return mevki; }
    public void setMevki(String mevki) { this.mevki = mevki; }
    public String getUlke() { return ulke; }
    public void setUlke(String ulke) { this.ulke = ulke; }
    public double getMaas() { return maas; }
    public void setMaas(double maas) { this.maas = maas; }
    public char getMevkisizKarakter() { return mevkisizKarakter; }
    public void setMevkisizKarakter(char mevkisizKarakter) { this.mevkisizKarakter = mevkisizKarakter; }

    // --- OVERRIDE METOTLAR (TOSTRING) ---
    @Override
    public String toString() {
        return String.format(
                "==========================================================\n" +
                        "| %-20s : %s\n" +
                        "----------------------------------------------------------\n" +
                        "| %-20s : %s %s\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %.0fM €\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %s\n" +
                        "==========================================================",
                "ÜNVAN", "FUTBOLCU",
                "Ad Soyad", getAd(), getSoyad(),
                "Mevki", getMevki(),
                "Maaş", getMaas(),
                "İstatistik", getPerformansDetayi(),
                "Durum", performansDurumuAnalizi()
        );
    }
}