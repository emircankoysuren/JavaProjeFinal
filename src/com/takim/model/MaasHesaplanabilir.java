package com.takim.model;

import java.time.Month;

/**
 * 4.3: Interface gereksinimi.
 * Güncellendi: Finansal analiz ve bütçe metotlarını içerir.
 */
public interface MaasHesaplanabilir {
    double yillikMaasArtisiOraniGetir();
    double vergiKesintisiHesapla(Month ay);
    double yillikBrutMaasGetir();

    // YENİ EKLENEN METOTLAR (Kullanım için)
    double kidemTazminatiHesapla();        // İşten ayrılma maliyeti
    double verimlilikPuaniHesapla(int perf); // Maaş/Performans oranı
    String maliyetDurumuAnaliziGetir();
    String butceDurumuGetir();             // Kulüp bütçesine etkisi
}