package com.takim.model;

import java.time.LocalDate;

/**
 * 4.3: Interface gereksinimi (1/2).
 */
public interface Raporlanabilir {
    String ozetRaporOlustur();
    String detayliIstatistikGetir(LocalDate baslangic, LocalDate bitis);
    boolean raporDurumuKontrolEt();
}