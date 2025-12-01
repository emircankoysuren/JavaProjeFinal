package com.takim.model;

import java.util.ArrayList;
import java.util.List;

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

    public void ekle(T eleman) {
        if (liste.size() < maksimumKapasite) {
            liste.add(eleman);
        }
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