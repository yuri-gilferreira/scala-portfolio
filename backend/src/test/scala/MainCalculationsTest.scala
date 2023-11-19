package com.YuriFerreira.PortfolioOptimization

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.apache.spark.sql.SparkSession
class MainCalculationsTest extends AnyWordSpec with Matchers {

  "dailyReturnMultipleStocks" should {
    "correctly calculate daily returns for" in {
      val tickers = List("IBM", "AAPL", "MSFT")
      val spark = SparkSession.builder().master("local[*]").getOrCreate()
      val result = MainCalculations.dailyReturnMultipleStocks(tickers, spark)
      result.count() should be(5032)
    }

}

 "calculateMeanReturn" should {
  "correctly calculate mean return for" in {
    val spark: SparkSession = SparkSession.builder()
        .appName("MeanReturnTest")
        .master("local[*]")
        .getOrCreate()
    // val tickers = List("IBM", "AAPL", "MSFT")
    // val spark = SparkSession.builder().master("local[*]").getOrCreate()

    val dataTest = Seq(
      ("2020-01-01", 0.0001, 0.0002, 0.0003),
      ("2020-01-02", 0.0002, 0.0003, 0.0004),
      ("2020-01-03", 0.0003, 0.0004, 0.0005),
      ("2020-01-04", 0.0004, 0.0005, 0.0006),
      ("2020-01-05", 0.0005, 0.0006, 0.0007)
    )

    val dataframeTest = spark.createDataFrame(dataTest).toDF("timestamp", "IBM", "AAPL", "MSFT")
    val result = MainCalculations.calculateMeanReturn(dataframeTest, "IBM")
    
    result should be(0.0002 +- 0.0001)
  }
 }
}