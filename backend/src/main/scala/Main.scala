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
      // .config("spark.hadoop.validateOutputSpecs", "false")
      .getOrCreate()
   
   import spark.implicits._

   
    val stockList = List("IBM", "AAPL")
    val min_date = "2018-10-01"
    val max_date = "2023-10-10"
    val dfMutipleStocks = MainCalculations.getMultipleStocks(stockList, spark, min_date, max_date)
    val PortfolioDailyReturn = MainCalculations.dailyReturnMultipleStocksOptimized(dfMutipleStocks)
    val (meanReturnDF, volatilityDF) = MainCalculations.createReturnAndVolatilityDataFrames(PortfolioDailyReturn, stockList, spark)

    
    val correlationMatrixDF = MainCalculations.calculateCorrelationMatrix(PortfolioDailyReturn, stockList, spark)
    val covarianceMatrixDF = MainCalculations.calculateCovarianceMatrix(PortfolioDailyReturn, stockList, spark)
    

    val dataFolder = "../data"

    val meanReturns: Array[Double] = meanReturnDF.select("MeanReturn").as[Double].collect() 
    val covarianceMatrix: Array[Array[Double]] = covarianceMatrixDF.drop("Ticker1").collect().map { 
      row => row.toSeq.toArray.map(_.toString.toDouble)
   }
    
    val numSimulations = 10000
    val results = Simulations.runMonteCarloSimulationSparkControlled(meanReturns, covarianceMatrix, numSimulations, spark)
    results.show()
    SparkFunctions.saveDataFrameToCSV(results, dataFolder, "simulations.csv")
    SparkFunctions.saveDataFrameToCSV(meanReturnDF, dataFolder, "mean_returns.csv")
    SparkFunctions.saveDataFrameToCSV(correlationMatrixDF, dataFolder,  "correlation_matrix.csv")
    SparkFunctions.saveDataFrameToCSV(covarianceMatrixDF, dataFolder,  "covariance_matrix.csv")
    SparkFunctions.saveDataFrameToCSV(volatilityDF, dataFolder,  "volatility.csv")


    
}