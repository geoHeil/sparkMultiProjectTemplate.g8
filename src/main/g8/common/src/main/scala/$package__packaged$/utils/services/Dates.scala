// $copyright$
package $organization$.$name$.utils.services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Dates {

  def currentDateFormatted(pattern: String): String = LocalDateTime.now.format(formatter(pattern))

  def formatter(pattern: String): DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)

}
