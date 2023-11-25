package com.YuriFerreira.PortfolioOptimization

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.apache.spark.sql.SparkSession
import com.YuriFerreira.PortfolioOptimization.PortfolioReturns 
import org.apache.spark.sql.types.{StructType, StructField, DoubleType, StringType}
import org.apache.spark.sql.functions._

import org.apache.spark.sql.Row

class PortfolioReturnsTest extends AnyWordSpec with Matchers {
  val apikey = sys.env("ALPHA_VANTAGE_API_KEY")
  val spark: SparkSession = SparkSession.builder()
        .appName("Testing")
        .master("local[*]")
        .getOrCreate()
  
  "portfolioDailyReturn" should {
    "get the correct values for" in {
      val stockList = List("IBM", "AAPL", "MSFT")
      val min_date = "2023-10-05"
      val max_date = "2023-10-10"
      val dfMutipleStocks = MainCalculations.getMultipleStocks(stockList, spark, apikey,  min_date, max_date)
      val dfDailyReturn = MainCalculations.dailyReturnMultipleStocksOptimized(dfMutipleStocks)
      val result = PortfolioReturns.portfolioDailyReturn(Array(0.3, 0.3, 0.4), dfDailyReturn, stockList, "test")

      val expectedSchema = StructType(
            StructField("timestamp", StringType, nullable = true) ::
              StructField("ticker_daily_return", DoubleType, nullable = true) :: Nil
              )

      val expectedData = Seq(
        Row("2023-10-10", -0.29297), 
        Row("2023-10-09", 0.6023),
        Row("2023-10-06", 1.540104),
        Row("2023-10-05", null)
        )
      val expected = spark.createDataFrame(
        spark.sparkContext.parallelize(expectedData),
        StructType(expectedSchema)
      ).withColumn("timestamp", col("timestamp").cast("timestamp"))

      result.show()
      TestUtils.assertDataFrameApproxEqual(result, expected, 0.0001)

    }
  }
  "getAllPortfolioReturns" should {
    "get the correct values for" in {
      import spark.implicits._
      val stockList = List("IBM", "AAPL", "MSFT")
      val min_date = "2023-10-05"
      val max_date = "2023-10-10"
      val dfMutipleStocks = MainCalculations.getMultipleStocks(stockList, spark, apikey, min_date, max_date)
      val dfDailyReturn = MainCalculations.dailyReturnMultipleStocksOptimized(dfMutipleStocks)

      val bestResultsDF = Seq(
        ("sharpe", (0.0,0.6,0.4)),
        ("test", (0.3,0.3,0.4)),
        ("risk", (0.4,0.5,0.1))
      ).toDF("type", "weights")

         val result = PortfolioReturns.getAllPortfolioReturns(bestResultsDF, dfDailyReturn, stockList)
        result.show()
            }
  }
  "getCompoundReturnSinglePortfolio" should {
    "get the correct values for" in {
      val stockList = List("IBM", "AAPL", "MSFT")
      val min_date = "2023-10-05"
      val max_date = "2023-10-10"
      val dfMutipleStocks = MainCalculations.getMultipleStocks(stockList, spark, apikey,  min_date, max_date)
      val dfDailyReturn = MainCalculations.dailyReturnMultipleStocksOptimized(dfMutipleStocks)

      val portfolioReturns = PortfolioReturns.portfolioDailyReturn(Array(0.3, 0.3, 0.4), dfDailyReturn, stockList, "test")
      val result = PortfolioReturns.getCompoundReturnSinglePortfolio(portfolioReturns, "test")
      result.show()
    }
  }

  "compoundReturnMultiplePortfolios" should {
    "get the correct values for" in {

      val portfoliosSchema = StructType(
            StructField("timestamp", StringType, nullable = true) ::
            StructField("portfolio_original_daily_return", DoubleType, nullable = true) ::
            StructField("portfolio_risk_daily_return", DoubleType, nullable = true) ::
            StructField("portfolio_sharpe_daily_return", DoubleType, nullable = true) :: Nil
              )

      val DailyReturnsData = Seq(
        Row("2023-10-10", -0.3745564850538239, -0.29297954949207733, -0.23628056718231147),
        Row("2023-10-09", 0.8199718781543834, 0.6023443744891226, 0.5486614907653703),
        Row("2023-10-06", 1.874505543016905, 1.5401041781091078, 1.1290411305868073),
        Row("2023-10-05", null, null, null)
        )
      
      val DailyReturnsPortfoliosDf = spark.createDataFrame(
        spark.sparkContext.parallelize(DailyReturnsData),
        StructType(portfoliosSchema)
      ).withColumn("timestamp", col("timestamp").cast("timestamp"))

      val result = PortfolioReturns.compoundReturnMultiplePortfolios(DailyReturnsPortfoliosDf)
      result.show()

    }
  }



}