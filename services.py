import yfinance as yf                      # Import yfinance to fetch stock data          # Retrieve data from Yahoo Finance
import pandas as pd                        # Import pandas for data manipulation            # DataFrame operations
import numpy as np                         # Import numpy for numerical operations          # Array math functions
from sklearn.preprocessing import MinMaxScaler  # Import MinMaxScaler for data normalization   # Normalize data

def fetch_stock_data(ticker, period='5y', interval='1d'):
    """
    Fetches stock data for the given ticker using yfinance,
    adjusts prices automatically, drops missing values, resamples
    the data to business days, and forward-fills missing values.
    """
    try:
        df = yf.Ticker(ticker).history(
            period=period,                  # Set the data period (default '5y')          # Data period selection
            interval=interval,              # Set the data interval (default '1d')          # Data interval selection
            auto_adjust=True                # Adjust prices for splits/dividends          # Auto-adjust prices
        ).dropna()                           # Drop rows with missing values                # Data cleaning
        return df.resample('B').last().ffill()  # Resample data to business days and forward-fill missing values  # Resample and fill data
    except Exception as e:
        return pd.DataFrame()                # Return an empty DataFrame on error             # Error handling

def prepare_data(df, column='Close'):
    """
    Prepares data by normalizing the specified column (default 'Close')
    using MinMaxScaler. Returns the normalized data along with the scaler's
    minimum and maximum values for later denormalization.
    """
    scaler = MinMaxScaler()                # Initialize the MinMaxScaler                   # Create scaler instance
    series = df[column].values.reshape(-1, 1)  # Extract the specified column as a 2D array      # Prepare data for scaling
    normalized = scaler.fit_transform(series)   # Normalize the data to range [0, 1]            # Apply scaling
    return normalized, scaler.data_min_[0], scaler.data_max_[0]  # Return normalized data and min, max values  # Provide normalization results

def create_sequences(data, window_size):
    """
    Creates sequences (input X and target y) from the normalized data.
    Each sequence of length 'window_size' has the next value as its target.
    """
    X, y = [], []                         # Initialize lists for input sequences and targets  # Containers for sequences
    for i in range(len(data) - window_size):  # Loop over data to create sequences         # Create sliding windows
        X.append(data[i:i+window_size])    # Append a sequence of 'window_size' values          # Build input sequence
        y.append(data[i+window_size])      # Append the value following the sequence as target  # Set target value
    return np.array(X), np.array(y)         # Convert lists to numpy arrays and return them    # Provide sequences and targets
