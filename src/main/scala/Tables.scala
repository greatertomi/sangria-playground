import slick.jdbc.H2Profile.api._

class Humans(tag: Tag) extends Table[(String, String, String, String, String)](tag, "HUMANS") {
  def id = column[String]("id", O.PrimaryKey)
  def name = column[String]("name")
  def friends = column[String]("friends")
  def appearsIn = column[String]("appearsIn")
  def homePlanet = column[String]("homePlanet")

  def * = (id, name, friends, appearsIn, homePlanet)
}

class Droids(tag: Tag) extends Table[(String, String, String, String, String)](tag, "DROIDS") {
  def id = column[String]("id", O.PrimaryKey)
  def name = column[String]("name")
  def friends = column[String]("friends")
  def appearsIn = column[String]("appearsIn")
  def primaryFunction = column[String]("primaryFunction")

  def * = (id, name, friends, appearsIn, primaryFunction)
}

val Humans = TableQuery[Humans]
val Droids = TableQuery[Droids]
