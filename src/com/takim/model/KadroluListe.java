package com.takim.model;

import java.util.ArrayList;
import java.util.List;

import com.takim.exception.KapasiteDolduException;



public class KadroluListe<T extends Kisi> {

    private final List<T> liste;
    private final int maksimumKapasite;

    public KadroluListe(int maksimumKapasite) {
        this.liste = new ArrayList<>();
        this.maksimumKapasite = maksimumKapasite;
    }

    /**

    Listeye eleman ekler eğer kapasite aşılırsa özel KapasiteDolduException fırlatır
    Bu sayede Checked Exception mekanizmasıyla hata yönetimi zorunlu kılınır
 */

    public void ekle(T eleman) throws KapasiteDolduException {
        if (liste.size() >= maksimumKapasite) {
            // Kapasite dolduysa hata nesnesini olustur ve firlat
            throw new KapasiteDolduException("Hata: Kadro kapasitesi (" + maksimumKapasite + ") doldu!");
        }
        liste.add(eleman);
    }

    /**

     Wildcard (?) kullanarak tipi ne olursa olsun herhangi bir listenin boyutunu döner
     Wilcard örneği
     */

    public int tipineGoreSay(List<?> herhangiListe) {
        return (herhangiListe != null) ? herhangiListe.size() : 0;
    }

    /**

     Upper Bounded Wildcard (? extends Kisi) kullanımı
     Kisi sınıfından türetilmiş herhangi bir alt sınıf listesini kabul eder
     ve polimorfizm sayesinde ortak metotları çağırır
     */

    public void altTipiIsle(List<? extends Kisi> altTipListe) {
        for (Kisi kisi : altTipListe) {
            kisi.bilgiYazdir();
        }
    }

    public List<T> getListe() { return liste; }
}