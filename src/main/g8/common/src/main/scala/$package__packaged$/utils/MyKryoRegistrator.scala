// Copyright (C) 2018
package $organization$.$name$.utils

import $organization$.$name$.model.MyModel
import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator
import org.slf4j.LoggerFactory;

class MyKryoRegistrator extends KryoRegistrator {

  @transient lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def registerClasses(kryo: Kryo): Unit = {
    logger.info("Registering custom serializers")
    kryo.register(classOf[MyModel])
    // TODO register all classes here for better performance
    ()
  }
}
