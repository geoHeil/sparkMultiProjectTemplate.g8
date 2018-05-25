// $copyright$
package $organization$.$name$.utils

object Tools {

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit final class AnyOps[A](self: A) {
    def ===(other: A): Boolean = self == other
    def =!=(other: A): Boolean = self != other
  }
}
