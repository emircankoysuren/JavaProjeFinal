package com.takim.app;

import com.takim.service.TakimService;
import com.takim.model.Futbolcu;
import com.takim.util.Formatlayici;
import com.takim.util.DosyaIslemleri;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.InputMismatchException;

/**
 * JavaFX GUI Uygulamasının Ana Sınıfı.
 * 12. Bölüm: Basit GUI (Bonus) gereksinimini karsilar.
 */
public class MainGUI extends Application {

    public static final String TAKIM_ADI = "GALATASARAY SPOR KULÜBÜ";

    private final TakimService service = new TakimService();

    // GUI ogelerini tutacak alanlar
    private TextArea messageArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(TAKIM_ADI + " - Yonetim Paneli");




        // Ana Layout: GridPane (GUI elemanlarini duzenlemek icin)
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15); // Dikey bosluk
        grid.setHgap(15); // Yatay bosluk

        // --- 1. KOMPONENT: Baslik ---
        Label title = new Label(TAKIM_ADI + " KADRO VE PERFORMANS YONETİMİ");
        // Hatanin olabilecegi satir (Tirnak isaretleri kontrol edildi)
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        GridPane.setConstraints(title, 0, 0, 2, 1); // 0. satir, 2 sutun kaplar

        // --- 2. Mesaj Alani (Konsol yerine) ---
        messageArea.setEditable(false);
        messageArea.setPrefHeight(150);
        messageArea.setWrapText(true);
        GridPane.setConstraints(messageArea, 0, 4, 2, 1); // 4. satir, 2 sutun kaplar

        // --- BUTONLAR ---

        Button addPlayerButton = new Button("1. Yeni Futbolcu Ekle (Orn)");
        addPlayerButton.setMaxWidth(Double.MAX_VALUE);
        addPlayerButton.setOnAction(e -> handleAddPlayer());
        GridPane.setConstraints(addPlayerButton, 0, 1);

        Button listPlayersButton = new Button("3. Kadroyu Listele (Konsol)");
        listPlayersButton.setMaxWidth(Double.MAX_VALUE);
        listPlayersButton.setOnAction(e -> handleListPlayers());
        GridPane.setConstraints(listPlayersButton, 1, 1);

        Button saveReportButton = new Button("6. Raporu Kaydet (I/O)");
        saveReportButton.setMaxWidth(Double.MAX_VALUE);
        saveReportButton.setOnAction(e -> handleSaveReport());
        GridPane.setConstraints(saveReportButton, 0, 2);

        Button sortGoalsButton = new Button("4. Gol Siralamasi");
        sortGoalsButton.setMaxWidth(Double.MAX_VALUE);
        sortGoalsButton.setOnAction(e -> handleSortGoals());
        GridPane.setConstraints(sortGoalsButton, 1, 2);

        // --- Tum Ogeleri Ekleme ---
        grid.getChildren().addAll(title, addPlayerButton, listPlayersButton, saveReportButton, sortGoalsButton, messageArea);

        Scene scene = new Scene(grid, 600, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ------------------- BUTON OLAY YONETICILERI -------------------

    private void showMessage(String message, String color) {
        // Konsol formatlayici yerine, mesaji hem TextArea'ya yazdirir
        messageArea.appendText(message + "\n");
        // Hem de debug/konsol ciktisinda rengi korur (Bonus gereksinimi)
        System.out.println(Formatlayici.renklendir(message, color));
    }

    private void handleAddPlayer() {
        // Bu metod, kullanici arayuzu basitelestirilmesi adina ornek veri ekler.
        try {
            // Ornek Ekleme (Test amacli, Form elemanlari eklenebilir)
            Futbolcu f = new Futbolcu("Mauro", "Icardi", LocalDate.of(1993, 2, 16), "GS9", 9, "FORVET", 15, 5);
               service.futbolcuEkle(f);
            showMessage(f.getAd() + " kadroya eklendi.", Formatlayici.YESIL);
        } catch (Exception e) {
            showMessage("Ekleme basarisiz: " + e.getMessage(), Formatlayici.KIRMİZİ);
        }
    }

    private void handleListPlayers() {
        // TableView yerine konsola listeleme
        showMessage("Mevcut kadro listesi konsola yaziliyor...", Formatlayici.MAVI);
        service.listeYazdir(service.getFutbolcuKadrosu());
    }

    private void handleSaveReport() {
        try {
            DosyaIslemleri.raporuDosyayaYaz(service.getFutbolcuKadrosu());
            showMessage("Rapor dosyaya basariyla kayd edildi.", Formatlayici.YESIL);
        } catch (Exception ex) {
            showMessage("Dosya Kaydedilemedi: " + ex.getMessage(), Formatlayici.KIRMİZİ);
        }
    }

    private void handleSortGoals() {
        showMessage("Gol siralamasi konsola yaziliyor...", Formatlayici.MAVI);
        service.golSiralamasiYap();
    }


    public static void main(String[] args) {
        launch(args);
    }
}