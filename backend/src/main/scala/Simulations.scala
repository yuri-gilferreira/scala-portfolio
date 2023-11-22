package com.YuriFerreira.PortfolioOptimization

// import com.YuriFerreira.PortfolioOptimization.MainCalculations
import org.apache.spark.sql.{SparkSession, DataFrame}
import scala.util.Random
import org.apache.spark.sql.functions._


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
    spark: SparkSession,
    numSimulations: Int = 10000, 
    riskFreeRate: Double = 0.02,
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
    val sharpeRatio = calculateSharpeRatio(portfolioReturn, portfolioRisk, riskFreeRate)
    val realReturn = portfolioReturn - riskFreeRate

    (portfolioReturn, portfolioRisk, realReturn, sharpeRatio, normalizedWeights.mkString(","))
  }

  val resultsDf = results.toDF("return", "risk", "real_return", "sharpe_ratio", "weights")
  resultsDf
}

def calculateSharpeRatio(portfolioReturn: Double, portfolioRisk: Double, riskFreeRate: Double): Double = {

  val sharpeRatio = (portfolioReturn - riskFreeRate) / portfolioRisk
  sharpeRatio
}

def runMonteCarloSimulationSparkControlled(
    meanReturns: Array[Double], 
    covarianceMatrix: Array[Array[Double]], 
    spark: SparkSession,
    riskFreeRate: Double = 0.02,
    numSimulations: Int = 10000, 
    seed: Option[Long] = None
): DataFrame = {
  import spark.implicits._

  val numAssets = meanReturns.length
  val simulations = spark.sparkContext.parallelize(1 to numSimulations)

  val results = simulations.map { _ =>
    val random = new Random(seed.getOrElse(System.nanoTime()))

    // Generating weights in a more controlled manner
    val weights = Array.fill(numAssets)(random.nextDouble())
    val totalWeight = weights.sum
    val normalizedWeights = weights.map(_ / totalWeight)

    // Ensure more uniform distribution
    val shuffledWeights = random.shuffle(normalizedWeights.toList).toArray

    val portfolioReturn = calculatePortfolioReturn(shuffledWeights, meanReturns)
    val portfolioRisk = calculatePortfolioRisk(shuffledWeights, covarianceMatrix)
    val sharpeRatio = calculateSharpeRatio(portfolioReturn, portfolioRisk, riskFreeRate)
    val realReturn = portfolioReturn - riskFreeRate

    (portfolioReturn, portfolioRisk, realReturn, sharpeRatio, shuffledWeights.mkString(","))
  }

  val resultsDf = results.toDF("return", "risk", "real_return", "sharpe_ratio", "weights")
  resultsDf
}

def getOriginalWeightsReturns(
  weights: Array[Double], 
  meanReturns: Array[Double],
  covarianceMatrix: Array[Array[Double]],
  spark: SparkSession,
  riskFreeRate: Double = 0.02,
   ): DataFrame = {
    import spark.implicits._
  val portfolioReturn = calculatePortfolioReturn(weights, meanReturns)
  val portfolioRisk = calculatePortfolioRisk(weights, covarianceMatrix)
  val sharpeRatio = calculateSharpeRatio(portfolioReturn, portfolioRisk, riskFreeRate)
  val realReturn = portfolioReturn - riskFreeRate
  val OriginalWeightsResult = Seq((portfolioReturn, portfolioRisk, realReturn, sharpeRatio, weights.mkString(",")))
  val OriginalWeightsResultDF = OriginalWeightsResult.toDF("return", "risk", "real_return", "sharpe_ratio", "weights")
  OriginalWeightsResultDF

}

def getBestResults(results: DataFrame, OriginalWeightsResultDF: DataFrame): DataFrame = {

  val bestSharpeResults = results
    .orderBy(col("sharpe_ratio").desc)
    .limit(1)
    .withColumn("type", lit("sharpe"))

  val bestRiskResults = results
    .orderBy(col("risk").asc)
    .limit(1)
    .withColumn("type", lit("risk"))
  
  val originalWeightDf = OriginalWeightsResultDF.withColumn("type", lit("original"))

  val joinedResults = bestSharpeResults.union(bestRiskResults).union(originalWeightDf)
  joinedResults

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
    val results = runMonteCarloSimulationSpark(meanReturns, covarianceMatrix, spark, numSimulations, 0.02)
    val originalWeightDf = getOriginalWeightsReturns(Array(0.5, 0.5), meanReturns, covarianceMatrix, spark)
    val bestResults = getBestResults(results,originalWeightDf)
    bestResults.show()
  }


}
