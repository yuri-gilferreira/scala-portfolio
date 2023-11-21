package com.YuriFerreira.PortfolioOptimization

import com.YuriFerreira.PortfolioOptimization.ApiCallAlphaVantage
import com.YuriFerreira.PortfolioOptimization.SparkFunctions

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._


case class SparkRDDConfig(name: String, masterUrl: String, transactionFile: String)

object MainCalculations {

def getSingleStock(ticker: String, spark: SparkSession, min_date: String, max_date: String): DataFrame = {
    val apiString = ApiCallAlphaVantage.fetchDataFromAPI(ticker, outputsize = "full")
    val fullDataset = SparkFunctions.loadData(spark, apiString).orderBy(col("timestamp").asc)
    val filteredDataet = fullDataset.filter(col("timestamp") >= min_date && col("timestamp") <= max_date)
    filteredDataet.select(col("timestamp"), col("adjusted_close").alias(s"${ticker}_adjusted_close"))
}

def getMultipleStocks(tickers: List[String], spark: SparkSession, min_date: String, max_date: String): DataFrame = {
    val dfs = tickers.map(ticker => getSingleStock(ticker, spark, min_date, max_date))
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
  
def dailyReturnMultipleStocks(tickers: List[String], spark: SparkSession, min_date: String, max_date: String): DataFrame = {
    val dataset = getMultipleStocks(tickers, spark, min_date, max_date)
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

  // Preparing column names with daily return suffix
  val tickers_daily = tickers.map(ticker => s"${ticker}_daily_return")

  // Calculating correlations for each unique pair of tickers
  val correlations = for {
    ticker1 <- tickers_daily
    ticker2 <- tickers_daily
    if ticker1 != ticker2 
    corr = dataFrame.stat.corr(ticker1, ticker2)
  } yield (ticker1.replace("_daily_return", ""), ticker2.replace("_daily_return", ""), corr)

  val correlationDF = correlations.toDF("Ticker1", "Ticker2", "Correlation")

  // Pivoting the DataFrame to get the matrix format
  val correlationMatrixDF = correlationDF.groupBy("Ticker1").pivot("Ticker2").agg(first("Correlation")).na.fill(1.0)

  correlationMatrixDF
}

def annualizeCovarianceMatrix(covarianceMatrixDF: DataFrame): DataFrame = {
  covarianceMatrixDF.columns.foldLeft(covarianceMatrixDF) { (df, colName) =>
    df.withColumn(colName, col(colName) * scala.math.sqrt(252))
  }
}


def calculateCovarianceMatrix(dataFrame: DataFrame, tickers: Seq[String], spark: SparkSession): DataFrame = {
  import spark.implicits._

  // Preparing column names with daily return suffix
  val tickers_daily = tickers.map(ticker => s"${ticker}_daily_return")

  // Calculating covariances for each unique pair of tickers
  val covariances = for {
    ticker1 <- tickers_daily
    ticker2 <- tickers_daily
    cov = dataFrame.stat.cov(ticker1, ticker2)
  } yield (ticker1.replace("_daily_return", ""), ticker2.replace("_daily_return", ""), cov)

  val covarianceDF = covariances.toDF("Ticker1", "Ticker2", "Covariance")

  // Pivoting the DataFrame to get the matrix format
  val covarianceMatrixDF = covarianceDF.groupBy("Ticker1").pivot("Ticker2").agg(first("Covariance"))
  val annualizedcovarianceMatrixDF = annualizeCovarianceMatrix(covarianceMatrixDF)
  annualizedcovarianceMatrixDF
}

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Portfolio-Optimization")
      .master("local[*]") // Use "local[*]" for local testing; replace with your cluster settings for production
      .getOrCreate()
    val stockList = List("IBM", "AAPL", "MSFT")
    val min_date = "2018-10-01"
    val max_date = "2023-10-10"
    val dfMutipleStocks = MainCalculations.getMultipleStocks(stockList, spark, min_date, max_date)
    val PortfolioDailyReturn = MainCalculations.dailyReturnMultipleStocksOptimized(dfMutipleStocks)
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
