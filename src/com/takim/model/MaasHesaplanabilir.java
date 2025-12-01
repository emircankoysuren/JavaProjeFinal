package com.takim.model;

import java.time.Month;

/**
 * 4.3: Interface gereksinimi (2/2).
 */
public interface MaasHesaplanabilir {
    double yillikMaasArtisiOraniGetir();
    double vergiKesintisiHesapla(Month ay);
    double yillikBrutMaasGetir();
}