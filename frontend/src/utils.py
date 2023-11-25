
import ast
import plotly.express as px
import plotly.graph_objects as go
import subprocess
import requests
from plotly.subplots import make_subplots


def round_weights_str(weights_str):
    weights_list = ast.literal_eval(weights_str)
    rounded_weights = [round(weight, 2) for weight in weights_list]
    return rounded_weights


def format_return_dataset(df):
    df['weights'] = df['weights'].apply(round_weights_str)
    df = df.round({'risk': 2, 'return': 2, 'real_return': 2, 'sharpe_ratio' : 2})
    return df

def format_correlation_matrix(df, tickers):
    df.rename(columns={'Ticker1': 'Ticker'}, inplace=True)
    df = df.set_index('Ticker').loc[tickers].reset_index()
    return df

def format_cov_matrix(df, tickers):
    df['Ticker'] = tickers
    return df


def create_risk_return_plot(df_simulations, df_best_results, plot_title, plot_real_return=False):
    y_column = 'real_return' if plot_real_return else 'return'

    fig = px.scatter(df_simulations, x='risk', y=y_column, hover_data=['sharpe_ratio', 'weights'])

    fig.update_layout(
        title={
            'text': plot_title,
            'y': 0.95,
            'x': 0.5,
            'xanchor': 'center',
            'yanchor': 'top',
            'font': {'size': 24}
        },
        xaxis_title='Risk',
        yaxis_title=y_column.capitalize(),
        hovermode='closest'
    )

    for trace in fig.data:
        trace.marker.opacity = 0.05
    labels = ["Best Sharpe", "Less Risk", "Original Weight"]
    colors = px.colors.qualitative.Plotly_r

    for i, row in df_best_results.iterrows():
        label = labels[i] if i < len(labels) else f'Point {i}'

        hovertemplate = (f'Risk: %{{x:.3f}}<br>' +
                         f'{y_column.capitalize()}: %{{y:.3f}}<br>' +
                         f'Sharpe Ratio: {row["sharpe_ratio"]}<br>' +
                         f'Weights: {row["weights"]}') 

        fig.add_trace(go.Scatter(
            x=[row['risk']],
            y=[row[y_column]],
            mode='markers+text',  
            marker=dict(
                color=colors[i % len(colors)],  
                size=15, 
                line=dict(
                    color='Black',  
                    width=2
                ),
                opacity=1  
            ),
            name=label,  
            textposition='top center',  
            hovertemplate=hovertemplate
        ))

    # fig.show()
    return fig


def plot_compound_returns(df):
    fig = go.Figure()

    labels = ["Best Sharpe", "Less Risk", "Original Weight"]

    fig.add_trace(go.Scatter(
        x=df['timestamp'], 
        y=df['portfolio_sharpe_compound_return'] * 100,
        mode='lines',
        name=labels[0]  # Set the label for the legend
    ))


    fig.add_trace(go.Scatter(
        x=df['timestamp'], 
        y=df['portfolio_risk_compound_return'] * 100,
        mode='lines',
        name=labels[1]
    ))

    fig.add_trace(go.Scatter(
        x=df['timestamp'], 
        y=df['portfolio_original_compound_return'] * 100,
        mode='lines',
        name=labels[2]
    ))

    fig.update_layout(
        title={
            'text': 'Compound Returns Over Time',
            'y': 0.95,
            'x': 0.5,
            'xanchor': 'center',
            'yanchor': 'top',
            'font': {'size': 24}
        },
        xaxis_title='Time',
        yaxis_title= "$100 Investment Returns Over Time",
        yaxis_tickprefix="$",
        yaxis_tickformat=',.2f'
    )

    # Show the plot
    return fig

def plot_pie_charts_side_by_side(df, labels):
    fig = make_subplots(rows=1, cols=len(df), specs=[[{'type': 'pie'}]*len(df)])
    title_labels = ["Best Sharpe", "Less Risk", "Original Weight"]
    
    for i, row in enumerate(df.itertuples(), start=1):
        weights = row.weights
        fig.add_trace(go.Pie(labels=labels, 
                             values=weights, 
                             name=title_labels[i-1], 
                             hoverinfo='label+percent'), row=1, col=i)
    
    for i, label in enumerate(title_labels, start=1):
        fig.add_annotation(
            text=label,
            x=(i-0.5)/len(df), xref="paper",
            y=1, yref="paper",
            showarrow=False,
            font_size=16,
            yanchor="bottom",
            xanchor="center"
        )
    
    fig.update_layout(
        title_text='Portfolio Weights Comparison',
        title_x=0.3,
        title_font_size=24
    )
    return fig

def run_scala_script(tickers, weights):
    # Define the path to your Scala script
    scala_script = "../../backend/src/main/scala/Main.scala"

    # Construct the command to run the Scala script with arguments
    command = ["scala", scala_script, "--tickers", tickers, "--weights", weights]

    try:
        # Run the Scala script using subprocess
        result = subprocess.run(command, capture_output=True, text=True, check=True)
        # Print the output of the Scala script
        print(result.stdout)
    except subprocess.CalledProcessError as e:
        print("Error:", e.stderr)


# Function to query AlphaVantage API
def search_symbol(query, api_key):
    url = f'https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords={query}&apikey={api_key}'
    response = requests.get(url)
    return response.json()


