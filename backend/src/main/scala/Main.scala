//
package com.YuriFerreira.PortfolioOptimization

import org.apache.spark.sql.SparkSession

import com.YuriFerreira.PortfolioOptimization.MainCalculations
import com.YuriFerreira.PortfolioOptimization.Simulations
import com.YuriFerreira.PortfolioOptimization.SparkFunctions
// import org.apache.spark.sql.functions.col


object Main extends App {


   val spark = SparkSession.builder()
      .appName("Portfolio-Optimization")
      .master("local[*]") // Use "local[*]" for local testing; replace with your cluster settings for production
      .getOrCreate()
   
   import spark.implicits._

   // MainCalculations.main(args)
    val stockList = List("IBM", "AAPL", "MSFT")
    val min_date = "2018-10-01"
    val max_date = "2023-10-10"
    val originalWeights = Array(0.3, 0.3, 0.4)
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