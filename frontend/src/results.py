import os
os.chdir('../../')

import streamlit as st
import pandas as pd
import plotly.express as px

st.title('Scatterplot Example')

# Read data (modify path as needed)
data_path = '../data/simulations.csv'

df = pd.read_csv(data_path, delimiter=';')
print(df.head())
# Creating a scatterplot
# fig = px.scatter(df, x='x_column_name', y='y_column_name', title='Your Scatterplot Title')

# Display the plot
# st.plotly_chart(fig)