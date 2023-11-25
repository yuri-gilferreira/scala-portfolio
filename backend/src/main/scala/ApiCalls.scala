package com.YuriFerreira.PortfolioOptimization

import scala.util.{Try, Success, Failure}


object ApiCallAlphaVantage {
  def fetchDataFromAPI(
    symbol: String, 
    apikey: String, 
    function: String = "TIME_SERIES_DAILY_ADJUSTED",
    datatype: String = "csv",
    outputsize: String = "compact"
    ): Option[String] = {
    val url = "https://www.alphavantage.co/query"
    val params = Map(
      "function" -> function, 
      "symbol" -> symbol,  
      "apikey" -> apikey,
      "datatype" ->  datatype,
      "outputsize" -> outputsize
    )

   Try(requests.get(url, params = params)) match {
      case Success(response) if response.statusCode == 200 =>
        Some(response.text())
      case Success(response) =>
        println(s"Failed to fetch data. Status code: ${response.statusCode}")
        None
      case Failure(exception) =>
        println(s"Error occurred: ${exception.getMessage}")
        None
    }
  }

  def main(args: Array[String]): Unit = {
    val apikey = sys.env("ALPHA_VANTAGE_API_KEY")
    val testing = fetchDataFromAPI("IBM", apikey,outputsize = "full")
    println(testing)
  }
}