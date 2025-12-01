package com.takim.exception;

/**
 * 7. Bolum: Ozel exception sinifi (1/2).
 * Futbolcu forma numarasinin 1-99 araligi disinda olmasi durumunda firlatilir.
 */
public class GecersizFormaNoException extends Exception {

    public GecersizFormaNoException(String message) {
        super(message);
    }
}