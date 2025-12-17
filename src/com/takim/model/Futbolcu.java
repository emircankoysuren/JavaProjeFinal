package com.takim.model;

import com.takim.exception.GecersizFormaNoException;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 4.1: Kalıtım zinciri (Kisi -> Futbolcu) [cite: 14]
 * 4.3: Interface uygulaması (Raporlanabilir) [cite: 52]
 */
public class Futbolcu extends Kisi implements Serializable, Raporlanabilir {

    private static final long serialVersionUID = 1L;
    private int formaNo;
    private String mevki;
    private int golSayisi;
    private int asistSayisi;
    private String ulke;
    private char mevkisizKarakter = 'A'; // Gereksinim 2: Primitive 'char' kullanımı

    // CONSTRUCTOR 1 (9 PARAMETRELİ) [cite: 35]
    public Futbolcu(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                    int formaNo, String mevki, int golSayisi, int asistSayisi, String ulke) throws GecersizFormaNoException {
        super(ad, soyad, dogumTarihi, tcKimlikNo);
        setFormaNo(formaNo);
        this.mevki = mevki;
        setGolSayisi(golSayisi);
        setAsistSayisi(asistSayisi);
        this.ulke = ulke;
    }

    // CONSTRUCTOR 2 (OVERLOADING) [cite: 35]
    public Futbolcu(String ad, String soyad, int formaNo, String mevki) {
        super(ad, soyad, null, null);
        this.formaNo = formaNo;
        this.mevki = mevki;
        this.golSayisi = 0;
        this.asistSayisi = 0;
        this.ulke = "Bilinmiyor";
    }

    public int getFormaNo() { return formaNo; }

    // Gereksinim 3.39: Özel Exception fırlatma [cite: 39]
    public void setFormaNo(int formaNo) throws GecersizFormaNoException {
        if (formaNo < 1 || formaNo > 99) {
            throw new GecersizFormaNoException("Hata: Forma numarasi 1-99 arasinda olmalidir: " + formaNo);
        }
        this.formaNo = formaNo;
    }

    public int getGolSayisi() { return golSayisi; }

    // Gereksinim 3.39: Aralık kontrolü ve Exception fırlatma [cite: 38, 39]
    public void setGolSayisi(int golSayisi) {
        if (golSayisi < 0) {
            throw new IllegalArgumentException("Hata: Gol sayısı negatif olamaz!");
        }
        this.golSayisi = golSayisi;
    }

    public int getAsistSayisi() { return asistSayisi; }

    // Gereksinim 3.39: Exception fırlatma [cite: 39]
    public void setAsistSayisi(int asistSayisi) {
        if (asistSayisi < 0) {
            throw new IllegalArgumentException("Hata: Asist sayısı negatif olamaz!");
        }
        this.asistSayisi = asistSayisi;
    }

    public String getMevki() { return mevki; }
    public void setMevki(String mevki) { this.mevki = mevki; }
    public String getUlke() { return ulke; }
    public void setUlke(String ulke) { this.ulke = ulke; }

    public int skorKatkisiHesapla() {
        return golSayisi + asistSayisi;
    }

    @Override
    public double genelKatkiHesapla() {
        return (double)skorKatkisiHesapla();
    }

    public String getSkorKatkisiDetay() {
        return "Forma No: " + formaNo +
                " | Adı Soyadı: " + getAd() + " " + getSoyad() +
                " | Ülke: " + ulke +
                " | Skor Katkısı: " + skorKatkisiHesapla() + " (Gol: " + golSayisi + ", Asist: " + asistSayisi + ")";
    }

    // Gereksinim 4.54: Metot Override [cite: 54]
    @Override
    public String toString() {
        return String.format(
                "==========================================================\n" +
                        "| %-20s : %s\n" +
                        "----------------------------------------------------------\n" +
                        "| %-20s : %s %s\n" +
                        "| %-20s : %d\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %d\n" +
                        "| %-20s : %d\n" +
                        "| %-20s : %d\n" +
                        "==========================================================",
                "ÜNVAN", "FUTBOLCU",
                "Ad Soyad", getAd(), getSoyad(),
                "Forma No", formaNo,
                "Mevki", mevki,
                "Uyruk", ulke,
                "Gol Sayısı", golSayisi,
                "Asist Sayısı", asistSayisi,
                "Toplam Katkı", skorKatkisiHesapla()
        );
    }

    // Gereksinim 4.55: Metot Overloading
    public void performansGuncelle(int gol, int asist) {
        setGolSayisi(this.golSayisi + gol);
        setAsistSayisi(this.asistSayisi + asist);
    }

    public void performansGuncelle(int gol) {
        setGolSayisi(this.golSayisi + gol);
    }

    // Raporlanabilir Interface Metotları [cite: 51]
    @Override public String ozetRaporOlustur() { return this.toString(); }
    @Override public String detayliIstatistikGetir(LocalDate baslangic, LocalDate bitis) { return "Performans Raporu."; }
    @Override public boolean raporDurumuKontrolEt() { return true; }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); }
}