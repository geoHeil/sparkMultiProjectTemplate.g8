// Copyright (C) 2018
package $organization$.$name$.utils.services

import org.apache.spark.sql.DataFrame

object Renamer {

  def renameDFtoLowerCase(df: DataFrame): DataFrame = df.toDF(df.columns.map(_.toLowerCase): _*)

}
