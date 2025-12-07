package com.takim.app;
import java.time.format.DateTimeParseException;
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
    private static int tdSayaci = 1; // Teknik Direktörler için ardışık ID (TRD1, TRD2...)

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
        System.out.println("5. Skor Katkısı Sıralamasını Gör (Gol + Asist)");
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
                service.skorKatkisiSiralamasiYap(); // Yeni sıralama metodunu çağır
                // Rengi MAVI yerine KIRMIZI yaptık
                System.out.println(Formatlayici.renklendir("\n--- SKOR KATKISI SIRALAMASI ---", Formatlayici.KIRMİZİ));

                // Sıralanmış listedeki her futbolcu için yeni detaylı metodu çağır
                for (com.takim.model.Futbolcu f : service.getFutbolcuKadrosu()) {
                    System.out.println(Formatlayici.renklendir(f.getSkorKatkisiBilgileri(), Formatlayici.YESIL));
                }

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
                DosyaIslemleri.personelVerileriniKaydet(service.getKisiListesi()); // Bu satırı ekleyin/düzeltin
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

    // --- EKLEME METOTLARI ---

    private static void futbolcuEklemeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- FUTBOLCU EKLEME ---", Formatlayici.YESIL));
        System.out.print("Ad: ");
        String ad = scanner.nextLine().trim();
        System.out.print("Soyad: ");
        String soyad = scanner.nextLine().trim();

        LocalDate dogumTarihi = null;
        // Doğum Tarihi Giriş Döngüsü
        while (dogumTarihi == null) {
            System.out.print("Doğum Tarihi (gg.AA.YYYY formatında, örn: 19.02.1993): ");
            String tarihGirdi = scanner.nextLine().trim();
            try {
                dogumTarihi = LocalDate.parse(tarihGirdi, com.takim.util.Formatlayici.TARİH_FORMATI);

                if (dogumTarihi.isAfter(LocalDate.now())) {
                    System.err.println(Formatlayici.renklendir("HATA: Doğum tarihi şimdiki tarihten ileri bir tarih olamaz. Tekrar deneyin.", Formatlayici.KIRMİZİ));
                    dogumTarihi = null;
                }

            } catch (java.time.format.DateTimeParseException e) {
                System.err.println(Formatlayici.renklendir("HATA: Hatalı tarih formatı. Lütfen gg.AA.YYYY formatını kullanın. Tekrar deneyin.", Formatlayici.KIRMİZİ));
                dogumTarihi = null;
            }
        }

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

        int formaNo;
        // Forma No girişini ve kontrolünü döngüye alıyoruz
        while (true) {
            try {
                System.out.print("Forma No (1-99): ");

                if (!scanner.hasNextInt()) {
                    System.err.println(Formatlayici.renklendir("HATA: Forma numarasi sayi olmalidir. Tekrar deneyin.", Formatlayici.KIRMİZİ));
                    scanner.nextLine();
                    continue;
                }

                formaNo = scanner.nextInt();
                scanner.nextLine();

                Futbolcu mevcutFutbolcu = service.futbolcuyuBul(formaNo);

                if (mevcutFutbolcu != null) {
                    System.err.println(Formatlayici.renklendir("HATA: " + formaNo + " numaralı forma zaten " + mevcutFutbolcu.getAd() + " " + mevcutFutbolcu.getSoyad() + " isimli oyuncuya aittir. Lütfen farklı bir numara seçin.", Formatlayici.KIRMİZİ));
                    continue;
                }

                if (formaNo < 1 || formaNo > 99) {
                    throw new GecersizFormaNoException("Forma numarası 1 ile 99 arasında olmalıdır.");
                }

                break;

            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Forma numarası sayısal olmalıdır. Tekrar deneyin.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            } catch (GecersizFormaNoException e) {
                System.err.println(Formatlayici.renklendir(e.getMessage(), Formatlayici.KIRMİZİ));
                continue;
            }
        }

        // Forma numarası başarıyla alındıktan sonra ekleme işlemi
        try {
            Futbolcu f = new Futbolcu(ad, soyad, dogumTarihi, "TR" + formaNo, formaNo, mevki, 0, 0);
            service.futbolcuEkle(f);

            System.out.println(Formatlayici.renklendir(ad + " " + soyad + " (" + formaNo + ") basariyla eklendi.", Formatlayici.YESIL));

        } catch (KapasiteDolduException e) {
            System.err.println(Formatlayici.renklendir(e.getMessage(), Formatlayici.KIRMİZİ));
        } catch (GecersizFormaNoException e) {
            // Unhandled exception hatası çözümü için buraya eklendi
            System.err.println(Formatlayici.renklendir("İÇ HATA: Nesne oluşturulurken beklenmedik bir GecersizFormaNoException oluştu: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    private static void teknikDirektorEklemeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- TEKNİK DİREKTÖR EKLEME ---", Formatlayici.YESIL));

        System.out.print("Ad: ");
        String ad = scanner.nextLine().trim();
        System.out.print("Soyad: ");
        String soyad = scanner.nextLine().trim();

        // --- 1. Doğum Tarihi Girişi ---
        LocalDate dogumTarihi = null;
        while (dogumTarihi == null) {
            System.out.print("Doğum Tarihi (gg.AA.YYYY): ");
            String tarihGirdi = scanner.nextLine().trim();
            try {
                dogumTarihi = LocalDate.parse(tarihGirdi, com.takim.util.Formatlayici.TARİH_FORMATI);
                if (dogumTarihi.isAfter(LocalDate.now())) {
                    System.err.println(Formatlayici.renklendir("HATA: Doğum tarihi ileri bir tarih olamaz.", Formatlayici.KIRMİZİ));
                    dogumTarihi = null;
                }
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println(Formatlayici.renklendir("HATA: Hatalı tarih formatı.", Formatlayici.KIRMİZİ));
            }
        }

        // --- 2. Maaş ve İşe Başlama Tarihi ---
        double maas = 0;
        while (maas <= 0) {
            System.out.print("Maaş (TL): ");
            try {
                maas = scanner.nextDouble();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Maaş sayısal olmalıdır.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            }
        }

        LocalDate iseBaslamaTarihi = null;
        while (iseBaslamaTarihi == null) {
            System.out.print("İşe Başlama Tarihi (gg.AA.YYYY): ");
            String tarihGirdi = scanner.nextLine().trim();
            try {
                iseBaslamaTarihi = LocalDate.parse(tarihGirdi, com.takim.util.Formatlayici.TARİH_FORMATI);
                if (iseBaslamaTarihi.isAfter(LocalDate.now())) {
                    System.err.println(Formatlayici.renklendir("HATA: İşe başlama tarihi ileri bir tarih olamaz.", Formatlayici.KIRMİZİ));
                    iseBaslamaTarihi = null;
                }
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println(Formatlayici.renklendir("HATA: Hatalı tarih formatı.", Formatlayici.KIRMİZİ));
            }
        }

        // --- 3. Özel TD Alanları ---
        int lisansYili = 0;
        while (lisansYili < 1900 || lisansYili > LocalDate.now().getYear()) {
            System.out.print("Lisans Yılı (YYYY): ");
            try {
                lisansYili = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Lisans Yılı sayısal olmalıdır.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            }
        }

        System.out.print("Taktik (örn: 4-3-3): ");
        String taktik = scanner.nextLine().trim();

        double bonusHedefi = 0;
        while (bonusHedefi <= 0) {
            System.out.print("Bonus Hedefi (TL): ");
            try {
                bonusHedefi = scanner.nextDouble();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Bonus Hedefi sayısal olmalıdır.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            }
        }

        System.out.print("Eski Takım: ");
        String eskiTakim = scanner.nextLine().trim();

        double puanOrt = 0;
        while (puanOrt <= 0 || puanOrt > 3.0) {
            System.out.print("Puan Ortalaması (0.0 - 3.0): ");
            try {
                puanOrt = scanner.nextDouble();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Puan Ortalaması sayısal olmalıdır.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            }
        }

        int kupaSayisi = -1;
        while (kupaSayisi < 0) {
            System.out.print("Kupa Sayısı: ");
            try {
                kupaSayisi = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Kupa Sayısı sayısal olmalıdır.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            }
        }


        try {
            // TC Kimlik No atama (TRD1, TRD2...)
            String tcKimlikNo = "TRD" + tdSayaci++;

            TeknikDirektor td = new TeknikDirektor(ad, soyad, dogumTarihi, tcKimlikNo,
                    maas, iseBaslamaTarihi,
                    lisansYili, taktik, bonusHedefi,
                    eskiTakim, puanOrt, kupaSayisi);

            // Personel listesine ekle
            service.getKisiListesi().add(td);

            System.out.println(Formatlayici.renklendir(td.toString(), Formatlayici.YESIL));
            System.out.println(Formatlayici.renklendir("Teknik direktor (" + tcKimlikNo + ") basariyla eklendi.", Formatlayici.YESIL));

        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Teknik direktor eklenemedi: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }


    private static void yardimciAntrenorEklemeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- YARDIMCI ANTRENÖR EKLEME ---", Formatlayici.YESIL));

        System.out.print("Ad: ");
        String ad = scanner.nextLine().trim();
        System.out.print("Soyad: ");
        String soyad = scanner.nextLine().trim();

        // Doğum Tarihi Girişi
        LocalDate dogumTarihi = null;
        while (dogumTarihi == null) {
            System.out.print("Doğum Tarihi (gg.AA.YYYY): ");
            String tarihGirdi = scanner.nextLine().trim();
            try {
                dogumTarihi = LocalDate.parse(tarihGirdi, com.takim.util.Formatlayici.TARİH_FORMATI);
                if (dogumTarihi.isAfter(LocalDate.now())) {
                    System.err.println(Formatlayici.renklendir("HATA: Doğum tarihi ileri bir tarih olamaz.", Formatlayici.KIRMİZİ));
                    dogumTarihi = null;
                }
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println(Formatlayici.renklendir("HATA: Hatalı tarih formatı.", Formatlayici.KIRMİZİ));
            }
        }

        double maas = 0;
        while (maas <= 0) {
            System.out.print("Maaş (TL): ");
            try {
                maas = scanner.nextDouble();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Maaş sayısal olmalıdır.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            }
        }

        LocalDate iseBaslamaTarihi = null;
        while (iseBaslamaTarihi == null) {
            System.out.print("İşe Başlama Tarihi (gg.AA.YYYY): ");
            String tarihGirdi = scanner.nextLine().trim();
            try {
                iseBaslamaTarihi = LocalDate.parse(tarihGirdi, com.takim.util.Formatlayici.TARİH_FORMATI);
                if (iseBaslamaTarihi.isAfter(LocalDate.now())) {
                    System.err.println(Formatlayici.renklendir("HATA: İşe başlama tarihi ileri bir tarih olamaz.", Formatlayici.KIRMİZİ));
                    iseBaslamaTarihi = null;
                }
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println(Formatlayici.renklendir("HATA: Hatalı tarih formatı.", Formatlayici.KIRMİZİ));
            }
        }

        System.out.print("Uzmanlık Alanı: ");
        String uzmanlikAlani = scanner.nextLine().trim();

        double sahaIciSure = 0;
        while (sahaIciSure <= 0) {
            System.out.print("Saha İçi Süre (Saat): ");
            try {
                sahaIciSure = scanner.nextDouble();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Saha İçi Süre sayısal olmalıdır.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            }
        }

        // Puanlar
        System.out.println("Lütfen Antrenör Puanlarını Girin (1-20 aralığında):");
        int hucumPuani = 0;
        int defansPuani = 0;
        int taktikPuani = 0;
        int teknikPuani = 0;
        int disiplinPuani = 0;
        int uyumlulukPuani = 0;

        try {
            System.out.print("Hücum Puanı: "); hucumPuani = scanner.nextInt(); scanner.nextLine();
            System.out.print("Defans Puanı: "); defansPuani = scanner.nextInt(); scanner.nextLine();
            System.out.print("Taktik Puanı: "); taktikPuani = scanner.nextInt(); scanner.nextLine();
            System.out.print("Teknik Puanı: "); teknikPuani = scanner.nextInt(); scanner.nextLine();
            System.out.print("Disiplin Puanı: "); disiplinPuani = scanner.nextInt(); scanner.nextLine();
            System.out.print("Uyumluluk Puanı: "); uyumlulukPuani = scanner.nextInt(); scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println(Formatlayici.renklendir("HATA: Puanlar sayısal olmalıdır. Ekleme iptal ediliyor.", Formatlayici.KIRMİZİ));
            scanner.nextLine();
            return;
        }


        try {
            // Yeni ID oluştur ve ata (YANT001, YANT002...)
            String tcKimlikNo = service.generateYardimciAntrenorId();

            YardimciAntrenor ya = new YardimciAntrenor(ad, soyad, dogumTarihi, tcKimlikNo,
                    maas, iseBaslamaTarihi,
                    uzmanlikAlani, sahaIciSure,
                    hucumPuani, defansPuani, taktikPuani,
                    teknikPuani, disiplinPuani, uyumlulukPuani);

            service.getKisiListesi().add(ya);

            System.out.println(Formatlayici.renklendir(ya.toString(), Formatlayici.YESIL));
            System.out.println(Formatlayici.renklendir("Yardimci Antrenor (" + tcKimlikNo + ") basariyla eklendi.", Formatlayici.YESIL));

        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Yardimci Antrenor eklenemedi: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }


    private static void fizyoterapistEklemeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- FİZYOTERAPİST EKLEME ---", Formatlayici.YESIL));

        System.out.print("Ad: ");
        String ad = scanner.nextLine().trim();
        System.out.print("Soyad: ");
        String soyad = scanner.nextLine().trim();

        // Doğum Tarihi Girişi
        LocalDate dogumTarihi = null;
        while (dogumTarihi == null) {
            System.out.print("Doğum Tarihi (gg.AA.YYYY): ");
            String tarihGirdi = scanner.nextLine().trim();
            try {
                dogumTarihi = LocalDate.parse(tarihGirdi, com.takim.util.Formatlayici.TARİH_FORMATI);
                if (dogumTarihi.isAfter(LocalDate.now())) {
                    System.err.println(Formatlayici.renklendir("HATA: Doğum tarihi ileri bir tarih olamaz.", Formatlayici.KIRMİZİ));
                    dogumTarihi = null;
                }
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println(Formatlayici.renklendir("HATA: Hatalı tarih formatı.", Formatlayici.KIRMİZİ));
            }
        }

        double maas = 0;
        while (maas <= 0) {
            System.out.print("Maaş (TL): ");
            try {
                maas = scanner.nextDouble();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println(Formatlayici.renklendir("HATA: Maaş sayısal olmalıdır.", Formatlayici.KIRMİZİ));
                scanner.nextLine();
            }
        }

        LocalDate iseBaslamaTarihi = null;
        while (iseBaslamaTarihi == null) {
            System.out.print("İşe Başlama Tarihi (gg.AA.YYYY): ");
            String tarihGirdi = scanner.nextLine().trim();
            try {
                iseBaslamaTarihi = LocalDate.parse(tarihGirdi, com.takim.util.Formatlayici.TARİH_FORMATI);
                if (iseBaslamaTarihi.isAfter(LocalDate.now())) {
                    System.err.println(Formatlayici.renklendir("HATA: İşe başlama tarihi ileri bir tarih olamaz.", Formatlayici.KIRMİZİ));
                    iseBaslamaTarihi = null;
                }
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println(Formatlayici.renklendir("HATA: Hatalı tarih formatı.", Formatlayici.KIRMİZİ));
            }
        }

        System.out.print("Sertifika No: ");
        String sertifikaNo = scanner.nextLine().trim();
        System.out.print("Uzmanlık Alanı: ");
        String uzmanlikAlani = scanner.nextLine().trim();
        System.out.print("Spor Masaj Yetkisi Var mı? (true/false): ");
        boolean sporMasajYetkisi = scanner.nextBoolean();
        scanner.nextLine();

        // Puanlar
        System.out.println("Lütfen Fizyoterapist Puanlarını Girin (1-20 aralığında):");
        int fizyoterapiPuani = 0;
        int sporBilimiPuani = 0;
        int uyumlulukPuani = 0;
        int disiplinPuani = 0;

        try {
            System.out.print("Fizyoterapi Puanı: "); fizyoterapiPuani = scanner.nextInt(); scanner.nextLine();
            System.out.print("Spor Bilimi Puanı: "); sporBilimiPuani = scanner.nextInt(); scanner.nextLine();
            System.out.print("Uyumluluk Puanı: "); uyumlulukPuani = scanner.nextInt(); scanner.nextLine();
            System.out.print("Disiplin Puanı: "); disiplinPuani = scanner.nextInt(); scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println(Formatlayici.renklendir("HATA: Puanlar sayısal olmalıdır. Ekleme iptal ediliyor.", Formatlayici.KIRMİZİ));
            scanner.nextLine();
            return;
        }


        try {
            // Yeni ID oluştur ve ata (FİZY001, FİZY002...)
            String tcKimlikNo = service.generateFizyoterapistId();

            Fizyoterapist fizyo = new Fizyoterapist(ad, soyad, dogumTarihi, tcKimlikNo,
                    maas, iseBaslamaTarihi,
                    sertifikaNo, uzmanlikAlani, sporMasajYetkisi,
                    fizyoterapiPuani, sporBilimiPuani, uyumlulukPuani, disiplinPuani);

            service.getKisiListesi().add(fizyo);

            System.out.println(Formatlayici.renklendir(fizyo.toString(), Formatlayici.YESIL));
            System.out.println(Formatlayici.renklendir("Fizyoterapist (" + tcKimlikNo + ") basariyla eklendi.", Formatlayici.YESIL));

        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Fizyoterapist eklenemedi: " + e.getMessage(), Formatlayici.KIRMİZİ));
        }
    }

    // ------------------- PERSONEL SİLME METODU (YENİ) -------------------

    private static void personelSilmeEkrani() {
        System.out.println(Formatlayici.renklendir("\n--- PERSONEL SİLME ---", Formatlayici.KIRMİZİ));

        // 1. Personel Tipi Seçimi
        System.out.println(Formatlayici.renklendir("Silinecek personel tipini seçin:", Formatlayici.MAVI));
        System.out.println("1. Futbolcu (Forma No ile sil)");
        System.out.println("2. Teknik Direktör (Tek kişiyi sil)");
        System.out.println("3. Yardımcı Antrenör (YANTxxx/TRAxxx ID ile sil)");
        System.out.println("4. Fizyoterapist (FİZYxxx/TRFxxx ID ile sil)");
        System.out.print("Seçim (1-4): ");

        // Giriş kontrolü
        int secim;
        try {
            secim = scanner.nextInt();
            scanner.nextLine(); // Satır sonunu temizle
        } catch (InputMismatchException e) {
            System.err.println(Formatlayici.renklendir("HATA: Lütfen sadece sayı girin. Menüye dönülüyor.", Formatlayici.KIRMİZİ));
            scanner.nextLine();
            return;
        }

        Kisi silinecekKisi = null;

        try {
            switch (secim) {
                case 1: // Futbolcu: Forma No ile silme
                    System.out.print("Silinecek Futbolcunun Forma No'su (1-99): ");
                    if (!scanner.hasNextInt()) {
                        System.err.println(Formatlayici.renklendir("HATA: Forma numarası sayı olmalıdır.", Formatlayici.KIRMİZİ));
                        scanner.nextLine();
                        return;
                    }
                    int formaNo = scanner.nextInt();
                    scanner.nextLine();
                    silinecekKisi = service.futbolcuyuBul(formaNo);
                    break;

                case 2: // Teknik Direktör: Tek kişiyi silme
                    silinecekKisi = service.getTeknikDirektor();
                    if (silinecekKisi == null) {
                        System.err.println(Formatlayici.renklendir("HATA: Takımda kayıtlı Teknik Direktör bulunamadı.", Formatlayici.KIRMİZİ));
                        return;
                    }
                    break;

                case 3: // Yardımcı Antrenör: ID ile silme
                    System.out.print("Silinecek Yardımcı Antrenör ID'si (örn: YANT001 veya TRA99): ");
                    String idYA = scanner.nextLine().trim().toUpperCase();
                    silinecekKisi = service.yardimciAntrenoruBul(idYA);
                    break;

                case 4: // Fizyoterapist: ID ile silme
                    System.out.print("Silinecek Fizyoterapist ID'si (örn: FİZY001 veya TRF99): ");
                    String idFizyo = scanner.nextLine().trim().toUpperCase();
                    silinecekKisi = service.fizyoterapistBul(idFizyo);
                    break;

                default:
                    System.err.println(Formatlayici.renklendir("HATA: Geçersiz personel tipi seçimi.", Formatlayici.KIRMİZİ));
                    return;
            }

            // 2. Silme Onayı ve İşlemi
            if (silinecekKisi == null) {
                System.err.println(Formatlayici.renklendir("HATA: Belirtilen kriterlere uygun personel bulunamadı.", Formatlayici.KIRMİZİ));
                return;
            }

            // Onay mesajı için dinamik kimlik bilgisi oluşturma
            String kimlikBilgisi;
            if (secim == 1) {
                kimlikBilgisi = "Forma No: " + ((Futbolcu)silinecekKisi).getFormaNo();
            } else if (silinecekKisi.getTcKimlikNo() != null) {
                kimlikBilgisi = "ID: " + silinecekKisi.getTcKimlikNo();
            } else {
                kimlikBilgisi = "Kimlik Bilgisi Yok";
            }

            String onayMesaji = silinecekKisi.getAd() + " " + silinecekKisi.getSoyad() + " (" + kimlikBilgisi + ")";

            System.out.print(Formatlayici.renklendir(onayMesaji + " isimli personeli silmek istediğinizden emin misiniz? (E/H): ", Formatlayici.MAVI));

            String onay = scanner.nextLine().trim().toUpperCase();
            if (onay.equals("E")) {
                boolean basarili = service.kisiSil(silinecekKisi); // Yeni merkezi silme metodunu çağırır

                if (basarili) {
                    System.out.println(Formatlayici.renklendir(silinecekKisi.getAd() + " " + silinecekKisi.getSoyad() + " başarıyla kadrodan çıkarıldı.", Formatlayici.YESIL));
                    // Silme sonrası veriyi dosyaya kaydet
                    DosyaIslemleri.personelVerileriniKaydet(service.getKisiListesi());
                } else {
                    System.err.println(Formatlayici.renklendir("HATA: Silme işlemi teknik bir hata nedeniyle başarısız oldu.", Formatlayici.KIRMİZİ));
                }

            } else {
                System.out.println(Formatlayici.renklendir("Silme işlemi iptal edildi.", Formatlayici.MAVI));
            }


        } catch (Exception e) {
            System.err.println(Formatlayici.renklendir("Beklenmedik hata: " + e.getMessage(), Formatlayici.KIRMİZİ));
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