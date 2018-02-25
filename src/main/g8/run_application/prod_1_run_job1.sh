#!/usr/bin/env bash
sbt assembly
kinit
spark-submit \
    --verbose \
    --class $organization$.$name$.app.Job1 \
    --master "local[4]" \
    --driver-memory=5G \
    --executor-memory=0.5G \
    --files customConfigFile.conf \
    --conf spark.driver.extraJavaOptions="-Dinputpath='<<TODO_CHANGEME>>' -Dconfig.file=customConfigFile.conf" \
    job1/target/scala_2.11/$name$-0.0.1-SNAPSHOT.jar