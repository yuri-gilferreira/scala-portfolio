file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/src/main/scala/PortfolioReturns.scala
### java.lang.UnsupportedOperationException: Position.start on NoPosition

occurred in the presentation compiler.

action parameters:
uri: file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/src/main/scala/PortfolioReturns.scala
text:
```scala
package com.YuriFerreira.PortfolioOptimization

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._


object PortfolioReturns {
  def portfolioDailyReturn(weights: Array[Double], dailyReturns : DataFrame, tickers: List[String], portfolioName : String): DataFrame = {
    
    val mapWeightedReturns = tickers.map(ticker => (col(s"${ticker}_daily_return") * weights(tickers.indexOf(ticker))).alias(s"${ticker}_weighted_return"))
    val mapReturns = tickers.map(ticker => col(s"${ticker}_daily_return"))
    val selectedColumns = List(col("timestamp"))++ mapReturns ++ mapWeightedReturns
    val weightedReturns = dailyReturns.select(selectedColumns: _*)
    val portfolioDailyReturn = weightedReturns.withColumn("portfolio_daily_return", mapWeightedReturns.reduce(_ + _))
    portfolioDailyReturn.select(col("timestamp"), col(s"portfolio_$_daily_return"))
  }

  def getAllPortfolioReturns(bestResultsDf : DataFrame, dailyReturns : DataFrame, tickers: List[String]): DataFrame = {
    val portfolioType = bestResultsDf.select(col("portfolio_type")).collect().map(_.getString(0))

    val bestWeights = bestResultsDf.select(col("weights")).collect().map(_.getDouble(0))
    mapReturns
    // val mapPortfolioReturns = bestWeights.map(weights => portfolioDailyReturn(weights, dailyReturns, tickers))
    // mapPortfolioReturns.reduce(_.join(_, Seq("timestamp"), "inner")).orderBy(col("timestamp").desc)
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
    val testing = portfolioDailyReturn(Array(0.3, 0.3, 0.4), dfDailyReturn, stockList)
    dfDailyReturn.show()
    testing.show()


  }

    

  
}

```



#### Error stacktrace:

```
scala.reflect.internal.util.Position.fail(Position.scala:24)
	scala.reflect.internal.util.UndefinedPosition.start(Position.scala:101)
	scala.reflect.internal.util.UndefinedPosition.start(Position.scala:97)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$4(PcCollector.scala:351)
	scala.collection.immutable.List.flatMap(List.scala:293)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:350)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$5(PcCollector.scala:363)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:363)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$5(PcCollector.scala:363)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:363)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$19(PcCollector.scala:469)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:469)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$3(PcCollector.scala:342)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:342)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$19(PcCollector.scala:469)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:469)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$3(PcCollector.scala:342)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:342)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:288)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$3(PcCollector.scala:342)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:342)
	scala.meta.internal.pc.PcCollector.traverseSought(PcCollector.scala:472)
	scala.meta.internal.pc.PcCollector.resultAllOccurences(PcCollector.scala:276)
	scala.meta.internal.pc.PcCollector.result(PcCollector.scala:208)
	scala.meta.internal.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:71)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticTokens$1(ScalaPresentationCompiler.scala:157)
```
#### Short summary: 

java.lang.UnsupportedOperationException: Position.start on NoPosition