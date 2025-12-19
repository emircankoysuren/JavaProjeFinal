package com.takim.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 1.1: Alt Sinif (4/4) - Utility sınıfı olarak da kullanılabilir.
 */
public class MacVerisi implements Serializable {
    private LocalDate macTarihi;
    private LocalTime macSaati;
    private String rakipTakim;
    private String skor;
    private String macTuru; // Örn: "Süper Lig", "Türkiye Kupası"

    public MacVerisi(LocalDate macTarihi, LocalTime macSaati, String rakipTakim, String skor, String macTuru) {
        this.macTarihi = macTarihi;
        this.macSaati = macSaati;
        this.rakipTakim = rakipTakim;
        this.skor = skor;
        this.macTuru = macTuru;
    }


    public String getRakipTakim() {
        return rakipTakim;
    }

    public String getSkor() {
        return skor;
    }

    public LocalDate getMacTarihi() {
        return macTarihi;
    }
    public String getMacTuru() { return macTuru; }
}
