// $copyright$
package $organization$.$name$.service

import $organization$.$name$.config.Job1Configuration
import $organization$.$name$.utils.io.IO
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame, SparkSession}

import scala.reflect.runtime.universe.TypeTag

object CountService {

  def count[T <: Product: TypeTag](spark: SparkSession, c: Job1Configuration): Unit = {
    import spark.implicits._
    val df     = IO.readCsv(c.inputFile, spark, ",").as[T]
    // TODO no typesafe referencing possible without using RDD API.
    // TODO how can I get this to a refactorable & at best typesafe column name using spark dataset api?
    val result = performCount(df.toDF, 'idNumber)
    println(result)
  }

  def performCount(df: DataFrame, columnToCount: Column): Seq[Int] = {
    import df.sparkSession.implicits._
    df.withColumn(columnToCount.toString, length(columnToCount)).select(columnToCount.toString).orderBy(columnToCount.toString).as[Int].collect
  }

}
