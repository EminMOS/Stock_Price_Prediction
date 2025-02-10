package org.example.stock.service.api;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AlphaVantageAPIService {
    private static final String API_KEY = "99WYAHQ9XGGR85HE";
    // Basis-URL für die Alpha Vantage API
    private static final String BASE_URL = "https://www.alphavantage.co/query";

    /**
     * Diese Methode ruft die täglichen Aktienkursdaten für ein gegebenes Symbol ab.
     *
     * @param symbol Das Tickersymbol der Aktie (z.B. "AAPL" für Apple)
     * @return Die Rohdaten als JSON-String
     * @throws IOException Falls beim Abruf ein Fehler auftritt
     */
    public String getStockData(String symbol) throws IOException {
        // Erstelle einen OkHttpClient, der die HTTP-Anfrage ausführt
        OkHttpClient client = new OkHttpClient();

        // Baue die URL mit den nötigen Parametern auf:
        // - function: Gibt an, welche Daten wir abrufen (z.B. "TIME_SERIES_DAILY")
        // - symbol: Das Aktiensymbol
        // - apikey: Unser API-Schlüssel
        // - outputsize: "compact" gibt eine begrenzte Datenmenge zurück (für schnellere Abfragen)
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("function", "TIME_SERIES_DAILY")
                .addQueryParameter("symbol", symbol)
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("outputsize", "compact")
                .build();

        // Erstelle eine GET-Anfrage mit der oben erstellten URL
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Führe die Anfrage aus und erhalte die Antwort
        try (Response response = client.newCall(request).execute()) {
            // Überprüfe, ob die Antwort erfolgreich war
            if (!response.isSuccessful()) {
                throw new IOException("Unerwarteter Fehler: " + response);
            }
            // Gib den Inhalt der Antwort (als String) zurück
            return response.body().string();
        }
    }
}