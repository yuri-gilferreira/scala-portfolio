//
package com.YuriFerreira.PortfolioOptimization

import org.apache.spark.sql.SparkSession
import com.YuriFerreira.PortfolioOptimization.MainCalculations
import com.YuriFerreira.PortfolioOptimization.Simulations
import com.YuriFerreira.PortfolioOptimization.SparkFunctions


object Main extends App {
   WebServer.main(Array.empty)

   def runMain(config: Config): Unit = {
      val spark = SparkSession.builder()
         .appName("Portfolio-Optimization")
         .master("local[*]") // Use "local[*]" for local testing; replace with your cluster settings for production
         .getOrCreate()

      import spark.implicits._
      
      val stockList = config.stockList.split(",").toList
      val originalWeights = config.originalWeights.split(",").map(_.toDouble)
      val dateRange = config.dateRange.split(",")
      val min_date = dateRange(0)
      val max_date = dateRange(1)
      val riskFreeRate = config.riskFreeRate
      val numSimulations = config.numSimulations
      val userKey = config.apiKey

      println(s"Received stockList: ${stockList}")
      println(s"Received originalWeights: ${originalWeights}")      
      println(s"Received min_date: ${min_date}")
      println(s"Received max_date: ${max_date}")
      println(s"Received riskFreeRate: ${riskFreeRate}")
      println(s"Received numSimulations: ${numSimulations}")
      println(s"Received userKey: ${userKey}")
      
      val dataFolder = "../data"

       // Main Calculations 
       val dfMutipleStocks = MainCalculations.getMultipleStocks(stockList, spark, userKey,  min_date, max_date)
       val PortfolioDailyReturn = MainCalculations.dailyReturnMultipleStocksOptimized(dfMutipleStocks)
       val (meanReturnDF, volatilityDF) = MainCalculations.createReturnAndVolatilityDataFrames(PortfolioDailyReturn, stockList, spark)

       val correlationMatrixDF = MainCalculations.calculateCorrelationMatrix(PortfolioDailyReturn, stockList, spark)
       val covarianceMatrixDF = MainCalculations.calculateCovarianceMatrix(PortfolioDailyReturn, stockList, spark)

       val meanReturns: Array[Double] = meanReturnDF.select("MeanReturn").as[Double].collect() 
       val covarianceMatrix: Array[Array[Double]] = covarianceMatrixDF.drop("Ticker").collect().map { 
         row => row.toSeq.toArray.map(_.toString.toDouble)
      }


      // Simulation and Optimization
       val results = Simulations.runMonteCarloSimulationSpark(meanReturns, covarianceMatrix, spark, numSimulations.toInt, riskFreeRate)
       val originalWeightDf = Simulations.getOriginalWeightsReturns(originalWeights, meanReturns, covarianceMatrix, spark, riskFreeRate)
       val bestResults = Simulations.getBestResults(results,originalWeightDf)

      // Portfolio Returns
      val portfolioReturns = PortfolioReturns.getAllPortfolioReturns(bestResults, PortfolioDailyReturn, stockList)
      val compoundedReturns = PortfolioReturns.compoundReturnMultiplePortfolios(portfolioReturns)

      //  Save results to CSV

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