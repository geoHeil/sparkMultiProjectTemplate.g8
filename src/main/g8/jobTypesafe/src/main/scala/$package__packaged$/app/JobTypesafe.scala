// $copyright
package $organization$.$name$.app

import frameless.TypedDataset
import frameless.syntax._
import $organization$.$name$.config.JobTypesafeConfiguration

final case class User(id: Int, name: String)

final case class Event(eventId: Int, userId: Int)

final case class UInfo(name: String)

final case class UInfoError(nameError: String)

final case class UserEvent(eventId: Int, userId: Int, name: String)

/**
 * follow along at https://typelevel.org/frameless/FeatureOverview.html
 * also here https://typelevel.org/frameless/TypedDatasetVsSparkDataset.html
  **/
object JobTypesafe extends SparkBaseRunner[JobTypesafeConfiguration] {
  val spark = createSparkSession(this.getClass.getName)

  import spark.implicits._

  // create the data sets
  val users = Seq(User(1, "Anna"), User(2, "Bob")).toDS
  val events = Seq(Event(101, 1),
                   Event(102, 2),
                   Event(103, 1),
                   Event(104, 2),
                   Event(105, 1),
                   Event(106, 2),
                   Event(107, 1),
                   Event(108, 2)).toDS

  val fUsers  = TypedDataset.create(users)
  val fEvents = TypedDataset.create(events)

  users.show
  users.printSchema
  users.explain
  events.printSchema
  events.show

  fUsers.show().run
  fUsers.printSchema
  fUsers.explain()
  fEvents.show().run

  // some basic operations
  // TODO cant refactor name of id column using IDE.
  // How to get typesafe refactoring for this operation?
  // Can Datasets[T] api be used to achieve desired behaviour?
  users.select('id).show
  users.select('id).explain
  users.select('id).as[Int].show
  users.select('id).as[Int].explain

  // TODO however, still 'id cant be refactored. Am I missing something here?
  fUsers.select(fUsers('name)).show().run
  fUsers.select(fUsers('name)).explain()

  // but using projection instead of SELECT
  // should work to have something refactorable
  fUsers.project[UInfo].show().run
  fUsers.project[UInfo].explain()
  fUsers.drop[UInfo].show().run
  fUsers.drop[UInfo].explain()
  // fUsers.project[UInfoError] // should fail due to typo

  // using a Dataset[T] map transformation causes additional serialization
  users.map(_.id).show
  users.map(_.id).explain

  // DF operations in spark
  val joinedDF = events.join(users, events("userId") === users("id")).drop('id)
  joinedDF.show
  joinedDF.explain
  joinedDF.printSchema

  // dataset
  val joinedDS = events.joinWith(users, events.col("userId") === users.col("id")).drop('id)
  joinedDS.show
  joinedDS.printSchema
  joinedDS.explain

  val filteredDF = joinedDF.filter('eventId >= 105)
  filteredDF.show
  filteredDF.explain

  val fJoined = fUsers.joinInner(fEvents) {
    fUsers('id) === fEvents('userId)
  } // TODO drop column id
  fJoined.show().run
  fJoined.explain()
  fJoined.printSchema

  // TODO nested column after join
  // TODO ask how to normalize columns after join
  // defined class AptPriceCity

  val normalized = fJoined
    .select(
      fJoined.colMany('_2, 'eventId),
      fJoined.colMany('_2, 'userId),
      fJoined.colMany('_1, 'name)
    )
    .as[UserEvent]
  val fFiltered = normalized.filter(normalized('eventId) >= 105)
  fFiltered.show().run
  fFiltered.explain()

  // finally convert back to spark dataframe
  fFiltered.dataset.explain
}
