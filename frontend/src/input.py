import streamlit as st
# import pandas as pd
# from pathlib import Path

# data_folder = '../data/'
# simulations_df = pd.read_csv(Path(data_folder, 'results.csv')
# st.title('Portfolio Optimization')


import streamlit as st

st.title('Portfolio Optimization')

# User inputs for tickers
tickers_input = st.text_area("Enter tickers, separated by commas or new lines:")
tickers = [ticker.strip().upper() for ticker in tickers_input.split(',') if ticker]

st.write("Tickers entered:", tickers)

# User inputs for weights - assuming they should sum up to 1
weights_input = st.text_area("Enter corresponding weights, separated by commas:")
weights = [float(weight.strip()) for weight in weights_input.split(',') if weight]

st.write("Weights entered:", weights)

if st.button('Optimize Portfolio'):
    # Ensure the number of tickers and weights match
    if len(tickers) != len(weights):
        st.error("The number of tickers and weights must be the same.")
    elif sum(weights) > 1.0:
        st.error("The sum of weights should not exceed 1.")
    else:
        # Your optimization logic here
        st.write("Running optimization...")

        # Example: Display the tickers and weights
        for ticker, weight in zip(tickers, weights):
            st.write(f"{ticker}: {weight}")

