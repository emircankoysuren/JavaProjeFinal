package com.takim.model;

import java.util.ArrayList;
import java.util.List;

import com.takim.exception.KapasiteDolduException;

/**
 * 5.1: Generic Sinif gereksinimini karsilar.
 */
public class KadroluListe<T extends Kisi> {

    private final List<T> liste;
    private final int maksimumKapasite;

    public KadroluListe(int maksimumKapasite) {
        this.liste = new ArrayList<>();
        this.maksimumKapasite = maksimumKapasite;
    }

    public void ekle(T eleman) throws KapasiteDolduException {
        if (liste.size() >= maksimumKapasite) {
            // Kapasite dolduysa hata nesnesini olustur ve firlat
            throw new KapasiteDolduException("Hata: Kadro kapasitesi (" + maksimumKapasite + ") doldu!");
        }
        liste.add(eleman);
    }

    // 5.1: Wildcard kullanimi ornegi - List<?>
    public int tipineGoreSay(List<?> herhangiListe) {
        return (herhangiListe != null) ? herhangiListe.size() : 0;
    }

    // 5.1: Wildcard kullanimi ornegi - List<? extends T>
    public void altTipiIsle(List<? extends Kisi> altTipListe) {
        for (Kisi kisi : altTipListe) {
            kisi.bilgiYazdir();
        }
    }

    public List<T> getListe() { return liste; }
}