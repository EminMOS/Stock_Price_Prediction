import streamlit as st                           # Import Streamlit for building the web app  # UI library for interactive applications
import pandas as pd                              # Import pandas for data manipulation            # DataFrame operations
import numpy as np                               # Import numpy for numerical operations          # Array math functions
import plotly.graph_objs as go                   # Import Plotly graph objects for interactive charts  # Create interactive charts
import os                                        # Import os for file system operations           # To check file existence
from tensorflow.keras.models import load_model   # Import load_model to load saved Keras models     # For persistent model saving

from services import fetch_stock_data, prepare_data, create_sequences  # Import functions from services.py  # Data retrieval and preprocessing
from model import create_model, train_model, predict                   # Import functions from model.py  # LSTM model creation, training, and prediction

# Forecast horizons in trading days (approximate trading days)
HORIZON_OPTIONS = {
    "1 Day": 1,            # 1 day forecast equals 1 trading day
    "1 Week": 5,           # 1 week forecast equals 5 trading days
    "2 Weeks": 10,         # 2 weeks forecast equals 10 trading days
    "3 Weeks": 15,         # 3 weeks forecast equals 15 trading days
    "1 Month": 20,         # 1 month forecast equals 20 trading days
    "2 Months": 40,        # 2 months forecast equals 40 trading days
    "3 Months": 60,        # 3 months forecast equals 60 trading days
    "6 Months": 120,       # 6 months forecast equals 120 trading days
    "1 Year": 240          # 1 year forecast equals 240 trading days
}

st.title("Stock Prediction with LSTM")          # Set the app title  # Display main title

# User inputs
ticker = st.text_input("Stock Ticker (e.g., AAPL):", value="AAPL").upper()  # Create text input for stock ticker; default "AAPL" and convert to uppercase
period = st.selectbox("Time Period:", ["max", "5y", "3y", "2y", "1y", "6mo"], index=0)  # Create dropdown for selecting historical data period
interval = st.selectbox("Interval:", ["1d", "1h", "5m"], index=0)  # Create dropdown for selecting data interval
selected_horizon = st.selectbox("Forecast Horizon:", list(HORIZON_OPTIONS.keys()), index=2)  # Create dropdown for forecast horizon; default index 2
future_steps = HORIZON_OPTIONS[selected_horizon]  # Set the number of future trading days based on selected horizon

# Define a function that checks for a saved model and loads it if available; otherwise, trains and saves the model
@st.cache_resource(show_spinner="Model is training... Please wait")  # Cache the resource so that repeated runs with the same parameters load the model
def get_trained_model(X, y, input_shape, model_file):
    if os.path.exists(model_file):               # Check if the model file already exists on disk
        model = load_model(model_file)           # Load the trained model from the file
    else:
        model = create_model(input_shape)        # Create a new LSTM model with the given input shape
        _ = train_model(model, X, y)               # Train the model on the provided data
        model.save(model_file)                   # Save the trained model to disk for future use
    return model                                 # Return the trained (or loaded) model

if st.button("Start Forecast"):                   # Create a button to start the forecast process  # Trigger forecast on click
    if not ticker.isalpha():                      # Validate that the ticker contains only letters  # Ensure valid ticker symbol
        st.error("Invalid symbol!")               # Show error message if the ticker is invalid  # Error output
        st.stop()                                 # Stop further execution  # Halt process
    
    data = fetch_stock_data(ticker, period, interval)  # Fetch historical stock data using yfinance  # Retrieve data
    if not data.empty:                            # Check if data was successfully fetched  # Ensure data exists
        if len(data) < 100:                       # If there are fewer than 100 rows, warn the user  # Check if there is sufficient data
            st.warning("Not enough data!")        # Display a warning if insufficient data is found  # Warning output
            st.stop()                             # Stop further execution  # Halt process
        
        # Display the daily historical data overview (full data table)
        st.write("Historical Data Overview (Daily Values):", data)  # Show the full daily historical data  # Display data table
        
        # Data preprocessing: normalize the 'Close' column using MinMaxScaler
        normalized, min_val, max_val = prepare_data(data, 'Close')  # Normalize the 'Close' column  # Preprocess target data
        
        # Create sequences for the LSTM model
        window_size = max(30, future_steps // 2)    # Set window size: at least 30 or half of future steps  # Determine sequence length
        X, y_seq = create_sequences(normalized, window_size)  # Create input sequences and corresponding targets  # Generate sequences
        X = X.reshape((X.shape[0], window_size, 1))    # Reshape X to (samples, window_size, 1) for LSTM  # Format data for LSTM
        
        # Define the input shape for the model
        input_shape = (window_size, 1)               # Set input shape as (window_size, 1)  # Model input configuration
        
        # Define a filename for the trained model based on key parameters
        model_file = f"{ticker}_{period}_{interval}_{window_size}_model.h5"  # Create a unique filename for the trained model  # Persistent model filename
        
        # Retrieve the trained model from cache or by training and saving it
        model = get_trained_model(X, y_seq, input_shape, model_file)  # Get the trained model (cached or trained now)  # Retrieve trained model
        
        # Forecasting: iterative multi-step prediction using the last window
        last_window = normalized[-window_size:]      # Get the last window_size normalized values as the seed  # Seed for forecasting
        future_predictions = []                      # Initialize an empty list to store future predictions  # Future predictions list
        current_seq = last_window.copy()             # Make a copy of the seed sequence for iterative predictions  # Prepare iterative sequence
        
        for _ in range(future_steps):                # Loop for each future trading day  # Iterate forecast steps
            pred = predict(model, current_seq)       # Predict the next normalized 'Close' value using the model  # Forecast next value
            future_predictions.append(pred)          # Append the prediction to the list  # Save prediction
            current_seq = np.append(current_seq[1:], [[pred]], axis=0)  # Update the sequence by removing the oldest value and appending the new prediction  # Slide window
        
        # Denormalize the predictions to convert them back to real price values
        denorm_predictions = [p * (max_val - min_val) + min_val for p in future_predictions]  # Denormalize each predicted value  # Convert to actual prices
        
        # Generate future dates for the forecast using business days ('B')
        future_dates = pd.date_range(
            start=data.index[-1] + pd.Timedelta(days=1),  # Start date is the day after the last historical date  # Future start date
            periods=future_steps,                           # Number of future trading days equals forecast horizon  # Define forecast periods
            freq='B'                                        # Frequency is set to business days  # Use trading days
        )
        
        # Visualization: create an interactive Plotly chart
        fig = go.Figure()                             # Initialize a new Plotly figure  # Create chart
        fig.add_trace(go.Scatter(                      # Add a trace for historical 'Close' prices  # Plot historical data
            x=data.index, y=data['Close'],
            name='Historical', line=dict(color='blue')
        ))
        fig.add_trace(go.Scatter(                      # Add a trace for forecasted prices  # Plot forecast data
            x=future_dates, y=denorm_predictions,
            name='Forecast', line=dict(color='orange', dash='dot')
        ))
        fig.update_layout(                             # Update chart layout  # Configure chart layout
            title=f"{ticker} Price Trend",           # Set chart title  # Chart title in English
            xaxis_title="Date",                        # Set x-axis label  # X-axis label
            yaxis_title="Price (USD)"                  # Set y-axis label  # Y-axis label
        )
        st.plotly_chart(fig)                          # Render the interactive Plotly chart  # Display chart
        
    else:
        st.error("No data found!")                   # Display an error message if no data was fetched  # Error output
