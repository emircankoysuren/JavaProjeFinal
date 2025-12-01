package com.takim.util;

import com.takim.model.Futbolcu;
//import com.takim.app.MainGUI; // Takım adına erişim
import java.io.*;
import java.util.List;

/**
 * 1.1: Utility sinifi (1/2). I/O ve 7. Bolum Exception islemleri.
 * 8. Bolum: Dosya okuma/yazma gereksinimlerini karsilar.
 */
public class DosyaIslemleri {

    private static final String RAPOR_DOSYASI = "takim_rapor.txt";
    private static final String PATH_AYIRICI = System.getProperty("file.separator"); // 10: Platform bagimsiz

    /**
     * 8. Bolum: Dosyaya yazma islemi (FileWriter + BufferedWriter).
     * 7. Bolum: throws ifadesi ile exception'i cagiran tarafa iletme.
     */
    public static void raporuDosyayaYaz(List<Futbolcu> kadroListesi) throws IOException { // 7. Bolum: throws ifadesi
        File dosya = new File("." + PATH_AYIRICI + RAPOR_DOSYASI);
        BufferedWriter writer = null;
        try { // 7. Bolum: try-catch blogu (1/5)
            writer = new BufferedWriter(new FileWriter(dosya));
           // writer.write("--- " + MainGUI.TAKIM_ADI + " Kadrosu Raporu ---\n");
            writer.write("Takim Kadro Sayisi: " + kadroListesi.size() + "\n\n");

            for (Futbolcu futbolcu : kadroListesi) {
                writer.write(futbolcu.ozetRaporOlustur().toUpperCase() + "\n");
            }
            System.out.println("Rapor basariyla dosyaya yazildi: " + dosya.getAbsolutePath());
        } catch (IOException e) { // 7. Bolum: IOException yakalama
            throw new IOException("Dosya yazma hatasi: " + e.getMessage());
        } finally { // 7. Bolum: finally blogu
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 8. Bolum: Dosyadan okuma islemi (BufferedReader + FileReader).
     */
    public static void dosyadanVeriOku() {
        File dosya = new File("." + PATH_AYIRICI + RAPOR_DOSYASI);
        try (BufferedReader reader = new BufferedReader(new FileReader(dosya))) { // 7. Bolum: try-catch (2/5)
            String satir;
            System.out.println("\n--- Dosya Icerigi ---");
            while ((satir = reader.readLine()) != null) { // 9. Bolum: while dongusu
                System.out.println("OKUNAN: " + satir);
            }
            System.out.println("----------------------");
        } catch (FileNotFoundException e) { // 7. Bolum: FileNotFoundException yakalama
            System.err.println("Hata: Rapor dosyasi bulunamadi. Yazma islemi yapilmamis olabilir.");
        } catch (IOException e) {
            System.err.println("Dosya okuma sirasinda genel bir hata olustu: " + e.getMessage());
        }
    }
}