# Stock Price Prediction

This application is a web tool for predicting stock prices using LSTM neural networks. It combines historical market data with modern machine learning techniques to forecast future prices based on user-defined parameters.

## 1. Project Description

The "Stock Price Prediction" application uses data from Yahoo Finance to retrieve and analyze historical stock prices. An LSTM model (Long Short-Term Memory) is employed to create a forecasting model that predicts future prices. The key components of the project are:

- **Data Acquisition and Preprocessing:**  
  - Retrieval of historical stock prices using the `yfinance` library.  
  - Normalization of the data (in particular, the “Close” values) using MinMaxScaler.  
  - Creation of sequences used as input for the LSTM model.

- **Model Creation and Training:**  
  - Construction of an LSTM network with two LSTM layers and dropout for regularization.  
  - Use of the Huber Loss function and the Adam optimizer with gradient clipping.  
  - Implementation of early stopping to prevent overfitting.  
  - Caching of the trained model to reduce training times for repeated runs.

- **Forecasting and Visualization:**  
  - Iterative prediction of future prices based on the most recent window of available data.  
  - Denormalization of predicted values to display actual prices.  
  - Interactive visualization of historical data and forecasts using Plotly.

The source code is structured into three main files:
- `app.py`: The main application that processes user inputs, fetches data, trains or loads the model, and visualizes predictions.
- `model.py`: Contains functions for creating, training, and making predictions with the LSTM model.
- `services.py`: Responsible for data retrieval, data preparation, and sequence creation.

## 2. How to Run the Program

### Prerequisites
- **Python 3.7+**
- Installation of the required packages (for example via a `requirements.txt`):
  - streamlit
  - pandas
  - numpy
  - plotly
  - yfinance
  - tensorflow
  - scikit-learn

### Steps
1. **Clone the repository or download the files:**  
   Clone the repository or download the source files (`app.py`, `model.py`, `services.py`) into a directory of your choice.

2. **Set up a virtual environment (optional but recommended):**  
   ```bash
   python -m venv venv
   source venv/bin/activate  # For Linux/macOS
   venv\Scripts\activate     # For Windows

3. Install dependencies:

 install the required packages manually:
 pip install streamlit pandas numpy plotly yfinance tensorflow scikit-learn

4. Start the application:
 Launch the app with Streamlit:
 streamlit run app.py
 The application will open in your default browser and display the user interface.

## 3. Features and Limitations

Features
- Interactive User Interface:
- Enter stock ticker symbols, select historical time frames, data intervals, and the forecast horizon.

- Data Acquisition:
- Automatic retrieval and preprocessing of historical data from Yahoo Finance.

- LSTM Model:

   - Automatic model creation and training.
   - Caching to avoid re-training with the same parameters.
   - Iterative prediction for multiple future trading days.
   - Visualization:
   - Display of historical prices and forecasts using an interactive Plotly chart.

Limitations
Data Dependency:
The accuracy of the forecasts heavily depends on the quality and quantity of the historical data. Changes or restrictions on Yahoo Finance may affect data retrieval.

Model Complexity:

The LSTM model may not capture all market complexities and volatilities.
Iterative forecasts can lead to cumulative errors, as each prediction step depends on the previous one.
Performance and Scalability:

Training the model can be time-consuming for large datasets or on less powerful hardware.
The application may not be optimal for high-frequency trading scenarios.

Scope of Application:
This application is intended as a demonstration of machine learning and LSTM models for stock price prediction and is not a substitute for professional financial advice.