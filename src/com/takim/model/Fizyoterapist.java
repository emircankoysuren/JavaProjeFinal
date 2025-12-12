package com.takim.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;

/**
 * 1.1: Alt Sinif (3/4). Calisan hiyerarsisi.
 * Serileştirme için Serializable uygulandı.
 */
public class Fizyoterapist extends Calisan implements Serializable {

    private static final long serialVersionUID = 1L; // Serileştirme ID'si
    private String sertifikaNo;
    private String uzmanlikAlani;
    private boolean sporMasajYetkisi;
    private short tecrubeYili;

    private int fizyoterapiPuani;
    private int sporBilimiPuani;
    private int uyumlulukPuani;
    private int disiplinPuani;

    private static int checkPuanRange(int puan, String fieldName) {
        if (puan < 1 || puan > 20) {
            System.err.println("HATA: " + fieldName + " puani 1 ile 20 arasinda olmalidir. Deger 1'e ayarlandi.");
            return 1;
        }
        return puan;
    }

    public Fizyoterapist(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                         double maas, LocalDate iseBaslamaTarihi,
                         String sertifikaNo, String uzmanlikAlani, boolean sporMasajYetkisi,
                         int fizyoterapiPuani, int sporBilimiPuani,
                         int uyumlulukPuani, int disiplinPuani) {
        super(ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi);
        this.sertifikaNo = sertifikaNo;
        this.uzmanlikAlani = uzmanlikAlani;
        this.sporMasajYetkisi = sporMasajYetkisi;
        this.tecrubeYili = (short)hizmetYiliHesapla();

        setFizyoterapiPuani(fizyoterapiPuani);
        setSporBilimiPuani(sporBilimiPuani);
        setUyumlulukPuani(uyumlulukPuani);
        setDisiplinPuani(disiplinPuani);
    }

    // GEREKLİ GETTER METOTLARI
    public String getSertifikaNo() { return sertifikaNo; }
    public String getUzmanlikAlani() { return uzmanlikAlani; }
    public boolean isSporMasajYetkisi() { return sporMasajYetkisi; }
    public short getTecrubeYili() { return tecrubeYili; }


    // YENİ PUAN GETTER/SETTER METOTLARI
    public int getFizyoterapiPuani() { return fizyoterapiPuani; }
    public void setFizyoterapiPuani(int fizyoterapiPuani) { this.fizyoterapiPuani = checkPuanRange(fizyoterapiPuani, "Fizyoterapi"); }
    public int getSporBilimiPuani() { return sporBilimiPuani; }
    public void setSporBilimiPuani(int sporBilimiPuani) { this.sporBilimiPuani = checkPuanRange(sporBilimiPuani, "Spor Bilimi"); }
    public int getUyumlulukPuani() { return uyumlulukPuani; }
    public void setUyumlulukPuani(int uyumlulukPuani) { this.uyumlulukPuani = checkPuanRange(uyumlulukPuani, "Uyumluluk"); }
    public int getDisiplinPuani() { return disiplinPuani; }
    public void setDisiplinPuani(int disiplinPuani) { this.disiplinPuani = checkPuanRange(disiplinPuani, "Disiplin"); }


    @Override public double maasHesapla() { return getMaas() + (this.sporMasajYetkisi ? 2000 : 500); }
    @Override public double primHesapla(int performansPuani) { return (double)tecrubeYili * (performansPuani / 100.0); }

    @Override public double yillikMaasArtisiOraniGetir() { return this.uzmanlikAlani.toLowerCase().contains("spor") ? 0.12 : 0.08; }
    @Override public double vergiKesintisiHesapla(Month ay) { return ay == Month.JANUARY ? maasHesapla() * 0.15 : maasHesapla() * 0.20; }
    @Override public double yillikBrutMaasGetir() { return (maasHesapla() * 12) + ((double)getTecrubeYili() * 1000); }
    @Override public void bilgiYazdir() { System.out.println(this.toString()); }

    @Override
    public String toString() {
        return "Fizyoterapist >> " + super.toString() +
                ", Sertifika: " + sertifikaNo +
                ", Fizyo Puanı: " + fizyoterapiPuani +
                ", Uyumluluk: " + uyumlulukPuani;
    }

    // Listeleme için formatlı dize döndüren metot
    public String bilgiGetir() {
        return String.format("%-25s | Uzm. Alan: %-15s | Fizyo Puanı: %2d",
                getAd() + " " + getSoyad(),
                getUzmanlikAlani(),
                getFizyoterapiPuani());
    }
}