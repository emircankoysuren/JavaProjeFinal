package com.takim.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;

public class TeknikDirektor extends Calisan implements Serializable, Performans {

    private static final long serialVersionUID = 1L;

    private int lisansYili;
    private String taktik;
    private double bonusHedefi;
    private String eskiTakim;
    private double puanOrt;
    private int kupaSayisi;
    private String uyruk;
    private String antrenorlukLisansi;
    private double gorevSuresiYil;

    public TeknikDirektor(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                          double maas, LocalDate iseBaslamaTarihi, int lisansYili, String taktik, double bonusHedefi,
                          String eskiTakim, double puanOrt, int kupaSayisi,
                          String uyruk, String antrenorlukLisansi, double gorevSuresiYil) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi);
        this.lisansYili = lisansYili;
        this.taktik = taktik;
        this.bonusHedefi = bonusHedefi;
        this.eskiTakim = eskiTakim;
        this.puanOrt = puanOrt;
        this.kupaSayisi = kupaSayisi;
        this.uyruk = uyruk;
        this.antrenorlukLisansi = antrenorlukLisansi;
        this.gorevSuresiYil = gorevSuresiYil;
    }

    // --- PERFORMANS INTERFACE METOTLARI ---

    @Override
    public double performansPuaniniHesapla() {
        // Puan Ortalaması * 20 + Kupa Sayısı * 50
        return (getPuanOrt() * 20.0) + (getKupaSayisi() * 50.0);
    }

    @Override
    public void performansGuncelle(double... veriler) {
        // [0]: Yeni Puan Ortalaması
        if (veriler.length > 0) this.puanOrt = veriler[0];
    }

    @Override
    public String performansDurumuAnalizi() {
        if (getPuanOrt() > 2.3) return "ŞAMPİYONLUK ADAYI";
        if (getPuanOrt() > 1.8) return "AVRUPA HEDEFİ";
        if (getPuanOrt() > 1.3) return "ORTA SIRA";
        return "RİSKLİ DURUM";
    }

    @Override
    public String getPerformansDetayi() {
        return String.format("%.2f Puan Ort. | %d Kupa", puanOrt, kupaSayisi);
    }

    // --- MEVCUT GETTER/SETTER ---

    public String getTaktik() { return taktik; }
    public String getUyruk() { return uyruk; }
    public String getAntrenorlukLisansi() { return antrenorlukLisansi; }
    public double getGorevSuresiYil() { return gorevSuresiYil; }
    public String getEskiTakim() { return eskiTakim; }
    public double getPuanOrt() { return puanOrt; }
    public int getKupaSayisi() { return kupaSayisi; }

    @Override
    public double maasHesapla() {
        // Senin manuel girdiğin temel maaşı döndürür
        return getMaas();
    }

    @Override
    public double primHesapla(int gol, int asist) {
        // Senin isteğin üzerine: Teknik direktör için prim hesaplamıyoruz
        // Bu yüzden her zaman 0 döner
        return 0.0;
    }

    @Override
    public double toplamMaliyetHesapla(int gol, int asist) {
        // Kulüp Gideri = Sadece temel maaş (prim 0 olduğu için)
        // Aritmetik işlem gereksinimi için: maas + prim
        return maasHesapla() + primHesapla(gol, asist);
    }



    @Override public void bilgiYazdir() { System.out.println(this.toString()); }

    @Override
    public String toString() {
        return String.format(
                "==========================================================\n" +
                        "| %-20s : %s\n" +
                        "----------------------------------------------------------\n" +
                        "| %-20s : %s %s\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %s\n" + // Performans Detayı
                        "| %-20s : %s\n" + // Analiz
                        "| %-20s : %s\n" + // Maaş
                        "==========================================================",
                "ÜNVAN", "TEKNİK DİREKTÖR",
                "Ad Soyad", getAd(), getSoyad(),
                "Uyruk", uyruk,
                "Taktik", (taktik != null ? taktik : "Yok"),
                "İstatistik", getPerformansDetayi(),
                "Durum", performansDurumuAnalizi(),
                "Maaş", String.format(Locale.GERMANY, "%,.0f €", getMaas())
        );
    }
}