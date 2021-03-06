// $copyright$
package $organization$.$name$.utils.config

import $organization$.$name$.utils.MyKryoRegistrator
import com.typesafe.config.ConfigFactory
import configs.Configs
import configs.syntax._
import org.apache.spark.SparkConf
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.SparkSession
import $organization$.$name$.utils.Tools._

object ConfigurationUtils {

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def createSparkSession(appName: String, useKryo: Boolean): SparkSession = {
    if (appName === "dev") {
      // https://stackoverflow.com/questions/48008343/sbt-test-does-not-work-for-spark-test
      // https://builds.apache.org/job/Derby-docs/lastSuccessfulBuild/artifact/trunk/out/security/csecjavasecurity.html
      // https://issues.apache.org/jira/browse/SPARK-22918
      System.setSecurityManager(null)
    }
    SparkSession
      .builder()
      .config(createConf(appName, useKryo))
      .enableHiveSupport()
      .getOrCreate()
  }

  def createConf(appName: String, useKryo: Boolean): SparkConf = {
    val baseConf = new SparkConf()
      .setAppName(appName)
      .setIfMissing("spark.master", "local[*]")
      .setIfMissing("spark.driver.memory", "9G")
      .setIfMissing("spark.default.parallelism", "12")
      .setIfMissing("spark.driver.maxResultSize", "1G")
      .setIfMissing("spark.speculation", "true")
      .setIfMissing("spark.driver.extraJavaOptions", "-XX:+UseG1GC")
      .setIfMissing("spark.executor.extraJavaOptions", "-XX:+UseG1GC")
      .setIfMissing("spark.memory.offHeap.enabled", "true")
      .setIfMissing("spark.memory.offHeap.size", "3g")
      .setIfMissing("spark.yarn.executor.memoryOverhead", "3g")
      .setIfMissing("spark.eventLog.compress", "true")

      // turn on Parquet push-down, stats filtering, and dictionary filtering
      .setIfMissing("parquet.filter.statistics.enabled", "true")
      .setIfMissing("parquet.filter.dictionary.enabled", "true")
      .setIfMissing("spark.sql.parquet.filterPushdown", "true")
      // set advanced options according to https://cdn.oreillystatic.com/en/assets/1/event/160/Parquet%20performance%20tuning_%20The%20missing%20guide%20Presentation.pdf
      // only if you know what you are doing
      // https://docs.hortonworks.com/HDPDocuments/HDP2/HDP-2.6.4/bk_spark-component-guide/content/orc-spark.html
      .setIfMissing("spark.sql.orc.impl", "native")
      .setIfMissing("park.sql.orc.enableVectorizedReader", "true")
      .setIfMissing("spark.sql.hive.convertMetastoreOrc", "true")
      .setIfMissing("spark.sql.orc.filterPushdown", "true")
      .setIfMissing("spark.sql.orc.char.enabled", "true")
        
      // cost based optimizer https://jaceklaskowski.gitbooks.io/mastering-spark-sql/spark-sql-cost-based-optimization.html
      .setIfMissing("spark.sql.cbo.enabled", "true")
      .setIfMissing("spark.sql.statistics.histogram.enabled", "true")

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
