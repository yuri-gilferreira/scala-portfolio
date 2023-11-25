import streamlit as st

from inputs import show_inputs_page
from results import show_results_page
from home import show_home_page
from intro import show_intro_page

sidebar = st.sidebar
page = sidebar.radio("Select a Page", ["Home", "How it works","Inputs", "Results"])


if page == "Home":
    show_home_page()

elif page == "How it works":
    show_intro_page()

elif page == "Inputs":
    show_inputs_page()

elif page == "Results":
    show_results_page()