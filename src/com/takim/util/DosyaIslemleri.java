package com.takim.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Dosya okuma ve yazma işlemlerini gerçekleştiren yardımcı sınıf.
 * Listeyi Serileştirme (Serialization) yoluyla kaydeder ve yükler.
 */
public class DosyaIslemleri {

    /**
     * Bir listeyi belirtilen dosyaya yazar (Serileştirir).
     */
    public static <T extends Serializable> void dosyayaYaz(List<T> liste, String dosyaAdi) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(dosyaAdi));
            oos.writeObject(liste);
            System.out.println(Formatlayici.renklendir(dosyaAdi + " dosyası başarıyla kaydedildi.", Formatlayici.MAVI));
        } finally {
            if (oos != null) {
                oos.close(); // Dosya her koşulda kapatılır
                System.out.println("Dosya kapatma işlemi finally bloğunda yapıldı.");
            }
        }
    }

    /**
     * Belirtilen dosyadan listeyi okur (De-serileştirir) ve NULL değerleri filtreler.
     */
    public static <T> List<T> dosyadanOku(String dosyaAdi, Class<T> tip) throws IOException, ClassNotFoundException {
        File file = new File(dosyaAdi);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dosyaAdi))) {
            @SuppressWarnings("unchecked")
            List<T> liste = (List<T>) ois.readObject();

            // YENİ: Okunan listedeki null elemanları temizle
            if (liste != null) {
                return liste.stream().filter(Objects::nonNull).collect(Collectors.toList());
            }
            return new ArrayList<>();

        } catch (EOFException e) {
            return new ArrayList<>();
        }
    }
}