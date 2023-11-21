file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/src/test/scala/MainCalculationsTest.scala
### java.lang.NullPointerException: Cannot invoke "scala.reflect.internal.Symbols$Symbol.owner()" because the return value of "scala.reflect.internal.Trees$Apply.symbol()" is null

occurred in the presentation compiler.

action parameters:
offset: 1401
uri: file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/src/test/scala/MainCalculationsTest.scala
text:
```scala
package com.YuriFerreira.PortfolioOptimization

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Row

import com.YuriFerreira.PortfolioOptimization.TestUtils
import org.apache.spark.sql.types.{StructType, StructField, DoubleType, StringType, NullType}


class MainCalculationsTest extends AnyWordSpec with Matchers {

  val spark: SparkSession = SparkSession.builder()
        .appName("Testing")
        .master("local[*]")
        .getOrCreate()
  
  
  "getSingleStock" should{
    "get the correct values for" in{
      val ticker = "IBM"
      val min_date = "2023-10-01"
      val max_date = "2023-10-10"
      val testing = MainCalculations.getSingleStock(ticker, spark, min_date, max_date)
      testing.show()
    }
  }
  
  "getMultipleStocks" should {
    "get the correct values for" in {
      val tickers = List("IBM", "AAPL", "MSFT")
      val min_date = "2023-10-01"
      val max_date = "2023-10-10"
      val testing = MainCalculations.getMultipleStocks(tickers, spark, min_date, max_date)
      testing.show()
    }
  }

"dailyReturnMultipleStocksOptimized" should {
  "correctly calculate daily returns for " {
     val tickers = List("IBM", "AAPL")
      val min_date = "2013-10-01"
      val max_date = "2023-10-10"
      val testi@@ng = MainCalculations.getMultipleStocks(tickers, spark, min_date, max_date)
      
      // val result = MainCalculations.dailyReturnMultipleStocksOptimized(testing)
      result.show()
  }
}
  "dailyReturnSingleStock" should {
    "correctly calculate daily returns for" in {
      import spark.implicits._
           val ticker = "IBM"
           val data = Seq(
             ("2023-01-01", 100.0),
             ("2023-01-02", 102.0),
             ("2023-01-03", 101.0)
           ).toDF("timestamp", s"${ticker}_adjusted_close")
             .withColumn("timestamp", col("timestamp").cast("timestamp"))
          val schema = StructType(
            StructField("timestamp", StringType, nullable = true) ::
              StructField("ticker_daily_return", DoubleType, nullable = true) :: Nil
              )
           val expected = spark.createDataFrame(
            spark.sparkContext.parallelize(Seq(
              Row("2023-01-01", null),
              Row("2023-01-02", 2.0),
              Row("2023-01-03", -0.98039216)
              )),
              schema).withColumn("timestamp", col("timestamp").cast("timestamp"))
           val result = MainCalculations.dailyReturnSingleStock(data, "IBM")
           // Compare the result with the expected output
           result.show()
           TestUtils.assertDataFrameApproxEqual(result, expected, 0.0001)
  }


}

"calculateMeanReturn" should {
  "correctly calculate mean return" in {
          import spark.implicits._
          val mockData = Seq(
            ("2023-01-01", 0.5),
            ("2023-01-02", 1.0),
            ("2023-01-03", 1.5),
            ("2023-01-04", 2.0)
            ).toDF("timestamp", "ticker_daily_return")

    val result = MainCalculations.calculateMeanReturn(mockData, "ticker")

    // Create DataFrames for the result and the expected value
    val resultDF = Seq((result)).toDF("annualized_mean_return")
    val expectedAnnualizedMeanReturn = (0.5 + 1.0 + 1.5 + 2.0) / 4 * 252
    val expectedDF = Seq((expectedAnnualizedMeanReturn)).toDF("annualized_mean_return")

    // Assert using TestUtils
    TestUtils.assertDataFrameApproxEqual(resultDF, expectedDF, 0.0001)
    
  }
}
}
```



#### Error stacktrace:

```
scala.meta.internal.pc.PcCollector.soughtTreeFilter$1(PcCollector.scala:248)
	scala.meta.internal.pc.PcCollector.$anonfun$resultWithSought$9(PcCollector.scala:266)
	scala.meta.internal.pc.PcCollector.$anonfun$resultWithSought$9$adapted(PcCollector.scala:266)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:348)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$19(PcCollector.scala:469)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:469)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$19(PcCollector.scala:469)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:469)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$10(PcCollector.scala:409)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:409)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$10(PcCollector.scala:409)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:409)
	scala.meta.internal.pc.PcCollector.traverseSought(PcCollector.scala:472)
	scala.meta.internal.pc.PcCollector.resultWithSought(PcCollector.scala:266)
	scala.meta.internal.pc.PcCollector.result(PcCollector.scala:207)
	scala.meta.internal.pc.PcDocumentHighlightProvider.highlights(PcDocumentHighlightProvider.scala:29)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$documentHighlight$1(ScalaPresentationCompiler.scala:340)
```
#### Short summary: 

java.lang.NullPointerException: Cannot invoke "scala.reflect.internal.Symbols$Symbol.owner()" because the return value of "scala.reflect.internal.Trees$Apply.symbol()" is null