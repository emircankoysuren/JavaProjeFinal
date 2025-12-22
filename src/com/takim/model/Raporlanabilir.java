package com.takim.model;

import java.time.LocalDate;

/**
 * Performans verileri interface'i
 */


public interface Raporlanabilir {
    String ozetRaporOlustur();
    String detayliIstatistikGetir(LocalDate baslangic, LocalDate bitis);
    boolean raporDurumuKontrolEt();
}