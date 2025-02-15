package org.example.stock.service.ml;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import java.util.List;

public class PredictionModel {

    private MultiLayerNetwork model;

    /**
     * Konstruktor zum Erstellen des LSTM-Modells.
     *
     * @param inputSize Anzahl der Eingabefeatures (bei univariater Zeitreihe = 1)
     * @param lstmLayerSize Anzahl der Neuronen im LSTM-Layer
     * @param learningRate Lernrate des Optimierers
     */
    public PredictionModel(int inputSize, int lstmLayerSize, double learningRate) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(1234)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(learningRate))
                .list()
                .layer(0, new LSTM.Builder()
                        .nIn(inputSize)
                        .nOut(lstmLayerSize)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(lstmLayerSize)
                        .nOut(1)
                        .build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(20));
    }

    /**
     * Trainiert das LSTM-Modell mit den bereitgestellten Sequenzen.
     *
     * @param sequences Liste von Sequenzen (Eingabefenster + Zielwert), erzeugt in LSTMDataPreprocessor
     * @param nEpochs Anzahl der Trainings-Epochen
     */
    public void trainModel(List<org.example.stock.service.LSTMDataPreprocessor.Sequence> sequences, int nEpochs) {
        int numExamples = sequences.size();
        int timeSteps = sequences.get(0).input.length;

        // Features: [numExamples, 1, timeSteps]
        INDArray features = Nd4j.create(new int[] {numExamples, 1, timeSteps});
        // Labels: [numExamples, 1, timeSteps] – nur der letzte Zeitschritt enthält den Zielwert
        INDArray labels = Nd4j.create(new int[] {numExamples, 1, timeSteps});
        // Label-Maske: [numExamples, timeSteps] – nur der letzte Zeitschritt wird berücksichtigt
        INDArray labelMask = Nd4j.zeros(numExamples, timeSteps);

        for (int i = 0; i < numExamples; i++) {
            double[] inputArray = sequences.get(i).input;
            for (int t = 0; t < timeSteps; t++) {
                features.putScalar(new int[] {i, 0, t}, inputArray[t]);
            }
            // Setze den Zielwert nur beim letzten Zeitschritt
            labels.putScalar(new int[] {i, 0, timeSteps - 1}, sequences.get(i).target);
            labelMask.putScalar(new int[] {i, timeSteps - 1}, 1.0);
        }

        DataSet trainingData = new DataSet(features, labels, null, labelMask);

        for (int epoch = 0; epoch < nEpochs; epoch++) {
            model.fit(trainingData);
        }
    }

    /**
     * Liefert eine Vorhersage basierend auf einem Eingabefenster.
     *
     * @param inputSequence Array von normalisierten Werten (Fenstergröße muss stimmen)
     * @return Vorhergesagter normalisierter Wert (letzter Zeitschritt)
     */
    public double predict(double[] inputSequence) {
        int timeSteps = inputSequence.length;
        INDArray input = Nd4j.create(new int[]{1, 1, timeSteps});
        for (int t = 0; t < timeSteps; t++) {
            input.putScalar(new int[]{0, 0, t}, inputSequence[t]);
        }
        INDArray output = model.output(input);
        return output.getDouble(0, 0, timeSteps - 1);
    }

    public MultiLayerNetwork getModel() {
        return model;
    }
}