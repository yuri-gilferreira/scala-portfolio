import streamlit as st
import pandas as pd
from pathlib import Path
from utils  import (
    plot_compound_returns, 
    plot_pie_charts_side_by_side, 
    create_risk_return_plot,
    format_return_dataset
)

import streamlit as st

sidebar = st.sidebar
page = sidebar.radio("Select a Page", ["Home", "Inputs", "Results"])


if page == "Home":
    st.title("Home Page")
    st.write("Welcome to the Home Page.")

elif page == "Inputs":
    st.title("Inputs")
    tickers_input = st.text_area("Enter tickers, separated by commas:")
    tickers = [ticker.strip().upper() for ticker in tickers_input.split(',') if ticker]

    st.write("Tickers entered:", tickers)

    weights_input = st.text_area("Enter corresponding weights, separated by commas:\n Or Leave as blank to use equal weights")
    if weights_input == "":
        if tickers == []:
            weights = []
        else:
            weights = [1/len(tickers)] * len(tickers)
    else:
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



elif page == "Results":
    st.title("Results")

    dataPath = Path('../../data')
    df_simulations = pd.read_csv(dataPath/'simulations.csv', delimiter=';')
    df_compounded_returns = pd.read_csv(dataPath/'compounded_returns.csv', delimiter=';')
    df_best_results = pd.read_csv(dataPath/'best_results.csv', delimiter=';')

    # Reformat data
    df_best_results = format_return_dataset(df_best_results)
    df_simulations = format_return_dataset(df_simulations)

    fig1 = create_risk_return_plot(df_simulations, df_best_results, "Risk vs. Return", plot_real_return=False)
    st.plotly_chart(fig1)

    fig2 = plot_compound_returns(df_compounded_returns)
    st.plotly_chart(fig2)

    fig3 = plot_pie_charts_side_by_side(df_best_results, ["IBM", "AAPL", "MSFT"])
    st.plotly_chart(fig3)
