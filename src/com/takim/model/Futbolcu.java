package com.takim.model;

import com.takim.exception.GecersizFormaNoException;
import java.time.LocalDate;

/**
 * 4.1: Ikinci kalitim zinciri tamamlandi (Kisi -> Futbolcu)
 * 4.3: Hem Kisi'den kalitim aliyor hem de Raporlanabilir interface'ini uyguluyor
 */
public class Futbolcu extends Kisi implements Raporlanabilir {

    private int formaNo;
    private String mevki;
    private int golSayisi;
    private int asistSayisi;

    // 3.5: Constructor Overloading (1. sinif)
    public Futbolcu(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                    int formaNo, String mevki, int golSayisi, int asistSayisi) throws GecersizFormaNoException {
        super(ad, soyad, dogumTarihi, tcKimlikNo);
        setFormaNo(formaNo); // Setter ile kontrol
        this.mevki = mevki;
        this.golSayisi = golSayisi;
        this.asistSayisi = asistSayisi;
    }

    // 3.5: Constructor Overloading (2. form)
    public Futbolcu(String ad, String soyad, int formaNo, String mevki) {
        // Exception yonetimi olmadigi icin dogrudan set etme
        super(ad, soyad, null, null);
        this.formaNo = formaNo;
        this.mevki = mevki;
        this.golSayisi = 0;
        this.asistSayisi = 0;
    }

    public int getFormaNo() { return formaNo; }
    public void setFormaNo(int formaNo) throws GecersizFormaNoException { // 3.9: Ozel Exception firlatma (1/5)
        if (formaNo < 1 || formaNo > 99) {
            throw new GecersizFormaNoException("Hata: Forma numarasi 1 ile 99 arasinda olmalidir. Girilen: " + formaNo);
        }
        this.formaNo = formaNo;
    }

    public int getGolSayisi() { return golSayisi; }
    public void setGolSayisi(int golSayisi) { // 3.8: Aralık kontrolü (2/5)
        if (golSayisi < 0) {
            System.err.println("Hata: Gol sayisi negatif olamaz. Deger 0'a ayarlandi.");
            this.golSayisi = 0;
        } else {
            this.golSayisi = golSayisi;
        }
    }

    public String getMevki() { return mevki; }
    public int getasistSayisi() { return asistSayisi; } // Mevcut küçük harfli getter
    // ---

    // DÜZELTME: Skor Katkısı Metotları (getter'lar kullanıldı)

    /**
     * Oyuncunun toplam skor katkısını (Gol + Asist) döndürür.
     */
    public int getSkorKatkisi() {
        return getGolSayisi() + getasistSayisi();
    }

    /**
     * Skor Katkısı sıralaması için istenen formatta bilgi döndürür.
     * Format: Forma No | Ad Soyad (Katkı: X, Gol: Y, Asist: Z)
     */
    public String getSkorKatkisiBilgileri() {
        return String.format("%d | %s %s (Katkı: %d, Gol: %d, Asist: %d)",
                getFormaNo(), getAd(), getSoyad(), getSkorKatkisi(), getGolSayisi(), getasistSayisi());
    }

    // ---

    @Override
    public String toString() { // 4.4: Override (3/5)
        return "Futbolcu >> " + super.toString() +
                ", Forma No: " + formaNo +
                ", Mevki: " + mevki;
    }

    // 4.4: Metot Overloading (1/3)
    public void performansGuncelle(int gol, int asist) {
        this.golSayisi += gol;
        this.asistSayisi += asist;
    }

    // 4.4: Metot Overloading (2/3)
    public void performansGuncelle(int gol) {
        this.golSayisi += gol;
    }

    // Raporlanabilir implementasyonu
    @Override public String ozetRaporOlustur() { return this.toString(); }
    @Override public String detayliIstatistikGetir(LocalDate baslangic, LocalDate bitis) { return "Detaylı rapor."; }
    @Override public boolean raporDurumuKontrolEt() { return true; }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); } // Kisi abstract metot implementasyonu

    /**
     * Futbolcunun forma numarası ile performans verilerini görüntüler.
     */
    public String getPerformansBilgileri() {
        // getAd() ve getSoyad() metotları Kisi sınıfından miras alınmıştır.
        return getAd() + " " + getSoyad() +
                " (" + mevki + ") | Forma No: " + formaNo +
                " | Gol: " + golSayisi +
                ", Asist: " + asistSayisi;
    }
}