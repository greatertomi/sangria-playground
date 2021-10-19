import slick.jdbc.PostgresProfile

class Tables (val jdbcProfile: PostgresProfile) {
  import jdbcProfile.api._

  object schema {
    class Humans(tag: Tag) extends Table[Human](tag, "humans") {
      def id = column[String]("id", O.PrimaryKey)
      def name = column[Option[String]]("name")
      def friends = column[String]("friends")
      def appearsIn = column[String]("appearsIn")
      def homePlanet = column[Option[String]]("homePlanet")

      def * = (id, name, friends, appearsIn, homePlanet) <> (Human.tupled, Human.unapply)

    }

    class Droids(tag: Tag) extends Table[(String, String, String, String, String)](tag, "droids") {
      def id = column[String]("id", O.PrimaryKey)
      def name = column[String]("name")
      def friends = column[String]("friends")
      def appearsIn = column[String]("appearsIn")
      def primaryFunction = column[String]("primaryFunction")

      def * = (id, name, friends, appearsIn, primaryFunction)
    }
  }

  val Droids = TableQuery[schema.Droids]
  val Humans = TableQuery[schema.Humans]
}
