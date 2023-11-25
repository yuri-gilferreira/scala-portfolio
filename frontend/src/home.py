import streamlit as st


def show_home_page():
    st.title("Welcome to the Portfolio Optimization Tool")
    st.markdown("""
        This tool is designed to help investors optimize their investment portfolios using advanced data analysis techniques.
        
        **Features include:**
        - Real-time stock data retrieval
        - Customizable portfolio composition
        - Risk-return optimization analysis
        """)
    
    st.subheader("How to Use")
    st.markdown("""
        1. Navigate to the **Inputs** page to enter your API AlphaVatnage key.
        2. Look for and enter stock symbols.
        3. Use the **Advanced Parameters** section to customize the optimization parameters.
        4. Click on **Optimize Portfolio** to run the calculation and optimization.
        5. Explore the **Results** page for detailed analysis and insights.
        """)
    
    resources_folder = "../../resources/"
    st.image(resources_folder + "risk_return.png", caption='Example of Risk vs Return Simulation Run')
    st.markdown("""
        #### Contact

For support or inquiries about the Portfolio Optimization Tool, feel free to reach out. You can contact me in the following ways:

- **Email**: [yuri.gferreira16@gmail.com](mailto:yuri.gferreira16@gmail.com)
- **LinkedIn**: [Yuri Gil Ferreira's LinkedIn](https://www.linkedin.com/in/yuri-ferreira-354a07154/) 
        """)
