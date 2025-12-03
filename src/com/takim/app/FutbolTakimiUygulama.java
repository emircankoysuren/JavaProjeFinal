package com.takim.app;

import com.takim.service.TakimService;
import com.takim.model.*;
import com.takim.util.DosyaIslemleri;
import com.takim.util.Formatlayici;
import com.takim.exception.GecersizFormaNoException;
import com.takim.exception.KapasiteDolduException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * 1.1: Ana sinif (main metodu iceren, projeyi baslatan sinif).
 * 11. Bolum: Konsol menulu arayuzu ve donguyu yonetir.
 */
public class FutbolTakimiUygulama {

    // 1.1: Ana Sınıf Gereksinimi
    public static final String TAKIM_ADI = "GALATASARAY SPOR KULÜBÜ";

    private static Scanner scanner = new Scanner(System.in);
    private static TakimService service = new TakimService();

    // 10: Statik alan gereksinimi (Ana sınıfta da bir static alan)
    private static boolean uygulamaCalisiyor = true;

    public static void main(String[] args) {

        System.out.println(Formatlayici.renklendir(TAKIM_ADI + " Konsol Yönetim Sistemi'ne Hos Geldiniz!", Formatlayici.YESIL));

        // 9. Bolum: do-while dongusu kullanimi (Menü döngüsü)
        do {
            anaMenuyuGoster();
            // 7. Bolum: try-catch blogu kullanimi (3/5) - Kullanici girislerini yonetmek icin
            try {
                System.out.print(Formatlayici.renklendir("Seciminizi girin (1-8): ", Formatlayici.MAVI));
                int secim = scanner.nextInt();
                scanner.nextLine(); // buffer temizleme

                menuyuIsle(secim);

            } catch (InputMismatchException e) {
                // 7. Bolum: 3 farkli exception turunden ucuncusu yakalandi.
                System.err.println(Formatlayici.renklendir("HATA: Lutfen sadece sayi giriniz.", Formatlayici.KIRMİZİ));
                scanner.nextLine(); // Yanlis girisi tuket
            } catch (Exception e) {
                System.err.println(Formatlayici.renklendir("Beklenmedik bir hata olustu: " + e.getMessage(), Formatlayici.KIRMİZİ));
            }

        } while (uygulamaCalisiyor); // 9. Bolum: while dongusu

        System.out.println("Uygulama kapatiliyor. Iyi gunler.");
    }

    // 10: Static metot gereksinimi (Ana sınıfta bir static metot)

    /**
     * 11. Bolum: En az 8 maddeli ana menuyu gosterir.
     * Güncellendi: 9 maddeli menü.
     */
    private static void anaMenuyuGoster() {
        System.out.println(Formatlayici.renklendir("\n--- ANA MENU ---", Formatlayici.MAVI)); //
        System.out.println("1. Yeni Personel/Futbolcu Ekle"); //
        System.out.println("2. Performans Verisi Gir (Gol/Asist)"); //
        System.out.println("3. Performans Verilerini Görüntüle"); // YENİ SIRA
        System.out.println("4. Kadroyu Listele"); // YENİ SIRA
        System.out.println("5. Gol Siralamasini Gor"); // YENİ SIRA
        System.out.println("6. Personel Maas Hesaplamasi Yap"); //
        System.out.println("7. Raporu Dosyaya Kaydet"); //
        System.out.println("8. Cikis"); //
    }

    /**
     * 11. Bolum: Menudeki secimi isler (switch-case).
     * Güncellendi: 9 seçeneğe göre case'ler yeniden düzenlendi.
     */
    private static void menuyuIsle(int secim) {
        // 9. Bolum: switch-case yapisi kullanimi (en az 4 case)
        switch (secim) {
            case 1:
                personelTipiSecimEkrani();
                break;
            case 2:
                performansGuncellemeEkrani();
                break;
            case 3: // Performans Verilerini Görüntüle (Eski 5)
                performansGoruntulemeEkrani();
                break;
            case 4: // Kadroyu Listele (Eski 3)
                // Dinamik polimorfizm
                service.listeYazdir(service.getFutbolcuKadrosu());
                break;
            case 5: // Gol Siralamasini Gor (Eski 4)
                service.golSiralamasiYap();
                break;
            case 6:
                maasHesaplamaEkrani();
                break;
            case 7:
                dosyayaKaydetmeEkrani();
                break;
            case 8: // Çıkış
                DosyaIslemleri.kadroVerileriniKaydet(service.getFutbolcuKadrosu());
                uygulamaCalisiyor = false;
                break;
            default:
                System.out.println(Formatlayici.renklendir("Hata: Gecersiz secim. Lutfen 1-9 arasi bir sayi girin.", Formatlayici.KIRMİZİ));
        }
    }

    // ------------------- YENİ EKLENEN METOT -------------------

    /**
     * Eklenen personel tipine gore ilgili ekleme metodunu cagirir.
     */
    private static void personelTipiSecimEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- KISI EKLEME SECIMI ---", Formatlayici.MAVI));
        System.out.println("1. Futbolcu");
        System.out.println("2. Teknik Direktor");
        System.out.println("3. Yardimci Antrenor");
        System.out.println("4. Fizyoterapist");
        System.out.print("Eklenecek personel tipini secin (1-4): ");

        try {
            int tipSecim = scanner.nextInt();
            scanner.nextLine();

            switch (tipSecim) {
                case 1:
                    futbolcuEklemeEkrani();
                    break;
                case 2:
                    teknikDirektorEklemeEkrani();
                    break;
                case 3:
                    yardimciAntrenorEklemeEkrani();
                    break;
                case 4:
                    fizyoterapistEklemeEkrani();
                    break;
                default:
                    System.out.println(Formatlayici.renklendir("Hata: Gecersiz personel tipi.", Formatlayici.KIRMİZİ));
            }
        } catch (InputMismatchException e) {
            System.err.println(Formatlayici.renklendir("HATA: Lutfen sadece sayi giriniz.", Formatlayici.KIRMİZİ));
            scanner.nextLine();
        }
    }

    // ------------------- EKLEME METOTLARI -------------------

    // Mevcut Futbolcu Ekleme Metodu
    private static void futbolcuEklemeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- FUTBOLCU EKLEME ---", Formatlayici.YESIL));
        System.out.print("Ad: ");
        String ad = scanner.nextLine().trim();
        System.out.print("Soyad: ");
        String soyad = scanner.nextLine().trim();

        // YENİ EKLENEN KISIM: Mevki girişi
        System.out.println("\nMevki Secenekleri: FORVET, KANAT, ORTASAHA, DEFANS, KALECI");
        System.out.print("Mevki: ");
        String mevki = scanner.nextLine().trim().toUpperCase(); // Büyük harfe çevirerek veri bütünlüğünü sağla

        // Örnek String Metot kullanımı: Adın sadece harf içerip içermediğini kontrol etme
        for (int i = 0; i < ad.length(); i++) { // 9. Bolum: for döngüsü
            if (!Character.isLetter(ad.charAt(i))) { // 2.2: charAt() metodu
                System.out.println(Formatlayici.renklendir("HATA: Ad rakam içeriyor. Otomatik düzeltme yapılıyor...", Formatlayici.KIRMİZİ));
                ad = ad.replace(String.valueOf(ad.charAt(i)), ""); // 2.2: replace() metodu
                break; //
            }
        }

        try {
            System.out.print("Forma No (1-99): ");
            int formaNo = scanner.nextInt();
            scanner.nextLine();

            // GÜNCELLEME: Mevki bilgisini varsayılan "DEFANS" yerine kullanıcıdan alınan 'mevki' değişkeni ile atıyoruz.
            Futbolcu f = new Futbolcu(ad, soyad, LocalDate.now(), "TR" + formaNo, formaNo, mevki, 0, 0);
            service.futbolcuEkle(f);

            System.out.println(Formatlayici.renklendir(ad + " " + soyad + " (" + formaNo + ") basariyla eklendi.", Formatlayici.YESIL));

        } catch (InputMismatchException e) {
            System.err.println(Formatlayici.renklendir("HATA: Forma numarasi sayi olmalidir.", Formatlayici.KIRMİZİ));
            scanner.nextLine();
        } catch (GecersizFormaNoException e) {
            System.err.println(Formatlayici.renklendir(e.getMessage(), Formatlayici.KIRMİZİ));
        } catch (KapasiteDolduException e) {
            System.err.println(Formatlayici.renklendir(e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    // Yeni Teknik Direktor Ekleme Metodu
    private static void teknikDirektorEklemeEkrani() {
        // BASİTLİK İÇİN ÖRNEK DEĞERLERLE ÇALIŞAN METOT
        try {
            TeknikDirektor td = new TeknikDirektor("Okan", "Buruk", LocalDate.of(1973, 10, 19), "TR1",
                    500000, LocalDate.of(2022, 6, 1),
                    2005, "4-2-3-1", 100000);
            System.out.println(Formatlayici.renklendir(td.toString(), Formatlayici.YESIL));
            System.out.println(Formatlayici.renklendir("Teknik direktor (Ornek) bilgileri konsola yazdirildi.", Formatlayici.YESIL));

        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Teknik direktor eklenemedi: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    // Yeni Yardimci Antrenor Ekleme Metodu
    private static void yardimciAntrenorEklemeEkrani() {
        try {
            YardimciAntrenor ya = new YardimciAntrenor("Ismail", "Şenol", LocalDate.of(1985, 3, 10), "TR2",
                    200000, LocalDate.of(2023, 7, 1),
                    "Hucum", 1000.5);
            System.out.println(Formatlayici.renklendir(ya.toString(), Formatlayici.YESIL));
            System.out.println(Formatlayici.renklendir("Yardimci Antrenor (Ornek) bilgileri konsola yazdirildi.", Formatlayici.YESIL));
        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Yardimci Antrenor eklenemedi: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    // Yeni Fizyoterapist Ekleme Metodu
    private static void fizyoterapistEklemeEkrani() {
        try {
            Fizyoterapist fizyo = new Fizyoterapist("Ali", "Can", LocalDate.of(1988, 1, 1), "TR3",
                    150000, LocalDate.of(2021, 5, 15),
                    "SERT_001", "Ortopedi", true);
            System.out.println(Formatlayici.renklendir(fizyo.toString(), Formatlayici.YESIL));
            System.out.println(Formatlayici.renklendir("Fizyoterapist (Ornek) bilgileri konsola yazdirildi.", Formatlayici.YESIL));
        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Fizyoterapist eklenemedi: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    // ------------------- YENİ METOT: PERFORMANS GÖRÜNTÜLEME -------------------





    private static void performansGoruntulemeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- PERFORMANS VERILERINI GÖRÜNTÜLE ---", Formatlayici.MAVI));
        System.out.print("Görüntülemek istediğiniz futbolcunun Forma Numarasini girin (1-99): ");

        try {
            int formaNo = scanner.nextInt();
            scanner.nextLine();

            // 1. Forma No Geçerlilik Kontrolü
            if (formaNo < 1 || formaNo > 99) {
                System.err.println(Formatlayici.renklendir("HATA: Forma numarasi 1 ile 99 arasinda olmalidir. Girilen: " + formaNo, Formatlayici.KIRMİZİ));
                return; // Hatalıysa metottan çık
            }

            Futbolcu f = service.futbolcuyuBul(formaNo);

            // 2. Null Kontrolü ve Detayları Yazdırma
            if (f != null) {
                // Futbolcu bulunduysa, performansı yazdır.
                System.out.println(Formatlayici.renklendir("\n--- OYUNCU PERFORMANS DETAYI ---", Formatlayici.MAVI));
                // f.getPerformansBilgileri() metodu (Futbolcu.java'ya eklediğimiz) burada kullanılır.
                System.out.println(Formatlayici.renklendir(f.getPerformansBilgileri(), Formatlayici.YESIL));
            } else {
                // Futbolcu bulunamadıysa, hata mesajını göster.
                System.err.println(Formatlayici.renklendir("HATA: " + formaNo + " numaralı futbolcu kadroda bulunamadı.", Formatlayici.KIRMİZİ));
            }

        } catch (InputMismatchException e) {
            // Sayı dışında bir giriş yapılırsa bu hatayı yakalar.
            System.err.println(Formatlayici.renklendir("HATA: Forma numarasi sayi olmalidir.", Formatlayici.KIRMİZİ));
            scanner.nextLine(); // Yanlış girişi tüket
        }
    }

    // ------------------- KALAN METOTLAR (GÖVDESİ DEĞİŞMEYENLER) -------------------

    private static void performansGuncellemeEkrani() {

        System.out.println(Formatlayici.renklendir("\n--- PERFORMANS VERISI GIRISI ---", Formatlayici.MAVI));
        System.out.print("Futbolcunun Forma Numarasini girin: ");
        int formaNo = scanner.nextInt();
        scanner.nextLine();

        // Yeni Kontrol: Futbolcuyu ara
        Futbolcu f = service.futbolcuyuBul(formaNo);

        if (f == null) {
            // Futbolcu bulunamazsa hemen hata ver ve çık
            System.err.println(Formatlayici.renklendir("HATA: " + formaNo + " numaralı futbolcu kadroda bulunamadı. Guncelleme yapilamadi.", Formatlayici.KIRMİZİ));
            return; // Metottan hemen çıkış
        }

        // Bu noktadan sonra futbolcu (f) nesnesi bulundu, performans verilerini sorabiliriz.

        System.out.print("Gol sayisini girin : ");
        String golGirdi = scanner.nextLine().trim();

        System.out.print(" Asist sayisini girin: ");
        String asistGirdi = scanner.nextLine().trim();

        // 2.2: Zorunlu String metotlarini kullanma: substring, indexOf, equals
        if (golGirdi.substring(0, 1).equals("0") && asistGirdi.indexOf("0") == 0) {
            System.out.println(Formatlayici.renklendir("Uyari: Gol ve asist sifir olarak girildi.", Formatlayici.KIRMİZİ));
        }

        try {
            int gol = Integer.parseInt(golGirdi);
            int asist = Integer.parseInt(asistGirdi);

            // Gerçek güncelleme mantığı Service'e devredildi
            // Futbolcu nesnesi zaten yukarıda (f != null) kontrol edildiği için
            // Service metodu burada her zaman true döndürmelidir.
            service.performansGuncelle(formaNo, gol, asist);
            System.out.println(Formatlayici.renklendir(formaNo + " numarali futbolcunun performansi basariyla guncellendi.", Formatlayici.YESIL));

        } catch (NumberFormatException e) {
            System.err.println(Formatlayici.renklendir("HATA: Gol/Asist verisi sayi olmalidir.", Formatlayici.KIRMİZİ));
        }
    }

    private static void maasHesaplamaEkrani() {
        // İçerik aynı kalacak
        // ...
        try {
            System.out.println(Formatlayici.renklendir("\n--- MAAS HESAPLAMA ---", Formatlayici.MAVI));

            // 9. Bolum: Tek Boyutlu Dizi Kullanimi
            String[] personelTipleri = {"TeknikDirektor", "YardimciAntrenor", "Fizyoterapist"};
            System.out.println("Personel Tipleri: ");

            for (int i = 0; i < personelTipleri.length; i++) {
                System.out.println((i + 1) + ". " + personelTipleri[i]);
            }

            double toplamMaas = 50000;
            int personelSayisi = personelTipleri.length;
            double ortalamaMaas = toplamMaas / personelSayisi;
            double kalanPara = toplamMaas % personelSayisi;

            System.out.printf("Ortalama Maas: %.2f TL, Kalan Bütçe: %.2f TL%n", ortalamaMaas, kalanPara);

        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Maaş hesaplamada hata: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    private static void dosyayaKaydetmeEkrani() {
        // İçerik aynı kalacak
        // ...
        try {
            DosyaIslemleri.raporuDosyayaYaz(service.getFutbolcuKadrosu());
        } catch (IOException e) {
            System.err.println(Formatlayici.renklendir("Hata: Dosya islemlerinde sorun olustu: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }
}