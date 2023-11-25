# Portfolio Optimization Tool

## Introduction
This Portfolio Optimization Tool is designed to help investors create an efficient portfolio using Modern Portfolio Theory (MPT). It leverages a Scala backend for heavy-duty data processing and Python with Streamlit for an interactive front-end experience.

## Features
- Real-time stock data retrieval from AlphaVantage API.
- Sophisticated algorithms to calculate historical returns, volatility, and stock correlations.
- Execution of 10,000 Monte Carlo simulations in parallel to optimize the portfolio.
- Visualization of the optimized portfolio and statistical analysis using Python and Streamlit.

## How It Works
1. **Scala Backend**: Performs data retrieval, analysis, and simulation.
   - Utilizes functional programming and concurrency for efficient processing.
   - Runs Monte Carlo simulations in parallel to optimize computation time.
2. **Python and Streamlit Frontend**: For interactive user experience and data visualization.
   - Implements intuitive UI for input and result presentation.
   - Uses Python's rich data science libraries for dynamic visualizations.

## Quick Start
Clone the repository and navigate to the project directory:

```bash
git clone https://github.com/<your-username>/<repository-name>.git
cd <repository-name>
```