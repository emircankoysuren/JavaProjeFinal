package com.takim.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 * 4.2: Abstract Sınıf (2/2). Maasli personel icin temel sinif.
 */
public abstract class Calisan extends Kisi implements MaasHesaplanabilir {

    private double maas;
    private LocalDate iseBaslamaTarihi;

    public Calisan(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                   double maas, LocalDate iseBaslamaTarihi) {
        super(ad, soyad, dogumTarihi, tcKimlikNo);
        this.maas = maas;
        this.iseBaslamaTarihi = iseBaslamaTarihi;
    }

    // 4.2: Somut metot (2/2)
    public int hizmetYiliHesapla() {
        if (this.iseBaslamaTarihi == null) return 0;
        return (int) ChronoUnit.YEARS.between(this.iseBaslamaTarihi, LocalDate.now());
    }

    public int yillikIzinHakki() { return hizmetYiliHesapla() < 5 ? 14 : 20; }

    // 4.2: Abstract metotlar
    public abstract double maasHesapla();
    public abstract double primHesapla(int performansPuani);

    // MaasHesaplanabilir arayuzunden gelen zorunlu abstract metotlar
    @Override public abstract double yillikMaasArtisiOraniGetir();
    @Override public abstract double vergiKesintisiHesapla(Month ay);
    @Override public abstract double yillikBrutMaasGetir();

    // Getter/Setter
    public double getMaas() { return maas; }
    public void setMaas(double maas) { // 3.8: Aralık kontrolü
        if (maas < 0) {
            System.err.println("Hata: Maas negatif olamaz. Deger 0'a ayarlandi.");
            this.maas = 0;
        } else {
            this.maas = maas;
        }
    }
    public LocalDate getIseBaslamaTarihi() { return iseBaslamaTarihi; }
    public void setIseBaslamaTarihi(LocalDate iseBaslamaTarihi) { this.iseBaslamaTarihi = iseBaslamaTarihi; }

    @Override
    public String toString() { // 4.4: Override (2/5)
        // super.toString() ile kademeli bilgi aktarimi
        return this.getClass().getSimpleName() + " >> " +
                super.toString() +
                ", Maas: " + maas +
                ", Hizmet Yili: " + hizmetYiliHesapla();
    }
}