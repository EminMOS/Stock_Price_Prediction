package org.example.stock.model;

public class HistoricalData {
    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

    // Konstruktor zum Erzeugen eines neuen HistoricalData-Objekts
    public HistoricalData(String date, double open, double high, double low, double close, long volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    // Getter-Methoden, damit andere Klassen auf die Werte zugreifen können
    public String getDate() {
        return date;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public long getVolume() {
        return volume;
    }

    // Überschreibe toString() für eine einfache Darstellung
    @Override
    public String toString() {
        return "HistoricalData{" +
                "date='" + date + '\'' +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                '}';
    }
}