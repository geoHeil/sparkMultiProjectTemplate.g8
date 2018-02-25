// Copyright (C) 2018
package $organization$.$name$.utils.config

import $organization$.$name$.utils.MyKryoRegistrator
import com.typesafe.config.ConfigFactory
import configs.Configs
import configs.syntax._
import org.apache.spark.SparkConf
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.SparkSession

object ConfigurationUtils {

  def createSparkSession(appName: String, useKryo: Boolean): SparkSession =
    SparkSession
      .builder()
      .config(createConf(appName, useKryo))
      .enableHiveSupport()
      .getOrCreate()

  def createConf(appName: String, useKryo: Boolean): SparkConf = {
    val baseConf = new SparkConf()
      .setAppName(appName)
      .setIfMissing("spark.master", "local[*]")
      .setIfMissing("spark.driver.memory", "12G")
      .setIfMissing("spark.default.parallelism", "12")
      .setIfMissing("spark.driver.maxResultSize", "1G")
      .setIfMissing("spark.speculation", "true")
      .setIfMissing("spark.driver.extraJavaOptions", "-XX:+UseG1GC")
      .setIfMissing("spark.executor.extraJavaOptions", "-XX:+UseG1GC")
      .setIfMissing("spark.memory.offHeap.enabled", "true")
      .setIfMissing("spark.memory.offHeap.size", "3g")
      .setIfMissing("spark.yarn.executor.memoryOverhead", "3g")

      // turn on Parquet push-down, stats filtering, and dictionary filtering
      .setIfMissing("parquet.filter.statistics.enabled", "true")
      .setIfMissing("parquet.filter.dictionary.enabled", "true")
      .setIfMissing("spark.sql.parquet.filterPushdown", "true")
      // set advanced options according to https://cdn.oreillystatic.com/en/assets/1/event/160/Parquet%20performance%20tuning_%20The%20missing%20guide%20Presentation.pdf
      // only if you know what you are doing

      // use the non-Hive read path
      //.setIfMissing("spark.sql.hive.convertMetastoreParquet", "true")
      // turn off schema merging, which turns off push-down
      //.setIfMissing("spark.sql.parquet.mergeSchema", "false")
      //.setIfMissing("spark.sql.hive.convertMetastoreParquet", "false")

    if (useKryo) {
      baseConf
        .setIfMissing("spark.serializer", classOf[KryoSerializer].getCanonicalName)
        .setIfMissing("spark.kryoserializer.buffer.max", "500m")
        // .setIfMissing("spark.kryo.registrationRequired", "true") // disabled as spark internals also fail this
        .setIfMissing("spark.kryo.unsafe", "true")
        .setIfMissing("spark.kryo.referenceTracking", "false")
        .setIfMissing("spark.kryo.registrator", classOf[MyKryoRegistrator].getCanonicalName)
    } else {
      baseConf
    }
  }

  def loadConfiguration[T <: Product](implicit A: Configs[T]): T = {
    val config = ConfigFactory.load()
    val r      = config.extract[T]
    r.toEither match {
      case Right(s) => s
      case Left(l) =>
        throw new ConfigurationException(
          s"Failed to start. There is a problem with the configuration: \${l.messages.foreach(println)}"
        )
    }
  }

}
