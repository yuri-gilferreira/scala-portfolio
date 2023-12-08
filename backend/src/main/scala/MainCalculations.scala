package com.YuriFerreira.PortfolioOptimization

import com.YuriFerreira.PortfolioOptimization.ApiCallAlphaVantage
import com.YuriFerreira.PortfolioOptimization.SparkFunctions

import org.apache.spark.sql.{SparkSession, DataFrame, Row}
import org.apache.spark.sql.types.{StructField, StructType, StringType, DoubleType}

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._


case class SparkRDDConfig(name: String, masterUrl: String, transactionFile: String)

object MainCalculations {

def getSingleStock(ticker: String, spark: SparkSession, apikey:String, min_date: String, max_date: String): DataFrame = {
    val apiString = ApiCallAlphaVantage.fetchDataFromAPI(ticker, apikey,outputsize = "full")
    val fullDataset = SparkFunctions.loadData(spark, apiString).orderBy(col("timestamp").asc)
    val filteredDataet = fullDataset.filter(col("timestamp") >= min_date && col("timestamp") <= max_date)
    filteredDataet.select(col("timestamp"), col("adjusted_close").alias(s"${ticker}_adjusted_close"))
}

def getMultipleStocks(tickers: List[String], spark: SparkSession, apikey:String, min_date: String, max_date: String): DataFrame = {
    val dfs = tickers.map(ticker => getSingleStock(ticker, spark, apikey, min_date, max_date))
    dfs.reduce(_.join(_, Seq("timestamp"), "inner")).orderBy(col("timestamp").desc)
}

def dailyReturnSingleStock(dataset:DataFrame, ticker: String): DataFrame = {
    
    val windowSpec = Window.orderBy(col("timestamp").asc)
    val dailyReturnColumn = (col(s"${ticker}_adjusted_close") - lag(col(s"${ticker}_adjusted_close"), 1).over(windowSpec)) /
                          lag(col(s"${ticker}_adjusted_close"), 1).over(windowSpec) * 100.0
  
    val result = dataset
      .withColumn("daily_return", dailyReturnColumn)
      .select(col("timestamp"), col("daily_return").alias(s"${ticker}_daily_return"))
      .orderBy(col("timestamp").asc) 

    result
}
  
def dailyReturnMultipleStocks(tickers: List[String], spark: SparkSession, apikey:String, min_date: String, max_date: String): DataFrame = {
    val dataset = getMultipleStocks(tickers, spark, apikey,min_date, max_date)
    val result = tickers.map(ticker => dailyReturnSingleStock(dataset, ticker))
    result.reduce(_.join(_, Seq("timestamp"), "inner")).orderBy(col("timestamp").desc)
}



def dailyReturnMultipleStocksOptimized(allStocksData: DataFrame): DataFrame = {
  val windowSpec = Window.orderBy(col("timestamp").asc)
  val dailyReturns = allStocksData.columns
    .filter(_.endsWith("_adjusted_close"))
    .map { colName =>
      val ticker = colName.replace("_adjusted_close", "")
      val dailyReturnColumn = (col(colName) - lag(col(colName), 1).over(windowSpec)) /
                              lag(col(colName), 1).over(windowSpec) * 100.0

      allStocksData.withColumn(s"${ticker}_daily_return", dailyReturnColumn)
        .select(col("timestamp"), col(s"${ticker}_daily_return"))
    }

  val combinedDailyReturns = dailyReturns.reduce(_.join(_, Seq("timestamp"), "inner"))
  combinedDailyReturns.orderBy(col("timestamp").desc)
}


def calculateMeanReturn(dataFrame: DataFrame, ticker: String): Double = {
  val meanDailyReturn = dataFrame
    .agg(avg(col(s"${ticker}_daily_return")))
    .first()
    .getAs[Double](0)

  val annualizedMeanReturn = meanDailyReturn * 252
  annualizedMeanReturn
}

def calculateVolatility(dataFrame: DataFrame, ticker: String): Double = {
  val dailyVolatility = dataFrame
    .agg(stddev(col(s"${ticker}_daily_return")))
    .first()
    .getAs[Double](0)

  val annualizedVolatility = dailyVolatility * scala.math.sqrt(252)
  annualizedVolatility
}

def createReturnAndVolatilityDataFrames(dataFrame: DataFrame, tickers: Seq[String], spark: SparkSession): (DataFrame, DataFrame) = {
  import spark.implicits._

  // Calculating Mean Return and Volatility for each ticker
  val meanReturns = tickers.map(ticker => (ticker, calculateMeanReturn(dataFrame, ticker)))
  val volatilities = tickers.map(ticker => (ticker, calculateVolatility(dataFrame, ticker)))

  // Creating DataFrames
  val meanReturnDF = meanReturns.toDF("Ticker", "MeanReturn")
  val volatilityDF = volatilities.toDF("Ticker", "Volatility")

  (meanReturnDF, volatilityDF)
}

def calculateCorrelationMatrix(dataFrame: DataFrame, tickers: Seq[String], spark: SparkSession): DataFrame = {
  import spark.implicits._

  // Calculating correlations for each pair of tickers, including self-correlation
  val correlations = for {
    ticker1 <- tickers
    ticker2 <- tickers
    corr = if (ticker1 == ticker2) 1.0 else dataFrame.stat.corr(s"${ticker1}_daily_return", s"${ticker2}_daily_return")
  } yield (ticker1, ticker2, corr)

  val correlationDF = correlations.toDF("Ticker1", "Ticker2", "Correlation")

  // Pivoting the DataFrame to get the matrix format
  val pivotDF = correlationDF
    .groupBy("Ticker1")
    .pivot("Ticker2", tickers) // Use the original tickers sequence
    .agg(first("Correlation"))

  // Sort the DataFrame to match the original tickers order for rows
  val orderedDF = pivotDF.orderBy($"Ticker1".asc)

  // Ensure the DataFrame is symmetrical
  val symmetricalDF = tickers.foldLeft(orderedDF) { (df, ticker) =>
    tickers.foldLeft(df) { (innerDf, innerTicker) =>
      if (ticker != innerTicker) {
        innerDf.withColumn(innerTicker, coalesce(col(innerTicker), col(ticker)))
      } else {
        innerDf
      }
    }
  }

  symmetricalDF
}

def annualizeCovarianceMatrix(covarianceMatrixDF: DataFrame): DataFrame = {
  covarianceMatrixDF.columns.foldLeft(covarianceMatrixDF) { (df, colName) =>
    df.withColumn(colName, col(colName) * scala.math.sqrt(252))
  }
}


def calculateCovarianceMatrix(dataFrame: DataFrame, tickers: Seq[String], spark: SparkSession): DataFrame = {

  val covariances = for {
    ticker1 <- tickers
    ticker2 <- tickers
    cov = dataFrame.stat.cov(s"${ticker1}_daily_return", s"${ticker2}_daily_return")
  } yield ((ticker1, ticker2), cov)

  val covarianceMap = covariances.toMap
  val schema = StructType(StructField("Ticker", StringType, nullable = false) +: tickers.map(StructField(_, DoubleType, nullable = true)))
  val initialRows = tickers.map(ticker => Row.fromSeq(ticker +: List.fill(tickers.length)(null)))

  val rows = initialRows.map { case Row(ticker: String, _*) =>
    val rowValues = tickers.map(tickerCol => covarianceMap.getOrElse((ticker, tickerCol), Double.NaN))
    Row.fromSeq(ticker +: rowValues)
  }

  val covarianceDF = spark.createDataFrame(spark.sparkContext.parallelize(rows), schema)
  val annualizedcovarianceMatrixDF = annualizeCovarianceMatrix(covarianceDF)
  annualizedcovarianceMatrixDF
}



  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Portfolio-Optimization")
      .master("local[*]") // Use "local[*]" for local testing; replace with your cluster settings for production
      .getOrCreate()
    val apiKey = sys.env("ALPHA_VANTAGE_API_KEY")
    val stockList = List("MSFT", "KO", "TSLA", "AAPL", "IBM", "AMZN", "GOOG")
    val min_date = "2023-10-01"
    val max_date = "2023-10-10"

    val dfMutipleStocks = getMultipleStocks(stockList, spark, apiKey, min_date, max_date)
    val PortfolioDailyReturn = dailyReturnMultipleStocksOptimized(dfMutipleStocks)
    val (meanReturnDF, volatilityDF) = createReturnAndVolatilityDataFrames(PortfolioDailyReturn, stockList, spark)

    
    val correlationMatrixDF = calculateCorrelationMatrix(PortfolioDailyReturn, stockList, spark)
    val covarianceMatrixDF = calculateCovarianceMatrix(PortfolioDailyReturn, stockList, spark)
    
    println("Mean Return DataFrame")
    meanReturnDF.show()

    println("Volatility DataFrame")
    volatilityDF.show()

    println("Correlation Matrix")
    correlationMatrixDF.show()
    
    println("Covariance Matrix")
    covarianceMatrixDF.show()

  }

}
