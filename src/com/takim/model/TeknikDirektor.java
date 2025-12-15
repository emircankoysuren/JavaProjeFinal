package com.takim.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;

public class TeknikDirektor extends Calisan implements Serializable {

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

    // GÜNCELLENMİŞ YAPICI METOT (15 PARAMETRELİ)
    public TeknikDirektor(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                          double maas, LocalDate iseBaslamaTarihi, int lisansYili, String taktik, double bonusHedefi,
                          String eskiTakim, double puanOrt, int kupaSayisi,
                          String uyruk, String antrenorlukLisansi, double gorevSuresiYil) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi); // HATA BURADAYDI!
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

    // Getter/Setter metotları (Sadece birkaçı gösterildi, diğerleri mevcut)
    public String getUyruk() { return uyruk; }
    public String getAntrenorlukLisansi() { return antrenorlukLisansi; }
    public double getGorevSuresiYil() { return gorevSuresiYil; }
    public String getEskiTakim() { return eskiTakim; }
    public double getPuanOrt() { return puanOrt; }
    public int getKupaSayisi() { return kupaSayisi; }

    @Override public double maasHesapla() { return getMaas() + (hizmetYiliHesapla() * 1000); }
    @Override public double primHesapla(int performansPuani) { return performansPuani > 80 ? getMaas() * 0.15 : 0; }
    @Override public double yillikMaasArtisiOraniGetir() { return hizmetYiliHesapla() > 5 ? 0.10 : 0.05; }
    @Override public double vergiKesintisiHesapla(Month ay) { return maasHesapla() * 0.20; }
    @Override public double yillikBrutMaasGetir() { return maasHesapla() * 12; }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); }

    // GÜNCELLENMİŞ toString() METODU (ID satırı kaldırıldı)
    @Override
    public String toString() {
        return String.format(
                "==========================================================\n" +
                        "| %-20s : %s\n" +
                        "----------------------------------------------------------\n" +
                        "| %-20s : %s %s\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %d Yıl\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %.2f Yıl\n" +
                        "| %-20s : %s\n" +
                        "| %-20s : %.2f\n" +
                        "| %-20s : %d\n" +
                        "| %-20s : %.2f TL\n" +
                        "==========================================================",
                "ÜNVAN", "TEKNİK DİREKTÖR",
                "Ad Soyad", getAd(), getSoyad(),
                "Uyruk", uyruk,
                "Tecrübe (Hizmet Yılı)", hizmetYiliHesapla(),
                "Lisans", antrenorlukLisansi,
                "Görev Süresi", gorevSuresiYil,
                "Tercih Edilen Taktik", taktik,
                "Puan Ortalaması", puanOrt,
                "Kupa Sayısı", kupaSayisi,
                "Maaş", getMaas()
        );
    }
}