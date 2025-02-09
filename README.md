# Stock Price Prediction Project

This project aims to develop a Java application that retrieves both historical and current stock market data, performs statistical analyses, and provides a simple forecast for future price developments. A key goal is to give users a quick overview of price movements, trends, and risks. In addition, the application should allow users to directly enter either a ticker symbol (e.g., TSLA, AAPL) or a company name (e.g., “Tesla”), which is then automatically converted into the corresponding ticker (e.g., using a symbol search via Alpha Vantage).

Components

Data Retrieval: 
Connection to the Alpha Vantage API to query stock prices (including symbol search)

Data Processing: 
Methods to calculate key metrics (e.g., moving average, volatility, drawdown)

Forecast Module: 
A simple model (e.g., linear regression) to predict future prices

Visualization: 
Charts (using JFreeChart or JavaFX) to display price history and forecasts

User Interface (GUI): 
Option to enter a company name or a ticker symbol. 
Interactive display of the results (historical data, statistical analysis, forecasts)
