// Copyright (C) 2018
package $organization$.$name$.utils

import $organization$.$name$.model.MyModel
import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator

class MyKryoRegistrator extends KryoRegistrator {

  override def registerClasses(kryo: Kryo): Unit = {
    kryo.register(classOf[MyModel])
    // TODO register all classes here for better performance
    ()
  }
}
