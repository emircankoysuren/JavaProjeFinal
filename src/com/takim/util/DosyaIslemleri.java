package com.takim.util;

import com.takim.model.Futbolcu;
import com.takim.exception.GecersizFormaNoException;
import java.io.*;
import java.time.LocalDate; // YENİ EKLENDİ
import java.util.ArrayList; // YENİ EKLENDİ
import java.util.List; // YENİ EKLENDİ
// import com.takim.app.MainGUI; // Eğer varsa bu satırı silin.

/**
 * 1.1: Utility sinifi (1/2).
 */
public class DosyaIslemleri {

    private static final String RAPOR_DOSYASI = "takim_rapor.txt";
    private static final String VERI_DOSYASI = "futbolcu_data.txt"; // Hata alınan sabit
    private static final String PATH_AYIRICI = System.getProperty("file.separator");



    /**
     * 8. Bolum: Dosyaya yazma islemi (FileWriter + BufferedWriter).
     * 7. Bolum: throws ifadesi ile exception'i cagiran tarafa iletme.
     */
    public static void raporuDosyayaYaz(java.util.List<com.takim.model.Futbolcu> kadroListesi) throws java.io.IOException {
        // RAPOR_DOSYASI sabitini kullanıyoruz
        java.io.File dosya = new java.io.File("." + PATH_AYIRICI + RAPOR_DOSYASI);
        java.io.BufferedWriter writer = null;

        try {
            writer = new java.io.BufferedWriter(new java.io.FileWriter(dosya));

            writer.write("Takim Kadro Sayisi: " + kadroListesi.size() + "\n\n");

            // Futbolcu nesneleri üzerinde döngü
            for (com.takim.model.Futbolcu futbolcu : kadroListesi) {
                // Futbolcu modelindeki ozetRaporOlustur() metodunu kullanır.
                writer.write(futbolcu.ozetRaporOlustur().toUpperCase() + "\n");
            }

            System.out.println("Rapor basariyla dosyaya yazildi: " + dosya.getAbsolutePath());

        } catch (java.io.IOException e) {
            // Hatayı çağırana ilet
            throw new java.io.IOException("Dosya yazma hatasi: " + e.getMessage());
        } finally { // finally blogu
            if (writer != null) {
                writer.close(); // Writer'ı kapat
            }
        }
    }
    // ... (raporuDosyayaYaz metodu ve diğer mevcut metotlar) ...

    // ----------------------------------------------------------------------------------
    // KALICILIK İÇİN VERİ KAYDETME METODU (Futbolcu.getMevki/getAsistSayisi gerektirir)
    // ----------------------------------------------------------------------------------
    public static void kadroVerileriniKaydet(List<Futbolcu> kadroListesi) {
        File dosya = new File("." + PATH_AYIRICI + VERI_DOSYASI);
        // ... (metot içeriği aynı kalabilir)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dosya))) {
            writer.write("Ad,Soyad,DogumTarihi,TCKimlik,FormaNo,Mevki,Gol,Asist\n");

            for (Futbolcu f : kadroListesi) {
                String satir = f.getAd() + "," + f.getSoyad() + "," +
                        // Tarih formatlama için Formatlayıcı.TARİH_FORMATI sabitini gerektirir
                        f.getDogumTarihi().format(Formatlayici.TARİH_FORMATI) + "," +
                        f.getTcKimlikNo() + "," + f.getFormaNo() + "," +
                        f.getMevki() + "," + f.getasistSayisi() + "," + f.getGolSayisi(); // Hata veren metotlar
                writer.write(satir + "\n");
            }
            // ... (çıktı mesajı) ...

        } catch (IOException e) {
            System.err.println(Formatlayici.renklendir("HATA: Veri kaydetme sirasinda sorun olustu: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }


    // ----------------------------------------------------------------------------------
    // KALICILIK İÇİN VERİ YÜKLEME METODU (ArrayList ve LocalDate gerektirir)
    // ----------------------------------------------------------------------------------
    public static List<Futbolcu> kadroVerileriniYukle() {
        List<Futbolcu> yuklenenKadro = new ArrayList<>();
        File dosya = new File("." + PATH_AYIRICI + VERI_DOSYASI);
        // ... (metot içeriği aynı kalabilir)

        // ... (Dosya kontrolü) ...

        try (BufferedReader reader = new BufferedReader(new FileReader(dosya))) {
            // ... (satır okuma mantığı) ...
        } catch (FileNotFoundException e) {
            // ...
        } catch (IOException | NumberFormatException e) {
            System.err.println(Formatlayici.renklendir("HATA: Veri okuma veya dönüştürme hatası: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
        return yuklenenKadro;
    }
}