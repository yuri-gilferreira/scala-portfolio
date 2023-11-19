package com.YuriFerreira.PortfolioOptimization

import com.YuriFerreira.PortfolioOptimization.Simulations
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
// import org.apache.spark.sql.{SparkSession, DataFrame}

class PortfolioRiskTest extends AnyWordSpec with Matchers {

  "calculatePortfolioRisk" should {
    "correctly calculate risk for balanced weights" in {
      val weights = Array(0.5, 0.5) 
      val covarianceMatrix = Array(
        Array(0.1, 0.05),
        Array(0.05, 0.2)
      )
      val expectedRisk = 0.3162
      val portfolioRisk = Simulations.calculatePortfolioRisk(weights, covarianceMatrix)
      portfolioRisk should be(expectedRisk +- 0.01)
    }

    "correctly calculate risk with heavy weight in one asset" in {
      val weights = Array(0.8, 0.2) // Heavily weighted in one asset
      val covarianceMatrix = Array(
        Array(0.1, 0.05),
        Array(0.05, 0.2)
      )
      val expectedRisk = 0.2966
      val portfolioRisk = Simulations.calculatePortfolioRisk(weights, covarianceMatrix)
      portfolioRisk should be(expectedRisk +- 0.01)
    }

    "correctly calculate risk with a varied covariance matrix" in {
      val weights = Array(0.8, 0.2) // Weights remain the same
      val variedCovarianceMatrix = Array(
        Array(0.15, 0.06), // Changed values
        Array(0.06, 0.25)
      )
      val expectedRisk = 0.3538
      val portfolioRisk = Simulations.calculatePortfolioRisk(weights, variedCovarianceMatrix)
      portfolioRisk should be(expectedRisk +- 0.01)
}
 
}

"calculatePortfolioReturn" should {

    "correctly calculate return for balanced weights" in {
      val weights = Array(0.5, 0.5) // Example balanced weights
      val meanReturns = Array(0.12, 0.08) 
      val expectedReturn = 0.1 

      val portfolioReturn = Simulations.calculatePortfolioReturn(weights, meanReturns)
      portfolioReturn should be (expectedReturn +- 0.01)
    }

    "correctly calculate return with heavy weight in one asset" in {
      val weights = Array(0.8, 0.2) // Heavily weighted in one asset
      val meanReturns = Array(0.15, 0.05) 
      val expectedReturn = 0.13 

      val portfolioReturn = Simulations.calculatePortfolioReturn(weights, meanReturns)
      portfolioReturn should be (expectedReturn +- 0.01)
    }

    "correctly calculate return with zero weight in one asset" in {
      val weights = Array(1.0, 0.0) 
      val meanReturns = Array(0.1, 0.2) 
      val expectedReturn = 0.1 

      val portfolioReturn = Simulations.calculatePortfolioReturn(weights, meanReturns)
      portfolioReturn should be (expectedReturn +- 0.01)
    }
    
    "correctly calculate return with three assets" in {
      val weights = Array(0.25, 0.25, 0.5) 
      val meanReturns = Array(0.1, 0.2, 0.3) 
      val expectedReturn = 0.1 

      val portfolioReturn = Simulations.calculatePortfolioReturn(weights, meanReturns)
      portfolioReturn should be (expectedReturn +- 0.01)
    }
  }
}