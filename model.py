import numpy as np                                      # Import numpy for array manipulation         # Array operations
from tensorflow.keras.models import Sequential          # Import Sequential model from Keras            # Model container
from tensorflow.keras.layers import LSTM, Dense, Dropout  # Import LSTM, Dense, and Dropout layers           # Neural network layers
from tensorflow.keras.optimizers import Adam            # Import Adam optimizer from Keras              # Optimizer for training
from tensorflow.keras.callbacks import EarlyStopping    # Import EarlyStopping to prevent overfitting    # Early stopping callback
from tensorflow.keras.losses import Huber               # Import Huber loss for robust regression         # Robust loss function

def create_model(input_shape, lstm_units1=128, lstm_units2=64,
                 dropout_rate=0.3, learning_rate=0.0005):
    """
    Creates an LSTM model with two LSTM layers and dropout.
    Uses the Huber loss function and Adam optimizer with gradient clipping.
    """
    model = Sequential()                                # Initialize a Sequential model                 # Create model instance
    
    # First LSTM layer with return_sequences=True to stack another LSTM
    model.add(LSTM(
        lstm_units1,                                    # Set number of units for the first LSTM layer     # LSTM layer size
        return_sequences=True,                          # Return sequences for stacking LSTMs             # Enable sequence output
        input_shape=input_shape,                        # Specify input shape                              # Model input dimensions
        activation='tanh'                               # Use tanh activation function                     # Activation function
    ))
    model.add(Dropout(dropout_rate))                      # Add Dropout layer to reduce overfitting          # Regularization
    
    # Second LSTM layer (by default returns only the last output)
    model.add(LSTM(lstm_units2, activation='tanh'))       # Add second LSTM layer with specified units         # Additional LSTM layer
    model.add(Dropout(dropout_rate))                      # Add another Dropout layer                        # Further regularization
    
    # Output layer: Dense layer with 1 neuron for regression
    model.add(Dense(1, activation='linear'))            # Add Dense layer with linear activation             # Output prediction layer
    
    # Configure the Adam optimizer with gradient clipping
    optimizer = Adam(
        learning_rate=learning_rate,                     # Set the learning rate                            # Learning rate parameter
        clipvalue=0.5                                    # Set gradient clipping value                       # Prevent exploding gradients
    )
    
    # Compile the model using the Huber loss function
    model.compile(optimizer=optimizer, loss=Huber())      # Compile model with Huber loss                     # Configure training settings
    return model                                        # Return the compiled model                        # Provide model

def train_model(model, X, y, epochs=150, batch_size=64, patience=10):
    """
    Trains the LSTM model using Early Stopping.
    """
    early_stop = EarlyStopping(
        monitor='loss',                                  # Monitor training loss                             # Loss monitoring
        patience=patience,                               # Wait for 'patience' epochs without improvement    # Early stopping patience
        restore_best_weights=True                        # Restore best weights after stopping training      # Best model selection
    )
    
    history = model.fit(
        X, y,                                            # Train using input X and target y                  # Model training
        epochs=epochs,                                   # Set number of training epochs                     # Training duration
        batch_size=batch_size,                           # Set batch size for training                       # Batch processing
        callbacks=[early_stop],                          # Use Early Stopping to avoid overfitting           # Early stopping callback
        verbose=1                                        # Verbose output during training                    # Show training progress
    )
    return history                                      # Return training history                           # Provide training log

def predict(model, input_sequence):
    """
    Predicts the next value using the trained model.
    Reshapes the input sequence to (1, window_size, 1) and returns the predicted value.
    """
    seq = np.array(input_sequence).reshape(1, len(input_sequence), 1)  # Reshape input to required dimensions (1, window_size, 1)  # Format input for LSTM
    return float(model.predict(seq, verbose=0)[0][0])     # Predict and return the scalar output               # Output prediction
