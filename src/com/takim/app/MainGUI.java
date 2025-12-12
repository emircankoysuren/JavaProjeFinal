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

        Button deletePlayerButton = new Button("4. Personel Sil (Ad/Soyad)");
        deletePlayerButton.setMaxWidth(Double.MAX_VALUE);
        deletePlayerButton.setOnAction(e -> handleDeletePlayer());
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
        Optional<String> adOpt = promptInput("Futbolcu Ekle", "Bilgiler", "Ad:", "Burak");
        Optional<String> soyadOpt = adOpt.flatMap(ad -> promptInput("Futbolcu Ekle", "Bilgiler", "Soyad:", "Yılmaz"));
        Optional<String> formaOpt = soyadOpt.flatMap(s -> promptInput("Futbolcu Ekle", "Bilgiler", "Forma No:", "9"));
        Optional<String> mevkiOpt = formaOpt.flatMap(f -> promptInput("Futbolcu Ekle", "Bilgiler", "Mevki:", "FORVET"));

        if (mevkiOpt.isPresent()) {
            try {
                String ad = adOpt.get();
                String soyad = soyadOpt.get();
                int forma = Integer.parseInt(formaOpt.get());
                String mevki = mevkiOpt.get().toUpperCase();

                Futbolcu f = new Futbolcu(ad, soyad, LocalDate.of(1990, 1, 1), "TC_"+forma, forma, mevki, 0, 0);
                service.futbolcuEkle(f);
                showMessage(f.getAd() + " " + f.getSoyad() + " eklendi.", Formatlayici.YESIL);
            } catch (Exception e) { showMessage("Hata: " + e.getMessage(), Formatlayici.KIRMİZİ); }
        }
    }

    private void handleAddNewTeknikDirektor() {
        try {
            TeknikDirektor td = new TeknikDirektor("Okan", "Buruk", LocalDate.of(1973, 10, 19), "TC_TD", 500000, LocalDate.now(), 2000, "4-2-3-1", 1000, "GS", 2.5, 10);
            service.teknikDirektorEkle(td);
            showMessage("Teknik Direktör eklendi.", Formatlayici.MAVI);
        } catch (Exception e) { showMessage("Hata: " + e.getMessage(), Formatlayici.KIRMİZİ); }
    }

    private void handleAddNewYardimciAntrenor() {
        try {
            YardimciAntrenor ya = new YardimciAntrenor("Ismail", "Kartal", LocalDate.of(1980, 1, 1), "TC_YA", 200000, LocalDate.now(), "Hucum", 100, 10, 10, 10, 10, 10, 10);
            service.yardimciAntrenorEkle(ya);
            showMessage("Yardımcı Antrenör eklendi.", Formatlayici.MAVI);
        } catch (Exception e) { showMessage("Hata: " + e.getMessage(), Formatlayici.KIRMİZİ); }
    }

    private void handleAddNewFizyoterapist() {
        try {
            Fizyoterapist f = new Fizyoterapist("Ali", "Veli", LocalDate.of(1985, 1, 1), "TC_F", 150000, LocalDate.now(), "SERT_1", "Masaj", true, 10, 10, 10, 10);
            service.fizyoterapistEkle(f);
            showMessage("Fizyoterapist eklendi.", Formatlayici.MAVI);
        } catch (Exception e) { showMessage("Hata: " + e.getMessage(), Formatlayici.KIRMİZİ); }
    }

    private void handleSkorKatkisiSiralamasi() {
        messageArea.setText(cleanAnsi(service.skorKatkisiRaporuGetir()));
    }

    private void handleDeletePlayer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sil");
        dialog.setContentText("Ad Soyad:");
        dialog.showAndWait().ifPresent(tamAd -> {
            String[] parts = tamAd.trim().split("\\s+", 2);
            if(parts.length == 2 && service.personelSil(parts[0], parts[1])) {
                showMessage("Silindi: " + tamAd, Formatlayici.YESIL);
            } else {
                showMessage("Bulunamadı: " + tamAd, Formatlayici.KIRMİZİ);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}