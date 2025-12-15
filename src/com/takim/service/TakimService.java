package com.takim.service;

import com.takim.model.*;
import com.takim.exception.GecersizFormaNoException;
import com.takim.util.DosyaIslemleri;
import com.takim.util.Formatlayici;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class TakimService {

    // YENİ ALANLAR: ID Sayacı
    private static int yardimciAntrenorCounter = 0;
    private static int fizyoterapistCounter = 0;

    // MEVCUT ALANLAR
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
        // YENİ ÇAĞRI: Sayaçları yüklenen verilere göre başlat
        initCounters();
        futbolcuKadrosu.forEach(f -> formaNoHaritasi.put(f.getFormaNo(), f));
    }

    // YENİ METOT: initCounters - Kayıtlı en büyük ID'yi bularak sayaçları başlatır
    private void initCounters() {
        // YARDXXX ID'sinin sayısal kısmından en yükseğini bul
        yardimciAntrenorCounter = yardimciAntrenorler.stream()
                .map(YardimciAntrenor::getId)
                .filter(id -> id != null && id.startsWith("YARD"))
                .map(id -> {
                    try {
                        return Integer.parseInt(id.substring(4));
                    } catch (NumberFormatException e) {
                        return -1; // Hatalı formatları atla
                    }
                })
                .max(Integer::compare)
                .orElse(-1) + 1;

        // FİZYXXX ID'sinin sayısal kısmından en yükseğini bul
        fizyoterapistCounter = fizyoterapistler.stream()
                .map(Fizyoterapist::getId)
                .filter(id -> id != null && id.startsWith("FİZY"))
                .map(id -> {
                    try {
                        return Integer.parseInt(id.substring(4));
                    } catch (NumberFormatException e) {
                        return -1; // Hatalı formatları atla
                    }
                })
                .max(Integer::compare)
                .orElse(-1) + 1;

        // Negatif gelirse 0'a sıfırla
        if (yardimciAntrenorCounter < 0) yardimciAntrenorCounter = 0;
        if (fizyoterapistCounter < 0) fizyoterapistCounter = 0;
    }

    // YENİ METOT: Yardımcı Antrenör ID üretimi
    private String generateYardimciAntrenorId() {
        return String.format("YARD%03d", yardimciAntrenorCounter++);
    }

    // YENİ METOT: Fizyoterapist ID üretimi
    private String generateFizyoterapistId() {
        return String.format("FİZY%03d", fizyoterapistCounter++);
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

    // GÜNCELLENDİ: ID ataması yapıldı
    public void yardimciAntrenorEkle(YardimciAntrenor ya) {
        ya.setId(generateYardimciAntrenorId());
        yardimciAntrenorler.add(ya);
        tumVerileriKaydet();
    }

    // GÜNCELLENDİ: ID ataması yapıldı
    public void fizyoterapistEkle(Fizyoterapist fizyo) {
        fizyo.setId(generateFizyoterapistId());
        fizyoterapistler.add(fizyo);
        tumVerileriKaydet();
    }

    public List<Futbolcu> getFutbolcuKadrosu() { return futbolcuKadrosu; }
    public List<TeknikDirektor> getTeknikDirektorler() { return teknikDirektorler; }
    public List<YardimciAntrenor> getYardimciAntrenorler() { return yardimciAntrenorler; }
    public List<Fizyoterapist> getFizyoterapistler() { return fizyoterapistler; }

    // KALDIRILDI: Eski personelSil(String ad, String soyad) metodu kaldırıldı.

    // YENİ SİLME METOTLARI
    // 1. Futbolcu Silme (Forma No ile)
    public boolean futbolcuSil(int formaNo) {
        boolean silindi = futbolcuKadrosu.removeIf(f -> f.getFormaNo() == formaNo);
        if (silindi) {
            formaNoHaritasi.remove(formaNo);
            tumVerileriKaydet();
        }
        return silindi;
    }

    // 2. Teknik Direktör Silme (Tek kişi)
    public boolean teknikDirektorSil() {
        boolean silindi = teknikDirektorler.size() > 0;
        teknikDirektorler.clear();
        if (silindi) {
            tumVerileriKaydet();
        }
        return silindi;
    }

    // 3. Yardımcı Antrenör Silme (YARDXXX ID ile)
    public boolean yardimciAntrenorSil(String id) {
        boolean silindi = yardimciAntrenorler.removeIf(ya -> ya.getId() != null && ya.getId().equalsIgnoreCase(id));
        if (silindi) {
            tumVerileriKaydet();
        }
        return silindi;
    }

    // 4. Fizyoterapist Silme (FİZYXXX ID ile)
    public boolean fizyoterapistSil(String id) {
        boolean silindi = fizyoterapistler.removeIf(f -> f.getId() != null && f.getId().equalsIgnoreCase(id));
        if (silindi) {
            tumVerileriKaydet();
        }
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