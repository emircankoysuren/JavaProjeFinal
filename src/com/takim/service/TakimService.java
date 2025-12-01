package com.takim.service;

import com.takim.exception.KapasiteDolduException;
import com.takim.model.*;
import java.util.*;
import java.time.LocalDate;

/**
 * Takim verilerini yonetir, is mantigini uygular ve koleksiyonlari tutar.
 */
public class TakimService {

    private List<Futbolcu> futbolcuKadrosu; // 5.2: List referansi
    private Map<Integer, Futbolcu> formaNoHaritasi; // 5.2: Map referansi
    private Map<LocalDate,MacVerisi> macGecmisi; // 5.2: TreeMap kullanilabilir
    private static final int MAX_KADRO_LIMITI = 28;

    public TakimService() {
        this.futbolcuKadrosu = new ArrayList<>(); // 5.2: ArrayList
        this.formaNoHaritasi = new HashMap<>(); // 5.2: HashMap
        this.macGecmisi = new TreeMap<>(); // 5.2: TreeMap
    }

    public void futbolcuEkle(Futbolcu futbolcu) throws KapasiteDolduException {
        // 7. Bolum: try-catch blogu (3/5)
        try {
            if (futbolcuKadrosu.size() >= MAX_KADRO_LIMITI) {
                throw new KapasiteDolduException("Kadro Limiti asildi.");
            }
            futbolcuKadrosu.add(futbolcu);
            formaNoHaritasi.put(futbolcu.getFormaNo(), futbolcu); // 5.2: Ekleme
        } catch (KapasiteDolduException e) {
            System.err.println("HATA: " + e.getMessage());
        }
    }

    // 5.1: Generic metot (1/2)
    public <T extends Kisi> void listeYazdir(List<T> liste) {
        for (T eleman : liste) { // 9. Bolum: for-each dongusu
            eleman.bilgiYazdir();
        }
    }

    // 5.1: Generic metot (2/2) ve Collections.sort (5.2)
    public void golSiralamasiYap() {
        Collections.sort(futbolcuKadrosu, (f1, f2) -> f2.getGolSayisi() - f1.getGolSayisi()); // 4.4: Lambda kullanimi
    }

    // ... Getter metodları
    public List<Futbolcu> getFutbolcuKadrosu() { return futbolcuKadrosu; }
}