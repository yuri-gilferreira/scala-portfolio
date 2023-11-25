package com.YuriFerreira.PortfolioOptimization

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.YuriFerreira.PortfolioOptimization.JsonFormats._
import spray.json._

class WebServerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {
  "The WebServer" should {
    "respond to the /run-optimization POST route" in {
      val apikey = sys.env("ALPHA_VANTAGE_API_KEY")
      val config = Config("AAPL,GOOG", "0.5,0.5", "2021-01-01,2021-12-31", 0.02, 10000, apikey)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, config.toJson.toString())

      Post("/run-optimization", requestEntity) ~> WebServer.route ~> check {
        status should === (StatusCodes.OK)
        // Add more assertions based on your expected response
      }
    }
  }
}
