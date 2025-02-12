package org.example.stock.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.example.stock.model.HistoricalData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataPreprocessingService {

    /**
     * Diese Methode parst den Roh-JSON-String von Alpha Vantage
     * und erstellt eine Liste von HistoricalData-Objekten.
     *
     * @param jsonData Der rohe JSON-String von der API
     * @return Eine Liste von HistoricalData-Objekten
     */
    public List<HistoricalData> parseHistoricalData(String jsonData) {
        List<HistoricalData> historicalDataList = new ArrayList<>();
        Gson gson = new Gson();
        // Wandle den JSON-String in ein JsonObject um
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);

        // Hole den Teil mit den täglichen Daten: "Time Series (Daily)"
        JsonObject timeSeries = jsonObject.getAsJsonObject("Time Series (Daily)");

        if (timeSeries != null) {
            // Gehe alle Einträge (jedes Datum) durch
            Set<Map.Entry<String, JsonElement>> entries = timeSeries.entrySet();

            for (Map.Entry<String, JsonElement> entry : entries) {
                String date = entry.getKey();  // Das Datum (z.B. "2025-02-14")
                JsonObject dailyData = entry.getValue().getAsJsonObject();

                // Wandle die einzelnen Werte von String in double bzw. long um
                double open = dailyData.get("1. open").getAsDouble();
                double high = dailyData.get("2. high").getAsDouble();
                double low = dailyData.get("3. low").getAsDouble();
                double close = dailyData.get("4. close").getAsDouble();
                long volume = dailyData.get("5. volume").getAsLong();

                // Erstelle ein HistoricalData-Objekt und füge es der Liste hinzu
                HistoricalData data = new HistoricalData(date, open, high, low, close, volume);
                historicalDataList.add(data);
            }
        }
        return historicalDataList;
    }
}