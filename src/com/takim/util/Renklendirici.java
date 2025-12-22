package com.takim.util;

import java.time.format.DateTimeFormatter;


public class Renklendirici {


    public static final String YESIL = "\u001B[32m";
    public static final String KIRMIZI = "\u001B[31m";
    public static final String MAVI = "\u001B[34m";
    public static final String RESET = "\u001B[0m";


    public static final DateTimeFormatter TARIH_FORMATI = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public static String renklendir(String mesaj, String renk) {
        return renk + mesaj + RESET;
    }
}