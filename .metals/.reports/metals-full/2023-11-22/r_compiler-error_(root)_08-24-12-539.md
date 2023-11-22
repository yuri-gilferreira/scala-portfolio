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
  val portfolios = Seq("original", "sharpe", "risk")

  for (ticker <- tickers) {
    val dailyReturnColumn = s"${ticker}_daily_return"
    val compoundReturnColumn = s"$_{ticker}_compound_return"

    if (portfolioReturns.columns.contains(dailyReturnColumn)) {
      compoundedReturns = compoundedReturns.withColumn(
        compoundReturnColumn,
        exp(sum(log(col(dailyReturnColumn) + 1)).over(windowSpec)) - 1
      )
    }
  }

  compoundedReturns
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
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$19(PcCollector.scala:469)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:469)
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