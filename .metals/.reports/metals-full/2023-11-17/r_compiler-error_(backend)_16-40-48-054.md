file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/src/main/scala/ApiCalls.scala
### java.lang.NullPointerException: Cannot invoke "scala.meta.internal.pc.CompilerWrapper.compiler()" because "pc" is null

occurred in the presentation compiler.

action parameters:
uri: file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/src/main/scala/ApiCalls.scala
text:
```scala
package com.YuriFerreira.PortfolioOptimization

import requests._
import scala.util.{Try, Success, Failure}


object ApiCallAlphaVantage {
  def fetchDataFromAPI(
    symbol: String, 
    function: String = "TIME_SERIES_DAILY_ADJUSTED",
    datatype: String = "csv",
    outputsize: String = "compact"
    ): Option[String] = {
    val url = "https://www.alphavantage.co/query"
    val params = Map(
      "function" -> function, 
      "symbol" -> symbol,  
      "apikey" -> "2FYQYEAUFMJCT69A",
      "datatype" ->  datatype,
      "outputsize" -> outputsize
    )

   Try(requests.get(url, params = params)) match {
      case Success(response) if response.statusCode == 200 =>
        // println(Some(response.text()))
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
    val testing = fetchDataFromAPI("IBM", outputsize = "full")
    println(testing)
  }
}
```



#### Error stacktrace:

```
scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticTokens$1(ScalaPresentationCompiler.scala:156)
```
#### Short summary: 

java.lang.NullPointerException: Cannot invoke "scala.meta.internal.pc.CompilerWrapper.compiler()" because "pc" is null