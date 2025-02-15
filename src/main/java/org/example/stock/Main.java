package org.example.stock;


import org.example.stock.model.HistoricalData;
import org.example.stock.service.DataPreprocessingService;
import org.example.stock.service.LSTMDataPreprocessor;
import org.example.stock.service.api.AlphaVantageAPIService;
import org.example.stock.service.ml.PredictionModel;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. API-Daten abrufen
            AlphaVantageAPIService apiService = new AlphaVantageAPIService();
            String jsonData = apiService.getStockData("AAPL");

            // 2. JSON-Daten parsen
            DataPreprocessingService dpService = new DataPreprocessingService();
            List<HistoricalData> dataList = dpService.parseHistoricalData(jsonData);

            // 3. Sequenzen erstellen (z. B. Fenstergröße 5)
            LSTMDataPreprocessor preprocessor = new LSTMDataPreprocessor();
            int windowSize = 5;
            List<LSTMDataPreprocessor.Sequence> sequences = preprocessor.createSequences(dataList, windowSize);

            // 4. PredictionModel initialisieren und trainieren
            PredictionModel predictionModel = new PredictionModel(1, 50, 0.001);
            int nEpochs = 50;
            predictionModel.trainModel(sequences, nEpochs);

            // 5. Vorhersage: Letzte Sequenz als Testinput nutzen
            LSTMDataPreprocessor.Sequence testSequence = sequences.get(sequences.size() - 1);
            double predictedValue = predictionModel.predict(testSequence.input);

            System.out.println("Letzte Eingabesequenz: " + java.util.Arrays.toString(testSequence.input));
            System.out.println("Tatsächlicher Zielwert: " + testSequence.target);
            System.out.println("Vorhergesagter Wert: " + predictedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}