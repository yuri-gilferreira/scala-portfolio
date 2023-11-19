package com.YuriFerreira.PortfolioOptimization


import org.apache.spark.sql.{SparkSession, DataFrame}
import java.nio.file.{Paths, Files}
import java.io.{File, PrintWriter}

object SparkFunctions {
  def loadData(spark: SparkSession, csvOption: Option[String]): DataFrame = {
    import spark.implicits._
    
    val csvString = csvOption.getOrElse(return spark.emptyDataFrame)
    val rdd = spark.sparkContext.parallelize(csvString.split("\n"))
    val dataset = rdd.toDS()
  
    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(dataset)
    
  }
  def saveDataFrameToFolder(df: DataFrame, folderPath:String, fileName: String): Unit = {
     val path = Paths.get(folderPath, fileName).toString
     
     val colaescedDf = df.coalesce(1)

     colaescedDf
       .write
       .mode("overwrite")
       .option("header", "true")
       .csv(path)
   }


def saveDataFrameToCSV(df: DataFrame, relativePath: String, fileName: String): Unit = {
  val currentDir = new File(".").getCanonicalPath
  val directoryPath = Paths.get(currentDir, relativePath)
  val fullPath = directoryPath.resolve(fileName).toString

  if (!Files.exists(directoryPath)) {
    Files.createDirectories(directoryPath) // This creates the directory structure if it doesn't exist
  }
  val headers = df.columns.mkString(";")
  val data = df.collect().map(row => row.mkString(";")).mkString("\n")

  val writer = new PrintWriter(fullPath) 
  try {
    writer.write(headers + "\n" + data) 
  } finally {
    writer.close()
  }
}

}