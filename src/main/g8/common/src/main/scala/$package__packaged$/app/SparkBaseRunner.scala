// $copyright
package $organization$.$name$.app

import $organization$.$name$.utils.config.ConfigurationUtils
import configs.Configs
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

abstract class SparkBaseRunner[T <: Product](implicit A: Configs[T], useKryo: Boolean = true) extends App {

  @transient lazy val logger = LoggerFactory.getLogger(this.getClass)

  val c = ConfigurationUtils.loadConfiguration[T]
  logger.info(s"configuration is: \$c")

  def createSparkSession(appName: String): SparkSession =
    ConfigurationUtils.createSparkSession(appName, useKryo)
}
