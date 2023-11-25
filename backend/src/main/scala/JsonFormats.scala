package com.YuriFerreira.PortfolioOptimization

import spray.json._

case class Config(stockList: String, originalWeights: String, dateRange: String, riskFreeRate: Double, numSimulations: Double, apiKey: String )

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val configFormat: RootJsonFormat[Config] = jsonFormat6(Config)
}