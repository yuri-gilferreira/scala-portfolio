package com.YuriFerreira.PortfolioOptimization

import org.scalatest.funsuite.AnyFunSuite
// Import your ApiCalls object. Adjust the import statement according to your package structure.
import com.YuriFerreira.PortfolioOptimization.ApiCallAlphaVantage 

class ApiCallTest extends AnyFunSuite {
  
  test("fetchDataFromAPI should return successful result") {
    val apikey = sys.env("ALPHA_VANTAGE_API_KEY")

    val testing = ApiCallAlphaVantage.fetchDataFromAPI("PEP", apikey, outputsize = "compact")
    print(testing)
  }

}