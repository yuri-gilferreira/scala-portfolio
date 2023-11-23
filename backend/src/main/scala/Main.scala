//
package com.YuriFerreira.PortfolioOptimization

import org.apache.spark.sql.SparkSession
import scopt.OParser
import com.YuriFerreira.PortfolioOptimization.MainCalculations
import com.YuriFerreira.PortfolioOptimization.Simulations
import com.YuriFerreira.PortfolioOptimization.SparkFunctions
// import org.apache.spark.sql.functions.col


case class Config(stockList: String = "",
                  originalWeights: String = "",
                  dateRange: String = "")

object Main extends App {
   val builder = OParser.builder[Config]
   val parser = {
      import builder._
      OParser.sequence(
         programName("Portfolio-Optimization"),
         opt[String]('s', "stockList")
            .required()
            .action((x, c) => c.copy(stockList = x))
            .text("List of stocks, comma-separated"),
         opt[String]('w', "originalWeights")
            .required()
            .action((x, c) => c.copy(originalWeights = x))
            .text("Original weights, comma-separated"),
         opt[String]('d', "dateRange")
            .required()
            .action((x, c) => c.copy(dateRange = x))
            .text("Date range, start and end dates separated by a comma")
      )
   }

   OParser.parse(parser, args, Config()) match {
      case Some(config) =>
         runMain(config)
      case _ =>
         println("Invalid arguments")
   }

   def runMain(config: Config): Unit = {
      val spark = SparkSession.builder()
         .appName("Portfolio-Optimization")
         .master("local[*]") // Use "local[*]" for local testing; replace with your cluster settings for production
         .getOrCreate()

      import spark.implicits._

      // MainCalculations.main(args)
      val stockList = config.stockList.split(",").toList
      val originalWeights = config.originalWeights.split(",").map(_.toDouble)
      val dateRange = config.dateRange.split(",")
      val min_date = dateRange(0)
      val max_date = dateRange(1)
      
      val dataFolder = "../data"

       // Main Calculations 
       val dfMutipleStocks = MainCalculations.getMultipleStocks(stockList, spark, min_date, max_date)
       val PortfolioDailyReturn = MainCalculations.dailyReturnMultipleStocksOptimized(dfMutipleStocks)
       val (meanReturnDF, volatilityDF) = MainCalculations.createReturnAndVolatilityDataFrames(PortfolioDailyReturn, stockList, spark)

       val correlationMatrixDF = MainCalculations.calculateCorrelationMatrix(PortfolioDailyReturn, stockList, spark)
       val covarianceMatrixDF = MainCalculations.calculateCovarianceMatrix(PortfolioDailyReturn, stockList, spark)

       val meanReturns: Array[Double] = meanReturnDF.select("MeanReturn").as[Double].collect() 
       val covarianceMatrix: Array[Array[Double]] = covarianceMatrixDF.drop("Ticker1").collect().map { 
         row => row.toSeq.toArray.map(_.toString.toDouble)
      }


       // Simulation and Optimization
       val numSimulations = 10000
       val results = Simulations.runMonteCarloSimulationSpark(meanReturns, covarianceMatrix, spark)
       val originalWeightDf = Simulations.getOriginalWeightsReturns(originalWeights, meanReturns, covarianceMatrix, spark)
       val bestResults = Simulations.getBestResults(results,originalWeightDf)

       // Portfolio Returns
      val portfolioReturns = PortfolioReturns.getAllPortfolioReturns(bestResults, PortfolioDailyReturn, stockList)
      val compoundedReturns = PortfolioReturns.compoundReturnMultiplePortfolios(portfolioReturns)

       // Save results to CSV

       results.show()
       SparkFunctions.saveDataFrameToCSV(results, dataFolder, "simulations.csv")
       SparkFunctions.saveDataFrameToCSV(bestResults, dataFolder, "best_results.csv")
       SparkFunctions.saveDataFrameToCSV(meanReturnDF, dataFolder, "mean_returns.csv")
       SparkFunctions.saveDataFrameToCSV(correlationMatrixDF, dataFolder,  "correlation_matrix.csv")
       SparkFunctions.saveDataFrameToCSV(covarianceMatrixDF, dataFolder,  "covariance_matrix.csv")
       SparkFunctions.saveDataFrameToCSV(volatilityDF, dataFolder,  "volatility.csv")
       SparkFunctions.saveDataFrameToCSV(portfolioReturns, dataFolder,  "portfolio_returns.csv")
       SparkFunctions.saveDataFrameToCSV(compoundedReturns, dataFolder,  "compounded_returns.csv")
   }
}