package com.YuriFerreira.PortfolioOptimization

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window


object PortfolioReturns {
  def portfolioDailyReturn(weights: Array[Double], dailyReturns : DataFrame, tickers: List[String], portfolioName : String): DataFrame = {
    
    val mapWeightedReturns = tickers.map(ticker => (col(s"${ticker}_daily_return") * weights(tickers.indexOf(ticker))).alias(s"${ticker}_weighted_return"))
    val mapReturns = tickers.map(ticker => col(s"${ticker}_daily_return"))
    val selectedColumns = List(col("timestamp"))++ mapReturns ++ mapWeightedReturns
    val weightedReturns = dailyReturns.select(selectedColumns: _*)
    val portfolioDailyReturn = weightedReturns.withColumn(s"portfolio_${portfolioName}_daily_return", mapWeightedReturns.reduce(_ + _))
    portfolioDailyReturn.select(col("timestamp"), col(s"portfolio_${portfolioName}_daily_return"))
  }

def getAllPortfolioReturns(bestResultsDf : DataFrame, dailyReturns : DataFrame, tickers: List[String]): DataFrame = {

  val portfolioType = bestResultsDf.select(col("type")).collect().map(_.getString(0))

  val portfolioReturns = portfolioType.map { portfolioName =>
    val weightsString = bestResultsDf
      .filter(col("type") === portfolioName)
      .select(col("weights"))
      .take(1).toSeq(0)(0).toString()

    val weights = weightsString
      .substring(1, weightsString.length - 1)
      .split(",")
      .map(_.toDouble)

    portfolioDailyReturn(
      weights,
      dailyReturns,
      tickers,
      portfolioName
    )
  }

  val combinedReturns = portfolioReturns.reduce(_.join(_, Seq("timestamp"), "inner")).orderBy(col("timestamp").desc)

  combinedReturns
}

def getCompoundReturnSinglePortfolio(portfolioReturns: DataFrame, portfolioName: String): DataFrame = {
  val windowSpec = Window.orderBy(col("timestamp")).rowsBetween(Window.unboundedPreceding, 0)
  val compoundReturns = portfolioReturns.withColumn(
    s"portfolio_${portfolioName}_compound_return",
    exp(sum(log(col(s"portfolio_${portfolioName}_daily_return") + 1)).over(windowSpec)) - 1
  )
  compoundReturns
}

def compoundReturnMultiplePortfolios(portfolioReturns: DataFrame): DataFrame = {
  val windowSpec = Window.orderBy("timestamp").rowsBetween(Window.unboundedPreceding, 0)
  var compoundedReturns = portfolioReturns
  var newColumns = Seq(col("timestamp"))
  val portfolios = Seq("original", "risk", "sharpe")

  for (portfolio <- portfolios) {
    val dailyReturnColumn = s"portfolio_${portfolio}_daily_return"
    val compoundReturnColumn = s"portfolio_${portfolio}_compound_return"

    if (portfolioReturns.columns.contains(dailyReturnColumn)) {
      compoundedReturns = compoundedReturns.withColumn(
        compoundReturnColumn,
        exp(sum(log(col(dailyReturnColumn)/100 + 1)).over(windowSpec))
      )
      newColumns = newColumns :+ col(compoundReturnColumn)
    }
  }

  compoundedReturns.na.fill(1.0).select(newColumns: _*)
}

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
        .appName("Testing")
        .master("local[*]")
        .getOrCreate()
    val stockList = List("IBM", "AAPL", "MSFT")
    val min_date = "2023-10-01"
    val max_date = "2023-10-10"
    val dfMutipleStocks = MainCalculations.getMultipleStocks(stockList, spark, min_date, max_date)
    val dfDailyReturn = MainCalculations.dailyReturnMultipleStocksOptimized(dfMutipleStocks)
    val testing = portfolioDailyReturn(Array(0.3, 0.3, 0.4), dfDailyReturn, stockList, "test")
    dfDailyReturn.show()
    testing.show()


  }

    

  
}
