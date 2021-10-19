import com.github.tminglei.slickpg._
import com.github.tminglei.slickpg.str.PgStringSupport
import io.circe._
import io.circe.parser._
import slick.basic.Capability

sealed trait BasePostgresProfile extends ExPostgresProfile
  with PgArraySupport
  with PgDateSupportJoda
  with PgDate2Support
  with PgRangeSupport
  with PgHStoreSupport
  with PgCirceJsonSupport
  with PgSearchSupport
  with PgNetSupport
  with PgLTreeSupport
  with PgStringSupport

sealed trait PostgresProfile extends BasePostgresProfile {
  def pgjson = "jsonb"

  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  override val api: DatabaseAPI = DatabaseAPI

  sealed trait BaseAPI extends API with ArrayImplicits
    with JodaDateTimeImplicits
    with DateTimeImplicits
    with JsonImplicits
    with NetImplicits
    with LTreeImplicits
    with RangeImplicits
    with HStoreImplicits
    with SearchImplicits
    with SearchAssistants
    with PgStringImplicits

  sealed trait DatabaseAPI extends BaseAPI {
    private def optimisticParse(s: String): Json = {
      parse(s) match {
        case Right(json) => json
        case Left(error) =>
          logger.warn("Error while parsing Json", error)
          Json.Null
      }
    }

    private def optimisticParse[T](decoder: Decoder[T], default: T)(s: String): T = {
      parse(s) flatMap { json =>
        decoder.decodeJson(json)
      } getOrElse default
    }

    implicit val strListTypeMapper: DriverJdbcType[Seq[String]] = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val circeJsonArrayTypeMapper: DriverJdbcType[Seq[Json]] = new AdvancedArrayJdbcType[Json](pgjson,
      s => utils.SimpleArrayUtils.fromString[Json](optimisticParse)(s).orNull,
      v => utils.SimpleArrayUtils.mkString[Json](_.spaces2)(v)
    ).to(_.toList)
  }

  object DatabaseAPI extends DatabaseAPI
}

object PostgresProfile extends PostgresProfile
