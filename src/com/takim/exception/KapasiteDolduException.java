package com.takim.exception;

/**
 * 7. Bolum: Ozel exception sinifi (2/2).
 * Takim kadrosu kapasitesi asildiginda firlatilir.
 */
public class KapasiteDolduException extends Exception {

    public KapasiteDolduException(String message) {
        super(message);
    }
}