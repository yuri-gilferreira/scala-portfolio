package com.YuriFerreira.PortfolioOptimization

// import com.YuriFerreira.PortfolioOptimization.MainCalculations
import org.apache.spark.sql.{SparkSession, DataFrame}
import scala.util.Random


object Simulations {
def calculatePortfolioRisk(weights: Array[Double], covarianceMatrix: Array[Array[Double]]): Double = {
  val weightedCovarianceVector = covarianceMatrix.map(row => 
    row.zip(weights).map { case (covariance, weight) => covariance * weight }.sum
  )
  val portfolioVariance = weights.zip(weightedCovarianceVector).map { case (weight, weightedCov) => 
    weight * weightedCov 
  }.sum

  val portfolioRisk = scala.math.sqrt(portfolioVariance)
  portfolioRisk
}

def calculatePortfolioReturn(weights: Array[Double], meanReturns: Array[Double]): Double = {
  weights.zip(meanReturns).map { case (weight, returns) => weight * returns}.sum
}

def runMonteCarloSimulationSpark(
    meanReturns: Array[Double], 
    covarianceMatrix: Array[Array[Double]], 
    numSimulations: Int, 
    spark: SparkSession,
    seed: Option[Long] = None
): DataFrame = {
  import spark.implicits._

  val numAssets = meanReturns.length
  val simulations = spark.sparkContext.parallelize(1 to numSimulations)

  val results = simulations.map { _ =>
    val random = new Random(seed.getOrElse(System.nanoTime()))

    val weights = Array.fill(numAssets)(random.nextDouble()).map(_ / numAssets).map(weight => BigDecimal(weight).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)
    val totalWeight = weights.sum
    val normalizedWeights = weights.map(_ / totalWeight)

    val portfolioReturn = calculatePortfolioReturn(normalizedWeights, meanReturns)
    val portfolioRisk = calculatePortfolioRisk(normalizedWeights, covarianceMatrix)

    (portfolioReturn, portfolioRisk, normalizedWeights.mkString(","))
  }

  results.toDF("Return", "Risk", "Weights")
}

  def main(args: Array[String]): Unit = {
    val meanReturns = Array(0.12, 0.08) 
    val covarianceMatrix = Array(
        Array(0.1, 0.05),
        Array(0.05, 0.2)
      )
    val numSimulations = 1000
    val spark = SparkSession.builder()
      .appName("Portfolio-Optimization")
      .master("local[*]") 
      .getOrCreate()
    val results = runMonteCarloSimulationSpark(meanReturns, covarianceMatrix, numSimulations, spark)
    results.show()
  }


}
