import akka.stream.alpakka.slick.scaladsl.SlickSession
import com.google.inject.Inject

import scala.concurrent.Future

object Episode extends Enumeration {
  val NEWHOPE, EMPIRE, JEDI = Value
}

trait Character {
  def id: String
  def name: Option[String]
  def friends: String
  def appearsIn: String
}

case class Human (
                 id: String,
                 name: Option[String],
                 friends: String,
                 appearsIn: String,
                 homePlanet: Option[String]
                 ) extends Character

case class Droid (
                 id: String,
                 name: Option[String],
                 friends: String,
                 appearsIn: String,
                 primaryFunction: Option[String]
                 ) extends Character

class CharacterRepo @Inject() (
  slickSession: SlickSession, tables: Tables
) {
  import CharacterRepo._
  import slick.jdbc.PostgresProfile.api._
  import tables._

  def getHero(episode: String): Option[Human] =
    humans.headOption

  def getHuman(id: String): Option[Human] = humans.find(c => c.id == id)

  def getDroid(id: String): Option[Droid] = droids.find(c => c.id == id)

  def getHumans(limit: Int, offset: Int): Future[Seq[Human]] = slickSession.db run {
    Humans.result
  }

  def getDroids(limit: Int, offset: Int): List[Droid] = droids.slice(offset, offset + limit)

  def addHuman(id: String, name: String, friends: String, appearsIn: String, homePlanet: Option[String]): Human = {
    val human = Human(id, Some(name), friends, appearsIn, homePlanet)
    println("adding data to human Human", human)
    humans = humans :+ human
    println("new human", humans)
    human
  }
}

object CharacterRepo {
  var humans = List(
    Human(
      id = "1000",
      name = Some("Luke Skywalker"),
      friends = "2001",
      appearsIn = "Hello",
      homePlanet = Some("Tatooine")),
    Human(
      id = "1001",
      name = Some("Darth Vader"),
      friends = "2002",
      appearsIn = "8948",
      homePlanet = Some("Tatooine")),
    Human(
      id = "1002",
      name = Some("Han Solo"),
      friends = "1000",
      appearsIn = "jduur",
      homePlanet = None),
    Human(
      id = "1003",
      name = Some("Leia Organa"),
      friends = "895",
      appearsIn = "Yuei",
      homePlanet = Some("Alderaan")),
    Human(
      id = "1004",
      name = Some("Wilhuff Tarkin"),
      friends = "1001",
      appearsIn = "Good",
      homePlanet = None)
  )

  val droids = List(
    Droid(
      id = "2000",
      name = Some("C-3PO"),
      friends = "3004",
      appearsIn = "NewHope",
      primaryFunction = Some("Protocol")),
    Droid(
      id = "2001",
      name = Some("R2-D2"),
      friends = "3004",
      appearsIn = "JEDI",
      primaryFunction = Some("Astromech"))
  )
}
