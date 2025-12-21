package com.takim.service;

import com.takim.model.*;
import com.takim.exception.GecersizFormaNoException;
import com.takim.exception.KapasiteDolduException;
import com.takim.util.DosyaIslemleri;
import com.takim.util.Formatlayici;

import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

/**
 * OPTIMIZE EDILMIS TakimService Sinifi.
 */
public class TakimService {

    private static int yardimciAntrenorCounter = 0;
    private static int fizyoterapistCounter = 0;

    private List<Futbolcu> futbolcuKadrosu;
    private List<TeknikDirektor> teknikDirektorler;
    private List<YardimciAntrenor> yardimciAntrenorler;
    private List<Fizyoterapist> fizyoterapistler;

    private final Map<Integer, Futbolcu> formaNoHaritasi = new HashMap<>();
    private final Map<LocalDate, MacVerisi> macGecmisi = new TreeMap<>();

    private final String[] gunler = {"Pazartesi", "Sali", "Carsamba", "Persembe", "Cuma", "Cumartesi", "Pazar"};
    private String[] haftalikAktiviteler = {"Kondisyon", "Taktik", "Mac", "Analiz", "Sut", "Hazirlik", "Dinlenme"};

    private static final String FUTBOLCU_DOSYA = "futbolcular.txt";
    private static final String TEKNIK_DIREKTOR_DOSYA = "teknikdirektorler.txt";
    private static final String YARDIMCI_ANTRENOR_DOSYA = "yardimciantrenorler.txt";
    private static final String FIZYOTERAPIST_DOSYA = "fizyoterapistler.txt";
    private static final String FIKSTUR_DOSYA = "fikstur.dat";

    private final KadroluListe<Futbolcu> genericKadro = new KadroluListe<>(24);

    public TakimService() {
        tumVerileriYukle();
        initCounters();
        futbolcuKadrosu.forEach(f -> formaNoHaritasi.put(f.getFormaNo(), f));
    }
    // Personel ID'lerinin cakismamasi icin mevcut kayitlar uzerinden sayac verilerini gunceller.
    private void initCounters() {
        yardimciAntrenorCounter = yardimciAntrenorler.stream()
                .map(YardimciAntrenor::getId)
                .filter(id -> id != null && id.startsWith("YARD"))
                .mapToInt(id -> Integer.parseInt(id.substring(4)))
                .max().orElse(-1) + 1;

        fizyoterapistCounter = fizyoterapistler.stream()
                .map(Fizyoterapist::getId)
                .filter(id -> id != null && id.startsWith("FİZY"))
                .mapToInt(id -> Integer.parseInt(id.substring(4)))
                .max().orElse(-1) + 1;

        if (yardimciAntrenorCounter < 0) yardimciAntrenorCounter = 0;
        if (fizyoterapistCounter < 0) fizyoterapistCounter = 0;
    }

    public void futbolcuEkle(Futbolcu futbolcu) throws GecersizFormaNoException, KapasiteDolduException {
        genericKadro.ekle(futbolcu);
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

    public boolean performansVerisiGir(int formaNo, int gol, int asist) {
        Futbolcu f = formaNoHaritasi.get(formaNo);
        if (f != null) {
            f.performansGuncelle((double) gol, (double) asist);
            tumVerileriKaydet();
            return true;
        }
        return false;
    }

    public void haftalikProgramGuncelle(String[] yeniAktiviteler) {
        if (yeniAktiviteler.length == 7) this.haftalikAktiviteler = yeniAktiviteler;
    }

    public String haftalikProgramiGoster() {
        StringBuilder sb = new StringBuilder(Formatlayici.renklendir("--- GUNCEL HAFTALIK PROGRAM ---\n", Formatlayici.MAVI));
        for (int i = 0; i < gunler.length; i++) {
            sb.append(String.format("%-10s : %s\n", gunler[i], haftalikAktiviteler[i]));
        }
        return sb.toString();
    }

    public boolean futbolcuSil(int fNo) {
        boolean silindi = futbolcuKadrosu.removeIf(f -> f.getFormaNo() == fNo);
        if (silindi) { formaNoHaritasi.remove(fNo); tumVerileriKaydet(); }
        return silindi;
    }

    public boolean teknikDirektorSil() {
        if (teknikDirektorler.isEmpty()) return false;
        teknikDirektorler.clear();
        tumVerileriKaydet();
        return true;
    }

    public boolean yardimciAntrenorSil(String id) {
        boolean silindi = yardimciAntrenorler.removeIf(y -> y.getId().equalsIgnoreCase(id));
        if (silindi) tumVerileriKaydet();
        return silindi;
    }

    public boolean fizyoterapistSil(String id) {
        boolean silindi = fizyoterapistler.removeIf(f -> f.getId().equalsIgnoreCase(id));
        if (silindi) tumVerileriKaydet();
        return silindi;
    }

    public void tumVerileriKaydet() {
        try {
            DosyaIslemleri.dosyayaYaz(futbolcuKadrosu, FUTBOLCU_DOSYA);
            DosyaIslemleri.dosyayaYaz(teknikDirektorler, TEKNIK_DIREKTOR_DOSYA);
            DosyaIslemleri.dosyayaYaz(yardimciAntrenorler, YARDIMCI_ANTRENOR_DOSYA);
            DosyaIslemleri.dosyayaYaz(fizyoterapistler, FIZYOTERAPIST_DOSYA);
            DosyaIslemleri.dosyayaYaz(new ArrayList<>(macGecmisi.values()), FIKSTUR_DOSYA);
        } catch (IOException e) { System.err.println("Kayit hatasi: " + e.getMessage()); }
    }

    public void tumVerileriYukle() {
        try {
            futbolcuKadrosu = DosyaIslemleri.dosyadanOku(FUTBOLCU_DOSYA, Futbolcu.class);
            teknikDirektorler = DosyaIslemleri.dosyadanOku(TEKNIK_DIREKTOR_DOSYA, TeknikDirektor.class);
            yardimciAntrenorler = DosyaIslemleri.dosyadanOku(YARDIMCI_ANTRENOR_DOSYA, YardimciAntrenor.class);
            fizyoterapistler = DosyaIslemleri.dosyadanOku(FIZYOTERAPIST_DOSYA, Fizyoterapist.class);
            List<MacVerisi> yuklenenMaclar = DosyaIslemleri.dosyadanOku(FIKSTUR_DOSYA, MacVerisi.class);
            if (yuklenenMaclar != null) yuklenenMaclar.forEach(m -> macGecmisi.put(m.getMacTarihi(), m));
        } catch (Exception e) { System.err.println("Yukleme hatasi veya dosya bos."); }
    }

    public String detayliFinansalAnalizRaporu() {
        double futbolcuMaasToplam = futbolcuKadrosu.stream().mapToDouble(Futbolcu::maasHesapla).sum();
        double futbolcuPrimToplam = futbolcuKadrosu.stream().mapToDouble(f -> f.primHesapla(0, 0)).sum();
        double tdMaasToplam = teknikDirektorler.stream().mapToDouble(TeknikDirektor::maasHesapla).sum();
        double antrenorMaasToplam = yardimciAntrenorler.stream().mapToDouble(YardimciAntrenor::maasHesapla).sum();
        double fizyoMaasToplam = fizyoterapistler.stream().mapToDouble(Fizyoterapist::maasHesapla).sum();

        String rapor = String.format(
                """
                        ========= KULUP FINANSAL ANALIZ RAPORU =========
                        
                        %-30s : %.2fM EUR
                        %-30s : %.3fM EUR
                        %-30s : %,.0f EUR
                        %-30s : %,.0f EUR
                        %-30s : %,.0f EUR
                        
                        ------------------------------------------------
                        TOPLAM KULUP GIDERI (Net): %,.0f EUR
                        ================================================""",
                "Futbolcu Maas Gideri", futbolcuMaasToplam, "Dagitilan Toplam Prim", futbolcuPrimToplam,
                "Teknik Direktor Gideri", tdMaasToplam, "Antrenor Kadrosu Gideri", antrenorMaasToplam,
                "Fizyoterapist Gideri", fizyoMaasToplam,
                ((futbolcuMaasToplam + futbolcuPrimToplam) * 1_000_000) + tdMaasToplam + antrenorMaasToplam + fizyoMaasToplam
        );

        finansalRaporuDosyayaKaydet(rapor);
        return rapor;
    }

    private void finansalRaporuDosyayaKaydet(String raporMetni) {
        try (PrintWriter out = new PrintWriter(new FileWriter("finansal_analiz_raporu.txt"))) {
            out.println(raporMetni);
        } catch (IOException e) { System.err.println("Rapor kaydedilemedi: " + e.getMessage()); }
    }

    public void macEkle(LocalDate tarih, String rakip, String skor, String macTuru) {
        macGecmisi.put(tarih, new MacVerisi(tarih, null, rakip, skor, macTuru));
        tumVerileriKaydet();
    }

    public Futbolcu futbolcuBul(String formaNoStr) {
        try {
            int arananNo = Integer.parseInt(formaNoStr);
            return futbolcuKadrosu.stream().filter(f -> f.getFormaNo() == arananNo).findFirst().orElse(null);
        } catch (NumberFormatException e) { return null; }
    }

    public List<Futbolcu> getFutbolcuKadrosu() { return futbolcuKadrosu; }
    public List<TeknikDirektor> getTeknikDirektorler() { return teknikDirektorler; }
    public List<YardimciAntrenor> getYardimciAntrenorler() { return yardimciAntrenorler; }
    public List<Fizyoterapist> getFizyoterapistler() { return fizyoterapistler; }
    public String[] getHaftalikAktiviteler() { return haftalikAktiviteler; }
    public String[] getGunler() { return gunler; }
    public Map<LocalDate, MacVerisi> getMacGecmisi() { return macGecmisi; }
}