package org.example.stock;

import org.example.stock.service.api.AlphaVantageAPIService;

public class Main {
    public static void main(String[] args) {
        AlphaVantageAPIService apiService = new AlphaVantageAPIService();
        try {
            // Teste die API, indem du Daten für "AAPL" abrufst
            String jsonData = apiService.getStockData("AAPL");
            System.out.println("Erhaltene Daten: " + jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}