// $copyright
package $organization$.$name$

/**
 * A simple test for everyone's favourite wordcount example.
 */

import $organization$.$name$.service.CountService
import com.holdenkarau.spark.testing.{DatasetSuiteBase, SharedSparkContext}
import org.scalatest.{FlatSpec, Matchers}

case class FooBar(foo:Int, bar:String)
class CountServiceTest extends FlatSpec with Matchers with SharedSparkContext with DatasetSuiteBase {
  "A CountService" should "count the length of a string in the column of a spark.dataFrame" in {

    import spark.implicits._
    val df = Seq(FooBar(1, "a"), FooBar(2, "bb")).toDS
    val result = CountService.performCount(df.toDF, 'bar)
    assert(result.equals(Seq(1,2)))
  }
}
