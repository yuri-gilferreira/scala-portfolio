import streamlit as st


def show_intro_page():
    st.title("How it works")
    st.markdown("## How the Backend Optimizes Your Portfolio")
    st.markdown("""
                #### Behind the Scenes: Advanced Calculations and Parallel Processing
                - 🚀 **Scala-Powered Backend**:  Our application leverages Scala's powerful capabilities, renowned for handling complex data-intensive tasks efficiently. Scala's functional programming features, combined with its concurrency support, make it an ideal choice for financial data processing and analysis.
                - 📈 **Real-Time Data Retrieval**:  When you input stock symbols, the backend immediately queries the AlphaVantage API to fetch real-time market data. This includes stock prices, trading volumes, and other relevant financial metrics.

                - 📊 **Data Analysis with Advanced Algorithms**: The retrieved data undergoes rigorous analysis. We utilize sophisticated algorithms to compute historical returns, assess volatility, and determine the correlations between different stocks in your portfolio. This analysis forms the basis for the optimization process.

                - ⚡ **Parallel Monte Carlo Simulations**: One of the core strengths of our tool is running Monte Carlo simulations, a method used to model the probability of different outcomes in a process that cannot easily be predicted due to random variables. 
                    -  We perform 10,000 simulations to explore a vast array of potential portfolio allocations.
                    -  These simulations are executed in parallel, harnessing Scala's concurrency features. This parallel processing dramatically speeds up the computation, allowing for a thorough exploration of possible investment scenarios in a fraction of the time it would take sequentially.
  
                - ⚖️ **Optimization Based on Modern Portfolio Theory**:  The simulations aim to identify portfolio allocations that optimize the risk-return profile, adhering to the principles of Modern Portfolio Theory (MPT). We look for the most efficient portfolios that offer the highest expected return for a given level of risk.

                - 🐍 **Visualization and Analysis**:  Once the calculations are complete, the results are sent back to the frontend of the application . Here, they are visualized and presented in an easy-to-understand format, allowing you to make informed investment decisions based on robust, data-driven analysis.
                """)

    
    st.subheader("")
    
    resources_folder = "../../resources/"
    st.image(resources_folder + "diagram.png", caption='Diagram of the Project')

    st.markdown("""
                #### Understanding Modern Portfolio Theory (MPT)
                Modern Portfolio Theory (MPT), introduced by Harry Markowitz in 1952, is a fundamental theory in finance that helps in constructing an investment portfolio. MPT assumes that investors are risk-averse; they prefer a less risky portfolio to a riskier one if both portfolios offer the same expected return. Key concepts include:
                - **Diversification**: By investing in a variety of assets, specific risks can be mitigated. The idea is that the performance of different assets will not be perfectly correlated, thus reducing the overall risk of the portfolio.
                - **Efficient Frontier**: This concept refers to an investment portfolio which occupies the 'efficient' parts of the risk-return spectrum. The efficient frontier is the set of optimal portfolios providing the maximum possible expected return for a given level of risk.
                - **Risk-Return Trade-Off**: It highlights the fundamental trade-off in investing: higher returns come with higher risk. MPT quantitatively measures this through mathematical models.
                
                ##### **Mathematical Framework**:
                **Expected Return of a Portfolio**: The return of a portfolio is the weighted sum of the individual asset returns, where weights represent the proportion of each asset in the portfolio. 
                """)              
    st.markdown(r'##### $$ R_p = \sum_{i=1}^{n} w_i R_i $$', unsafe_allow_html=True)
    st.markdown(r"""
                Where:
                - $$ R_p $$ is the portfolio return.
                - $$ w_i $$ the weight of the $$ i^{th} $$ asset. 
                - $$ R_i $$ the return of the $$ i^{th} $$ asset.
                """, unsafe_allow_html=True)
    st.markdown("""
                **Portfolio Variance and Standard Deviation**: In MPT, the portfolio variance for a multi-asset portfolio can be represented using the covariance matrix. The formula for the portfolio variance using the covariance matrix is :
                """)
    st.markdown(r'##### $$ \sigma_p^2 = \mathbf{w}^T \mathbf{\Sigma} \mathbf{w} $$', unsafe_allow_html=True)
    st.markdown(r"""
                Where:
                - $$ \sigma_p^2 $$ is the portfolio variance.
                - $$ \mathbf{w} $$ is the vector of weights of assets in the portfolio.
                - $$ \mathbf{\Sigma} $$ is the covariance matrix of asset returns.
                - $$ \mathbf{w}^T $$ is the transpose of the weight vector.
                """, unsafe_allow_html=True)

    st.markdown(""" Our tool uses these principles and mathematical models to assist you in creating a portfolio tailored to your desired risk and return levels.""")
    st.markdown(""" If you would like to learn more about MPT, check out this [article](https://www.investopedia.com/terms/m/modernportfoliotheory.asp) on Investopedia.""")    

