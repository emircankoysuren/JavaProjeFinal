package com.takim.app;

import com.takim.service.TakimService;
import com.takim.model.Futbolcu;
import com.takim.model.TeknikDirektor;
import com.takim.model.YardimciAntrenor;
import com.takim.model.Fizyoterapist;
import com.takim.model.Kisi;
import com.takim.util.Formatlayici;
import com.takim.util.DosyaIslemleri;
import com.takim.exception.GecersizFormaNoException;

// JavaFX Gerekli İçe Aktarmalar
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

// Ek İçe Aktarmalar
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX GUI Uygulamasının Ana Sınıfı.
 * Rapor kaydetme kaldırıldı, butonlar yeniden numaralandırıldı ve Skor Katkısı sıralaması eklendi.
 */
public class MainGUI extends Application {

    public static final String TAKIM_ADI = "GALATASARAY SPOR KULÜBÜ";

    private final TakimService service = new TakimService();

    private TextArea messageArea = new TextArea();

    private int dynamicPlayerCounter = 1;
    private int dynamicStaffCounter = 1;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(TAKIM_ADI + " - Yonetim Paneli");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(15);

        // --- 1. KOMPONENT: Baslik ---
        Label title = new Label(TAKIM_ADI + " KADRO VE PERFORMANS YONETİMİ");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        GridPane.setConstraints(title, 0, 0, 2, 1);

        // --- 2. Mesaj Alanı ---
        messageArea.setEditable(false);
        messageArea.setPrefHeight(300);
        messageArea.setWrapText(true);
        GridPane.setConstraints(messageArea, 0, 4, 2, 2);

        // --- BUTONLAR ---

        // Satır 1
        Button addPersonnelButton = new Button("1. Yeni Personel Ekle (Seçim)");
        addPersonnelButton.setMaxWidth(Double.MAX_VALUE);
        addPersonnelButton.setOnAction(e -> handleNewPersonnelSelection());
        GridPane.setConstraints(addPersonnelButton, 0, 1);

        Button listPlayersButton = new Button("2. Personel Listesini Görüntüle");
        listPlayersButton.setMaxWidth(Double.MAX_VALUE);
        listPlayersButton.setOnAction(e -> handleListPlayers());
        GridPane.setConstraints(listPlayersButton, 1, 1);

        // Satır 2
        // YENİ İSİM VE NUMARA
        Button sortGoalsButton = new Button("3. Skor Katkısı Sıralaması (Konsola Yaz)");
        sortGoalsButton.setMaxWidth(Double.MAX_VALUE);
        sortGoalsButton.setOnAction(e -> handleSkorKatkisiSiralamasi()); // Yeni metot çağrısı
        GridPane.setConstraints(sortGoalsButton, 0, 2);

        Button deletePlayerButton = new Button("4. Personel Sil (Ad/Soyad)");
        deletePlayerButton.setMaxWidth(Double.MAX_VALUE);
        deletePlayerButton.setOnAction(e -> handleDeletePlayer());
        GridPane.setConstraints(deletePlayerButton, 1, 2);

        // Satır 3
        Button saveDataButton = new Button("5. Futbolcu Verilerini Kaydet");
        saveDataButton.setMaxWidth(Double.MAX_VALUE);
        saveDataButton.setOnAction(e -> handleSaveData());
        GridPane.setConstraints(saveDataButton, 0, 3, 2, 1);


        // --- Uygulama Kapanırken Kaydetme ---
        primaryStage.setOnCloseRequest(event -> {
            handleSaveData();
            System.out.println("Uygulama kapatiliyor. Veriler kaydedildi.");
        });

        // --- Tum Ogeleri Ekleme ---
        grid.getChildren().addAll(title, addPersonnelButton, listPlayersButton,
                sortGoalsButton, deletePlayerButton,
                saveDataButton,
                messageArea);

        Scene scene = new Scene(grid, 750, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ------------------- YARDIMCI METOTLAR -------------------

    private Optional<String> promptInput(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    private void showMessage(String message, String color) {
        messageArea.appendText(message + "\n");
        System.out.println(Formatlayici.renklendir(message, color));
    }

    private void handleNewPersonnelSelection() {
        List<String> choices = Arrays.asList(
                "1. Futbolcu (Oyuncu)",
                "2. Teknik Direktör",
                "3. Yardımcı Antrenör",
                "4. Fizyoterapist"
        );

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Yeni Personel Ekleme");
        dialog.setHeaderText("Lütfen eklemek istediğiniz personel tipini seçin.");
        dialog.setContentText("Tip:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            switch (result.get().charAt(0)) {
                case '1':
                    handleAddNewFutbolcu();
                    break;
                case '2':
                    handleAddNewTeknikDirektor();
                    break;
                case '3':
                    handleAddNewYardimciAntrenor();
                    break;
                case '4':
                    handleAddNewFizyoterapist();
                    break;
                default:
                    showMessage("Hata: Geçersiz personel tipi seçimi.", Formatlayici.KIRMİZİ);
            }
        }
    }

    /**
     * Futbolcu ekleme metodu: Kullanıcıdan manuel veri alır.
     */
    private void handleAddNewFutbolcu() {
        // Ardışık veri girişi
        Optional<String> adOpt = promptInput("Futbolcu Ekle", "Temel Bilgiler", "Ad:", "Burak");
        Optional<String> soyadOpt = adOpt.flatMap(ad -> promptInput("Futbolcu Ekle", "Temel Bilgiler", "Soyad:", "Yılmaz"));

        Optional<String> formaNoOpt = soyadOpt.flatMap(soyad -> promptInput("Futbolcu Ekle", "Kadro Bilgileri", "Forma No (1-99):", "9"));
        Optional<String> mevkiOpt = formaNoOpt.flatMap(fn -> promptInput("Futbolcu Ekle", "Kadro Bilgileri", "Mevki (Örn: FORVET):", "FORVET"));

        Optional<String> golOpt = mevkiOpt.flatMap(m -> promptInput("Futbolcu Ekle", "Performans Verisi", "Gol Sayısı:", "0"));
        Optional<String> asistOpt = golOpt.flatMap(g -> promptInput("Futbolcu Ekle", "Performans Verisi", "Asist Sayısı:", "0"));

        if (asistOpt.isPresent()) {
            try {
                String ad = adOpt.get();
                String soyad = soyadOpt.get();
                String mevki = mevkiOpt.get().toUpperCase();

                int formaNo = Integer.parseInt(formaNoOpt.get());
                int golSayisi = Integer.parseInt(golOpt.get());
                int asistSayisi = Integer.parseInt(asistOpt.get());

                String tcKimlikNo = "MANUAL_" + formaNo;
                LocalDate dogumTarihi = LocalDate.of(1990, 1, 1);

                Futbolcu f = new Futbolcu(ad, soyad, dogumTarihi, tcKimlikNo,
                        formaNo, mevki, golSayisi, asistSayisi);

                service.futbolcuEkle(f);
                showMessage(f.getAd() + " " + f.getSoyad() + " ("+f.getFormaNo()+") kadroya manuel olarak eklendi.", Formatlayici.YESIL);

                handleSaveData();

            } catch (NumberFormatException e) {
                showMessage("HATA: Forma No, Gol veya Asist sayısal olmalıdır. Girişler kontrol edilsin.", Formatlayici.KIRMİZİ);
            } catch (GecersizFormaNoException e) {
                showMessage("HATA: Forma No 1-99 arasında olmalıdır: " + e.getMessage(), Formatlayici.KIRMİZİ);
            } catch (Exception e) {
                showMessage("Futbolcu ekleme basarisiz: " + e.getMessage(), Formatlayici.KIRMİZİ);
            }
        } else {
            showMessage("Futbolcu ekleme işlemi iptal edildi.", Formatlayici.MAVI);
        }
    }

    // ------------------- TEKNİK DİREKTÖR EKLEME -------------------

    private void handleAddNewTeknikDirektor() {
        Optional<String> adOpt = promptInput("Teknik Direktör Ekle", "Temel Bilgiler", "Ad:", "Okan");
        Optional<String> soyadOpt = adOpt.flatMap(ad -> promptInput("Teknik Direktör Ekle", "Temel Bilgiler", "Soyad:", "Buruk"));
        Optional<String> maasOpt = soyadOpt.flatMap(soyad -> promptInput("Teknik Direktör Ekle", "Temel Bilgiler", "Maaş:", "500000"));

        Optional<String> eskiTakimOpt = maasOpt.flatMap(maas -> promptInput("Teknik Direktör Özellikleri", "Özel Bilgiler", "Eski Takım:", "B.B. Erzurumspor"));
        Optional<String> puanOrtOpt = eskiTakimOpt.flatMap(eskiTakim -> promptInput("Teknik Direktör Özellikleri", "Özel Bilgiler", "Puan Ortalaması (örn: 1.5):", "1.5"));
        Optional<String> kupaSayisiOpt = puanOrtOpt.flatMap(puan -> promptInput("Teknik Direktör Özellikleri", "Özel Bilgiler", "Kupa Sayısı:", "5"));

        if (kupaSayisiOpt.isPresent()) {
            try {
                String ad = adOpt.get();
                String soyad = soyadOpt.get();
                double maas = Double.parseDouble(maasOpt.get());
                String tcNo = "TRD"+dynamicStaffCounter;

                String eskiTakim = eskiTakimOpt.get();
                double puanOrt = Double.parseDouble(puanOrtOpt.get());
                int kupaSayisi = Integer.parseInt(kupaSayisiOpt.get());

                TeknikDirektor td = new TeknikDirektor(ad, soyad, LocalDate.of(1973, 10, 19), tcNo,
                        maas, LocalDate.of(2022, 6, 1), 2005, "4-2-3-1", 100000,
                        eskiTakim, puanOrt, kupaSayisi);

                showMessage(td.getAd() + " " + td.getSoyad() + " (Teknik Direktör) eklendi. Konsola bakınız.", Formatlayici.MAVI);
                td.bilgiYazdir();
                dynamicStaffCounter++;

            } catch (NumberFormatException e) {
                showMessage("HATA: Maaş, Puan Ortalaması veya Kupa Sayısı sayısal olmalıdır. Girişler kontrol edilsin.", Formatlayici.KIRMİZİ);
            } catch (Exception e) {
                showMessage("Beklenmedik bir hata oluştu: " + e.getMessage(), Formatlayici.KIRMİZİ);
            }
        }
    }

    // ------------------- YARDIMCI ANTRENÖR EKLEME -------------------

    private void handleAddNewYardimciAntrenor() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Puanlar 1 ile 20 arasında girilmelidir.", ButtonType.OK);
        alert.setTitle("Puan Aralığı Uyarısı");
        alert.showAndWait();

        Optional<String> adOpt = promptInput("Yrd. Antrenör Ekle", "Temel Bilgiler", "Ad:", "Ismail");
        Optional<String> soyadOpt = adOpt.flatMap(ad -> promptInput("Yrd. Antrenör Ekle", "Temel Bilgiler", "Soyad:", "Şenol"));
        Optional<String> maasOpt = soyadOpt.flatMap(soyad -> promptInput("Yrd. Antrenör Ekle", "Temel Bilgiler", "Maaş:", "200000"));

        Optional<String> uzmanlikOpt = maasOpt.flatMap(m -> promptInput("Yrd. Antrenör Özellikleri", "Özel Bilgiler", "Uzmanlık Alanı:", "Hucum"));
        Optional<String> hucumOpt = uzmanlikOpt.flatMap(u -> promptInput("Yrd. Antrenör Puanları (1-20)", "Puan Girişi", "Hücum Puanı:", "15"));
        Optional<String> defansOpt = hucumOpt.flatMap(h -> promptInput("Yrd. Antrenör Puanları (1-20)", "Puan Girişi", "Defans Puanı:", "10"));
        Optional<String> taktikOpt = defansOpt.flatMap(d -> promptInput("Yrd. Antrenör Puanları (1-20)", "Puan Girişi", "Taktik Puanı:", "17"));
        Optional<String> teknikOpt = taktikOpt.flatMap(t -> promptInput("Yrd. Antrenör Puanları (1-20)", "Puan Girişi", "Teknik Puanı:", "16"));
        Optional<String> disiplinOpt = teknikOpt.flatMap(te -> promptInput("Yrd. Antrenör Puanları (1-20)", "Puan Girişi", "Disiplin Puanı:", "18"));
        Optional<String> uyumOpt = disiplinOpt.flatMap(di -> promptInput("Yrd. Antrenör Puanları (1-20)", "Puan Girişi", "Uyumluluk Puanı:", "19"));


        if (uyumOpt.isPresent()) {
            try {
                String ad = adOpt.get();
                String soyad = soyadOpt.get();
                double maas = Double.parseDouble(maasOpt.get());
                String tcNo = "TRA"+dynamicStaffCounter;

                String uzmanlik = uzmanlikOpt.get();
                int hucum = Integer.parseInt(hucumOpt.get());
                int defans = Integer.parseInt(defansOpt.get());
                int taktik = Integer.parseInt(taktikOpt.get());
                int teknik = Integer.parseInt(teknikOpt.get());
                int disiplin = Integer.parseInt(disiplinOpt.get());
                int uyum = Integer.parseInt(uyumOpt.get());

                YardimciAntrenor ya = new YardimciAntrenor(ad, soyad, LocalDate.of(1985, 3, 10), tcNo,
                        maas, LocalDate.of(2023, 7, 1), uzmanlik, 1000.5,
                        hucum, defans, taktik, teknik, disiplin, uyum);

                showMessage(ya.getAd() + " " + ya.getSoyad() + " (Yardimci Antrenor) eklendi.", Formatlayici.MAVI);
                ya.bilgiYazdir();

                dynamicStaffCounter++;

            } catch (NumberFormatException e) {
                showMessage("HATA: Maaş veya Puanlar sayısal olmalıdır. Girişler kontrol edilsin.", Formatlayici.KIRMİZİ);
            } catch (Exception e) {
                showMessage("Hata oluştu: " + e.getMessage(), Formatlayici.KIRMİZİ);
            }
        }
    }

    // ------------------- FİZYOTERAPİST EKLEME -------------------

    private void handleAddNewFizyoterapist() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Puanlar 1 ile 20 arasında girilmelidir.", ButtonType.OK);
        alert.setTitle("Puan Aralığı Uyarısı");
        alert.showAndWait();

        Optional<String> adOpt = promptInput("Fizyoterapist Ekle", "Temel Bilgiler", "Ad:", "Ali");
        Optional<String> soyadOpt = adOpt.flatMap(ad -> promptInput("Fizyoterapist Ekle", "Temel Bilgiler", "Soyad:", "Can"));
        Optional<String> maasOpt = soyadOpt.flatMap(soyad -> promptInput("Fizyoterapist Ekle", "Temel Bilgiler", "Maaş:", "150000"));

        Optional<String> fizyoPuanOpt = maasOpt.flatMap(m -> promptInput("Fizyoterapist Puanları (1-20)", "Puan Girişi", "Fizyoterapi Puanı:", "18"));
        Optional<String> sporBilimiOpt = fizyoPuanOpt.flatMap(f -> promptInput("Fizyoterapist Puanları (1-20)", "Puan Girişi", "Spor Bilimi Puanı:", "15"));
        Optional<String> uyumOpt = sporBilimiOpt.flatMap(s -> promptInput("Fizyoterapist Puanları (1-20)", "Puan Girişi", "Uyumluluk Puanı:", "19"));
        Optional<String> disiplinOpt = uyumOpt.flatMap(u -> promptInput("Fizyoterapist Puanları (1-20)", "Puan Girişi", "Disiplin Puanı:", "17"));


        if (disiplinOpt.isPresent()) {
            try {
                String ad = adOpt.get();
                String soyad = soyadOpt.get();
                double maas = Double.parseDouble(maasOpt.get());
                String tcNo = "TRF"+dynamicStaffCounter;
                String sertifika = "SERT_X"+dynamicStaffCounter;

                int fizyoPuan = Integer.parseInt(fizyoPuanOpt.get());
                int sporBilimi = Integer.parseInt(sporBilimiOpt.get());
                int uyum = Integer.parseInt(uyumOpt.get());
                int disiplin = Integer.parseInt(disiplinOpt.get());

                Fizyoterapist fizyo = new Fizyoterapist(ad, soyad, LocalDate.of(1988, 1, 1), tcNo,
                        maas, LocalDate.of(2021, 5, 15),
                        sertifika, "Ortopedi", true,
                        fizyoPuan, sporBilimi, uyum, disiplin);

                showMessage(fizyo.getAd() + " " + fizyo.getSoyad() + " (Fizyoterapist) eklendi.", Formatlayici.MAVI);
                fizyo.bilgiYazdir();

                dynamicStaffCounter++;
            } catch (NumberFormatException e) {
                showMessage("HATA: Maaş veya Puanlar sayısal olmalıdır. Girişler kontrol edilsin.", Formatlayici.KIRMİZİ);
            } catch (Exception e) {
                showMessage("Hata oluştu: " + e.getMessage(), Formatlayici.KIRMİZİ);
            }
        }
    }

    // ------------------- KALAN METOTLAR -------------------

    private void handleListPlayers() {
        showMessage("Kadro listesi konsola yazdırılıyor...", Formatlayici.MAVI);
        // Bu metot, Futbolcu listesini konsola yazdırır
        service.listeYazdir(service.getFutbolcuKadrosu());
    }

    /**
     * YENİ METOT: Skor Katkısı Sıralaması (Gol + Asist) yapar ve konsola yazar.
     */
    private void handleSkorKatkisiSiralamasi() {
        showMessage("Skor katkısı sıralaması yapılıyor...", Formatlayici.MAVI);

        // 1. Sıralama işlemini servise yaptır
        service.skorKatkisiSiralamasiYap();

        // 2. Sıralanmış listeyi konsola yazdır (İstenen formatta)
        System.out.println(Formatlayici.renklendir("\n--- SKOR KATKISI SIRALAMASI ---", Formatlayici.YESIL));
        for (Futbolcu f : service.getFutbolcuKadrosu()) {
            System.out.println(f.getSkorKatkisiDetay());
        }
        System.out.println(Formatlayici.renklendir("--- SIRALAMA SONU ---", Formatlayici.YESIL));
    }

    private void handleSaveData() {
        try {
            DosyaIslemleri.kadroVerileriniKaydet(service.getFutbolcuKadrosu());
        } catch (Exception ex) {
            showMessage("Veri Kaydedilemedi: " + ex.getMessage(), Formatlayici.KIRMİZİ);
        }
    }

    private void handleDeletePlayer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("4. Personel Sil");
        dialog.setHeaderText("Silinecek personelin Adını ve Soyadını girin (Örn: Mauro Icardi)");
        dialog.setContentText("Ad Soyad:");

        dialog.showAndWait().ifPresent(tamAd -> {
            try {
                String[] adSoyad = tamAd.trim().split("\\s+", 2);
                if (adSoyad.length != 2) {
                    showMessage("HATA: Lütfen tam adı (Ad Soyad) girin.", Formatlayici.KIRMİZİ);
                    return;
                }
                String ad = adSoyad[0];
                String soyad = adSoyad[1];

                boolean success = service.personelSil(ad, soyad);

                if (success) {
                    showMessage(ad + " " + soyad + " başarıyla kadrodan çıkarıldı.", Formatlayici.YESIL);
                    handleSaveData();
                } else {
                    showMessage("HATA: " + ad + " " + soyad + " kadroda bulunamadı.", Formatlayici.KIRMİZİ);
                }

            } catch (Exception e) {
                showMessage("Hata oluştu: " + e.getMessage(), Formatlayici.KIRMİZİ);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}