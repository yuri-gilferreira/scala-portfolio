package com.YuriFerreira.PortfolioOptimization

import org.scalatest.funsuite.AnyFunSuite
// Import your ApiCalls object. Adjust the import statement according to your package structure.
import com.YuriFerreira.PortfolioOptimization.ApiCallAlphaVantage 

class ApiCallTest extends AnyFunSuite {
  
  test("fetchDataFromAPI should return successful result") {
    val testing = ApiCallAlphaVantage.fetchDataFromAPI("PEP", outputsize = "full")
    print(testing)

    // You might want to assert something about the results here.
    // For example, if fetchDataFromAPI returns a boolean indicating success/failure,
    // you could do: assert(ApiCalls.fetchDataFromAPI("TIME_SERIES_DAILY", "IBM"))
  }

  // Additional tests as needed
}