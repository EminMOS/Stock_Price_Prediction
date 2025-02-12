package org.example.stock;

import org.example.stock.model.HistoricalData;
import org.example.stock.service.DataPreprocessingService;
import org.example.stock.service.api.AlphaVantageAPIService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Erstelle den API-Service und rufe die Daten ab
        AlphaVantageAPIService apiService = new AlphaVantageAPIService();
        try {
            String jsonData = apiService.getStockData("AAPL");
            System.out.println("Erhaltene Rohdaten: " + jsonData);

            // Erstelle den DataPreprocessingService und parse die Daten
            DataPreprocessingService preprocessingService = new DataPreprocessingService();
            List<HistoricalData> dataList = preprocessingService.parseHistoricalData(jsonData);

            // Gib die geparsten Daten aus
            for (HistoricalData data : dataList) {
                System.out.println(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}