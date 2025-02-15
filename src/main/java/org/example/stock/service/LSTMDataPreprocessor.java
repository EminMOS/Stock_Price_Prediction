package org.example.stock.service;

import org.example.stock.model.HistoricalData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LSTMDataPreprocessor {

    // Innere Klasse, die eine Sequenz (Eingabefenster und Zielwert) repräsentiert
    public static class Sequence {
        public double[] input;
        public double target;

        public Sequence(double[] input, double target) {
            this.input = input;
            this.target = target;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Input: [");
            for (double v : input) {
                sb.append(String.format("%.3f", v)).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("] => Target: ").append(String.format("%.3f", target));
            return sb.toString();
        }
    }

    /**
     * Sortiert die Daten chronologisch (älteste zuerst), normalisiert die Schlusskurse
     * und erstellt Sequenzen. Jedes Fenster besteht aus 'windowSize' Tagen als Eingabe,
     * wobei der darauffolgende Wert als Zielwert genutzt wird.
     *
     * @param dataList Liste von HistoricalData-Objekten
     * @param windowSize Anzahl der Tage, die als Eingabe genutzt werden sollen
     * @return Eine Liste von Sequenzen (Eingabefenster + Zielwert)
     */
    public List<Sequence> createSequences(List<HistoricalData> dataList, int windowSize) {
        // Sortiere die Daten chronologisch (älteste zuerst)
        dataList.sort(Comparator.comparing(HistoricalData::getDate));

        List<Sequence> sequences = new ArrayList<>();

        // Finde min und max der Schlusskurse
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (HistoricalData data : dataList) {
            double close = data.getClose();
            if (close < min) min = close;
            if (close > max) max = close;
        }

        // Normalisiere die Schlusskurse: (close - min) / (max - min)
        List<Double> normalized = new ArrayList<>();
        for (HistoricalData data : dataList) {
            double normValue = (data.getClose() - min) / (max - min);
            normalized.add(normValue);
        }

        // Erstelle Sequenzen: Jedes Fenster (windowSize Eingabewerte) und der darauffolgende Wert als Target
        for (int i = 0; i < normalized.size() - windowSize; i++) {
            double[] input = new double[windowSize];
            for (int j = 0; j < windowSize; j++) {
                input[j] = normalized.get(i + j);
            }
            double target = normalized.get(i + windowSize);
            sequences.add(new Sequence(input, target));
        }
        return sequences;
    }
}