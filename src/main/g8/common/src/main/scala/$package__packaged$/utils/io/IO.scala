// Copyright (C) 2018
package $organization$.$name$.utils.io

import $organization$.$name$.utils.services.Renamer
import org.apache.spark.sql.{ DataFrame, Dataset, SaveMode, SparkSession }

import scala.reflect.runtime.universe.TypeTag

object IO {

  def readDsHive[T <: Product: TypeTag](table: String, spark: SparkSession, db: String): Dataset[T] = {
    import spark.implicits._
    readHive(table, spark, db).as[T]
  }

  def readHive(table: String, spark: SparkSession, db: String): DataFrame = {
    spark.sql(s"USE \$db")
    spark.sql(table)
  }

  /**
   * Writes a spark dataFrame to hive, all columns are renamed. In case a partitioning column is passed
   * the data is partitioned using this column.
   *
   * Outputted data is on orc(zlib) format.
   *
   * @param df              a spark.DataFrame
   * @param tableName       full qualified name of the table including the database
   * @param partitionColumn name of the column to partition by
   * @param saveMode        whether to append or overwrite the data
   */
  def writeHive(df: DataFrame, tableName: String, partitionColumn: Option[String] = None, saveMode: SaveMode): Unit = {
    val renamed = Renamer.renameDFtoLowerCase(df)
    val dfToBeWrittenToHive = partitionColumn match {
      case Some(s) => renamed.write.partitionBy(s)
      case None    => renamed.write
    }
    dfToBeWrittenToHive
      .mode(saveMode)
      .format("orc")
      .option("compression", "zlib")
      .saveAsTable(tableName)
  }

  def getFullQualifiedTableName(db: String, tableName: String): String = db + "." + tableName

  def writeParquet(df: DataFrame, path: String): Unit =
    df.write
      .option("compression", "gzip")
      .mode(SaveMode.Overwrite)
      .parquet(path)

  def readCsv(path: String, spark: SparkSession, delimiter: String): DataFrame =
    spark.read
      .option("header", "true")
      .option("inferSchema", true)
      .option("charset", "UTF-8")
      .option("delimiter", delimiter)
      .csv(path)
}
