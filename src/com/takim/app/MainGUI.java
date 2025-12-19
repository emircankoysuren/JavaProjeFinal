package com.takim.app;

import com.takim.service.TakimService;
import com.takim.model.*;
import com.takim.util.Formatlayici;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import com.takim.exception.GecersizFormaNoException;
import com.takim.exception.KapasiteDolduException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Locale;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;



/**
 * Projenin ana GUI sınıfı.
 * GÜNCELLEME: Haftalık performans ortalaması (hardcoded) kaldırıldı.
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



        // Başlık (0. Satır, 2 Sütun Kaplar)
        Label title = new Label(TAKIM_ADI + " KADRO VE PERFORMANS YONETİMİ");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        GridPane.setConstraints(title, 0, 0, 2, 1);

        // 1. Satır Butonları
        Button addPersonnelButton = new Button("1. Yeni Personel Ekle (Seçim)");
        addPersonnelButton.setMaxWidth(Double.MAX_VALUE);
        addPersonnelButton.setOnAction(e -> handleNewPersonnelSelection());
        GridPane.setConstraints(addPersonnelButton, 0, 1);

        Button listPlayersButton = new Button("2. Çalışan Listesini Görüntüle");
        listPlayersButton.setMaxWidth(Double.MAX_VALUE);
        listPlayersButton.setOnAction(e -> handleDisplayPersonnelSelection());
        GridPane.setConstraints(listPlayersButton, 1, 1);

        // 2. Satır Butonları
        Button sortGoalsButton = new Button("3. Skor Katkısı Sıralaması");
        sortGoalsButton.setMaxWidth(Double.MAX_VALUE);
        sortGoalsButton.setOnAction(e -> handleSkorKatkisiSiralamasi());
        GridPane.setConstraints(sortGoalsButton, 0, 2);

        Button deletePlayerButton = new Button("4. Personel Sil");
        deletePlayerButton.setMaxWidth(Double.MAX_VALUE);
        deletePlayerButton.setOnAction(e -> personelSilmeEkraniGoster());
        GridPane.setConstraints(deletePlayerButton, 1, 2);

        // 3. Satır Butonları
        Button weeklyProgramButton = new Button("5. Haftalık Antrenman İşlemleri");
        weeklyProgramButton.setMaxWidth(Double.MAX_VALUE);
        weeklyProgramButton.setOnAction(e -> handleTrainingMenuSelection());
        GridPane.setConstraints(weeklyProgramButton, 0, 3);

        Button updatePerformanceButton = new Button("6. Performans Verisi Gir (Gol/Asist)");
        updatePerformanceButton.setMaxWidth(Double.MAX_VALUE);
        updatePerformanceButton.setOnAction(e -> handleUpdatePerformance());
        GridPane.setConstraints(updatePerformanceButton, 1, 3);

        // 4. Satır Butonları (7 ve 8 Yan Yana)
        Button fixtureButton = new Button("7. Fikstür Yönetimi");
        fixtureButton.setMaxWidth(Double.MAX_VALUE);
        fixtureButton.setOnAction(e -> handleFixtureMenu());
        GridPane.setConstraints(fixtureButton, 0, 4);

        Button finansAnalizBtn = new Button("8. Finansal Analiz ve Bütçe Raporu");
        finansAnalizBtn.setMaxWidth(Double.MAX_VALUE);
        finansAnalizBtn.setOnAction(e -> handleFinansalAnalizMenu());
        GridPane.setConstraints(finansAnalizBtn, 1, 4); // 8. buton 4. satır 1. sütuna alındı

        // Mesaj Alanı (5. Satır, 2 Sütun Kaplar)
        messageArea.setEditable(false);
        messageArea.setPrefHeight(300);
        messageArea.setWrapText(true);
        messageArea.setStyle("-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 14px; -fx-text-fill: #333;");

        GridPane.setConstraints(messageArea, 0, 5, 2, 2);

        // Ekleme İşlemi (grid.getChildren().clear() satırını sildik çünkü koordinatları bozar)
        grid.getChildren().addAll(
                title, addPersonnelButton, listPlayersButton,
                sortGoalsButton, deletePlayerButton, weeklyProgramButton,
                updatePerformanceButton, fixtureButton, finansAnalizBtn, messageArea
        );

        primaryStage.setOnCloseRequest(event -> {
            service.tumVerileriKaydet();
            System.out.println("Sistem kapatılıyor, veriler kaydedildi.");
        });

        Scene scene = new Scene(grid, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

    }



    // --- YENİ EKLENEN/GÜNCELLENEN METOTLAR (5. BUTON İÇİN) ---

    // 1. Adım: Menü Seçimi
    private void handleTrainingMenuSelection() {
        List<String> choices = Arrays.asList("1. Haftalık Antrenman Gir", "2. Haftalık Antrenman Görüntüle");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(1), choices);
        dialog.setTitle("Antrenman Yönetimi");
        dialog.setHeaderText("Yapmak istediğiniz işlemi seçiniz:");
        dialog.setContentText("İşlem:");

        dialog.showAndWait().ifPresent(selection -> {
            if (selection.contains("1")) {
                handleEnterTrainingProgram(); // Veri giriş ekranını aç
            } else {
                handleDisplayWeeklyProgram(); // Mevcut programı göster
            }
        });
    }

    // 2. Adım: Veri Giriş Ekranı (Yeni Pencere)
    private void handleEnterTrainingProgram() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Haftalık Antrenman Programı Girişi");
        dialog.setHeaderText("Her gün için antrenman aktivitesini giriniz.");

        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        String[] gunler = service.getGunler();
        String[] mevcutAktiviteler = service.getHaftalikAktiviteler();
        List<TextField> inputs = new ArrayList<>();

        // 7 Gün için dinamik olarak Label ve TextField oluştur
        for (int i = 0; i < gunler.length; i++) {
            grid.add(new Label(gunler[i] + ":"), 0, i);
            TextField tf = new TextField(mevcutAktiviteler[i]); // Mevcut değeri varsayılan yap
            tf.setPromptText("Örn: Kondisyon");
            inputs.add(tf);
            grid.add(tf, 1, i);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Tüm girdileri topla
                String[] yeniProgram = new String[7];
                for (int i = 0; i < 7; i++) {
                    String val = inputs.get(i).getText().trim();
                    yeniProgram[i] = val.isEmpty() ? "Dinlenme" : val; // Boşsa Dinlenme yaz
                }
                return yeniProgram;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(yeniProgram -> {
            service.haftalikProgramGuncelle(yeniProgram);
            showMessage("Haftalık antrenman programı başarıyla güncellendi.", Formatlayici.YESIL);
            handleDisplayWeeklyProgram(); // Kaydettikten sonra otomatik göster
        });
    }

    // 3. Adım: Programı Görüntüle (GÜNCELLENDİ: ORTALAMA SİLİNDİ)
    private void handleDisplayWeeklyProgram() {
        service.sistemBütünlükKontrolü();
        String program = service.haftalikProgramiGoster();
        // Ortalama hesaplama ve yazdırma satırları kaldırıldı.
        messageArea.setText(cleanAnsi(program));
    }

    // --- DİĞER İŞLEYİCİ METOTLAR (MEVCUT KODLAR) ---

    private void handleDisplayPersonnelSelection() {
        List<String> choices = Arrays.asList("1. Futbolcu", "2. Teknik Direktör", "3. Yardımcı Antrenör", "4. Fizyoterapist");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.showAndWait().ifPresent(this::displayFilteredPersonnel);
    }

    private void displayFilteredPersonnel(String selection) {
        StringBuilder sb = new StringBuilder();

        if (selection.contains("1")) { // FUTBOLCU
            List<Futbolcu> liste = service.getFutbolcuKadrosu();
            sb.append("======================================================================\n");
            sb.append(String.format("%-30s\n", "                  FUTBOLCU KADRO LİSTESİ"));
            sb.append("======================================================================\n\n");

            if(liste.isEmpty()) {
                sb.append("Kayıtlı futbolcu bulunmamaktadır.\n");
            } else {
                listeleMevkiGrubu(sb, liste, "KALECİLER", "Kaleci");
                listeleMevkiGrubu(sb, liste, "DEFANSLAR", "Defans");
                listeleMevkiGrubu(sb, liste, "ORTA SAHALAR", "Ortasaha");
                listeleMevkiGrubu(sb, liste, "FORVETLER", "Forvet");
            }

        } else if (selection.contains("2")) { // TEKNİK DİREKTÖR
            List<TeknikDirektor> liste = service.getTeknikDirektorler();
            sb.append("======================================================================\n");
            sb.append("                  TEKNİK DİREKTÖR KADROSU\n");
            sb.append("======================================================================\n\n");

            for(TeknikDirektor t : liste) {
                sb.append(String.format("► %s %s (%s)\n", t.getAd().toUpperCase(), t.getSoyad().toUpperCase(), t.getUyruk()));
                sb.append(String.format("  Performans : %s\n", t.getPerformansDetayi()));
                sb.append(String.format("  Durum      : %s (Puan: %.1f)\n", t.performansDurumuAnalizi(), t.performansPuaniniHesapla()));
                String formatliMaas = String.format(Locale.GERMANY, "%,.0f €", t.getMaas());
                sb.append(String.format("  Maaş       : %s\n", formatliMaas));
                sb.append("----------------------------------------------------------------------\n");
            }
        }
        else {
        // Üstte genel bir liste tanımlamak yerine, her blokta kendi listesini kullanıyoruz
        StringBuilder sbDetay = new StringBuilder();

        if (selection.contains("3")) { // YARDIMCI ANTRENÖR
            List<YardimciAntrenor> yardimciListe = service.getYardimciAntrenorler();
            sb.append("======================================================================\n");
            sb.append("                  YARDIMCI ANTRENÖR KADROSU\n");
            sb.append("======================================================================\n\n");

            if (yardimciListe.isEmpty()) {
                sb.append("Kayıtlı yardımcı antrenör bulunamadı.\n");
            } else {
                for (YardimciAntrenor y : yardimciListe) {
                    sb.append(String.format("► %s %s\n", y.getAd().toUpperCase(), y.getSoyad().toUpperCase()));
                    sb.append(String.format("  ID         : %s\n", (y.getId() != null ? y.getId() : "N/A")));
                    sb.append(String.format("  Görev      : %s\n", y.getUzmanlikAlani()));
                    sb.append(String.format("  Lisans     : %s\n", y.getAntrenorlukLisansi()));
                    sb.append(String.format("  Tecrübe    : %.1f Yıl\n", y.getSahaIciSure()));
                    // maasHesapla() metodunu kullanarak toplam maliyeti gösteriyoruz
                    sb.append(String.format("  Maaş       : %,.0f €\n", y.maasHesapla()));
                    sb.append("----------------------------------------------------------------------\n");
                }
            }
        } else if (selection.contains("4")) { // FİZYOTERAPİST
            List<Fizyoterapist> fizyoListe = service.getFizyoterapistler();
            sb.append("======================================================================\n");
            sb.append("                  FİZYOTERAPİST KADROSU\n");
            sb.append("======================================================================\n\n");
            if (fizyoListe.isEmpty()) {
                sb.append("Kayıtlı fizyoterapist bulunamadı.\n");
            } else {
                for (Fizyoterapist f : fizyoListe) {
                    sb.append(String.format("► %s %s\n", f.getAd().toUpperCase(), f.getSoyad().toUpperCase()));
                    sb.append(String.format("  ID         : %s\n", (f.getId() != null ? f.getId() : "N/A")));
                    sb.append(String.format("  Uzmanlık   : %s\n", f.getUzmanlikAlani()));
                    sb.append(String.format("  Üniversite : %s\n", f.getMezuniyetUniversitesi()));
                    // Ondalıklı yıl gösterimi (Örn: 3.5 Yıl)
                    sb.append(String.format("  Tecrübe    : %.1f Yıl\n", f.getGorevSuresiYil()));
                    sb.append(String.format("  Masaj Yetk.: %s\n", (f.isSporMasajYetkisi() ? "Var" : "Yok")));
                    // Manuel girilen maaş + ek ödemeler
                    sb.append(String.format("  Maaş       : %,.0f €\n", f.maasHesapla()));
                    sb.append("----------------------------------------------------------------------\n");
                }
            }
        }
    }

        messageArea.setText(sb.toString());
    }

    private void listeleMevkiGrubu(StringBuilder sb, List<Futbolcu> anaListe, String baslik, String arananMevki) {
        sb.append(String.format("=== %s ===\n", baslik));
        boolean oyuncuVar = false;
        for (Futbolcu f : anaListe) {
            if (f.getMevki().equalsIgnoreCase(arananMevki)) {
                oyuncuVar = true;
                sb.append(String.format("► AD SOYAD   : %s %s\n", f.getAd().toUpperCase(), f.getSoyad().toUpperCase()));
                sb.append(String.format("  Forma No   : %-5d |  Mevki : %s\n", f.getFormaNo(), f.getMevki()));
                sb.append(String.format("  Uyruk      : %-15s\n", f.getUlke()));

                String formatliMaas = String.format("%.1fM €", f.getMaas());
                sb.append(String.format("  Maaş       : %s\n", formatliMaas));

                sb.append(String.format("  İstatistik : %s\n", f.getPerformansDetayi()));
                sb.append(String.format("  Analiz     : %s (Skor: %.0f)\n", f.performansDurumuAnalizi(), f.performansPuaniniHesapla()));

                sb.append("----------------------------------------------------------------------\n");
            }
        }
        if (!oyuncuVar) {
            sb.append("  (Bu mevkide kayıtlı oyuncu yok)\n");
            sb.append("----------------------------------------------------------------------\n");
        }
        sb.append("\n");
    }

    private void handleUpdatePerformance() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Performans Güncelleme");
        dialog.setHeaderText("Futbolcu Seçimi");
        dialog.setContentText("Lütfen Futbolcunun Forma Numarasını Girin:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(formaNoStr -> {
            try {
                int formaNo = Integer.parseInt(formaNoStr.trim());
                TextInputDialog golDialog = new TextInputDialog("0");
                golDialog.setTitle("Gol Verisi");
                golDialog.setHeaderText("Forma No: " + formaNo + " - Gol Sayısı");
                golDialog.setContentText("Bu hafta kaç gol attı?");
                Optional<String> golRes = golDialog.showAndWait();
                int gol = Integer.parseInt(golRes.orElse("0"));

                TextInputDialog asistDialog = new TextInputDialog("0");
                asistDialog.setTitle("Asist Verisi");
                asistDialog.setHeaderText("Forma No: " + formaNo + " - Asist Sayısı");
                asistDialog.setContentText("Bu hafta kaç asist yaptı?");
                Optional<String> asistRes = asistDialog.showAndWait();
                int asist = Integer.parseInt(asistRes.orElse("0"));

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

    private void handleAddNewFutbolcu() {
        Dialog<Futbolcu> dialog = new Dialog<>();
        dialog.setTitle("Yeni Futbolcu Kaydı");
        dialog.setHeaderText("Futbolcu bilgilerini ve maaşını giriniz.");
        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField adField = new TextField();
        adField.setPromptText("Örn: Mauro");
        TextField soyadField = new TextField();
        soyadField.setPromptText("Örn: Icardi");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1993, 2, 19));
        TextField ulkeField = new TextField();
        ulkeField.setPromptText("Örn: Arjantin");
        TextField fNoField = new TextField();
        fNoField.setPromptText("1-99 arası");
        ComboBox<String> mevkiBox = new ComboBox<>();
        mevkiBox.getItems().addAll("Kaleci", "Defans", "Ortasaha", "Forvet");
        mevkiBox.setValue("Forvet");
        TextField maasField = new TextField();
        maasField.setPromptText("Örn: 15 (Milyon €)");


        grid.add(new Label("Ad:"), 0, 0);
        grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);
        grid.add(soyadField, 1, 1);
        grid.add(new Label("Doğum Tarihi:"), 0, 2);
        grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Ülke:"), 0, 3);
        grid.add(ulkeField, 1, 3);
        grid.add(new Label("Forma No:"), 0, 4);
        grid.add(fNoField, 1, 4);
        grid.add(new Label("Mevki:"), 0, 5);
        grid.add(mevkiBox, 1, 5);
        grid.add(new Label("Maaş (Milyon €):"), 0, 6);
        grid.add(maasField, 1, 6);

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
                    gosterHataAlert("Giriş Hatası", "Forma numarası ve Maaş sadece sayı olmalıdır!");
                } catch (Exception e) {
                    gosterHataAlert("Hata", "Lütfen tüm alanları kontrol edin.");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(futbolcu -> {
            try {
                // Burada hem kapasiteyi hem de forma numarasını kontrol ediyoruz
                service.futbolcuEkle(futbolcu);

                String formatliMaas = String.format(Locale.GERMANY, "%,.0f €", futbolcu.getMaas());
                showMessage(futbolcu.getAd() + " " + futbolcu.getSoyad() + " kadroya eklendi. (Maaş: " + formatliMaas + ")", Formatlayici.YESIL);

            } catch (KapasiteDolduException e) {
                // KadroluListe'den fırlatılan kapasite hatası burada yakalanır
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Kadro Dolu");
                alert.setHeaderText("Kapasite Sınırı Aşıldı");
                alert.setContentText(e.getMessage());
                alert.showAndWait();

            } catch (GecersizFormaNoException e) {
                // Forma numarası hatası burada yakalanır
                gosterHataAlert("Hata", e.getMessage());

            } catch (Exception e) {
                // Diğer beklenmedik hatalar için
                gosterHataAlert("Ekleme Hatası", "Bir sorun oluştu: " + e.getMessage());
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

        TextField adField = new TextField(); adField.setPromptText("Örn: Okan");
        TextField soyadField = new TextField(); soyadField.setPromptText("Örn: Buruk");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1973, 10, 19));
        TextField ulkeField = new TextField(); ulkeField.setPromptText("Örn: Türkiye");
        TextField gorevSuresiField = new TextField(); gorevSuresiField.setPromptText("Örn: 2");
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
                            ortPuan, kupaSayisi, ulkeField.getText().trim(), "Süper Lig", 5.0
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Yıl, Puan ve Kupa alanları sadece sayı olmalıdır!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(td -> {
            try {
                service.teknikDirektorEkle(td);
                showMessage("BAŞARILI: Teknik Direktör " + td.getAd() + " " + td.getSoyad() + " başarıyla eklendi.", "YESIL");
            } catch (Exception e) {
                showMessage("HATA: " + e.getMessage(), "KIRMIZI");
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

        TextField adField = new TextField(); adField.setPromptText("Örn: Ayhan");
        TextField soyadField = new TextField(); soyadField.setPromptText("Örn: Akman");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1977, 2, 23));
        TextField uyrukField = new TextField(); uyrukField.setPromptText("Örn: Türkiye");
        TextField lisansField = new TextField(); lisansField.setPromptText("Örn: UEFA A");
        TextField sureField = new TextField(); sureField.setPromptText("Tecrübe (Yıl - Örn: 3.5)");
        ComboBox<String> gorevBox = new ComboBox<>();
        gorevBox.getItems().addAll("Yardımcı Antrenör", "Kaleci Antrenörü", "Atletik Performans Antrenörü", "Maç Analisti", "Duran Top Antrenörü");
        gorevBox.setValue("Yardımcı Antrenör");
        TextField maasField = new TextField(); maasField.setPromptText("Maaş (Örn: 50000)");

        grid.add(new Label("Ad:"), 0, 0);               grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);            grid.add(soyadField, 1, 1);
        grid.add(new Label("Doğum Tarihi:"), 0, 2);      grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Uyruk:"), 0, 3);             grid.add(uyrukField, 1, 3);
        grid.add(new Label("Lisans:"), 0, 4);           grid.add(lisansField, 1, 4);
        grid.add(new Label("Tecrübe (Yıl):"), 0, 5);     grid.add(sureField, 1, 5);
        grid.add(new Label("Görev Alanı:"), 0, 6);       grid.add(gorevBox, 1, 6);
        grid.add(new Label("Maaş (€):"), 0, 7); grid.add(maasField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    double girilenMaas = Double.parseDouble(maasField.getText().trim()); // Manuel giriş
                    double tecrube = Double.parseDouble(sureField.getText().trim());
                    return new YardimciAntrenor(
                            adField.getText().trim(), soyadField.getText().trim(), dogumTarihiPicker.getValue(),
                            "ANT-" + System.currentTimeMillis() % 10000,
                            girilenMaas, // Buraya girilen maaş geliyor
                            LocalDate.now(), gorevBox.getValue(), tecrube,
                            uyrukField.getText().trim(), lisansField.getText().trim()
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Maaş ve Tecrübe sayı olmalıdır!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ant -> {
            try { service.yardimciAntrenorEkle(ant); showMessage("BAŞARILI: " + ant.getAd() + " eklendi.", "YESIL");
            } catch (Exception e) { showMessage("HATA: " + e.getMessage(), "KIRMIZI"); }
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

        TextField adField = new TextField(); adField.setPromptText("Örn: Yener");
        TextField soyadField = new TextField(); soyadField.setPromptText("Örn: İnce");
        DatePicker dogumTarihiPicker = new DatePicker(LocalDate.of(1980, 1, 1));
        TextField uyrukField = new TextField(); uyrukField.setPromptText("Örn: Türkiye");
        TextField uzmanlikField = new TextField(); uzmanlikField.setPromptText("Örn: Sporcu Sağlığı");
        TextField uniField = new TextField(); uniField.setPromptText("Mezun Olunan Üniversite");
        TextField gorevSuresiField = new TextField(); gorevSuresiField.setPromptText("Yıl (Örn: 5.5)");
        TextField maasField = new TextField(); maasField.setPromptText("Maaş (Örn: 100000)");
        CheckBox masajYetkisiBox = new CheckBox("Spor Masaj Yetkisi Var mı?");
        masajYetkisiBox.setSelected(true);

        grid.add(new Label("Ad:"), 0, 0);                 grid.add(adField, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);              grid.add(soyadField, 1, 1);
        grid.add(new Label("Doğum Tarihi:"), 0, 2);       grid.add(dogumTarihiPicker, 1, 2);
        grid.add(new Label("Uyruk:"), 0, 3);              grid.add(uyrukField, 1, 3);
        grid.add(new Label("Uzmanlık Alanı:"), 0, 4);     grid.add(uzmanlikField, 1, 4);
        grid.add(new Label("Üniversite:"), 0, 5);         grid.add(uniField, 1, 5);
        grid.add(new Label("Görev Süresi (Yıl):"), 0, 6); grid.add(gorevSuresiField, 1, 6);
        grid.add(new Label("Maaş (€):"), 0, 7);           grid.add(maasField, 1, 7);
        grid.add(masajYetkisiBox, 1, 8);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    String otomatikTC = "FT-" + System.currentTimeMillis() % 10000;
                    double gorevSuresi = Double.parseDouble(gorevSuresiField.getText().trim());
                    double girilenMaas = Double.parseDouble(maasField.getText().trim()); // Manuel Maaş

                    return new Fizyoterapist(
                            adField.getText().trim(), soyadField.getText().trim(), dogumTarihiPicker.getValue(),
                            otomatikTC, girilenMaas, LocalDate.now(), uzmanlikField.getText().trim(),
                            masajYetkisiBox.isSelected(), uyrukField.getText().trim(), uniField.getText().trim(), gorevSuresi
                    );
                } catch (NumberFormatException e) {
                    gosterHataAlert("Giriş Hatası", "Görev süresi ve maaş sayı olmalıdır!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ft -> {
            try { service.fizyoterapistEkle(ft); showMessage("BAŞARILI: " + ft.getAd() + " eklendi.", "YESIL");
            } catch (Exception e) { showMessage("HATA: " + e.getMessage(), "KIRMIZI"); }
        });
    }

    private void handleSkorKatkisiSiralamasi() {
        Stage reportStage = new Stage();
        reportStage.setTitle("Skor Katkısı Sıralaması (Top-Down)");

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
        Label header = new Label("Futbolcu Performans Sıralaması");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        vbox.getChildren().addAll(header, table);

        Scene scene = new Scene(vbox, 500, 400);
        reportStage.setScene(scene);
        reportStage.show();

        if (!liste.isEmpty()) {
            Futbolcu lider = liste.get(0);
            showMessage("SIRALAMA: Takımın skor lideri: " + lider.getAd() + " " + lider.getSoyad() +
                    " (" + (lider.getGolSayisi() + lider.getAsistSayisi()) + " katkı)", "YESIL");
        }
    }
    // FİKSTÜR MENÜSÜ YÖNETİMİ

    private void handleUpdateFixture() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Yeni Maç Ekle");

        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField typeField = new TextField();
        typeField.setPromptText("Örn: Süper Lig, Şampiyonlar Ligi");

        grid.add(new Label("Maç Türü:"), 0, 5);
        grid.add(typeField, 1, 5);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField opponentField = new TextField();
        ComboBox<String> locationBox = new ComboBox<>();
        locationBox.getItems().addAll("İç Saha", "Deplasman");
        locationBox.setValue("İç Saha");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Oynandı", "Gelecek Maç");
        statusBox.setValue("Gelecek Maç");

        TextField scoreField = new TextField();
        scoreField.setPromptText("Örn: 1-0 (Oynanmadıysa boş bırakın)");
        scoreField.setDisable(true);

        statusBox.setOnAction(e -> scoreField.setDisable(statusBox.getValue().equals("Gelecek Maç")));

        grid.add(new Label("Tarih:"), 0, 0);       grid.add(datePicker, 1, 0);
        grid.add(new Label("Rakip:"), 0, 1);       grid.add(opponentField, 1, 1);
        grid.add(new Label("Yer:"), 0, 2);         grid.add(locationBox, 1, 2);
        grid.add(new Label("Durum:"), 0, 3);       grid.add(statusBox, 1, 3);
        grid.add(new Label("Skor:"), 0, 4);        grid.add(scoreField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                String skor = statusBox.getValue().equals("Oynandı") ? scoreField.getText() : "Henüz Belirlenmedi";
                // Servis metodunu yeni parametreyle çağırıyoruz:
                service.macEkle(datePicker.getValue(), opponentField.getText(), skor, typeField.getText());
                return new String[]{"Success"};
            }
            return null;
        });
        dialog.showAndWait();
    }


    private void handleFixtureMenu() {
        List<String> choices = Arrays.asList("1. Fikstürü Güncelle (Maç Ekle)", "2. Fikstürü Görüntüle");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Fikstür Yönetimi");
        dialog.setHeaderText("İşlem Seçin");

        dialog.showAndWait().ifPresent(selection -> {
            if (selection.contains("1")) {
                handleUpdateFixture();
            } else {
                handleDisplayFixture();
            }
        });
    }
    private void handleDisplayFixture() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================== GALATASARAY FİKSTÜR ===================\n\n");

        if (service.getMacGecmisi().isEmpty()) {
            sb.append("Henüz eklenmiş bir maç bulunmuyor.");
        } else {
            service.getMacGecmisi().forEach((tarih, veri) -> {
                // Maç Türü (Başlık)
                sb.append(String.format(">> %s\n", veri.getMacTuru().toUpperCase()));

                // Maç Bilgileri
                String tarihStr = Formatlayici.TARİH_FORMATI.format(tarih);
                sb.append(String.format("[%s] GS vs %-15s | Skor: %s\n",
                        tarihStr,
                        veri.getRakipTakim(),
                        veri.getSkor()));
                sb.append("-----------------------------------------------------------\n");
            });
        }
        messageArea.setText(sb.toString());
    }
// FİNANSAL ANALİZ BUTONU

    private void handleFinansalAnalizMenu() {
        // Seçenekleri içeren bir liste oluşturuyoruz
        List<String> secenekler = Arrays.asList(
                "Futbolcu Performans Primi Ekle",
                "Genel Bütçe Raporunu Görüntüle"
        );

        // Seçim kutusu (ChoiceDialog) oluşturuluyor
        ChoiceDialog<String> dialog = new ChoiceDialog<>(secenekler.get(1), secenekler);
        dialog.setTitle("Finansal Analiz ve Veri Yönetimi");
        dialog.setHeaderText("Yapılacak İşlemi Seçiniz");
        dialog.setContentText("İşlem:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(secim -> {
            if (secim.equals("Futbolcu Performans Primi Ekle")) {
                // 1. SEÇENEK: KÜMÜLATİF PERFORMANS GİRİŞİ
                TextInputDialog noDialog = new TextInputDialog();
                noDialog.setHeaderText("Performans eklenecek futbolcu forma no:");
                noDialog.showAndWait().ifPresent(no -> {
                    // Servisteki yeni yazdığımız metodla futbolcuyu buluyoruz
                    Futbolcu f = service.futbolcuBul(no);

                    if (f != null) {
                        // getSayıGirişi metodunu kullanarak verileri alıyoruz
                        int yeniGol = getSayıGirişi(f.getAd() + " için ek GOL sayısı:");
                        int yeniAsist = getSayıGirişi(f.getAd() + " için ek ASİST sayısı:");

                        // Futbolcu sınıfındaki toplamGol ve toplamAsist'i güncelliyoruz
                        f.performansEkle(yeniGol, yeniAsist);

                        messageArea.setText("Veriler Güncellendi: " + f.getAd() + "\n" +
                                "Sezon Toplamı: " + f.getToplamGol() + " Gol, " +
                                f.getToplamAsist() + " Asist.");
                    } else {
                        showError("Hata: " + no + " numaralı futbolcu bulunamadı!");
                    }
                });
            } else {
                // 2. SEÇENEK: GENEL RAPOR (Parametresiz yeni metodumuz)
                String rapor = service.detayliFinansalAnalizRaporu();
                messageArea.setText(rapor);

                // Başarı mesajı
                System.out.println("Finansal rapor oluşturuldu ve dosyaya kaydedildi.");
            }
        });
    }

    private void handleTekilPerformansGirisi() {
        TextInputDialog numaraDialog = new TextInputDialog();
        numaraDialog.setHeaderText("Performans Eklenecek Futbolcu Forma No:");

        numaraDialog.showAndWait().ifPresent(no -> {
            Futbolcu f = service.futbolcuBul(no); // Serviste bu metot olmalı
            if (f != null) {
                // Gol ve asist sayılarını al (Hata payı için try-catch kullanmalısın)
                int gol = getSayıGirişi("Yeni Gol Sayısı:");
                int asist = getSayıGirişi("Yeni Asist Sayısı:");

                f.performansEkle(gol, asist); // İŞTE BURADA ÜSTÜNE EKLENİYOR
                messageArea.setText(f.getAd() + " için veriler güncellendi.\n" +
                        "Toplam: " + f.getToplamGol() + " Gol, " + f.getToplamAsist() + " Asist.");
            } else {
                showError("Oyuncu bulunamadı!");
            }
        });
    }

    private int getSayıGirişi(String mesaj) {
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Veri Girişi");
        dialog.setHeaderText(mesaj);
        dialog.setContentText("Sayı:");

        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Integer::parseInt).orElse(0);
        } catch (NumberFormatException e) {
            showError("Lütfen geçerli bir tam sayı giriniz!");
            return 0;
        }
    }

    // Hata mesajlarını göstermek için (Hata 791)
    private void showError(String mesaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }







// ÜYE SİLME METHOTLARI
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
    private void showMessage(String message, String color) { messageArea.appendText(cleanAnsi(message) + "\n"); }
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