
package com.YuriFerreira.PortfolioOptimization


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.YuriFerreira.PortfolioOptimization.JsonFormats._
import spray.json._

class JsonFormatsSpec extends AnyFlatSpec with Matchers {
  
  "Config" should "serialize and deserialize correctly" in {
    val config = Config("AAPL,GOOG", "0.5,0.5", "2021-01-01,2021-12-31", 0.02, 10000, "demo")

    // Serialize to JSON and then parse it back to an object
    val json = config.toJson
    val parsedConfig = json.convertTo[Config]
    println(parsedConfig)

    // Compare objects instead of strings
    parsedConfig shouldBe config
  }
}
