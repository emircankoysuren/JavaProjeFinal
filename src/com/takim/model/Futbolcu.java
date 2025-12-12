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

    public Futbolcu(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                    int formaNo, String mevki, int golSayisi, int asistSayisi) throws GecersizFormaNoException {
        super(ad, soyad, dogumTarihi, tcKimlikNo);
        setFormaNo(formaNo);
        this.mevki = mevki;
        this.golSayisi = golSayisi;
        this.asistSayisi = asistSayisi;
    }

    public Futbolcu(String ad, String soyad, int formaNo, String mevki) {
        super(ad, soyad, null, null);
        this.formaNo = formaNo;
        this.mevki = mevki;
        this.golSayisi = 0;
        this.asistSayisi = 0;
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


    public int skorKatkisiHesapla() {
        return golSayisi + asistSayisi;
    }

    public String getSkorKatkisiDetay() {
        return "Forma No: " + formaNo +
                " | Adı Soyadı: " + getAd() + " " + getSoyad() +
                " | Skor Katkısı: " + skorKatkisiHesapla() + " (Gol: " + golSayisi + ", Asist: " + asistSayisi + ")";
    }


    @Override
    public String toString() {
        return "Futbolcu >> " + super.toString() +
                ", Forma No: " + formaNo +
                ", Mevki: " + mevki;
    }

    // toString'i baz alarak, listeleme için temiz bilgi döndüren metot.
    public String bilgiGetir() {
        return String.format("%-10s | %-15s | Gol: %2d | Asist: %2d",
                getFormaNo(),
                getAd() + " " + getSoyad(),
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
                " | Gol: " + golSayisi +
                ", Asist: " + asistSayisi;
    }
}