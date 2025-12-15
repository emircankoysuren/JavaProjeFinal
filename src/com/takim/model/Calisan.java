package com.takim.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 * 4.2: Abstract Sınıf (2/2). Maasli personel icin temel sinif.
 */
public abstract class Calisan extends Kisi implements MaasHesaplanabilir, Raporlanabilir { // Raporlanabilir eklendi

    protected String id; // ID alanı eklendi
    private double maas;
    private LocalDate iseBaslamaTarihi;

    public Calisan(String ad, String soyad, LocalDate dogumTarihi, String tcKimlikNo,
                   double maas, LocalDate iseBaslamaTarihi) {
        super(ad, soyad, dogumTarihi, tcKimlikNo); // Kisi'nin 4 parametreli constructor'ını çağırır
        this.maas = maas;
        this.iseBaslamaTarihi = iseBaslamaTarihi;
    }

    // 4.2: Somut metot (2/2) - Hata veren metot buradadır
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

    // Getter/Setter - Hata veren getMaas metodu buradadır
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

    // ID Getter/Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Raporlanabilir'deki abstract metotlar
    @Override public String ozetRaporOlustur() { return this.toString(); }
    @Override public String detayliIstatistikGetir(LocalDate baslangic, LocalDate bitis) { return "Detaylı rapor."; }
    @Override public boolean raporDurumuKontrolEt() { return true; }

    // Kisi'den gelen abstract metot
    @Override public double genelKatkiHesapla() { return maas / 1000; }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " >> ID: " + (id != null ? id : "N/A") + ", " +
                super.toString() +
                ", Maas: " + maas +
                ", Hizmet Yili: " + hizmetYiliHesapla();
    }
}