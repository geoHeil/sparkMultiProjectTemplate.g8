// Copyright (C) 2018
package $organization$.$name$.app

import $organization$.$name$.config.JobTypesafeConfiguration
import frameless.TypedDataset
import frameless.syntax._

case class User(id:Int, name:String)
case class Event(eventId:Int, userId:Int)
case class UEInfo(name:String, eventId:Int)
case class UEInfoError(nameError:String, eventId:Int)

/**
 * follow along at https://typelevel.org/frameless/FeatureOverview.html
 * also here https://typelevel.org/frameless/TypedDatasetVsSparkDataset.html
**/
object JobTypesafe extends SparkBaseRunner[JobTypesafeConfiguration] {
  val spark = createSparkSession(this.getClass.getName)

  import spark.implicits._
  val users = Seq(User(1, "Anna"), User(2, "Bob")).toDS
  val events = Seq(Event(101, 1), Event(102, 2),
  	Event(103, 1), Event(104, 2), Event(105, 1),
  	Event(106, 2), Event(107, 1), Event(108, 2))
  
  users.printSchema
  users.show

  // TODO cant refactor name of id column using IDE.
  // How to get typesafe refactoring for this operation?
  // Can Datasets[T] api be used to achieve desired behaviour?
  val foo = users.select('id)
  foo.show
  println(foo.explain)

  // using a Dataset[T] map transformation causes additional serialization
  // TODO map
  users.map(_.id).show
  println(users.map(_.id).explain)
  
  events.printSchema
  events.show

  // DF operations in spark
  val joinedDF = events.join(users, eventsDF("userId") === users("id")).drop(users("userId"))
  joinedDF.show

  val filteredDF = joinedDF.filter('eventId >= 105)
  filteredDF.show
  println(filteredDF.explain)

  // now using frameless typesafe operations
  val fUsers = TypedDataset.create(users)
  val fEvents = TypedDataset.create(events)

  val fFoo = fUsers.select(fUsers('id))
  // fUsers.select(fUsers('idError)) // should fail
  fFoo.show.run
  println(fFoo.explain)

  val fJoined = fUsers.joinInner(fEvents) { fUsers('id) === fEvents('userId) }
  fJoined.show.run
  println(fJoined.explain)

  val fFiltered = fJoined.filter(fJoined('eventId) >= 105).show.run
  fFiltered.show.run
  println(fFiltered.explain)

  val fFoo2 = fFiltered.project[UEInfo]
  fFoo2.show.run
  println(fFoo2.explain)
  val fFoo3 = fFiltered.drop[UEInfo]
  fFoo3.show.run
  // fFiltered.project[UEInfoError] // should fail due to typo

  // TODO move around
  // TODO test cache

  // composition of jobs --------------------------------------------
  val ds = TypedDataset.create(1 to 20)
  val countAndTakeJob =
    for {
      count <- ds.count()
      sample <- ds.take((count/5).toInt)
    } yield sample

  countAndTakeJob.run

  import frameless.Job
  def computeMinOfSample(sample: Job[Seq[Int]]): Job[Int] = sample.map(_.min)
  val finalJob = computeMinOfSample(countAndTakeJob)

  finalJob.
  withGroupId("samplingJob").
  withDescription("Samples 20% of elements and computes the min").
  run
}
