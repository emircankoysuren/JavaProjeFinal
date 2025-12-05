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

public class FutbolTakimiUygulama {

    public static final String TAKIM_ADI = "GALATASARAY SPOR KULÜBÜ";

    private static Scanner scanner = new Scanner(System.in);
    private static TakimService service = new TakimService();

    private static boolean uygulamaCalisiyor = true;

    public static void main(String[] args) {

        System.out.println(Formatlayici.renklendir(TAKIM_ADI + " Konsol Yönetim Sistemi'ne Hos Geldiniz!", Formatlayici.YESIL));

        do {
            anaMenuyuGoster();
            try {
                System.out.print(Formatlayici.renklendir("Seciminizi girin (1-9): ", Formatlayici.MAVI));
                int secim = scanner.nextInt();
                scanner.nextLine();

                menuyuIsle(secim);

            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Lutfen sadece sayi giriniz.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            } catch (Exception e) {
                System.err.println(Formatlayici.renklendir("Beklenmedik bir hata olustu: " + e.getMessage(), Formatlayici.KIRMİZİ));
            }

        } while (uygulamaCalisiyor);

        System.out.println("Uygulama kapatiliyor. Iyi gunler.");
    }

    private static void anaMenuyuGoster() {
        System.out.println(Formatlayici.renklendir("\n--- ANA MENU ---", Formatlayici.MAVI));
        System.out.println("1. Yeni Personel/Futbolcu Ekle");
        System.out.println("2. Performans Verisi Gir (Gol/Asist)");
        System.out.println("3. Performans Verilerini Görüntüle");
        System.out.println("4. Kadroyu Listele");
        System.out.println("5. Gol Siralamasini Gor");
        System.out.println("6. Personel Maas Hesaplamasi Yap");
        System.out.println("7. Raporu Dosyaya Kaydet");
        System.out.println("8. Personel Sil (Ad/Soyad)");
        System.out.println("9. Cikis");
    }

    private static void menuyuIsle(int secim) {
        switch (secim) {
            case 1:
                personelTipiSecimEkrani();
                break;
            case 2:
                performansGuncellemeEkrani();
                break;
            case 3:
                performansGoruntulemeEkrani();
                break;
            case 4:
                service.listeYazdir(service.getFutbolcuKadrosu());
                break;
            case 5:
                service.golSiralamasiYap();
                break;
            case 6:
                maasHesaplamaEkrani();
                break;
            case 7:
                dosyayaKaydetmeEkrani();
                break;
            case 8: // Personel Silme
                personelSilmeEkrani();
                break;
            case 9: // Çıkış
                DosyaIslemleri.kadroVerileriniKaydet(service.getFutbolcuKadrosu());
                uygulamaCalisiyor = false;
                break;
            default:
                System.out.println(Formatlayici.renklendir("Hata: Gecersiz secim. Lutfen 1-9 arasi bir sayi girin.", Formatlayici.KIRMİZİ));
        }
    }

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

    // --- EKLEME METOTLARI (CONSTRUCTORLAR DÜZELTİLDİ) ---

    private static void futbolcuEklemeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- FUTBOLCU EKLEME ---", Formatlayici.YESIL));
        System.out.print("Ad: ");
        String ad = scanner.nextLine().trim();
        System.out.print("Soyad: ");
        String soyad = scanner.nextLine().trim();

        System.out.println("\nMevki Secenekleri: FORVET, KANAT, ORTASAHA, DEFANS, KALECI");
        System.out.print("Mevki: ");
        String mevki = scanner.nextLine().trim().toUpperCase();

        for (int i = 0; i < ad.length(); i++) {
            if (!Character.isLetter(ad.charAt(i))) {
                System.out.println(Formatlayici.renklendir("HATA: Ad rakam içeriyor. Otomatik düzeltme yapılıyor...", Formatlayici.KIRMİZİ));
                ad = ad.replace(String.valueOf(ad.charAt(i)), "");
                break;
            }
        }

        try {
            System.out.print("Forma No (1-99): ");
            int formaNo = scanner.nextInt();
            scanner.nextLine();

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

    private static void teknikDirektorEklemeEkrani() {
        try {
            // 12 PARAMETRELİ CONSTRUCTOR ÇAĞRISI DÜZELTİLDİ
            TeknikDirektor td = new TeknikDirektor("Okan", "Buruk", LocalDate.of(1973, 10, 19), "TR1",
                    500000, LocalDate.of(2022, 6, 1),
                    2005, "4-2-3-1", 100000,
                    "B.B. Erzurumspor", 1.5, 5); // Yeni 3 parametre

            System.out.println(Formatlayici.renklendir(td.toString(), Formatlayici.YESIL));
            System.out.println(Formatlayici.renklendir("Teknik direktor (Ornek) bilgileri konsola yazdirildi.", Formatlayici.YESIL));

        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Teknik direktor eklenemedi: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    private static void yardimciAntrenorEklemeEkrani() {
        try {
            // 14 PARAMETRELİ CONSTRUCTOR ÇAĞRISI DÜZELTİLDİ
            YardimciAntrenor ya = new YardimciAntrenor("Ismail", "Şenol", LocalDate.of(1985, 3, 10), "TR2",
                    200000, LocalDate.of(2023, 7, 1),
                    "Hucum", 1000.5,
                    18, 15, 17, 16, 19, 20); // Yeni 6 puan parametresi

            System.out.println(Formatlayici.renklendir(ya.toString(), Formatlayici.YESIL));
            System.out.println(Formatlayici.renklendir("Yardimci Antrenor (Ornek) bilgileri konsola yazdirildi.", Formatlayici.YESIL));
        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Yardimci Antrenor eklenemedi: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    private static void fizyoterapistEklemeEkrani() {
        try {
            // 13 PARAMETRELİ CONSTRUCTOR ÇAĞRISI DÜZELTİLDİ
            Fizyoterapist fizyo = new Fizyoterapist("Ali", "Can", LocalDate.of(1988, 1, 1), "TR3",
                    150000, LocalDate.of(2021, 5, 15),
                    "SERT_001", "Ortopedi", true,
                    18, 15, 19, 17); // Yeni 4 puan parametresi

            System.out.println(Formatlayici.renklendir(fizyo.toString(), Formatlayici.YESIL));
            System.out.println(Formatlayici.renklendir("Fizyoterapist (Ornek) bilgileri konsola yazdirildi.", Formatlayici.YESIL));
        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Fizyoterapist eklenemedi: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    // ------------------- PERSONEL SİLME METODU (YENİ) -------------------

    private static void personelSilmeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- PERSONEL SILME (Ad/Soyad) ---", Formatlayici.KIRMİZİ));
        System.out.print("Silinecek personelin Adi: ");
        String ad = scanner.nextLine().trim();
        System.out.print("Silinecek personelin Soyadi: ");
        String soyad = scanner.nextLine().trim();

        if (ad.isEmpty() || soyad.isEmpty()) {
            System.err.println(Formatlayici.renklendir("HATA: Ad ve soyad boş birakilamaz.", Formatlayici.KIRMİZİ));
            return;
        }

        // Not: TakimService.java dosyasında personelSil metodu doğru implemente edilmiş olmalıdır.
        boolean basarili = service.personelSil(ad, soyad);

        if (basarili) {
            System.out.println(Formatlayici.renklendir(ad + " " + soyad + " isimli personel/futbolcu basariyla silindi.", Formatlayici.YESIL));
        } else {
            System.err.println(Formatlayici.renklendir("HATA: " + ad + " " + soyad + " isimli personel/futbolcu kadroda bulunamadi.", Formatlayici.KIRMİZİ));
        }
    }

    // ------------------- KALAN METOTLAR -------------------

    private static void performansGoruntulemeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- PERFORMANS VERILERINI GÖRÜNTÜLE ---", Formatlayici.MAVI));
        System.out.print("Görüntülemek istediğiniz futbolcunun Forma Numarasini girin (1-99): ");

        try {
            int formaNo = scanner.nextInt();
            scanner.nextLine();

            if (formaNo < 1 || formaNo > 99) {
                System.err.println(Formatlayici.renklendir("HATA: Forma numarasi 1 ile 99 arasinda olmalidir. Girilen: " + formaNo, Formatlayici.KIRMİZİ));
                return;
            }

            Futbolcu f = service.futbolcuyuBul(formaNo);

            if (f != null) {
                System.out.println(Formatlayici.renklendir("\n--- OYUNCU PERFORMANS DETAYI ---", Formatlayici.MAVI));
                System.out.println(Formatlayici.renklendir(f.getPerformansBilgileri(), Formatlayici.YESIL));
            } else {
                System.err.println(Formatlayici.renklendir("HATA: " + formaNo + " numaralı futbolcu kadroda bulunamadı.", Formatlayici.KIRMİZİ));
            }

        } catch (InputMismatchException e) {
            System.err.println(Formatlayici.renklendir("HATA: Forma numarasi sayi olmalidir.", Formatlayici.KIRMİZİ));
            scanner.nextLine();
        }
    }

    private static void performansGuncellemeEkrani() {

        System.out.println(Formatlayici.renklendir("\n--- PERFORMANS VERISI GIRISI ---", Formatlayici.MAVI));
        System.out.print("Futbolcunun Forma Numarasini girin: ");
        int formaNo = scanner.nextInt();
        scanner.nextLine();

        Futbolcu f = service.futbolcuyuBul(formaNo);

        if (f == null) {
            System.err.println(Formatlayici.renklendir("HATA: " + formaNo + " numaralı futbolcu kadroda bulunamadı. Guncelleme yapilamadi.", Formatlayici.KIRMİZİ));
            return;
        }

        System.out.print("Gol sayisini girin : ");
        String golGirdi = scanner.nextLine().trim();

        System.out.print(" Asist sayisini girin: ");
        String asistGirdi = scanner.nextLine().trim();

        if (golGirdi.substring(0, 1).equals("0") && asistGirdi.indexOf("0") == 0) {
            System.out.println(Formatlayici.renklendir("Uyari: Gol ve asist sifir olarak girildi.", Formatlayici.KIRMİZİ));
        }

        try {
            int gol = Integer.parseInt(golGirdi);
            int asist = Integer.parseInt(asistGirdi);

            service.performansGuncelle(formaNo, gol, asist);
            System.out.println(Formatlayici.renklendir(formaNo + " numarali futbolcunun performansi basariyla guncellendi.", Formatlayici.YESIL));

        } catch (NumberFormatException e) {
            System.err.println(Formatlayici.renklendir("HATA: Gol/Asist verisi sayi olmalidir.", Formatlayici.KIRMİZİ));
        }
    }

    private static void maasHesaplamaEkrani() {
        try {
            System.out.println(Formatlayici.renklendir("\n--- MAAS HESAPLAMA ---", Formatlayici.MAVI));

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
        try {
            DosyaIslemleri.raporuDosyayaYaz(service.getFutbolcuKadrosu());
        } catch (IOException e) {
            System.err.println(Formatlayici.renklendir("Hata: Dosya islemlerinde sorun olustu: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }
}