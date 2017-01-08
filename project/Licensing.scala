import de.heikoseeberger.sbtheader._
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import de.heikoseeberger.sbtheader.license.Apache2_0

object Licensing {

  private[this] val LicenseYear = "2017"

  val settings = Seq(
    headers := Map(
      "scala" -> license(),
      "java"  -> license(),
      "conf"  -> license("#"),
      "yml"   -> license("#"),
      "sh"    -> license("#")
    )
  )

  private[this] def license(commentStyle: String = "*") =
    Apache2_0(LicenseYear, "Antonio Alonso Dominguez", commentStyle)

}