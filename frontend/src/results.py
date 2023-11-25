import streamlit as st
import pandas as pd
from pathlib import Path

from utils  import (
    plot_compound_returns, 
    plot_pie_charts_side_by_side, 
    create_risk_return_plot,
    format_return_dataset,
    format_correlation_matrix,
    format_cov_matrix
)

def show_results_page():
    st.title("Results")

    dataPath = Path('../../data')
    df_simulations = pd.read_csv(dataPath/'simulations.csv', delimiter=';')
    df_compounded_returns = pd.read_csv(dataPath/'compounded_returns.csv', delimiter=';')
    df_best_results = pd.read_csv(dataPath/'best_results.csv', delimiter=';')
    df_correlation_matrix = pd.read_csv(dataPath/'correlation_matrix.csv', delimiter=';')
    df_volatility = pd.read_csv(dataPath/'volatility.csv', delimiter=';')
    df_mean_returns = pd.read_csv(dataPath/'mean_returns.csv', delimiter=';')
    df_cov_matrix = pd.read_csv(dataPath/'covariance_matrix.csv', delimiter=';')

    ## Get labels
    labels = df_mean_returns["Ticker"].tolist()

    # Reformat data
    df_best_results = format_return_dataset(df_best_results)
    df_simulations = format_return_dataset(df_simulations)

    df_correlation_matrix = format_correlation_matrix(df_correlation_matrix, labels)
    df_cov_matrix = format_cov_matrix(df_cov_matrix, labels)


    # Plotting
    st.subheader("Return Plots")
    fig1 = create_risk_return_plot(df_simulations, df_best_results, "Risk vs. Return", plot_real_return=False)
    st.plotly_chart(fig1)

    fig2 = plot_compound_returns(df_compounded_returns)
    st.plotly_chart(fig2)

    fig3 = plot_pie_charts_side_by_side(df_best_results, labels)
    st.plotly_chart(fig3)

    
    st.subheader("Tables")
    # Create two columns for the layout
    col1, col2 = st.columns(2)

    # Tables
    with col1:
        st.write("Mean Returns")
        st.dataframe(df_mean_returns.set_index('Ticker'))

        st.write("Correlation Matrix")
        st.dataframe(df_correlation_matrix.set_index('Ticker'))
    
    with col2:
        st.write("Volatility")
        st.dataframe(df_volatility.set_index('Ticker'))

        st.write("Covariance Matrix")
        st.dataframe(df_cov_matrix.set_index('Ticker'))


    

  