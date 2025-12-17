package com.takim.app;

import com.takim.service.TakimService;
import com.takim.model.*;
import com.takim.util.Formatlayici;
import com.takim.exception.GecersizFormaNoException;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Projenin ana GUI sınıfı.
 * Bölüm 11 (Menü Tasarımı) ve Bölüm 12 (JavaFX GUI Bonus) gereksinimlerini karşılar.
 */
public class MainGUI extends Application {

    public static final String TAKIM_ADI = "GALATASARAY SPOR KULÜBÜ";
    private final TakimService service = new TakimService();
    private TextArea messageArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(TAKIM_ADI + " - Yonetim Paneli");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(15);

        Label title = new Label(TAKIM_ADI + " KADRO VE PERFORMANS YONETİMİ");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        GridPane.setConstraints(title, 0, 0, 2, 1);

        messageArea.setEditable(false);
        messageArea.setPrefHeight(300);
        messageArea.setWrapText(true);
        messageArea.setStyle("-fx-font-family: 'Monospaced';");
        GridPane.setConstraints(messageArea, 0, 5, 2, 2);

        // 1. Personel Ekleme Butonu
        Button addPersonnelButton = new Button("1. Yeni Personel Ekle (Seçim)");
        addPersonnelButton.setMaxWidth(Double.MAX_VALUE);
        addPersonnelButton.setOnAction(e -> handleNewPersonnelSelection());
        GridPane.setConstraints(addPersonnelButton, 0, 1);

        // 2. Listeleme Butonu
        Button listPlayersButton = new Button("2. Çalışan Listesini Görüntüle");
        listPlayersButton.setMaxWidth(Double.MAX_VALUE);
        listPlayersButton.setOnAction(e -> handleDisplayPersonnelSelection());
        GridPane.setConstraints(listPlayersButton, 1, 1);

        // 3. Sıralama Butonu
        Button sortGoalsButton = new Button("3. Skor Katkısı Sıralaması");
        sortGoalsButton.setMaxWidth(Double.MAX_VALUE);
        sortGoalsButton.setOnAction(e -> handleSkorKatkisiSiralamasi());
        GridPane.setConstraints(sortGoalsButton, 0, 2);

        // 4. Silme Butonu
        Button deletePlayerButton = new Button("4. Personel Sil");
        deletePlayerButton.setMaxWidth(Double.MAX_VALUE);
        deletePlayerButton.setOnAction(e -> personelSilmeEkraniGoster());
        GridPane.setConstraints(deletePlayerButton, 1, 2);

        // 5. Haftalık Program Butonu (Döngü ve Dizi Gereksinimi)
        Button weeklyProgramButton = new Button("5. Haftalık Antrenman Programı");
        weeklyProgramButton.setMaxWidth(Double.MAX_VALUE);
        weeklyProgramButton.setOnAction(e -> handleWeeklyProgram());
        GridPane.setConstraints(weeklyProgramButton, 0, 3);

        // 6. Performans Güncelleme Butonu (Forma No ile)
        Button updatePerformanceButton = new Button("6. Performans Verisi Gir (Gol/Asist)");
        updatePerformanceButton.setMaxWidth(Double.MAX_VALUE);
        updatePerformanceButton.setOnAction(e -> handleUpdatePerformance());
        GridPane.setConstraints(updatePerformanceButton, 1, 3);

        primaryStage.setOnCloseRequest(event -> {
            service.tumVerileriKaydet();
            System.out.println("Uygulama kapatılıyor. Veriler kaydedildi.");
        });

        grid.getChildren().addAll(title, addPersonnelButton, listPlayersButton,
                sortGoalsButton, deletePlayerButton, weeklyProgramButton,
                updatePerformanceButton, messageArea);

        Scene scene = new Scene(grid, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- İŞLEYİCİ METOTLAR (HANDLERS) ---

    private void handleWeeklyProgram() {
        service.sistemBütünlükKontrolü(); // while/do-while çalıştırır
        String program = service.haftalikProgramiGoster(); // Çok boyutlu dizi/for çalıştırır
        double ortalama = service.antrenmanPuanOrtalamasiHesapla();

        messageArea.setText(cleanAnsi(program));
        messageArea.appendText("\n" + cleanAnsi(Formatlayici.renklendir(
                "Sistem Analizi: Haftalık Performans Ortalaması: " + String.format("%.2f", ortalama), Formatlayici.YESIL)));
    }

    private void handleUpdatePerformance() {
        // 1. Adım: Forma No Girişi
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Performans Güncelleme");
        dialog.setHeaderText("Futbolcu Seçimi");
        dialog.setContentText("Lütfen Futbolcunun Forma Numarasını Girin:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(formaNoStr -> {
            try {
                int formaNo = Integer.parseInt(formaNoStr.trim());

                // 2. Adım: Gol Sayısı Girişi (Başlık ve Header güncellendi)
                TextInputDialog golDialog = new TextInputDialog("0");
                golDialog.setTitle("Gol Verisi"); // Pencere başlığı
                golDialog.setHeaderText("Forma No: " + formaNo + " - Gol Sayısı"); // Üst açıklama
                golDialog.setContentText("Bu hafta kaç gol attı?");

                Optional<String> golRes = golDialog.showAndWait();
                int gol = Integer.parseInt(golRes.orElse("0"));

                // 3. Adım: Asist Sayısı Girişi (Başlık ve Header güncellendi)
                TextInputDialog asistDialog = new TextInputDialog("0");
                asistDialog.setTitle("Asist Verisi"); // Pencere başlığı
                asistDialog.setHeaderText("Forma No: " + formaNo + " - Asist Sayısı"); // Üst açıklama
                asistDialog.setContentText("Bu hafta kaç asist yaptı?");

                Optional<String> asistRes = asistDialog.showAndWait();
                int asist = Integer.parseInt(asistRes.orElse("0"));

                // Servis katmanında işlemi gerçekleştir
                if (service.performansVerisiGir(formaNo, gol, asist)) {
                    showMessage("Forma No " + formaNo + " başarıyla güncellendi.", Formatlayici.YESIL);
                } else {
                    gosterHataAlert("Hata", "Bu forma numarasına sahip futbolcu bulunamadı.");
                }
            } catch (NumberFormatException e) {
                gosterHataAlert("Giriş Hatası", "Lütfen sadece sayısal değerler giriniz.");
            }
        });
    }

    private void handleNewPersonnelSelection() {
        List<String> choices = Arrays.asList("1. Futbolcu", "2. Teknik Direktör", "3. Yardımcı Antrenör", "4. Fizyoterapist");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.showAndWait().ifPresent(res -> {
            if (res.contains("1")) handleAddNewFutbolcu();
            else if (res.contains("2")) handleAddNewTeknikDirektor();
            else if (res.contains("3")) handleAddNewYardimciAntrenor();
            else if (res.contains("4")) handleAddNewFizyoterapist();
        });
    }

    private void handleDisplayPersonnelSelection() {
        List<String> choices = Arrays.asList("1. Futbolcu", "2. Teknik Direktör", "3. Yardımcı Antrenör", "4. Fizyoterapist");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.showAndWait().ifPresent(this::displayFilteredPersonnel);
    }

    private void displayFilteredPersonnel(String selection) {
        List<? extends Kisi> liste = new ArrayList<>();
        if (selection.contains("1")) liste = service.getFutbolcuKadrosu();
        else if (selection.contains("2")) liste = service.getTeknikDirektorler();
        else if (selection.contains("3")) liste = service.getYardimciAntrenorler();
        else if (selection.contains("4")) liste = service.getFizyoterapistler();

        messageArea.setText(cleanAnsi(service.listeYazdir(liste)));
    }

    // --- YARDIMCI GÖRSEL METOTLAR ---

    private void handleAddNewFutbolcu() {
        // 1. Dialog Penceresini Yapılandır
        Dialog<Futbolcu> dialog = new Dialog<>();
        dialog.setTitle("Yeni Futbolcu Kaydı");
        dialog.setHeaderText("Futbolcu bilgilerini giriniz.");

        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // 2. Giriş Alanlarını Tasarla
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Ad ve Soyad Örnek Yazıları (Prompt Text)
        TextField adField = new TextField();
        adField.setPromptText("Örn: Mauro");
        TextField soyadField = new TextField();
        soyadField.setPromptText("Örn: Icardi");

        // Doğum Tarihi ve Ülke Örneği
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1993, 2, 19));
        TextField ulkeField = new TextField();
        ulkeField.setPromptText("Örn: Arjantin");

        // Forma No (Blurlu İpucu)
        TextField fNoField = new TextField();
        fNoField.setPromptText("1-99 arası");

        // Mevki Seçim Kutusu (ComboBox)
        ComboBox<String> mevkiBox = new ComboBox<>();
        mevkiBox.getItems().addAll("Kaleci", "Defans", "Ortasaha", "Forvet");
        mevkiBox.setValue("Forvet"); // Varsayılan değer

        // Alanları Yerleştir
        grid.add(new Label("Ad:"), 0, 0);          grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);       grid.add(soyadField, 1, 1);
        grid.add(new Label("Doğum Tarihi:"), 0, 2); grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Ülke:"), 0, 3);        grid.add(ulkeField, 1, 3);
        grid.add(new Label("Forma No:"), 0, 4);    grid.add(fNoField, 1, 4);
        grid.add(new Label("Mevki:"), 0, 5);       grid.add(mevkiBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // 3. Veriyi Nesneye Dönüştür ve Hataları Yakala (Try-Catch Çözümü)
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int formaNo = Integer.parseInt(fNoField.getText().trim());

                    // Constructor parametrelerini eksiksiz göndererek Argument hatasını çözer
                    return new Futbolcu(
                            adField.getText().trim(),
                            soyadField.getText().trim(),
                            dogumTarihiPicker.getValue(),
                            "TC-" + System.currentTimeMillis() % 1000,
                            formaNo,
                            mevkiBox.getValue(),
                            0, 0,
                            ulkeField.getText().trim()
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Forma numarası sadece sayı olmalıdır!");
                } catch (com.takim.exception.GecersizFormaNoException e) {
                    // Görseldeki "Unhandled exception" hatasını gideren kritik blok
                    gosterHataAlert("Kural İhlali", e.getMessage());
                } catch (Exception e) {
                    gosterHataAlert("Hata", "Lütfen tüm alanları kontrol edin.");
                }
            }
            return null;
        });

        // 4. Sonucu Al ve Servise Kaydet
        dialog.showAndWait().ifPresent(futbolcu -> {
            try {
                service.futbolcuEkle(futbolcu);
                showMessage(futbolcu.getAd() + " " + futbolcu.getSoyad() + " kadroya eklendi.", Formatlayici.YESIL);
            } catch (Exception e) {
                gosterHataAlert("Ekleme Hatası", e.getMessage());
            }
        });
    }

    private void handleAddNewTeknikDirektor() {
        Dialog<TeknikDirektor> dialog = new Dialog<>();
        dialog.setTitle("Yeni Teknik Direktör Kaydı");
        dialog.setHeaderText("Teknik direktör bilgilerini manuel olarak giriniz.");

        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Manuel Giriş Alanları (ComboBox'lar TextField'a dönüştürüldü)
        TextField adField = new TextField(); adField.setPromptText("Örn: Okan");
        TextField soyadField = new TextField(); soyadField.setPromptText("Örn: Buruk");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1973, 10, 19));
        TextField ulkeField = new TextField(); ulkeField.setPromptText("Örn: Türkiye");
        TextField gorevSuresiField = new TextField(); gorevSuresiField.setPromptText("Örn: 2");

        // Lisans ve Taktik artık manuel girişli
        TextField lisansField = new TextField(); lisansField.setPromptText("Örn: UEFA Pro");
        TextField dizilisField = new TextField(); dizilisField.setPromptText("Örn: 4-2-3-1");

        TextField puanField = new TextField(); puanField.setPromptText("Örn: 2.15");
        TextField kupaField = new TextField(); kupaField.setPromptText("Örn: 5");

        grid.add(new Label("Ad:"), 0, 0);               grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);            grid.add(soyadField, 1, 1);
        grid.add(new Label("Doğum Tarihi:"), 0, 2);      grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Ülke:"), 0, 3);             grid.add(ulkeField, 1, 3);
        grid.add(new Label("Görev Süresi (Yıl):"), 0, 4); grid.add(gorevSuresiField, 1, 4);
        grid.add(new Label("Lisans (Manuel):"), 0, 5);  grid.add(lisansField, 1, 5);
        grid.add(new Label("Taktik (Manuel):"), 0, 6);  grid.add(dizilisField, 1, 6);
        grid.add(new Label("Maç Başı Puan:"), 0, 7);    grid.add(puanField, 1, 7);
        grid.add(new Label("Kupa Sayısı:"), 0, 8);      grid.add(kupaField, 1, 8);

        dialog.getDialogPane().setContent(grid);

        // Veri Dönüştürme ve Hata Kontrolü
        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    int gorevYili = Integer.parseInt(gorevSuresiField.getText().trim());
                    double ortPuan = Double.parseDouble(puanField.getText().trim());
                    int kupaSayisi = Integer.parseInt(kupaField.getText().trim());

                    // Beklenen 11 parametreyi tam göndererek Argument hatasını çözer
                    return new TeknikDirektor(
                            adField.getText().trim(),
                            soyadField.getText().trim(),
                            dogumTarihiPicker.getValue(),
                            "TD-" + System.currentTimeMillis() % 1000,
                            500000.0, // Varsayılan Maaş
                            LocalDate.now(),
                            gorevYili,
                            dizilisField.getText().trim(), // Manuel giriş
                            ortPuan,
                            lisansField.getText().trim(), // Manuel giriş
                            ortPuan,
                            kupaSayisi,
                            ulkeField.getText().trim(),
                            "Süper Lig",
                            5.0
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Yıl, Puan ve Kupa alanları sadece sayı olmalıdır!");
                    return null;
                }
            }
            return null;
        });

        // Try-Catch ve showMessage kullanımı
        dialog.showAndWait().ifPresent(td -> {
            try {
                service.teknikDirektorEkle(td);
                // Başarılı işlem bildirimi
                showMessage("BAŞARILI: Teknik Direktör " + td.getAd() + " " + td.getSoyad() + " başarıyla eklendi.", "YESIL");
            } catch (Exception e) {
                // Hata durumunda bildirim
                showMessage("HATA: Kayıt yapılamadı: " + e.getMessage(), "KIRMIZI");
                gosterHataAlert("Sistem Hatası", e.getMessage());
            }
        });
    }

    private void handleAddNewYardimciAntrenor() {
        Dialog<YardimciAntrenor> dialog = new Dialog<>();
        dialog.setTitle("Yeni Yardımcı Antrenör Kaydı");
        dialog.setHeaderText("Antrenör bilgilerini ve uzmanlık alanını giriniz.");

        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Giriş Alanları
        TextField adField = new TextField(); adField.setPromptText("Örn: Ayhan");
        TextField soyadField = new TextField(); soyadField.setPromptText("Örn: Akman");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1977, 2, 23));
        TextField uyrukField = new TextField(); uyrukField.setPromptText("Örn: Türkiye");
        TextField lisansField = new TextField(); lisansField.setPromptText("Örn: UEFA A");
        TextField sureField = new TextField(); sureField.setPromptText("Tecrübe (Yıl - Örn: 3.5)");

        // Görev/Uzmanlık Seçimi
        ComboBox<String> gorevBox = new ComboBox<>();
        gorevBox.getItems().addAll("Yardımcı Antrenör", "Kaleci Antrenörü", "Atletik Performans Antrenörü", "Maç Analisti", "Duran Top Antrenörü");
        gorevBox.setValue("Yardımcı Antrenör");

        grid.add(new Label("Ad:"), 0, 0);               grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);            grid.add(soyadField, 1, 1);
        grid.add(new Label("Doğum Tarihi:"), 0, 2);      grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Uyruk:"), 0, 3);             grid.add(uyrukField, 1, 3);
        grid.add(new Label("Lisans:"), 0, 4);           grid.add(lisansField, 1, 4);
        grid.add(new Label("Tecrübe (Yıl):"), 0, 5);     grid.add(sureField, 1, 5);
        grid.add(new Label("Görev Alanı:"), 0, 6);       grid.add(gorevBox, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    // Arka planda otomatik atananlar (TC, Maaş, Başlama)
                    String tcNo = "ANT-" + System.currentTimeMillis() % 10000;
                    double varsayilanMaas = 85000.0;
                    LocalDate bugun = LocalDate.now();
                    double tecrube = Double.parseDouble(sureField.getText().trim());

                    // SENİN SINIFINDAKİ SIRALAMA: (ad, soyad, dt, tc, maas, iseBaslama, uzmanlik, sahaSure, uyruk, lisans)
                    return new YardimciAntrenor(
                            adField.getText().trim(),        // 1. ad
                            soyadField.getText().trim(),     // 2. soyad
                            dogumTarihiPicker.getValue(),    // 3. dogumTarihi
                            tcNo,                            // 4. tcKimlikNo
                            varsayilanMaas,                  // 5. maas
                            bugun,                           // 6. iseBaslamaTarihi
                            gorevBox.getValue(),             // 7. uzmanlikAlani (Görev)
                            tecrube,                         // 8. sahaIciSure (double)
                            uyrukField.getText().trim(),     // 9. uyruk
                            lisansField.getText().trim()     // 10. antrenorlukLisansi
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Tecrübe süresi sayı olmalıdır!");
                    return null;
                }
            }
            return null;
        });

        // Unhandled Exception ve showMessage çözümü
        dialog.showAndWait().ifPresent(ant -> {
            try {
                service.yardimciAntrenorEkle(ant);
                showMessage("BAŞARILI: " + ant.getAd() + " antrenör kadrosuna eklendi.", "YESIL");
            } catch (Exception e) {
                // Görseldeki kırmızı hatayı yakalayan blok
                showMessage("HATA: Kayıt yapılamadı: " + e.getMessage(), "KIRMIZI");
                gosterHataAlert("Sistem Hatası", e.getMessage());
            }
        });
    }


    private void handleAddNewFizyoterapist() {
        Dialog<Fizyoterapist> dialog = new Dialog<>();
        dialog.setTitle("Yeni Fizyoterapist Kaydı");
        dialog.setHeaderText("Fizyoterapist akademik ve kariyer bilgilerini giriniz.");

        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Manuel Giriş Alanları (Kullanıcının dolduracağı kısımlar)
        TextField adField = new TextField(); adField.setPromptText("Örn: Yener");
        TextField soyadField = new TextField(); soyadField.setPromptText("Örn: İnce");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1980, 1, 1));
        TextField uyrukField = new TextField(); uyrukField.setPromptText("Örn: Türkiye");
        TextField uzmanlikField = new TextField(); uzmanlikField.setPromptText("Örn: Sporcu Sağlığı");
        TextField uniField = new TextField(); uniField.setPromptText("Mezun Olunan Üniversite");
        TextField gorevSuresiField = new TextField(); gorevSuresiField.setPromptText("Yıl (Örn: 5.5)");
        CheckBox masajYetkisiBox = new CheckBox("Spor Masaj Yetkisi Var mı?");
        masajYetkisiBox.setSelected(true);

        // Alanları Yerleştir (TC, Maaş ve İşe Başlama kaldırıldı)
        grid.add(new Label("Ad:"), 0, 0);               grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);            grid.add(soyadField, 1, 1);
        grid.add(new Label("Doğum Tarihi:"), 0, 2);      grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Uyruk:"), 0, 3);             grid.add(uyrukField, 1, 3);
        grid.add(new Label("Uzmanlık Alanı:"), 0, 4);    grid.add(uzmanlikField, 1, 4);
        grid.add(new Label("Üniversite:"), 0, 5);        grid.add(uniField, 1, 5);
        grid.add(new Label("Görev Süresi (Yıl):"), 0, 6); grid.add(gorevSuresiField, 1, 6);
        grid.add(masajYetkisiBox, 1, 7);

        dialog.getDialogPane().setContent(grid);

        // Veri Dönüştürme ve Arka Planda Otomatik Atama
        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    // Görselden kaldırılan alanlar için otomatik değerler
                    String otomatikTC = "FT-" + System.currentTimeMillis() % 10000;
                    double varsayilanMaas = 100000.0;
                    LocalDate bugun = LocalDate.now();
                    double gorevSuresi = Double.parseDouble(gorevSuresiField.getText().trim());

                    // 11 Parametreli Constructor'a tam uyumlu gönderim
                    return new Fizyoterapist(
                            adField.getText().trim(),            // 1
                            soyadField.getText().trim(),         // 2
                            dogumTarihiPicker.getValue(),        // 3
                            otomatikTC,                          // 4 (Otomatik)
                            varsayilanMaas,                      // 5 (Otomatik)
                            bugun,                               // 6 (Otomatik)
                            uzmanlikField.getText().trim(),      // 7
                            masajYetkisiBox.isSelected(),        // 8
                            uyrukField.getText().trim(),         // 9
                            uniField.getText().trim(),           // 10
                            gorevSuresi                          // 11
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Görev süresi sayı olmalıdır!");
                    return null;
                }
            }
            return null;
        });

        // Unhandled Exception hatasını çözen blok
        dialog.showAndWait().ifPresent(ft -> {
            try {
                service.fizyoterapistEkle(ft);
                showMessage("BAŞARILI: Fizyoterapist " + ft.getAd() + " eklendi.", "YESIL");
            } catch (Exception e) {
                showMessage("HATA: " + e.getMessage(), "KIRMIZI");
                gosterHataAlert("Ekleme Hatası", e.getMessage());
            }
        });
    }

    private void handleSkorKatkisiSiralamasi() {
        // 1. Yeni bir pencere (Stage) oluştur
        Stage reportStage = new Stage();
        reportStage.setTitle("Skor Katkısı Sıralaması (Top-Down)");

        // 2. Tablo yapısını kur
        TableView<Futbolcu> table = new TableView<>();

        TableColumn<Futbolcu, String> nameCol = new TableColumn<>("Ad Soyad");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getAd() + " " + data.getValue().getSoyad()));

        TableColumn<Futbolcu, Integer> golCol = new TableColumn<>("Gol");
        golCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("golSayisi"));

        TableColumn<Futbolcu, Integer> asistCol = new TableColumn<>("Asist");
        asistCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("asistSayisi"));

        TableColumn<Futbolcu, Integer> toplamCol = new TableColumn<>("Toplam Katkı");
        toplamCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(
                data.getValue().getGolSayisi() + data.getValue().getAsistSayisi()));

        // Tablo sütunlarını ekle
        table.getColumns().addAll(Arrays.asList(nameCol, golCol, asistCol, toplamCol));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 3. Verileri al ve sırala (Polimorfizm ve List kullanımı)
        List<Futbolcu> liste = new ArrayList<>(service.getFutbolcuKadrosu());

        // En yüksek skoru olan en üstte (Descending Order)
        liste.sort((f1, f2) -> {
            int skor1 = f1.getGolSayisi() + f1.getAsistSayisi();
            int skor2 = f2.getGolSayisi() + f2.getAsistSayisi();
            return Integer.compare(skor2, skor1); // f2 vs f1 yaparak büyükten küçüğe sıralar
        });

        table.getItems().addAll(liste);

        // 4. Arayüz düzeni
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        Label header = new Label("Futbolcu Performans Sıralaması");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        vbox.getChildren().addAll(header, table);

        Scene scene = new Scene(vbox, 500, 400);
        reportStage.setScene(scene);
        reportStage.show();

        // Ana ekrandaki messageArea'ya da kısa bir özet geç
        if (!liste.isEmpty()) {
            Futbolcu lider = liste.get(0);
            showMessage("SIRALAMA: Takımın skor lideri: " + lider.getAd() + " " + lider.getSoyad() +
                    " (" + (lider.getGolSayisi() + lider.getAsistSayisi()) + " katkı)", "YESIL");
        }
    }

    private void personelSilmeEkraniGoster() {
        List<String> secimler = Arrays.asList("Futbolcu (Forma No)", "Teknik Direktör", "Yardımcı Antrenör (ID)", "Fizyoterapist (ID)");
        ChoiceDialog<String> dialogSecim = new ChoiceDialog<>(secimler.get(0), secimler);
        dialogSecim.showAndWait().ifPresent(secilen -> {
            if (secilen.startsWith("Futbolcu")) futbolcuSilmeEkrani();
            else if (secilen.startsWith("Teknik")) teknikDirektorSilmeEkrani();
            else calisanSilmeEkrani(secilen);
        });
    }

    private void futbolcuSilmeEkrani() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Silinecek futbolcunun Forma No:");
        dialog.showAndWait().ifPresent(res -> {
            try {
                boolean silindi = service.futbolcuSil(Integer.parseInt(res.trim()));
                gosterSonucAlert(silindi, "İşlem Sonucu", "Başarıyla silindi.", "Forma No bulunamadı.");
            } catch (Exception e) { gosterHataAlert("Hata", "Geçersiz giriş."); }
        });
    }

    private void teknikDirektorSilmeEkrani() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Teknik direktörü silmek istiyor musunuz?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) service.teknikDirektorSil();
        });
    }

    private void calisanSilmeEkrani(String tip) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(tip + " ID girin:");
        dialog.showAndWait().ifPresent(id -> {
            boolean silindi = tip.contains("Yardımcı") ? service.yardimciAntrenorSil(id) : service.fizyoterapistSil(id);
            gosterSonucAlert(silindi, "İşlem Sonucu", "Başarıyla silindi.", "ID bulunamadı.");
        });
    }

    private String cleanAnsi(String text) { return text.replaceAll("\\[\\d+m", ""); }

    private void showMessage(String message, String color) {
        messageArea.appendText(cleanAnsi(message) + "\n");
    }

    private void gosterSonucAlert(boolean basarili, String baslik, String basariMesaj, String hataMesaj) {
        Alert alert = new Alert(basarili ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setContentText(basarili ? basariMesaj : hataMesaj);
        alert.showAndWait();
    }

    private void gosterHataAlert(String baslik, String mesaj) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}