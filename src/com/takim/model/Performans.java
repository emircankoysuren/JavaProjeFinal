package com.takim.model;

public interface Performans {
    // Performans puanını (0-100 arası veya skor bazlı) hesaplar
    double performansPuaniniHesapla();

    // Performans verilerini günceller (Esnek yapı: Gol, Asist, Puan, Kupa vb.)
    void performansGuncelle(double... veriler);

    // Durumu metin olarak analiz eder ("Formda", "Yıldız", "Riskli" vb.)
    String performansDurumuAnalizi();

    // Detaylı veriyi (Gol-Asist veya Puan-Kupa) tek bir string olarak döner
    String getPerformansDetayi();
}