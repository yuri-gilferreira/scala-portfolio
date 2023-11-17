package com.YuriFerreira.PortfolioOptimization

import com.YuriFerreira.PortfolioOptimization.ApiCallAlphaVantage
import org.apache.spark.sql.{SparkSession, DataFrame}



case class SparkRDDConfig(name: String, masterUrl: String, transactionFile: String)

object Portfolio {
  
  
def loadData(spark: SparkSession, csvOption: Option[String]): DataFrame = {
    import spark.implicits._
    
    val csvString = csvOption.getOrElse(return spark.emptyDataFrame)
    val rdd = spark.sparkContext.parallelize(csvString.split("\n"))
    val dataset = rdd.toDS()
  
    val dataFrame = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(dataset)

  dataFrame
    
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("CSV to DataFrame")
      .master("local[*]") // Use "local[*]" for local testing; replace with your cluster settings for production
      .getOrCreate()

    // Example CSV string (replace this with your actual CSV string)
    val testing = ApiCallAlphaVantage.fetchDataFromAPI("IBM", outputsize = "compact")

    val dataFrame = loadData(spark, testing)
    dataFrame.show()
  }

}
