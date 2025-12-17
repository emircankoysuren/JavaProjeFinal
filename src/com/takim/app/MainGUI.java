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
        // ... (Dialog kodları önceki adımlardaki gibi devam eder)
        // Özet: TextField'lardan veriler alınır ve service.futbolcuEkle(f) çağrılır.
    }

    private void handleAddNewTeknikDirektor() {
        // ... (Dialog kodları önceki adımlardaki gibi devam eder)
    }

    private void handleAddNewYardimciAntrenor() {
        // ... (Dialog kodları önceki adımlardaki gibi devam eder)
    }

    private void handleAddNewFizyoterapist() {
        // ... (Dialog kodları önceki adımlardaki gibi devam eder)
    }

    private void handleSkorKatkisiSiralamasi() {
        messageArea.setText(cleanAnsi(service.skorKatkisiRaporuGetir()));
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