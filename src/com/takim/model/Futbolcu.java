package com.takim.model;

import com.takim.exception.GecersizFormaNoException;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 4.1: Ikinci kalitim zinciri tamamlandi (Kisi -> Futbolcu)
 * Serileştirme için Serializable uygulandı.
 */
public class Futbolcu extends Kisi implements Serializable, Raporlanabilir {

    private static final long serialVersionUID = 1L; // Serileştirme ID'si
    private int formaNo;
    private String mevki;
    private int golSayisi;
    private int asistSayisi;
    private String ulke; // Futbolcunun ülkesi

    // CONSTRUCTOR 1 (9 PARAMETRELİ)
    public Futbolcu(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                    int formaNo, String mevki, int golSayisi, int asistSayisi, String ulke) throws GecersizFormaNoException {
        super(ad, soyad, dogumTarihi, tcKimlikNo);
        setFormaNo(formaNo);
        this.mevki = mevki;
        this.golSayisi = golSayisi;
        this.asistSayisi = asistSayisi;
        this.ulke = ulke;
    }

    // CONSTRUCTOR 2 (Eski 4 parametreli)
    public Futbolcu(String ad, String soyad, int formaNo, String mevki) {
        super(ad, soyad, null, null);
        this.formaNo = formaNo;
        this.mevki = mevki;
        this.golSayisi = 0;
        this.asistSayisi = 0;
        this.ulke = "Bilinmiyor";
    }

    public int getFormaNo() { return formaNo; }
    public void setFormaNo(int formaNo) throws GecersizFormaNoException {
        if (formaNo < 1 || formaNo > 99) {
            throw new GecersizFormaNoException("Hata: Forma numarasi 1 ile 99 arasinda olmalidir. Girilen: " + formaNo);
        }
        this.formaNo = formaNo;
    }

    public int getGolSayisi() { return golSayisi; }
    public void setGolSayisi(int golSayisi) {
        if (golSayisi < 0) {
            System.err.println("Hata: Gol sayisi negatif olamaz. Deger 0'a ayarlandi.");
            this.golSayisi = 0;
        } else {
            this.golSayisi = golSayisi;
        }
    }
    public int getAsistSayisi() { return asistSayisi; }
    public void setAsistSayisi(int asistSayisi) {
        if (asistSayisi < 0) {
            System.err.println("Hata: Asist sayisi negatif olamaz. Deger 0'a ayarlandi.");
            this.asistSayisi = 0;
        } else {
            this.asistSayisi = asistSayisi;
        }
    }
    public String getMevki() { return mevki; }
    public void setMevki(String mevki) { this.mevki = mevki; }

    public String getUlke() { return ulke; }
    public void setUlke(String ulke) { this.ulke = ulke; }


    public int skorKatkisiHesapla() {
        return golSayisi + asistSayisi;
    }

    // YENİ EKLENEN METOT: Kisi'den gelen abstract metot implementasyonu
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

    public String bilgiGetir() {
        return String.format("%-10s | %-15s | %-10s | Gol: %2d | Asist: %2d",
                getFormaNo(),
                getAd() + " " + getSoyad(),
                getUlke(),
                getGolSayisi(),
                getAsistSayisi());
    }

    public void performansGuncelle(int gol, int asist) {
        this.golSayisi += gol;
        this.asistSayisi += asist;
    }

    public void performansGuncelle(int gol) {
        this.golSayisi += gol;
    }

    @Override public String ozetRaporOlustur() { return this.toString(); }
    @Override public String detayliIstatistikGetir(LocalDate baslangic, LocalDate bitis) { return "Detaylı rapor."; }
    @Override public boolean raporDurumuKontrolEt() { return true; }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); }

    /**
     * Futbolcunun forma numarası ile performans verilerini görüntüler.
     */
    public String getPerformansBilgileri() {
        return getAd() + " " + getSoyad() +
                " (" + mevki + ") | Forma No: " + formaNo +
                " | Ülke: " + ulke +
                " | Gol: " + golSayisi +
                ", Asist: " + asistSayisi;
    }
}