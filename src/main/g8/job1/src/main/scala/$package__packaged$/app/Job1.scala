// Copyright (C) 2018
package $organization$.$name$.app

import $organization$.$name$.config.Job1Configuration
import $organization$.$name$.service.CountService
import $organization$.$name$.model.MyModel

object Job1 extends SparkBaseRunner[Job1Configuration] {
  val spark = createSparkSession(this.getClass.getName)
  CountService.count[MyModel](spark, c)
}
