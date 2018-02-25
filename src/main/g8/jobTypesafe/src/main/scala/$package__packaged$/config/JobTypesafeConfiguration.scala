// Copyright (C) 2018
package $organization$.$name$.config

import org.apache.spark.sql.SaveMode

final case class JobTypesafeConfiguration(saveMode: SaveMode, inputFile: String)
