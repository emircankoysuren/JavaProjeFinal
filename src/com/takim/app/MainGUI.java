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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
// YENİ EKLENEN IMPORTLAR
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class MainGUI extends Application {

    public static final String TAKIM_ADI = "GALATASARAY SPOR KULÜBÜ";
    private final TakimService service = new TakimService();
    private TextArea messageArea = new TextArea();
    private int dynamicStaffCounter = 1;

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
        messageArea.setStyle("-fx-font-family: 'Monospaced';"); // Düzgün hizalama için
        GridPane.setConstraints(messageArea, 0, 4, 2, 2);

        Button addPersonnelButton = new Button("1. Yeni Personel Ekle (Seçim)");
        addPersonnelButton.setMaxWidth(Double.MAX_VALUE);
        addPersonnelButton.setOnAction(e -> handleNewPersonnelSelection());
        GridPane.setConstraints(addPersonnelButton, 0, 1);

        Button listPlayersButton = new Button("2. Çalışan Listesini Görüntüle");
        listPlayersButton.setMaxWidth(Double.MAX_VALUE);
        listPlayersButton.setOnAction(e -> handleDisplayPersonnelSelection());
        GridPane.setConstraints(listPlayersButton, 1, 1);

        Button sortGoalsButton = new Button("3. Skor Katkısı Sıralaması (GUI'a Yaz)");
        sortGoalsButton.setMaxWidth(Double.MAX_VALUE);
        sortGoalsButton.setOnAction(e -> handleSkorKatkisiSiralamasi());
        GridPane.setConstraints(sortGoalsButton, 0, 2);

        // GÜNCELLENDİ: Buton metni ve aksiyon metodu değiştirildi
        Button deletePlayerButton = new Button("4. Personel Sil");
        deletePlayerButton.setMaxWidth(Double.MAX_VALUE);
        deletePlayerButton.setOnAction(e -> personelSilmeEkraniGoster());
        GridPane.setConstraints(deletePlayerButton, 1, 2);

        primaryStage.setOnCloseRequest(event -> {
            service.tumVerileriKaydet();
            System.out.println("Uygulama kapatılıyor.");
        });

        grid.getChildren().addAll(title, addPersonnelButton, listPlayersButton,
                sortGoalsButton, deletePlayerButton,
                messageArea);

        Scene scene = new Scene(grid, 750, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String cleanAnsi(String text) {
        return text.replaceAll("\\[\\d+m", "");
    }

    private Optional<String> promptInput(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    private void showMessage(String message, String color) {
        messageArea.appendText(cleanAnsi(message) + "\n");
        System.out.println(Formatlayici.renklendir(message, color));
    }

    private void handleNewPersonnelSelection() {
        List<String> choices = Arrays.asList("1. Futbolcu (Oyuncu)", "2. Teknik Direktör", "3. Yardımcı Antrenör", "4. Fizyoterapist");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Yeni Personel Ekleme");
        dialog.setHeaderText("Personel tipi seçin:");
        dialog.setContentText("Tip:");
        dialog.showAndWait().ifPresent(res -> {
            switch (res.charAt(0)) {
                case '1': handleAddNewFutbolcu(); break;
                case '2': handleAddNewTeknikDirektor(); break;
                case '3': handleAddNewYardimciAntrenor(); break;
                case '4': handleAddNewFizyoterapist(); break;
            }
        });
    }

    private void handleDisplayPersonnelSelection() {
        List<String> choices = Arrays.asList("1. Futbolcu (Oyuncu)", "2. Teknik Direktör", "3. Yardımcı Antrenör", "4. Fizyoterapist");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Çalışan Listesi");
        dialog.setHeaderText("Görüntülenecek ekip:");
        dialog.setContentText("Ekip:");
        dialog.showAndWait().ifPresent(this::displayFilteredPersonnel);
    }

    private void displayFilteredPersonnel(String selection) {
        String filterClass;
        // KRİTİK DÜZELTME: Liste başlatılıyor.
        List<? extends Kisi> liste = new ArrayList<>();

        switch (selection.charAt(0)) {
            case '1': filterClass = "Futbolcu"; liste = service.getFutbolcuKadrosu(); break;
            case '2': filterClass = "TeknikDirektor"; liste = service.getTeknikDirektorler(); break;
            case '3': filterClass = "YardimciAntrenor"; liste = service.getYardimciAntrenorler(); break;
            case '4': filterClass = "Fizyoterapist"; liste = service.getFizyoterapistler(); break;
            default: showMessage("Geçersiz seçim.", Formatlayici.KIRMİZİ); return;
        }

        String listeCiktisi = service.listeYazdir(liste);
        messageArea.setText(cleanAnsi(Formatlayici.renklendir("--- " + filterClass.toUpperCase() + " LİSTESİ ---\n", Formatlayici.MAVI)) + cleanAnsi(listeCiktisi));
    }

    private void handleAddNewFutbolcu() {
        // Dialog penceresi oluşturma
        Dialog<Futbolcu> dialog = new Dialog<>();
        dialog.setTitle("Futbolcu Ekle");
        dialog.setHeaderText("Lütfen yeni futbolcunun bilgilerini girin.");

        // OK ve İPTAL butonlarını ekleme
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Giriş alanları için GridPane düzeni oluşturma
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 1. Ad (Text Input)
        TextField adField = new TextField();
        adField.setPromptText("Ad");
        grid.add(new Label("Ad:"), 0, 0);
        grid.add(adField, 1, 0);

        // 2. Soyad (Text Input)
        TextField soyadField = new TextField();
        soyadField.setPromptText("Soyad");
        grid.add(new Label("Soyad:"), 0, 1);
        grid.add(soyadField, 1, 1);

        // 3. Forma No (Text Input)
        TextField formaNoField = new TextField();
        formaNoField.setPromptText("Forma No (1-99)");
        grid.add(new Label("Forma No:"), 0, 2);
        grid.add(formaNoField, 1, 2);

        // 4. Mevki (Text Input)
        TextField mevkiField = new TextField();
        mevkiField.setPromptText("FORVET, ORTA SAHA, vb.");
        grid.add(new Label("Mevki:"), 0, 3);
        grid.add(mevkiField, 1, 3);

        // 5. Doğum Tarihi (DatePicker)
        DatePicker dogumTarihiPicker = new DatePicker();
        dogumTarihiPicker.setValue(LocalDate.of(1995, 1, 1)); // Varsayılan değer
        grid.add(new Label("Doğum Tarihi:"), 0, 4);
        grid.add(dogumTarihiPicker, 1, 4);

        // 6. Ülke (Text Input) - YENİ EKLENDİ
        TextField ulkeField = new TextField();
        ulkeField.setPromptText("Örn: Türkiye");
        grid.add(new Label("Ülke:"), 0, 5);
        grid.add(ulkeField, 1, 5);


        dialog.getDialogPane().setContent(grid);

        // OK butonuna basıldığında sonucu işleme
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String ad = adField.getText().trim();
                    String soyad = soyadField.getText().trim();
                    int formaNo = Integer.parseInt(formaNoField.getText().trim());
                    String mevki = mevkiField.getText().trim().toUpperCase();
                    LocalDate dogumTarihi = dogumTarihiPicker.getValue();
                    String ulke = ulkeField.getText().trim(); // Ülke bilgisini al

                    // Basit alan kontrolü
                    if (ad.isEmpty() || soyad.isEmpty() || mevki.isEmpty() || dogumTarihi == null || ulke.isEmpty()) {
                        gosterHataAlert("Eksik Bilgi", "Lütfen tüm alanları doldurun.");
                        return null;
                    }

                    String tcKimlikNo = "TC_" + formaNo;

                    // Yeni Futbolcu nesnesini döndür (YENİ YAPICI METOT KULLANILDI)
                    return new Futbolcu(ad, soyad, dogumTarihi, tcKimlikNo, formaNo, mevki, 0, 0, ulke);

                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Forma Numarası sadece sayı olmalıdır.");
                    return null;
                } catch (GecersizFormaNoException e) {
                    gosterHataAlert("Kural Hatası", e.getMessage());
                    return null;
                } catch (Exception e) {
                    gosterHataAlert("Genel Hata", "Personel eklenirken beklenmedik bir hata oluştu: " + e.getMessage());
                    return null;
                }
            }
            return null; // İptal veya kapatma durumunda null döndür
        });

        // Diyaloğu göster ve sonucu al
        Optional<Futbolcu> sonuc = dialog.showAndWait();

        // Sonucu işleme
        sonuc.ifPresent(f -> {
            try {
                service.futbolcuEkle(f);
                showMessage(f.getAd() + " " + f.getSoyad() + " (" + f.getFormaNo() + ") başarıyla eklendi.", Formatlayici.YESIL);
            } catch (Exception e) {
                // Bu catch bloğu, service.futbolcuEkle'den gelen hataları yakalar
                gosterHataAlert("Ekleme Hatası", e.getMessage());
            }
        });
    }

    private void handleAddNewTeknikDirektor() {
        // Dialog penceresi oluşturma
        Dialog<TeknikDirektor> dialog = new Dialog<>();
        dialog.setTitle("Teknik Direktör Ekle");
        dialog.setHeaderText("Lütfen Teknik Direktörün tüm profil bilgilerini girin.");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Input Alanları - Tümü BOŞ BAŞLATILDI
        TextField adField = new TextField();
        adField.setPromptText("Örn: Okan");

        TextField soyadField = new TextField();
        soyadField.setPromptText("Örn: Buruk");

        DatePicker dogumTarihiPicker = new DatePicker();
        dogumTarihiPicker.setPromptText("Tarih Seçin");

        // TextField dogumYeriField = new TextField(); // KALDIRILDI
        // dogumYeriField.setPromptText("Örn: İstanbul"); // KALDIRILDI

        TextField uyrukField = new TextField();
        uyrukField.setPromptText("Örn: Türkiye");

        TextField lisansField = new TextField();
        lisansField.setPromptText("Örn: UEFA Pro Lisans");

        TextField gorevSuresiField = new TextField();
        gorevSuresiField.setPromptText("Örn: 1.5");

        TextField taktikField = new TextField();
        taktikField.setPromptText("Örn: 4-2-3-1");

        TextField puanOrtField = new TextField();
        puanOrtField.setPromptText("Örn: 2.05");

        TextField maasField = new TextField();
        maasField.setPromptText("Örn: 500000");

        // GİZLİ/Varsayılan Alanlar için yine de boş TextField kullanmak daha güvenli:
        // Bu değerler constructor çağrısında hala gereklidir.

        int row = 0;
        grid.add(new Label("Ad:"), 0, row); grid.add(adField, 1, row++);
        grid.add(new Label("Soyad:"), 0, row); grid.add(soyadField, 1, row++);
        grid.add(new Label("Doğum Tarihi:"), 0, row); grid.add(dogumTarihiPicker, 1, row++);
        // Doğum Yeri Satırı KALDIRILDI: grid.add(new Label("Doğum Yeri:"), 0, row); grid.add(dogumYeriField, 1, row++);
        grid.add(new Label("Uyruk/Ülke:"), 0, row); grid.add(uyrukField, 1, row++);
        grid.add(new Label("Antrenör Lisansı:"), 0, row); grid.add(lisansField, 1, row++);
        grid.add(new Label("Görev Süresi (Yıl):"), 0, row); grid.add(gorevSuresiField, 1, row++);
        grid.add(new Label("Tercih Edilen Taktik:"), 0, row); grid.add(taktikField, 1, row++);
        grid.add(new Label("Maç Başına Puan Ort.:"), 0, row); grid.add(puanOrtField, 1, row++);
        grid.add(new Label("Maaş (TL):"), 0, row); grid.add(maasField, 1, row++);

        dialog.getDialogPane().setContent(grid);

        // OK butonuna basıldığında sonucu işleme
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    // Metin ve Tarih bilgileri
                    String ad = adField.getText().trim();
                    String soyad = soyadField.getText().trim();
                    LocalDate dogumTarihi = dogumTarihiPicker.getValue();
                    String taktik = taktikField.getText().trim();
                    // String dogumYeri = dogumYeriField.getText().trim(); // KALDIRILDI
                    String uyruk = uyrukField.getText().trim();
                    String lisans = lisansField.getText().trim();

                    // Sayısal bilgileri parse etmeden önce boşluk kontrolü yapın
                    String puanOrtStr = puanOrtField.getText().trim();
                    String maasStr = maasField.getText().trim();
                    String gorevSuresiStr = gorevSuresiField.getText().trim();

                    // Basit Kontroller (Zorunlu alanlar)
                    if (ad.isEmpty() || soyad.isEmpty() || uyruk.isEmpty() || taktik.isEmpty() || dogumTarihi == null || maasStr.isEmpty() || puanOrtStr.isEmpty() || gorevSuresiStr.isEmpty()) {
                        gosterHataAlert("Eksik Bilgi", "Lütfen tüm zorunlu alanları doldurun.");
                        return null;
                    }

                    // Sayısal bilgiler (Parsing yapılır)
                    double puanOrt = Double.parseDouble(puanOrtStr);
                    double maas = Double.parseDouble(maasStr);
                    double gorevSuresi = Double.parseDouble(gorevSuresiStr);

                    // Sabit/Varsayılan Değerler (Constructor gerektirdiği için mantıklı bir değer atanmalı)
                    String tcKimlikNo = "TD_TC_" + ad.charAt(0);
                    LocalDate iseBaslamaTarihi = LocalDate.of(LocalDate.now().getYear() - (int)Math.ceil(gorevSuresi), 1, 1);
                    int lisansYili = LocalDate.now().getYear() - 20;
                    int kupaSayisi = 0;
                    String eskiTakim = "Bilinmiyor";
                    double bonusHedefi = maas * 0.1;

                    // Yeni TeknikDirektor nesnesini döndür (15 PARAMETRELİ YAPICI KULLANILDI)
                    return new TeknikDirektor(
                            ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi,
                            lisansYili, taktik, bonusHedefi, eskiTakim, puanOrt, kupaSayisi,
                            uyruk, lisans, gorevSuresi // dogumYeri parametresi çıkarıldı
                    );

                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Maaş, Puan Ortalaması ve Görev Süresi alanlarına sadece sayı giriniz.");
                    return null;
                } catch (Exception e) {
                    gosterHataAlert("Genel Hata", "Teknik Direktör eklenirken bir hata oluştu: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            return null; // İptal veya kapatma durumunda null döndür
        });

        // Diyaloğu göster ve sonucu al
        Optional<TeknikDirektor> sonuc = dialog.showAndWait();

        // Sonucu işleme
        sonuc.ifPresent(td -> {
            try {
                service.teknikDirektorEkle(td);
                showMessage("Teknik Direktör " + td.getAd() + " " + td.getSoyad() + " başarıyla eklendi.", Formatlayici.MAVI);
            } catch (Exception e) {
                gosterHataAlert("Ekleme Hatası", e.getMessage());
            }
        });
    }
    private void handleAddNewYardimciAntrenor() {
        // Dialog penceresi oluşturma
        Dialog<YardimciAntrenor> dialog = new Dialog<>();
        dialog.setTitle("Yardımcı Antrenör Ekle");
        dialog.setHeaderText("Lütfen Yardımcı Antrenörün profil bilgilerini girin.");

        // HATA DÜZELTİLDİ: ButtonType.BUTTON_CANCEL yerine ButtonType.CANCEL kullanıldı
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Input Alanları
        TextField adField = new TextField();
        adField.setPromptText("Örn: İrfan");

        TextField soyadField = new TextField();
        soyadField.setPromptText("Örn: Saraloğlu");

        DatePicker dogumTarihiPicker = new DatePicker();
        dogumTarihiPicker.setPromptText("Tarih Seçin");

        TextField uyrukField = new TextField();
        uyrukField.setPromptText("Örn: Türkiye");

        TextField lisansField = new TextField();
        lisansField.setPromptText("Örn: UEFA B Lisans");

        TextField gorevSuresiField = new TextField();
        gorevSuresiField.setPromptText("Örn: 2.5");

        // Görev/Uzmanlık Seçimi (ChoiceBox)
        ChoiceBox<String> gorevAlaniChoice = new ChoiceBox<>();
        gorevAlaniChoice.getItems().addAll(
                "Yardımcı Antrenör",
                "Kaleci Antrenörü",
                "Atletik Performans Antrenörü",
                "Maç Analisti",
                "Duran Top Antrenörü"
        );
        gorevAlaniChoice.setValue("Yardımcı Antrenör"); // Varsayılan değer

        TextField maasField = new TextField();
        maasField.setPromptText("Örn: 200000");

        int row = 0;
        grid.add(new Label("Ad:"), 0, row); grid.add(adField, 1, row++);
        grid.add(new Label("Soyad:"), 0, row); grid.add(soyadField, 1, row++);
        grid.add(new Label("Doğum Tarihi:"), 0, row); grid.add(dogumTarihiPicker, 1, row++);
        grid.add(new Label("Uyruk/Ülke:"), 0, row); grid.add(uyrukField, 1, row++);
        grid.add(new Label("Antrenör Lisansı:"), 0, row); grid.add(lisansField, 1, row++);
        grid.add(new Label("Görev Süresi (Yıl):"), 0, row); grid.add(gorevSuresiField, 1, row++);
        grid.add(new Label("Görev:"), 0, row); grid.add(gorevAlaniChoice, 1, row++);
        grid.add(new Label("Maaş (TL):"), 0, row); grid.add(maasField, 1, row++);

        dialog.getDialogPane().setContent(grid);

        // OK butonuna basıldığında sonucu işleme
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    // Metin ve Tarih bilgileri
                    String ad = adField.getText().trim();
                    String soyad = soyadField.getText().trim();
                    LocalDate dogumTarihi = dogumTarihiPicker.getValue();
                    String uyruk = uyrukField.getText().trim();
                    String lisans = lisansField.getText().trim();
                    String gorevAlani = gorevAlaniChoice.getValue();

                    String gorevSuresiStr = gorevSuresiField.getText().trim();
                    String maasStr = maasField.getText().trim();

                    // Basit Kontroller (Zorunlu alanlar)
                    if (ad.isEmpty() || soyad.isEmpty() || uyruk.isEmpty() || gorevAlani == null || dogumTarihi == null || maasStr.isEmpty() || gorevSuresiStr.isEmpty()) {
                        gosterHataAlert("Eksik Bilgi", "Lütfen tüm zorunlu alanları doldurun.");
                        return null;
                    }

                    // Sayısal bilgiler (Parsing yapılır)
                    double maas = Double.parseDouble(maasStr);
                    double gorevSuresi = Double.parseDouble(gorevSuresiStr);

                    // Sabit/Varsayılan Değerler
                    String tcKimlikNo = "YA_TC_" + ad.charAt(0);
                    LocalDate iseBaslamaTarihi = LocalDate.of(LocalDate.now().getYear() - (int)Math.ceil(gorevSuresi), 1, 1);

                    // Performans Puanları (Basitleştirmek için default 15 atandı)
                    int defaultPuan = 15;

                    // Yeni YardimciAntrenor nesnesini döndür (16 PARAMETRELİ YAPICI KULLANILDI)
                    return new YardimciAntrenor(
                            ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi,
                            gorevAlani, gorevSuresi, uyruk, lisans,
                            defaultPuan, defaultPuan, defaultPuan, defaultPuan, defaultPuan, defaultPuan
                    );

                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Maaş ve Görev Süresi alanlarına sadece sayı giriniz.");
                    return null;
                } catch (Exception e) {
                    gosterHataAlert("Genel Hata", "Yardımcı Antrenör eklenirken bir hata oluştu: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            return null; // İptal veya kapatma durumunda null döndür
        });

        // Diyaloğu göster ve sonucu al
        Optional<YardimciAntrenor> sonuc = dialog.showAndWait();

        // Sonucu işleme
        sonuc.ifPresent(ya -> {
            try {
                service.yardimciAntrenorEkle(ya);
                showMessage("Yardımcı Antrenör " + ya.getAd() + " " + ya.getSoyad() + " başarıyla eklendi.", Formatlayici.MAVI);
            } catch (Exception e) {
                gosterHataAlert("Ekleme Hatası", e.getMessage());
            }
        });
    }


    // MainGUI.java içindeki metot:
    private void handleAddNewFizyoterapist() {
        // Dialog penceresi oluşturma
        Dialog<Fizyoterapist> dialog = new Dialog<>();
        dialog.setTitle("Fizyoterapist Ekle");
        dialog.setHeaderText("Lütfen Fizyoterapistin profil bilgilerini girin.");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Input Alanları
        TextField adField = new TextField();
        adField.setPromptText("Ad");

        TextField soyadField = new TextField();
        soyadField.setPromptText("Soyad");

        DatePicker dogumTarihiPicker = new DatePicker();
        dogumTarihiPicker.setPromptText("Tarih Seçin");

        TextField uyrukField = new TextField();
        uyrukField.setPromptText("Örn: Türkiye");

        TextField universiteField = new TextField();
        universiteField.setPromptText("Örn: Marmara Üniversitesi");

        TextField gorevSuresiField = new TextField();
        gorevSuresiField.setPromptText("Örn: 4.0");

        // TextField sertifikaField = new TextField(); // KALDIRILDI
        // sertifikaField.setPromptText("Örn: SERT_001"); // KALDIRILDI

        TextField uzmanlikField = new TextField();
        uzmanlikField.setPromptText("Örn: Spor Ortopedisi");

        CheckBox masajYetkisiCheck = new CheckBox("Spor Masaj Yetkisi Var");

        TextField maasField = new TextField();
        maasField.setPromptText("Örn: 150000");

        int row = 0;
        grid.add(new Label("Ad:"), 0, row); grid.add(adField, 1, row++);
        grid.add(new Label("Soyad:"), 0, row); grid.add(soyadField, 1, row++);
        grid.add(new Label("Doğum Tarihi:"), 0, row); grid.add(dogumTarihiPicker, 1, row++);
        grid.add(new Label("Uyruk/Ülke:"), 0, row); grid.add(uyrukField, 1, row++);
        grid.add(new Label("Mezuniyet Üni.:"), 0, row); grid.add(universiteField, 1, row++);
        grid.add(new Label("Görev Süresi (Yıl):"), 0, row); grid.add(gorevSuresiField, 1, row++);
        grid.add(new Label("Uzmanlık Alanı:"), 0, row); grid.add(uzmanlikField, 1, row++);
        // Sertifika Satırı KALDIRILDI
        grid.add(new Label("Maaş (TL):"), 0, row); grid.add(maasField, 1, row++);
        grid.add(masajYetkisiCheck, 1, row++);

        dialog.getDialogPane().setContent(grid);

        // OK butonuna basıldığında sonucu işleme
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    // Metin ve Tarih bilgileri
                    String ad = adField.getText().trim();
                    String soyad = soyadField.getText().trim();
                    LocalDate dogumTarihi = dogumTarihiPicker.getValue();
                    String uyruk = uyrukField.getText().trim();
                    String universite = universiteField.getText().trim();
                    // String sertifika = ""; // KALDIRILDI
                    String uzmanlik = uzmanlikField.getText().trim();
                    boolean masajYetkisi = masajYetkisiCheck.isSelected();

                    String gorevSuresiStr = gorevSuresiField.getText().trim();
                    String maasStr = maasField.getText().trim();

                    // Basit Kontroller (Zorunlu alanlar)
                    if (ad.isEmpty() || soyad.isEmpty() || uyruk.isEmpty() || dogumTarihi == null || universite.isEmpty() || gorevSuresiStr.isEmpty() || maasStr.isEmpty()) {
                        gosterHataAlert("Eksik Bilgi", "Lütfen tüm zorunlu alanları doldurun.");
                        return null;
                    }

                    // Sayısal bilgiler (Parsing yapılır)
                    double maas = Double.parseDouble(maasStr);
                    double gorevSuresi = Double.parseDouble(gorevSuresiStr);

                    // Sabit/Varsayılan Değerler (Constructor gerektirdiği için)
                    String tcKimlikNo = "FIZYO_TC_" + ad.charAt(0);
                    LocalDate iseBaslamaTarihi = LocalDate.of(LocalDate.now().getYear() - (int)Math.ceil(gorevSuresi), 1, 1);

                    // Performans Puanları (Basitleştirmek için default 15 atandı)
                    int defaultPuan = 15;

                    // Sertifika No parametresi artık yok, sabit değer atandı
                    String varsayilanSertifikaNo = "YOK";

                    // Yeni Fizyoterapist nesnesini döndür (YENİ 15 PARAMETRELİ YAPICI KULLANILDI)
                    return new Fizyoterapist(
                            ad, soyad, dogumTarihi, tcKimlikNo, maas, iseBaslamaTarihi,
                            uzmanlik, masajYetkisi, // sertifikaNo çıkarıldı
                            uyruk, universite, gorevSuresi,
                            defaultPuan, defaultPuan, defaultPuan, defaultPuan
                    );

                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Maaş ve Görev Süresi alanlarına sadece sayı giriniz.");
                    return null;
                } catch (Exception e) {
                    gosterHataAlert("Genel Hata", "Fizyoterapist eklenirken bir hata oluştu: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            return null; // İptal veya kapatma durumunda null döndür
        });

        // Diyaloğu göster ve sonucu al
        Optional<Fizyoterapist> sonuc = dialog.showAndWait();

        // Sonucu işleme
        sonuc.ifPresent(f -> {
            try {
                service.fizyoterapistEkle(f);
                showMessage("Fizyoterapist " + f.getAd() + " " + f.getSoyad() + " başarıyla eklendi (ID: " + f.getId() + ").", Formatlayici.MAVI);
            } catch (Exception e) {
                gosterHataAlert("Ekleme Hatası", e.getMessage());
            }
        });
    }

    private void handleSkorKatkisiSiralamasi() {
        messageArea.setText(cleanAnsi(service.skorKatkisiRaporuGetir()));
    }

    // YENİ SİLME AKIŞI METOTLARI

    /**
     * Personel silme işleminin ilk adımı: Hangi tip personelin silineceğini seçtirir.
     */
    private void personelSilmeEkraniGoster() {
        List<String> secimler = Arrays.asList(
                "Futbolcu (Forma No)",
                "Teknik Direktör (Tek Kişi)",
                "Yardımcı Antrenör (YARDXXX ID)",
                "Fizyoterapist (FİZYXXX ID)"
        );

        ChoiceDialog<String> dialogSecim = new ChoiceDialog<>(secimler.get(0), secimler);
        dialogSecim.setTitle("Personel Silme");
        dialogSecim.setHeaderText("Lütfen silmek istediğiniz personel tipini seçin.");
        dialogSecim.setContentText("Personel Tipi:");

        Optional<String> sonucSecim = dialogSecim.showAndWait();

        if (sonucSecim.isPresent()) {
            String secilenTip = sonucSecim.get();

            switch (secilenTip) {
                case "Futbolcu (Forma No)":
                    futbolcuSilmeEkrani();
                    break;
                case "Teknik Direktör (Tek Kişi)":
                    teknikDirektorSilmeEkrani();
                    break;
                case "Yardımcı Antrenör (YARDXXX ID)":
                    calisanSilmeEkrani("Yardımcı Antrenör", "YARDXXX ID");
                    break;
                case "Fizyoterapist (FİZYXXX ID)":
                    calisanSilmeEkrani("Fizyoterapist", "FİZYXXX ID");
                    break;
            }
        }
    }

    // Forma No ile Futbolcu Silme
    private void futbolcuSilmeEkrani() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Futbolcu Sil");
        dialog.setHeaderText("Silinecek futbolcunun Forma Numarasını girin.");
        dialog.setContentText("Forma No:");

        Optional<String> sonuc = dialog.showAndWait();
        if (sonuc.isPresent() && !sonuc.get().trim().isEmpty()) {
            try {
                int formaNo = Integer.parseInt(sonuc.get().trim());
                boolean silindi = service.futbolcuSil(formaNo); // Service çağrısı
                gosterSonucAlert(silindi,
                        "Futbolcu Silme Sonucu",
                        "Futbolcu (" + formaNo + ") başarıyla silindi.",
                        "HATA: Bu forma numarasına sahip futbolcu bulunamadı.");
            } catch (NumberFormatException e) {
                gosterHataAlert("Giriş Hatası", "Forma numarası sadece rakamlardan oluşmalıdır.");
            }
        }
    }

    // Teknik Direktör Silme
    private void teknikDirektorSilmeEkrani() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Teknik Direktör Silme Onayı");
        alert.setHeaderText("Teknik Direktör silinecektir.");
        alert.setContentText("Bu işlemi onaylıyor musunuz?");

        Optional<ButtonType> sonuc = alert.showAndWait();
        if (sonuc.isPresent() && sonuc.get() == ButtonType.OK) {
            boolean silindi = service.teknikDirektorSil(); // Service çağrısı
            gosterSonucAlert(silindi,
                    "Teknik Direktör Silme Sonucu",
                    "Teknik Direktör başarıyla silindi.",
                    "HATA: Teknik Direktör bulunamadı veya kadro zaten boş.");
        }
    }

    // YARDXXX/FİZYXXX ID ile Çalışan Silme
    private void calisanSilmeEkrani(String tip, String idFormat) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(tip + " Sil");
        dialog.setHeaderText("Silinecek " + tip + "'in " + idFormat + " kodunu girin.");
        dialog.setContentText(idFormat + " Kodu:");

        Optional<String> sonuc = dialog.showAndWait();
        if (sonuc.isPresent() && !sonuc.get().trim().isEmpty()) {
            String id = sonuc.get().trim().toUpperCase();
            boolean silindi = false;

            if (tip.contains("Yardımcı")) {
                silindi = service.yardimciAntrenorSil(id); // Service çağrısı
            } else if (tip.contains("Fizyoterapist")) {
                silindi = service.fizyoterapistSil(id); // Service çağrısı
            }

            gosterSonucAlert(silindi,
                    tip + " Silme Sonucu",
                    tip + " (" + id + ") başarıyla silindi.",
                    "HATA: Bu ID koduna (" + id + ") sahip " + tip + " bulunamadı.");
        }
    }

    // Genel Sonuç ve Hata Alert metotları (Kullanıcıya geri bildirim için)
    private void gosterSonucAlert(boolean basarili, String baslik, String basariMesaj, String hataMesaj) {
        Alert alert = new Alert(basarili ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(basarili ? basariMesaj : hataMesaj);
        alert.showAndWait();
    }

    private void gosterHataAlert(String baslik, String mesaj) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}