package com.YuriFerreira.PortfolioOptimization

import org.apache.spark.sql.DataFrame

object TestUtils {
def assertDataFrameApproxEqual(df1: DataFrame, df2: DataFrame, tolerance: Double): Unit = {
  val df1Collect = df1.collect().sortBy(_.toString)
  val df2Collect = df2.collect().sortBy(_.toString)

  assert(df1Collect.length == df2Collect.length, "DataFrames do not have the same number of rows")

  df1Collect.zip(df2Collect).foreach { case (row1, row2) =>
    row1.toSeq.zip(row2.toSeq).foreach { case (value1, value2) =>
      (value1, value2) match {
        case (d1: Double, d2: Double) => assert(math.abs(d1 - d2) <= tolerance, s"Values $d1 and $d2 differ by more than $tolerance")
        case _ => assert(value1 == value2, s"Values $value1 and $value2 are not equal")
      }
    }
  }
}
}