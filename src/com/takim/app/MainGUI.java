package com.takim.app;

import com.takim.service.TakimService;
import com.takim.model.*;
import com.takim.util.Renklendirici;
import com.takim.exception.GecersizFormaNoException;
import com.takim.exception.KapasiteDolduException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Galatasaray Spor Kulubu Yonetim Paneli Ana Uygulama Sinifi.
 * Bu sinif, kulup personelinin, performans verilerinin ve antrenman programlarinin
 * merkezi bir arayuz uzerinden yonetilmesini saglar.
 */
public class MainGUI extends Application {

    public static final String TAKIM_ADI = "GALATASARAY SPOR KULUBU";
    private static final Pattern ANSI_PATTERN = Pattern.compile("\\[\\d+m");

    private final TakimService service = new TakimService();
    private final TextArea messageArea = new TextArea();

    // Uygulamanın grafik arayüzünü ve temel düzenini yapılandırır.
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(TAKIM_ADI + " - Yonetim Paneli");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(15);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        Label title = new Label(TAKIM_ADI + " KADRO VE PERFORMANS YONETIMI");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        GridPane.setConstraints(title, 0, 0, 2, 1);
        GridPane.setHalignment(title, HPos.CENTER);

        Button addPersonnelButton = createStyledButton("1. Yeni Personel Ekle (Secim)");
        addPersonnelButton.setOnAction(e -> handleNewPersonnelSelection());
        GridPane.setConstraints(addPersonnelButton, 0, 1);

        Button listPlayersButton = createStyledButton("2. Calisan Listesini Goruntule");
        listPlayersButton.setOnAction(e -> handleDisplayPersonnelSelection());
        GridPane.setConstraints(listPlayersButton, 1, 1);

        Button sortGoalsButton = createStyledButton("3. Skor Katkisi Siralamasi");
        sortGoalsButton.setOnAction(e -> handleSkorKatkisiSiralamasi());
        GridPane.setConstraints(sortGoalsButton, 0, 2);

        Button deletePlayerButton = createStyledButton("4. Personel Sil");
        deletePlayerButton.setOnAction(e -> personelSilmeEkraniGoster());
        GridPane.setConstraints(deletePlayerButton, 1, 2);

        Button weeklyProgramButton = createStyledButton("5. Haftalik Antrenman Islemleri");
        weeklyProgramButton.setOnAction(e -> handleTrainingMenuSelection());
        GridPane.setConstraints(weeklyProgramButton, 0, 3);

        Button updatePerformanceButton = createStyledButton("6. Performans Verisi Gir (Gol/Asist)");
        updatePerformanceButton.setOnAction(e -> handleUpdatePerformance());
        GridPane.setConstraints(updatePerformanceButton, 1, 3);

        Button fixtureButton = createStyledButton("7. Fikstur Yonetimi");
        fixtureButton.setOnAction(e -> handleFixtureMenu());
        GridPane.setConstraints(fixtureButton, 0, 4);

        Button finansAnalizBtn = createStyledButton("8. Finansal Analiz ve Butce Raporu");
        finansAnalizBtn.setOnAction(e -> handleFinansalAnalizMenu());
        GridPane.setConstraints(finansAnalizBtn, 1, 4);

        messageArea.setEditable(false);
        messageArea.setPrefHeight(350);
        messageArea.setMaxWidth(700);
        messageArea.setWrapText(false);
        messageArea.setStyle("-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 13px; -fx-control-inner-background: #f4f4f4; -fx-border-color: #dcdcdc;");

        GridPane.setConstraints(messageArea, 0, 5, 2, 2);
        GridPane.setHalignment(messageArea, HPos.CENTER);

        grid.getChildren().addAll(
                title, addPersonnelButton, listPlayersButton,
                sortGoalsButton, deletePlayerButton, weeklyProgramButton,
                updatePerformanceButton, fixtureButton, finansAnalizBtn, messageArea
        );

        // Uygulama kapatılırken mevcut tüm verilerin güvenli bir şekilde kaydedilmesini sağlar.
        primaryStage.setOnCloseRequest(event -> {
            service.tumVerileriKaydet();
            System.out.println("Sistem kapatiliyor, veriler kaydedildi.");
        });

        Scene scene = new Scene(grid, 950, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Arayüz butonları için standart görsel stil oluşturur.
    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-font-size: 13px;");
        return btn;
    }

    // Personel kategorilerine göre kayıt ekranlarını yönlendirir.
    private void handleNewPersonnelSelection() {
        List<String> choices = Arrays.asList("1. Futbolcu", "2. Teknik Direktor", "3. Yardimci Antrenor", "4. Fizyoterapist");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.showAndWait().ifPresent(res -> {
            if (res.contains("1")) handleAddNewFutbolcu();
            else if (res.contains("2")) handleAddNewTeknikDirektor();
            else if (res.contains("3")) handleAddNewYardimciAntrenor();
            else if (res.contains("4")) handleAddNewFizyoterapist();
        });
    }

    // Yeni futbolcu kaydı için gerekli veri girişlerini alır ve kapasite kontrolü yapar.
    private void handleAddNewFutbolcu() {
        Dialog<Futbolcu> dialog = new Dialog<>();
        dialog.setTitle("Yeni Futbolcu Kaydi");
        dialog.setHeaderText("Futbolcu bilgilerini ve maasini giriniz.");
        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField adField = new TextField(); adField.setPromptText("Orn: Mauro");
        TextField soyadField = new TextField(); soyadField.setPromptText("Orn: Icardi");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1993, 2, 19));
        TextField ulkeField = new TextField(); ulkeField.setPromptText("Orn: Arjantin");
        TextField fNoField = new TextField(); fNoField.setPromptText("1-99 arasi");
        ComboBox<String> mevkiBox = new ComboBox<>();
        mevkiBox.getItems().addAll("Kaleci", "Defans", "Ortasaha", "Forvet");
        mevkiBox.setValue("Forvet");
        TextField maasField = new TextField(); maasField.setPromptText("Orn: 15 (Milyon EUR)");

        grid.add(new Label("Ad:"), 0, 0);               grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);            grid.add(soyadField, 1, 1);
        grid.add(new Label("Dogum Tarihi:"), 0, 2);     grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Ulke:"), 0, 3);             grid.add(ulkeField, 1, 3);
        grid.add(new Label("Forma No:"), 0, 4);         grid.add(fNoField, 1, 4);
        grid.add(new Label("Mevki:"), 0, 5);            grid.add(mevkiBox, 1, 5);
        grid.add(new Label("Maas (Milyon EUR):"), 0, 6); grid.add(maasField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int formaNo = Integer.parseInt(fNoField.getText().trim());
                    double maas = Double.parseDouble(maasField.getText().trim());
                    return new Futbolcu(
                            adField.getText().trim(), soyadField.getText().trim(), dogumTarihiPicker.getValue(),
                            "TC-" + System.currentTimeMillis() % 1000, formaNo, mevkiBox.getValue(),
                            0, 0, ulkeField.getText().trim(), maas
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giris Hatasi", "Forma numarasi ve Maas sadece sayi olmalidir!");
                } catch (Exception e) {
                    gosterHataAlert("Hata", "Lutfen tum alanlari kontrol edin.");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(futbolcu -> {
            try {
                service.futbolcuEkle(futbolcu);
                String formatliMaas = String.format(Locale.GERMANY, "%,.0f EUR", futbolcu.getMaas());
                showMessage(futbolcu.getAd() + " " + futbolcu.getSoyad() + " kadroya eklendi. (Maas: " + formatliMaas + ")", Renklendirici.YESIL);
            } catch (KapasiteDolduException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Kadro Dolu");
                alert.setHeaderText("Kapasite Siniri Asildi");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } catch (GecersizFormaNoException e) {
                gosterHataAlert("Hata", e.getMessage());
            } catch (Exception e) {
                gosterHataAlert("Ekleme Hatasi", "Bir sorun olustu: " + e.getMessage());
            }
        });
    }

    // Teknik direktor bilgilerini alir ve sisteme kaydeder.
    private void handleAddNewTeknikDirektor() {
        Dialog<TeknikDirektor> dialog = new Dialog<>();
        dialog.setTitle("Yeni Teknik Direktor Kaydi");
        dialog.setHeaderText("Teknik direktor bilgilerini manuel olarak giriniz.");
        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField adField = new TextField(); adField.setPromptText("Orn: Okan");
        TextField soyadField = new TextField(); soyadField.setPromptText("Orn: Buruk");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1973, 10, 19));
        TextField ulkeField = new TextField(); ulkeField.setPromptText("Orn: Turkiye");
        TextField gorevSuresiField = new TextField(); gorevSuresiField.setPromptText("Orn: 2");
        TextField lisansField = new TextField(); lisansField.setPromptText("Orn: UEFA Pro");
        TextField dizilisField = new TextField(); dizilisField.setPromptText("Orn: 4-2-3-1");
        TextField puanField = new TextField(); puanField.setPromptText("Orn: 2.15");
        TextField kupaField = new TextField(); kupaField.setPromptText("Orn: 5");

        grid.add(new Label("Ad:"), 0, 0);               grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);            grid.add(soyadField, 1, 1);
        grid.add(new Label("Dogum Tarihi:"), 0, 2);     grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Ulke:"), 0, 3);             grid.add(ulkeField, 1, 3);
        grid.add(new Label("Gorev Suresi (Yil):"), 0, 4); grid.add(gorevSuresiField, 1, 4);
        grid.add(new Label("Lisans (Manuel):"), 0, 5);  grid.add(lisansField, 1, 5);
        grid.add(new Label("Taktik (Manuel):"), 0, 6);  grid.add(dizilisField, 1, 6);
        grid.add(new Label("Mac Basi Puan:"), 0, 7);    grid.add(puanField, 1, 7);
        grid.add(new Label("Kupa Sayisi:"), 0, 8);      grid.add(kupaField, 1, 8);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    int gorevYili = Integer.parseInt(gorevSuresiField.getText().trim());
                    double ortPuan = Double.parseDouble(puanField.getText().trim());
                    int kupaSayisi = Integer.parseInt(kupaField.getText().trim());
                    return new TeknikDirektor(
                            adField.getText().trim(), soyadField.getText().trim(), dogumTarihiPicker.getValue(),
                            "TD-" + System.currentTimeMillis() % 1000, 500000.0, LocalDate.now(),
                            gorevYili, dizilisField.getText().trim(), ortPuan, lisansField.getText().trim(),
                            ortPuan, kupaSayisi, ulkeField.getText().trim(), "Super Lig", 5.0
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giris Hatasi", "Yil, Puan ve Kupa alanlari sadece sayi olmalidir!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(td -> {
            try {
                service.teknikDirektorEkle(td);
                showMessage("BASARILI: Teknik Direktor " + td.getAd() + " " + td.getSoyad() + " basariyla eklendi.", "YESIL");
            } catch (Exception e) {
                showMessage("HATA: " + e.getMessage(), "KIRMIZI");
            }
        });
    }

    // Yardimci antrenor kayit islemlerini yurutur.
    private void handleAddNewYardimciAntrenor() {
        Dialog<YardimciAntrenor> dialog = new Dialog<>();
        dialog.setTitle("Yeni Yardimci Antrenor Kaydi");
        dialog.setHeaderText("Antrenor bilgilerini ve uzmanlik alanini giriniz.");
        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField adField = new TextField(); adField.setPromptText("Orn: Ayhan");
        TextField soyadField = new TextField(); soyadField.setPromptText("Orn: Akman");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1977, 2, 23));
        TextField uyrukField = new TextField(); uyrukField.setPromptText("Orn: Turkiye");
        TextField lisansField = new TextField(); lisansField.setPromptText("Orn: UEFA A");
        TextField sureField = new TextField(); sureField.setPromptText("Tecrube (Yil - Orn: 3.5)");
        ComboBox<String> gorevBox = new ComboBox<>();
        gorevBox.getItems().addAll("Yardimci Antrenor", "Kaleci Antrenoru", "Atletik Performans Antrenoru", "Mac Analisti", "Duran Top Antrenoru");
        gorevBox.setValue("Yardimci Antrenor");
        TextField maasField = new TextField(); maasField.setPromptText("Maas (Orn: 50000)");

        grid.add(new Label("Ad:"), 0, 0);               grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);            grid.add(soyadField, 1, 1);
        grid.add(new Label("Dogum Tarihi:"), 0, 2);     grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Uyruk:"), 0, 3);            grid.add(uyrukField, 1, 3);
        grid.add(new Label("Lisans:"), 0, 4);           grid.add(lisansField, 1, 4);
        grid.add(new Label("Tecrube (Yil):"), 0, 5);    grid.add(sureField, 1, 5);
        grid.add(new Label("Gorev Alani:"), 0, 6);      grid.add(gorevBox, 1, 6);
        grid.add(new Label("Maas (EUR):"), 0, 7);       grid.add(maasField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    double girilenMaas = Double.parseDouble(maasField.getText().trim());
                    double tecrube = Double.parseDouble(sureField.getText().trim());
                    return new YardimciAntrenor(
                            adField.getText().trim(), soyadField.getText().trim(), dogumTarihiPicker.getValue(),
                            "ANT-" + System.currentTimeMillis() % 10000, girilenMaas, LocalDate.now(),
                            gorevBox.getValue(), tecrube, uyrukField.getText().trim(), lisansField.getText().trim()
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giris Hatasi", "Maas ve Tecrube sayi olmalidir!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ant -> {
            try { service.yardimciAntrenorEkle(ant); showMessage("BASARILI: " + ant.getAd() + " eklendi.", "YESIL");
            } catch (Exception e) { showMessage("HATA: " + e.getMessage(), "KIRMIZI"); }
        });
    }

    // Saglik ekibi personeli kayit surecini yurutur.
    private void handleAddNewFizyoterapist() {
        Dialog<Fizyoterapist> dialog = new Dialog<>();
        dialog.setTitle("Yeni Fizyoterapist Kaydi");
        dialog.setHeaderText("Fizyoterapist akademik ve kariyer bilgilerini giriniz.");
        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField adField = new TextField(); adField.setPromptText("Orn: Yener");
        TextField soyadField = new TextField(); soyadField.setPromptText("Orn: Ince");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1980, 1, 1));
        TextField uyrukField = new TextField(); uyrukField.setPromptText("Orn: Turkiye");
        TextField uzmanlikField = new TextField(); uzmanlikField.setPromptText("Orn: Sporcu Sagligi");
        TextField uniField = new TextField(); uniField.setPromptText("Mezun Olunan Universite");
        TextField gorevSuresiField = new TextField(); gorevSuresiField.setPromptText("Yil (Orn: 5.5)");
        TextField maasField = new TextField(); maasField.setPromptText("Maas (Orn: 100000)");
        CheckBox masajYetkisiBox = new CheckBox("Spor Masaj Yetkisi Var mi?");
        masajYetkisiBox.setSelected(true);

        grid.add(new Label("Ad:"), 0, 0);                 grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);              grid.add(soyadField, 1, 1);
        grid.add(new Label("Dogum Tarihi:"), 0, 2);       grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Uyruk:"), 0, 3);              grid.add(uyrukField, 1, 3);
        grid.add(new Label("Uzmanlik Alani:"), 0, 4);     grid.add(uzmanlikField, 1, 4);
        grid.add(new Label("Universite:"), 0, 5);         grid.add(uniField, 1, 5);
        grid.add(new Label("Gorev Suresi (Yil):"), 0, 6); grid.add(gorevSuresiField, 1, 6);
        grid.add(new Label("Maas (EUR):"), 0, 7);         grid.add(maasField, 1, 7);
        grid.add(masajYetkisiBox, 1, 8);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    String otomatikTC = "FT-" + System.currentTimeMillis() % 10000;
                    double gorevSuresi = Double.parseDouble(gorevSuresiField.getText().trim());
                    double girilenMaas = Double.parseDouble(maasField.getText().trim());
                    return new Fizyoterapist(
                            adField.getText().trim(), soyadField.getText().trim(), dogumTarihiPicker.getValue(),
                            otomatikTC, girilenMaas, LocalDate.now(), uzmanlikField.getText().trim(),
                            masajYetkisiBox.isSelected(), uyrukField.getText().trim(), uniField.getText().trim(), gorevSuresi
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giris Hatasi", "Gorev suresi ve maas sayi olmalidir!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ft -> {
            try { service.fizyoterapistEkle(ft); showMessage("BASARILI: " + ft.getAd() + " eklendi.", "YESIL");
            } catch (Exception e) { showMessage("HATA: " + e.getMessage(), "KIRMIZI"); }
        });
    }

    // Calisan listesi goruntuleme seceneklerini sunar.
    private void handleDisplayPersonnelSelection() {
        List<String> choices = Arrays.asList("1. Futbolcu", "2. Teknik Direktor", "3. Yardimci Antrenor", "4. Fizyoterapist");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.showAndWait().ifPresent(this::displayFilteredPersonnel);
    }

    // Secilen kategoriye gore mevcut personel verilerini raporlar.
    private void displayFilteredPersonnel(String selection) {
        StringBuilder sb = new StringBuilder();

        if (selection.contains("1")) {
            List<Futbolcu> liste = service.getFutbolcuKadrosu();
            sb.append("======================================================================\n");
            sb.append(String.format("%-30s\n", "                  FUTBOLCU KADRO LISTESI"));
            sb.append("======================================================================\n\n");

            if(liste.isEmpty()) {
                sb.append("Kayitli futbolcu bulunmamaktadir.\n");
            } else {
                Map<String, List<Futbolcu>> gruplanmisOyuncular = liste.stream()
                        .collect(Collectors.groupingBy(f -> f.getMevki().toUpperCase(Locale.ENGLISH)));

                listeleMevkiGrubuOptimize(sb, gruplanmisOyuncular.getOrDefault("KALECI", Collections.emptyList()), "KALECILER", "Kaleci");
                listeleMevkiGrubuOptimize(sb, gruplanmisOyuncular.getOrDefault("DEFANS", Collections.emptyList()), "DEFANSLAR", "Defans");
                listeleMevkiGrubuOptimize(sb, gruplanmisOyuncular.getOrDefault("ORTASAHA", Collections.emptyList()), "ORTA SAHALAR", "Ortasaha");
                listeleMevkiGrubuOptimize(sb, gruplanmisOyuncular.getOrDefault("FORVET", Collections.emptyList()), "FORVETLER", "Forvet");
            }
        } else if (selection.contains("2")) {
            List<TeknikDirektor> liste = service.getTeknikDirektorler();
            sb.append("======================================================================\n");
            sb.append("                  TEKNIK DIREKTOR KADROSU\n");
            sb.append("======================================================================\n\n");
            for(TeknikDirektor t : liste) {
                sb.append(String.format("> %s %s (%s)\n", t.getAd().toUpperCase(), t.getSoyad().toUpperCase(), t.getUyruk()));
                sb.append(String.format("  Performans : %s\n", t.getPerformansDetayi()));
                sb.append(String.format("  Durum      : %s (Puan: %.1f)\n", t.performansDurumuAnalizi(), t.performansPuaniniHesapla()));
                String formatliMaas = String.format(Locale.GERMANY, "%,.0f EUR", t.getMaas());
                sb.append(String.format("  Maas       : %s\n", formatliMaas));
                sb.append("----------------------------------------------------------------------\n");
            }
        } else {
            if (selection.contains("3")) {
                List<YardimciAntrenor> yardimciListe = service.getYardimciAntrenorler();
                sb.append("======================================================================\n");
                sb.append("                  YARDIMCI ANTRENOR KADROSU\n");
                sb.append("======================================================================\n\n");
                if (yardimciListe.isEmpty()) {
                    sb.append("Kayitli yardimci antrenor bulunamadi.\n");
                } else {
                    for (YardimciAntrenor y : yardimciListe) {
                        sb.append(String.format("> %s %s\n", y.getAd().toUpperCase(), y.getSoyad().toUpperCase()));
                        sb.append(String.format("  ID         : %s\n", (y.getId() != null ? y.getId() : "N/A")));
                        sb.append(String.format("  Gorev      : %s\n", y.getUzmanlikAlani()));
                        sb.append(String.format("  Lisans     : %s\n", y.getAntrenorlukLisansi()));
                        sb.append(String.format("  Tecrube    : %.1f Yil\n", y.getSahaIciSure()));
                        sb.append(String.format("  Maas       : %,.0f EUR\n", y.maasHesapla()));
                        sb.append("----------------------------------------------------------------------\n");
                    }
                }
            } else if (selection.contains("4")) {
                List<Fizyoterapist> fizyoListe = service.getFizyoterapistler();
                sb.append("======================================================================\n");
                sb.append("                  FIZYOTERAPIST KADROSU\n");
                sb.append("======================================================================\n\n");
                if (fizyoListe.isEmpty()) {
                    sb.append("Kayitli fizyoterapist bulunamadi.\n");
                } else {
                    for (Fizyoterapist f : fizyoListe) {
                        sb.append(String.format("> %s %s\n", f.getAd().toUpperCase(), f.getSoyad().toUpperCase()));
                        sb.append(String.format("  ID         : %s\n", (f.getId() != null ? f.getId() : "N/A")));
                        sb.append(String.format("  Uzmanlik   : %s\n", f.getUzmanlikAlani()));
                        sb.append(String.format("  Universite : %s\n", f.getMezuniyetUniversitesi()));
                        sb.append(String.format("  Tecrube    : %.1f Yil\n", f.getGorevSuresiYil()));
                        sb.append(String.format("  Masaj Yetk.: %s\n", (f.isSporMasajYetkisi() ? "Var" : "Yok")));
                        sb.append(String.format("  Maas       : %,.0f EUR\n", f.maasHesapla()));
                        sb.append("----------------------------------------------------------------------\n");
                    }
                }
            }
        }
        messageArea.setText(sb.toString());
    }

    // Futbolculari oynadiklari mevkilere gore gruplayarak rapor hazirlar.
    private void listeleMevkiGrubuOptimize(StringBuilder sb, List<Futbolcu> mevkiListesi, String baslik, String arananMevki) {
        sb.append(String.format("=== %s ===\n", baslik));

        if (mevkiListesi == null || mevkiListesi.isEmpty()) {
            sb.append("  (Bu mevkide kayitli oyuncu yok)\n");
            sb.append("----------------------------------------------------------------------\n");
            sb.append("\n");
            return;
        }

        for (Futbolcu f : mevkiListesi) {
            sb.append(String.format("> AD SOYAD   : %s %s\n", f.getAd().toUpperCase(), f.getSoyad().toUpperCase()));
            sb.append(String.format("  Forma No   : %-5d |  Mevki : %s\n", f.getFormaNo(), f.getMevki()));
            sb.append(String.format("  Uyruk      : %-15s\n", f.getUlke()));
            String formatliMaas = String.format("%.1fM EUR", f.getMaas());
            sb.append(String.format("  Maas       : %s\n", formatliMaas));
            sb.append(String.format("  Istatistik : %s\n", f.getPerformansDetayi()));
            sb.append(String.format("  Analiz     : %s (Skor: %.0f)\n", f.performansDurumuAnalizi(), f.performansPuaniniHesapla()));
            sb.append("----------------------------------------------------------------------\n");
        }
        sb.append("\n");
    }

    // Sistemden personel kaydi silme islemlerini baslatir.
    private void personelSilmeEkraniGoster() {
        List<String> secimler = Arrays.asList("Futbolcu (Forma No)", "Teknik Direktor", "Yardimci Antrenor (ID)", "Fizyoterapist (ID)");
        ChoiceDialog<String> dialogSecim = new ChoiceDialog<>(secimler.get(0), secimler);
        dialogSecim.showAndWait().ifPresent(secilen -> {
            if (secilen.startsWith("Futbolcu")) futbolcuSilmeEkrani();
            else if (secilen.startsWith("Teknik")) teknikDirektorSilmeEkrani();
            else calisanSilmeEkrani(secilen);
        });
    }

    // Forma numarasi girilen futbolcunun kaydini siler.
    private void futbolcuSilmeEkrani() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Silinecek futbolcunun Forma No:");
        dialog.showAndWait().ifPresent(res -> {
            try {
                boolean silindi = service.futbolcuSil(Integer.parseInt(res.trim()));
                gosterSonucAlert(silindi, "Islem Sonucu", "Basariyla silindi.", "Forma No bulunamadi.");
            } catch (Exception e) { gosterHataAlert("Hata", "Gecersiz giris."); }
        });
    }

    // Teknik direktorun gorevine son verilme islemini onaylar.
    private void teknikDirektorSilmeEkrani() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Teknik direktoru silmek istiyor musunuz?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) service.teknikDirektorSil();
        });
    }

    // ID bilgisi verilen personele ait kaydi siler.
    private void calisanSilmeEkrani(String tip) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(tip + " ID girin:");
        dialog.showAndWait().ifPresent(id -> {
            boolean silindi = tip.contains("Yardimci") ? service.yardimciAntrenorSil(id) : service.fizyoterapistSil(id);
            gosterSonucAlert(silindi, "Islem Sonucu", "Basariyla silindi.", "ID bulunamadi.");
        });
    }

    // Futbolcularin gol ve asist katkilari uzerinden basari siralamasini raporlar.
    private void handleSkorKatkisiSiralamasi() {
        Stage reportStage = new Stage();
        reportStage.setTitle("Skor Katkisi Siralamasi (Top-Down)");

        TableView<Futbolcu> table = new TableView<>();

        TableColumn<Futbolcu, String> nameCol = new TableColumn<>("Ad Soyad");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getAd() + " " + data.getValue().getSoyad()));
        nameCol.setStyle("-fx-alignment: CENTER-LEFT;");

        TableColumn<Futbolcu, Integer> golCol = new TableColumn<>("Gol");
        golCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("golSayisi"));
        golCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Futbolcu, Integer> asistCol = new TableColumn<>("Asist");
        asistCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("asistSayisi"));
        asistCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Futbolcu, Integer> toplamCol = new TableColumn<>("Toplam Katki");
        toplamCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(
                data.getValue().getGolSayisi() + data.getValue().getAsistSayisi()));
        toplamCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

        table.getColumns().addAll(Arrays.asList(nameCol, golCol, asistCol, toplamCol));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        List<Futbolcu> liste = new ArrayList<>(service.getFutbolcuKadrosu());
        liste.sort((f1, f2) -> {
            int skor1 = f1.getGolSayisi() + f1.getAsistSayisi();
            int skor2 = f2.getGolSayisi() + f2.getAsistSayisi();
            return Integer.compare(skor2, skor1);
        });
        table.getItems().addAll(liste);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER);

        Label header = new Label("Futbolcu Performans Siralamasi");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        vbox.getChildren().addAll(header, table);

        Scene scene = new Scene(vbox, 500, 400);
        reportStage.setScene(scene);
        reportStage.show();

        if (!liste.isEmpty()) {
            Futbolcu lider = liste.get(0);
            showMessage("SIRALAMA: Takimin skor lideri: " + lider.getAd() + " " + lider.getSoyad() +
                    " (" + (lider.getGolSayisi() + lider.getAsistSayisi()) + " katki)", "YESIL");
        }
    }

    // Secilen futbolcunun haftalik gol ve asist verilerini gunceller.
    private void handleUpdatePerformance() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Performans Guncelleme");
        dialog.setHeaderText("Futbolcu Secimi");
        dialog.setContentText("Lutfen Futbolcunun Forma Numarasini Girin:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(formaNoStr -> {
            try {
                int formaNo = Integer.parseInt(formaNoStr.trim());
                TextInputDialog golDialog = new TextInputDialog("0");
                golDialog.setTitle("Gol Verisi");
                golDialog.setHeaderText("Forma No: " + formaNo + " - Gol Sayisi");
                golDialog.setContentText("Bu hafta kac gol atti?");
                Optional<String> golRes = golDialog.showAndWait();
                int gol = Integer.parseInt(golRes.orElse("0"));

                TextInputDialog asistDialog = new TextInputDialog("0");
                asistDialog.setTitle("Asist Verisi");
                asistDialog.setHeaderText("Forma No: " + formaNo + " - Asist Sayisi");
                asistDialog.setContentText("Bu hafta kac asist yapti?");
                Optional<String> asistRes = asistDialog.showAndWait();
                int asist = Integer.parseInt(asistRes.orElse("0"));

                if (service.performansVerisiGir(formaNo, gol, asist)) {
                    showMessage("Forma No " + formaNo + " basariyla guncellendi.", Renklendirici.YESIL);
                } else {
                    gosterHataAlert("Hata", "Bu forma numarasina sahip futbolcu bulunamadi.");
                }
            } catch (NumberFormatException e) {
                gosterHataAlert("Giris Hatasi", "Lutfen sadece sayisal degerler giriniz.");
            }
        });
    }

    // Antrenman programi yonetimi icin ilgili menuleri yonlendirir.
    private void handleTrainingMenuSelection() {
        List<String> choices = Arrays.asList("1. Haftalik Antrenman Gir", "2. Haftalik Antrenman Goruntule");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(1), choices);
        dialog.setTitle("Antrenman Yonetimi");
        dialog.setHeaderText("Yapmak istediginiz islemi seciniz:");
        dialog.setContentText("Islem:");

        dialog.showAndWait().ifPresent(selection -> {
            if (selection.contains("1")) handleEnterTrainingProgram();
            else handleDisplayWeeklyProgram();
        });
    }

    // Yeni bir haftalik calisma programi girilmesini saglar.
    private void handleEnterTrainingProgram() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Haftalik Antrenman Programi Girişi");
        dialog.setHeaderText("Her gun icin antrenman aktivitesini giriniz.");
        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        String[] gunler = service.getGunler();
        String[] mevcutAktiviteler = service.getHaftalikAktiviteler();
        List<TextField> inputs = new ArrayList<>();

        for (int i = 0; i < gunler.length; i++) {
            grid.add(new Label(gunler[i] + ":"), 0, i);
            TextField tf = new TextField(mevcutAktiviteler[i]);
            tf.setPromptText("Orn: Kondisyon");
            inputs.add(tf);
            grid.add(tf, 1, i);
        }
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String[] yeniProgram = new String[7];
                for (int i = 0; i < 7; i++) {
                    String val = inputs.get(i).getText().trim();
                    yeniProgram[i] = val.isEmpty() ? "Dinlenme" : val;
                }
                return yeniProgram;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(yeniProgram -> {
            service.haftalikProgramGuncelle(yeniProgram);
            showMessage("Haftalik antrenman programi basariyla guncellendi.", Renklendirici.YESIL);
            handleDisplayWeeklyProgram();
        });
    }

    // Mevcut haftalik calisma programini raporlar.
    private void handleDisplayWeeklyProgram() {
        String program = service.haftalikProgramiGoster();
        messageArea.setText(cleanAnsi(program));
    }

    // Fikstur yonetim ekranlarini yonlendirir.
    private void handleFixtureMenu() {
        List<String> choices = Arrays.asList("1. Fiksturu Guncelle (Mac Ekle)", "2. Fiksturu Goruntule");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Fikstur Yonetimi");
        dialog.setHeaderText("Islem Secin");

        dialog.showAndWait().ifPresent(selection -> {
            if (selection.contains("1")) handleUpdateFixture();
            else handleDisplayFixture();
        });
    }

    // Fiksture yeni bir mac verisi dahil eder.
    private void handleUpdateFixture() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Yeni Mac Ekle");
        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField typeField = new TextField();
        typeField.setPromptText("Orn: Super Lig, Sampiyonlar Ligi");
        grid.add(new Label("Mac Turu:"), 0, 5);
        grid.add(typeField, 1, 5);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField opponentField = new TextField();
        ComboBox<String> locationBox = new ComboBox<>();
        locationBox.getItems().addAll("Ic Saha", "Deplasman");
        locationBox.setValue("Ic Saha");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Oynandi", "Gelecek Mac");
        statusBox.setValue("Gelecek Mac");

        TextField scoreField = new TextField();
        scoreField.setPromptText("Orn: 1-0 (Oynanmadiysa bos birakin)");
        scoreField.setDisable(true);
        statusBox.setOnAction(e -> scoreField.setDisable(statusBox.getValue().equals("Gelecek Mac")));

        grid.add(new Label("Tarih:"), 0, 0);       grid.add(datePicker, 1, 0);
        grid.add(new Label("Rakip:"), 0, 1);       grid.add(opponentField, 1, 1);
        grid.add(new Label("Yer:"), 0, 2);         grid.add(locationBox, 1, 2);
        grid.add(new Label("Durum:"), 0, 3);       grid.add(statusBox, 1, 3);
        grid.add(new Label("Skor:"), 0, 4);        grid.add(scoreField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                String skor = statusBox.getValue().equals("Oynandi") ? scoreField.getText() : "Henuz Belirlenmedi";
                service.macEkle(datePicker.getValue(), opponentField.getText(), skor, typeField.getText());
                return new String[]{"Success"};
            }
            return null;
        });
        dialog.showAndWait();
    }

    // Kayitli tum mac fiksturunu listeler.
    private void handleDisplayFixture() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================== GALATASARAY FIKSTUR ===================\n\n");
        if (service.getMacGecmisi().isEmpty()) {
            sb.append("Henuz eklenmis bir mac bulunmuyor.");
        } else {
            service.getMacGecmisi().forEach((tarih, veri) -> {
                sb.append(String.format(">> %s\n", veri.getMacTuru().toUpperCase()));
                String tarihStr = Renklendirici.TARIH_FORMATI.format(tarih);
                sb.append(String.format("[%s] GS vs %-15s | Skor: %s\n", tarihStr, veri.getRakipTakim(), veri.getSkor()));
                sb.append("-----------------------------------------------------------\n");
            });
        }
        messageArea.setText(sb.toString());
    }

    // Kulubun finansal raporunu olusturur veya ek odeme verisi girilmesini saglar.
    private void handleFinansalAnalizMenu() {
        List<String> secenekler = Arrays.asList("Futbolcu Performans Primi Ekle", "Genel Butce Raporunu Goruntule");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(secenekler.get(1), secenekler);
        dialog.setTitle("Finansal Analiz ve Veri Yonetimi");
        dialog.setHeaderText("Yapilacak Islemi Seciniz");
        dialog.setContentText("Islem:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(secim -> {
            if (secim.equals("Futbolcu Performans Primi Ekle")) {
                TextInputDialog noDialog = new TextInputDialog();
                noDialog.setHeaderText("Performans eklenecek futbolcu forma no:");
                noDialog.showAndWait().ifPresent(no -> {
                    Futbolcu f = service.futbolcuBul(no);
                    if (f != null) {
                        int yeniGol = getSayiGirisi(f.getAd() + " icin ek GOL sayisi:");
                        int yeniAsist = getSayiGirisi(f.getAd() + " icin ek ASIST sayisi:");
                        f.performansEkle(yeniGol, yeniAsist);
                        messageArea.setText("Veriler Guncellendi: " + f.getAd() + "\n" +
                                "Sezon Toplami: " + f.getToplamGol() + " Gol, " + f.getToplamAsist() + " Asist.");
                    } else {
                        showError("Hata: " + no + " numarali futbolcu bulunamadi!");
                    }
                });
            } else {
                String rapor = service.detayliFinansalAnalizRaporu();
                messageArea.setText(rapor);
                System.out.println("Finansal rapor olusturuldu ve dosyaya kaydedildi.");
            }
        });
    }

    // Kullanicidan tam sayi degerleri almak icin ortak giris diyalogu olusturur.
    private int getSayiGirisi(String mesaj) {
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Veri Girisi");
        dialog.setHeaderText(mesaj);
        dialog.setContentText("Sayi:");
        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Integer::parseInt).orElse(0);
        } catch (NumberFormatException e) {
            showError("Lutfen gecerli bir tam sayi giriniz!");
            return 0;
        }
    }

    // Metin icerisindeki teknik renk kodlarini temizler.
    private String cleanAnsi(String text) {
        return ANSI_PATTERN.matcher(text).replaceAll("");
    }

    // Uygulama mesaj alanina metin ekler.
    private void showMessage(String message, String color) { messageArea.appendText(cleanAnsi(message) + "\n"); }

    // Islem sonuclari hakkinda bilgi pencereleri sunar.
    private void gosterSonucAlert(boolean basarili, String baslik, String basariMesaj, String hataMesaj) {
        Alert alert = new Alert(basarili ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setTitle(baslik);
        alert.setHeaderText(baslik);
        alert.setContentText(basarili ? basariMesaj : hataMesaj);
        alert.showAndWait();
    }

    // Kritik hata durumlarini kullaniciya bildirir.
    private void gosterHataAlert(String baslik, String mesaj) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(baslik);
        alert.setHeaderText(baslik);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    // Standart hata pencereleri olusturur.
    private void showError(String mesaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}