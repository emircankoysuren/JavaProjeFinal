package com.takim.service;

import com.takim.exception.KapasiteDolduException;
import com.takim.model.*;
import java.util.*;
import java.time.LocalDate;
import com.takim.util.DosyaIslemleri;
import com.takim.util.Formatlayici;

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
        orneKVeriYukle();
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
    // TakimService.java içinde yer almalıdır

    /**
     * Futbolcunun performans verilerini günceller. (Menü 2 için)
     * @param formaNo Güncellenecek futbolcunun forma numarası.
     * @param gol Eklenecek gol sayısı.
     * @param asist Eklenecek asist sayısı.
     * @return Güncelleme başarılıysa true, futbolcu bulunamazsa false.
     */
    public boolean performansGuncelle(int formaNo, int gol, int asist) {
        Futbolcu f = futbolcuyuBul(formaNo);
        if (f != null) {
            // Futbolcu modelindeki Metot Overloading kullanılır.
            // Bu metot, Futbolcu.java'daki golSayisi ve asistSayisi alanlarını artırır.
            f.performansGuncelle(gol, asist);
            return true;
        }
        return false;
    }

    /**
     * Forma numarasıyla futbolcuyu bulma metodu (Zaten mevcut olmalı, hatanın kaynağı bu değil ama kontrol edin).
     */
    public Futbolcu futbolcuyuBul(int formaNo) {
        // Hızlı arama için HashMap kullanılır.
        // formaNoHaritasi'nın TakimService içinde tanımlı ve kullanılıyor olması gerekir.

        return formaNoHaritasi.get(formaNo);
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
    private void orneKVeriYukle() {

        // Dosyadan kayıtlı verileri yüklemeyi dene
        List<Futbolcu> yuklenenKadro = DosyaIslemleri.kadroVerileriniYukle();

        if (!yuklenenKadro.isEmpty()) {
            // Kayıtlı veri bulunduysa, mevcut listeye ekle ve HashMap'i doldur
            for (Futbolcu f : yuklenenKadro) {
                futbolcuKadrosu.add(f);
                formaNoHaritasi.put(f.getFormaNo(), f);
            }
        } else {
            // Kayıtlı veri yoksa, ilk başlangıç için örnek verileri elle ekle (opsiyonel)
            // Bu kısmı isteğinize bağlı olarak kaldırabilirsiniz.
            System.out.println(Formatlayici.renklendir("Kayitli veri bulunamadi, ornek kadro oluşturuluyor...", Formatlayici.MAVI));
            try {
                Futbolcu f1 = new Futbolcu("Mauro", "Icardi", LocalDate.of(1993, 2, 19), "TC1", 9, "FORVET", 15, 5);
                futbolcuEkle(f1);
            } catch (Exception e) {
                System.err.println("Ornek veri yuklenirken hata olustu.");
            }
        }
    }
}