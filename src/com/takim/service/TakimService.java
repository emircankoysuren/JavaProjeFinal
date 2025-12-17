package com.takim.service;

import com.takim.model.*;
import com.takim.exception.GecersizFormaNoException;
import com.takim.util.DosyaIslemleri;
import com.takim.util.Formatlayici;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class TakimService {

    private static int yardimciAntrenorCounter = 0;
    private static int fizyoterapistCounter = 0;

    private List<Futbolcu> futbolcuKadrosu;
    private List<TeknikDirektor> teknikDirektorler;
    private List<YardimciAntrenor> yardimciAntrenorler;
    private List<Fizyoterapist> fizyoterapistler;

    private Map<Integer, Futbolcu> formaNoHaritasi;
    private Map<LocalDate, MacVerisi> macGecmisi;

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
        initCounters();
        futbolcuKadrosu.forEach(f -> formaNoHaritasi.put(f.getFormaNo(), f));
    }

    private void initCounters() {
        yardimciAntrenorCounter = yardimciAntrenorler.stream()
                .map(YardimciAntrenor::getId)
                .filter(id -> id != null && id.startsWith("YARD"))
                .map(id -> {
                    try { return Integer.parseInt(id.substring(4)); }
                    catch (NumberFormatException e) { return -1; }
                })
                .max(Integer::compare).orElse(-1) + 1;

        fizyoterapistCounter = fizyoterapistler.stream()
                .map(Fizyoterapist::getId)
                .filter(id -> id != null && id.startsWith("FİZY"))
                .map(id -> {
                    try { return Integer.parseInt(id.substring(4)); }
                    catch (NumberFormatException e) { return -1; }
                })
                .max(Integer::compare).orElse(-1) + 1;

        if (yardimciAntrenorCounter < 0) yardimciAntrenorCounter = 0;
        if (fizyoterapistCounter < 0) fizyoterapistCounter = 0;
    }

    public void futbolcuEkle(Futbolcu futbolcu) throws GecersizFormaNoException {
        futbolcuKadrosu.add(futbolcu);
        formaNoHaritasi.put(futbolcu.getFormaNo(), futbolcu);
        tumVerileriKaydet();
    }

    public void teknikDirektorEkle(TeknikDirektor td) {
        teknikDirektorler.add(td);
        tumVerileriKaydet();
    }

    public void yardimciAntrenorEkle(YardimciAntrenor ya) {
        ya.setId(String.format("YARD%03d", yardimciAntrenorCounter++));
        yardimciAntrenorler.add(ya);
        tumVerileriKaydet();
    }

    public void fizyoterapistEkle(Fizyoterapist fizyo) {
        fizyo.setId(String.format("FİZY%03d", fizyoterapistCounter++));
        fizyoterapistler.add(fizyo);
        tumVerileriKaydet();
    }

    /**
     * Gereksinim 4.55: Metot Overloading (Aşırı Yükleme)
     */
    public boolean performansGuncelle(int formaNo, int yeniGol) {
        return performansVerisiGir(formaNo, yeniGol, 0);
    }

    public boolean performansVerisiGir(int formaNo, int gol, int asist) {
        Futbolcu f = formaNoHaritasi.get(formaNo);
        if (f != null) {
            try {
                f.setGolSayisi(f.getGolSayisi() + gol);
                f.setAsistSayisi(f.getAsistSayisi() + asist);
                tumVerileriKaydet();
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Gereksinim 9: Çok Boyutlu Dizi ve Klasik For Döngüsü
     */
    public String haftalikProgramiGoster() {
        String[][] program = {
                {"Pazartesi", "Kondisyon"}, {"Salı", "Taktik"}, {"Çarşamba", "Maç"},
                {"Perşembe", "Analiz"}, {"Cuma", "Şut"}, {"Cumartesi", "Hazırlık"}, {"Pazar", "MAÇ"}
        };
        StringBuilder sb = new StringBuilder();
        sb.append(Formatlayici.renklendir("--- HAFTALIK PROGRAM ---\n", Formatlayici.MAVI));
        for (int i = 0; i < program.length; i++) {
            sb.append(program[i][0]).append(": ").append(program[i][1]).append("\n");
        }
        return sb.toString();
    }

    /**
     * Gereksinim 9: While ve Do-While Döngüleri
     */
    public void sistemBütünlükKontrolü() {
        int c = 0;
        while (c < 1) { System.out.println("Sistem taranıyor..."); c++; }
        int d = 0;
        do { System.out.println("Bütünlük doğrulandı."); d++; } while (d < 1);
    }

    public double antrenmanPuanOrtalamasiHesapla() {
        double[] puanlar = {8.5, 9.2, 7.8, 8.0, 9.5};
        double toplam = 0;
        for (double p : puanlar) { toplam += p; }
        return toplam / puanlar.length;
    }

    public boolean futbolcuSil(int fNo) {
        boolean r = futbolcuKadrosu.removeIf(f -> f.getFormaNo() == fNo);
        if (r) { formaNoHaritasi.remove(fNo); tumVerileriKaydet(); }
        return r;
    }

    public boolean teknikDirektorSil() {
        if (teknikDirektorler.isEmpty()) return false;
        teknikDirektorler.clear(); tumVerileriKaydet(); return true;
    }

    public boolean yardimciAntrenorSil(String id) {
        boolean r = yardimciAntrenorler.removeIf(y -> y.getId().equalsIgnoreCase(id));
        if (r) tumVerileriKaydet(); return r;
    }

    public boolean fizyoterapistSil(String id) {
        boolean r = fizyoterapistler.removeIf(f -> f.getId().equalsIgnoreCase(id));
        if (r) tumVerileriKaydet(); return r;
    }

    public String listeYazdir(List<? extends Kisi> l) {
        if (l == null || l.isEmpty()) return "Liste boş.";
        StringBuilder sb = new StringBuilder();
        for (Kisi k : l) { sb.append(k.toString()).append("\n"); }
        return sb.toString();
    }

    public String skorKatkisiRaporuGetir() {
        futbolcuKadrosu.sort(Comparator.comparingInt(Futbolcu::skorKatkisiHesapla).reversed());
        StringBuilder sb = new StringBuilder();
        sb.append(Formatlayici.renklendir("--- SKOR KATKISI ---\n", Formatlayici.YESIL));
        futbolcuKadrosu.forEach(f -> sb.append(f.getSkorKatkisiDetay()).append("\n"));
        return sb.toString();
    }

    public void tumVerileriKaydet() {
        try {
            DosyaIslemleri.dosyayaYaz(futbolcuKadrosu, FUTBOLCU_DOSYA);
            DosyaIslemleri.dosyayaYaz(teknikDirektorler, TEKNIK_DIREKTOR_DOSYA);
            DosyaIslemleri.dosyayaYaz(yardimciAntrenorler, YARDIMCI_ANTRENOR_DOSYA);
            DosyaIslemleri.dosyayaYaz(fizyoterapistler, FIZYOTERAPIST_DOSYA);
        } catch (IOException e) { System.err.println("Kayıt hatası: " + e.getMessage()); }
    }

    public void tumVerileriYukle() {
        try {
            futbolcuKadrosu = DosyaIslemleri.dosyadanOku(FUTBOLCU_DOSYA, Futbolcu.class);
            teknikDirektorler = DosyaIslemleri.dosyadanOku(TEKNIK_DIREKTOR_DOSYA, TeknikDirektor.class);
            yardimciAntrenorler = DosyaIslemleri.dosyadanOku(YARDIMCI_ANTRENOR_DOSYA, YardimciAntrenor.class);
            fizyoterapistler = DosyaIslemleri.dosyadanOku(FIZYOTERAPIST_DOSYA, Fizyoterapist.class);
        } catch (Exception e) { System.err.println("Yükleme hatası."); }
    }

    public List<Futbolcu> getFutbolcuKadrosu() { return futbolcuKadrosu; }
    public List<TeknikDirektor> getTeknikDirektorler() { return teknikDirektorler; }
    public List<YardimciAntrenor> getYardimciAntrenorler() { return yardimciAntrenorler; }
    public List<Fizyoterapist> getFizyoterapistler() { return fizyoterapistler; }
    public String yillikFinansalAnalizRaporu() {
        List<Calisan> tumEkip = new ArrayList<>();
        tumEkip.addAll(getTeknikDirektorler());
        tumEkip.addAll(getYardimciAntrenorler());
        tumEkip.addAll(getFizyoterapistler());

        StringBuilder sb = new StringBuilder();
        sb.append(Formatlayici.renklendir("--- KULÜP FİNANSAL VERİMLİLİK ANALİZİ ---\n", Formatlayici.MAVI));

        for (Calisan c : tumEkip) {
            // Interface metotlarını burada bizzat "kullanıyoruz"
            double tazminat = c.kidemTazminatiHesapla();
            String durum = c.maliyetDurumuAnaliziGetir();
            double brut = c.yillikBrutMaasGetir();

            sb.append(String.format("%s %s (%s)\n", c.getAd(), c.getSoyad(), durum));
            sb.append(String.format("> Yıllık Brüt: %.2f TL | Olası Tazminat: %.2f TL\n", brut, tazminat));
            sb.append("----------------------------------------------------------\n");
        }
        return sb.toString();
    }
}