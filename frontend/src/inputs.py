import streamlit as st
import requests
from datetime import timedelta, datetime
from utils import search_symbol

def show_inputs_page():
    st.title("Inputs")
    user_api_key = st.text_input("Enter your AlphaVantage API Key:", type="password")
    query = st.text_input("Search for a stock symbol:")
    if query:
        results = search_symbol(query, user_api_key)

        if 'bestMatches' in results:
            matches = results['bestMatches']
            for match in matches:
                st.text(f"Symbol: {match['1. symbol']} - Name: {match['2. name']}")
        else:
            st.error("No matches found")

    tickers_input = st.text_area("Enter tickers, separated by commas:")

    # Advanced Parameters
    with st.expander("Advanced Parameters"):
        today = datetime.today()
        three_years_ago = today - timedelta(days=3*365)  # Approximately 3 years ago

        start_date = st.date_input("Start Date", three_years_ago)
        end_date = st.date_input("End Date", today)
        
        weights_input = st.text_area("Enter corresponding weights, separated by commas:\n(Leave as blank to use equal weights)")
        risk_free_rate = st.number_input("Risk-Free Rate (as a percentage):", min_value=0.0, max_value=100.0, value=2.5)
        num_simulations = st.number_input("Number of Simulations:", min_value=1000, max_value=100000, value=10000, step=1000)

    if st.button('Optimize Portfolio'):
        tickers = [ticker.strip().upper() for ticker in tickers_input.split(',') if ticker]
        weights = [float(weight.strip()) for weight in weights_input.split(',') if weight] if weights_input else [1/len(tickers)] * len(tickers)

        if len(tickers) != len(weights):
            st.error("The number of tickers and weights must be the same.")
        elif sum(weights) > 1.0:
            st.error("The sum of weights should not exceed 1.")
        else:
            data = {
                "stockList": ','.join(tickers),
                "originalWeights": ','.join(map(str, weights)),
                "dateRange": f"{start_date.strftime('%Y-%m-%d')},{end_date.strftime('%Y-%m-%d')}",
                "riskFreeRate": risk_free_rate / 100,  # Convert percentage to a decimal
                "numSimulations": num_simulations,
                "apiKey": user_api_key
            }
            # st.write(data)
            st.write("Running optimization...")
            response = requests.post("http://localhost:8080/run-optimization", json=data)
            if response.status_code == 200:
                st.success("Optimization completed successfully.")
            else:
                st.error("Failed to run optimization.")
