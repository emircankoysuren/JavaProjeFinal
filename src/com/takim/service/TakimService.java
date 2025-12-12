package com.takim.service;

import com.takim.model.*;
import com.takim.exception.GecersizFormaNoException;
import com.takim.util.DosyaIslemleri;
import com.takim.util.Formatlayici;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class TakimService {

    private List<Futbolcu> futbolcuKadrosu;
    private List<TeknikDirektor> teknikDirektorler;
    private List<YardimciAntrenor> yardimciAntrenorler;
    private List<Fizyoterapist> fizyoterapistler;

    private Map<Integer, Futbolcu> formaNoHaritasi;
    private Map<LocalDate,MacVerisi> macGecmisi;

    private static final String FUTBOLCU_DOSYA = "futbolcular.txt";
    private static final String TEKNIK_DIREKTOR_DOSYA = "teknikdirektorler.txt";
    private static final String YARDIMCI_ANTRENOR_DOSYA = "yardimciantrenorler.txt";
    private static final String FIZYOTERAPIST_DOSYA = "fizyoterapistler.txt";

    public TakimService() {
        futbolcuKadrosu = new ArrayList<>();
        teknikDirektorler = new ArrayList<>();
        yardimciAntrenorler = new ArrayList<>();
        fizyoterapistler = new ArrayList<>();
        formaNoHaritasi = new HashMap<>();
        macGecmisi = new TreeMap<>();

        tumVerileriYukle();
        futbolcuKadrosu.forEach(f -> formaNoHaritasi.put(f.getFormaNo(), f));
    }

    public void futbolcuEkle(Futbolcu futbolcu) throws GecersizFormaNoException {
        if (futbolcu.getFormaNo() < 1 || futbolcu.getFormaNo() > 99) {
            throw new GecersizFormaNoException("Forma numarası 1-99 arasında olmalıdır.");
        }
        futbolcuKadrosu.add(futbolcu);
        formaNoHaritasi.put(futbolcu.getFormaNo(), futbolcu);
        tumVerileriKaydet();
    }

    public void teknikDirektorEkle(TeknikDirektor td) {
        teknikDirektorler.add(td);
        tumVerileriKaydet();
    }

    public void yardimciAntrenorEkle(YardimciAntrenor ya) {
        yardimciAntrenorler.add(ya);
        tumVerileriKaydet();
    }

    public void fizyoterapistEkle(Fizyoterapist fizyo) {
        fizyoterapistler.add(fizyo);
        tumVerileriKaydet();
    }

    public List<Futbolcu> getFutbolcuKadrosu() { return futbolcuKadrosu; }
    public List<TeknikDirektor> getTeknikDirektorler() { return teknikDirektorler; }
    public List<YardimciAntrenor> getYardimciAntrenorler() { return yardimciAntrenorler; }
    public List<Fizyoterapist> getFizyoterapistler() { return fizyoterapistler; }

    public boolean personelSil(String ad, String soyad) {
        boolean silindi = false;
        silindi |= futbolcuKadrosu.removeIf(f -> f.getAd().equalsIgnoreCase(ad) && f.getSoyad().equalsIgnoreCase(soyad));
        silindi |= teknikDirektorler.removeIf(t -> t.getAd().equalsIgnoreCase(ad) && t.getSoyad().equalsIgnoreCase(soyad));
        silindi |= yardimciAntrenorler.removeIf(y -> y.getAd().equalsIgnoreCase(ad) && y.getSoyad().equalsIgnoreCase(soyad));
        silindi |= fizyoterapistler.removeIf(f -> f.getAd().equalsIgnoreCase(ad) && f.getSoyad().equalsIgnoreCase(soyad));

        if(silindi) tumVerileriKaydet();
        return silindi;
    }

    public Futbolcu futbolcuyuBul(int formaNo) {
        return formaNoHaritasi.get(formaNo);
    }

    public boolean performansGuncelle(int formaNo, int gol, int asist) {
        Futbolcu f = futbolcuyuBul(formaNo);
        if (f != null) {
            f.performansGuncelle(gol, asist);
            tumVerileriKaydet();
            return true;
        }
        return false;
    }

    // DÜZELTİLEN METOT: Jenerik tip hatası giderildi.
    public String listeYazdir(List<? extends Kisi> liste) {
        if (liste == null || liste.isEmpty()) {
            return "Liste boş.";
        }
        StringBuilder sb = new StringBuilder();
        for (Kisi kisi : liste) {
            if (kisi != null) {
                sb.append(kisi.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    public void skorKatkisiSiralamasiYap() {
        Collections.sort(futbolcuKadrosu, Comparator.comparingInt(Futbolcu::skorKatkisiHesapla).reversed());
    }

    public String skorKatkisiRaporuGetir() {
        skorKatkisiSiralamasiYap();
        StringBuilder sb = new StringBuilder();
        sb.append(Formatlayici.renklendir("--- SKOR KATKISI SIRALAMASI ---\n", Formatlayici.YESIL));
        futbolcuKadrosu.forEach(f -> sb.append(f.getSkorKatkisiDetay()).append("\n"));
        return sb.toString();
    }

    public void tumVerileriKaydet() {
        try {
            DosyaIslemleri.dosyayaYaz((List)futbolcuKadrosu, FUTBOLCU_DOSYA);
            DosyaIslemleri.dosyayaYaz((List)teknikDirektorler, TEKNIK_DIREKTOR_DOSYA);
            DosyaIslemleri.dosyayaYaz((List)yardimciAntrenorler, YARDIMCI_ANTRENOR_DOSYA);
            DosyaIslemleri.dosyayaYaz((List)fizyoterapistler, FIZYOTERAPIST_DOSYA);
        } catch (IOException e) {
            System.err.println("Kayit hatasi: " + e.getMessage());
        }
    }

    public void tumVerileriYukle() {
        try {
            futbolcuKadrosu = DosyaIslemleri.dosyadanOku(FUTBOLCU_DOSYA, Futbolcu.class);
            teknikDirektorler = DosyaIslemleri.dosyadanOku(TEKNIK_DIREKTOR_DOSYA, TeknikDirektor.class);
            yardimciAntrenorler = DosyaIslemleri.dosyadanOku(YARDIMCI_ANTRENOR_DOSYA, YardimciAntrenor.class);
            fizyoterapistler = DosyaIslemleri.dosyadanOku(FIZYOTERAPIST_DOSYA, Fizyoterapist.class);
        } catch (Exception e) {
            System.err.println("Veri yukleme hatasi (ilk calisma olabilir): " + e.getMessage());
        }
    }
}