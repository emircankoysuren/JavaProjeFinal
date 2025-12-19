package com.takim.model;

/**
 * 4.3: Interface gereksinimi.
 * Finansal Analiz Raporu için gerekli temel hesaplamaları sağlar.
 */
public interface MaasHesaplanabilir {
    // 1. Temel Maaş Hesaplama (Kullanıcı girişi + varsa kıdem/bonus)
    double maasHesapla();

    // 2. Prim Hesaplama (8. madde için kritik: Gol/Asist veya Başarı Primi)
    double primHesapla(int gol, int asist);

    // 3. Toplam Maliyet Analizi (Maaş + Prim + Sigorta/Vergi yükü)
    double toplamMaliyetHesapla(int gol, int asist);
}