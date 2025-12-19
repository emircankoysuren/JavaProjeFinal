package com.takim.util;

import java.time.format.DateTimeFormatter; // 6. Bolum: DateTimeFormatter kullanimi

/**
 * 1.1: Utility sinifi (2/2).
 * Bonus Renkli Cikti ve Tarih Formatlama gereksinimlerini karsilar.
 */
public class Formatlayici {

    // Bonus: Renkli Konsol Ciktilari - Statik alanlar (10. Bolum)
    public static final String YESIL = "\u001B[32m";
    public static final String KIRMIZI = "\u001B[31m";
    public static final String MAVI = "\u001B[34m";
    public static final String RESET = "\u001B[0m";

    // 6. Bolum: Tarih Formatlama ornegi - Statik alanlar
    public static final DateTimeFormatter TARİH_FORMATI = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Verilen mesaji istenen renkte dondurur.
     */
    // 10. Bolum: Static metot kullanimi
    public static String renklendir(String mesaj, String renk) {
        return renk + mesaj + RESET;
    }
}