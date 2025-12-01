package com.takim.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 1.1: Alt Sinif (4/4) - Utility sınıfı olarak da kullanılabilir.
 */
public class MacVerisi {
    private LocalDate macTarihi;
    private LocalTime macSaati;
    private String rakipTakim;
    private String skor;

    public MacVerisi(LocalDate macTarihi, LocalTime macSaati, String rakipTakim, String skor) {
        this.macTarihi = macTarihi;
        this.macSaati = macSaati;
        this.rakipTakim = rakipTakim;
        this.skor = skor;
    }
}