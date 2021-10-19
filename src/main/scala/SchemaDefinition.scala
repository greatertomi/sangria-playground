import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._

import scala.concurrent.Future

object SchemaDefinition {
  val characters: Fetcher[CharacterRepo, Character with Product with Serializable, Character with Product with Serializable, String] = Fetcher.caching(
    (ctx: CharacterRepo, ids: Seq[String]) =>
      Future.successful(ids.flatMap(id => ctx.getHuman(id) orElse ctx.getDroid(id))))(HasId(_.id))

  val Character: InterfaceType[CharacterRepo, Character] =
    InterfaceType(
      "Character",
      "A Character in the Star Wars Trilogy",
      () => fields[CharacterRepo, Character](
        Field("id", StringType,
          Some("The id of the character"),
          resolve = _.value.id
        ),
        Field("name", OptionType(StringType),
          Some("The name of the character"),
          resolve = _.value.name
        ),
        Field("friends", StringType,
          Some("The friends of the character, or an empty list if they have none"),
          resolve = _.value.friends
        ),
        Field("appearsIn", StringType,
          Some("Which movies they appear in."),
          resolve = _.value.appearsIn
        )
      )
    )

  val Human: ObjectType[CharacterRepo, Human] =
    ObjectType(
      "Human",
      "A humanoid creature in the Star Wars universe.",
      interfaces[CharacterRepo, Human](Character),
      fields[CharacterRepo, Human](
        Field("id", StringType,
          Some("The id of the human."),
          resolve = _.value.id
        ),
        Field("name", OptionType(StringType),
          Some("The name of the human"),
          resolve = _.value.name
        ),
        Field("friends", StringType,
          Some("The friends of the human, or an empty list if they have none"),
          resolve = _.value.friends
        ),
        Field("appearsIn", StringType,
          Some("Which movies they appear in."),
          resolve = _.value.appearsIn
        ),
        Field("homePlanet", OptionType(StringType),
          Some("The home planet of the human, or null if unknown"),
          resolve = _.value.homePlanet
        )
      )
    )

  val Droid: ObjectType[CharacterRepo, Droid] = ObjectType(
    "Droid",
    "A mechanical creature in the Star Wars universe.",
    interfaces[CharacterRepo, Droid](Character),
    fields[CharacterRepo, Droid](
      Field("id", StringType,
        Some("The id of the droid."),
        resolve = _.value.id),
      Field("name", OptionType(StringType),
        Some("The name of the droid."),
        resolve = _.value.name),
      Field("friends", StringType,
        Some("The friends of the droid, or an empty list if they have none."),
        resolve = _.value.friends),
      Field("appearsIn", StringType,
        Some("Which movies they appear in."),
        resolve = _.value.appearsIn),
      Field("primaryFunction", OptionType(StringType),
        Some("The primary function of the droid."),
        resolve = _.value.primaryFunction)
    ))

  val ID: Argument[String] = Argument("id", StringType, description = "id of the character")

  val EpisodeArg = Argument("episode", StringType,
    description = "If omitted, returns the heri of the whole saga. If provided, returns the hero of that particular episode.")

  val LimitArg: Argument[Int] = Argument("limit", OptionInputType(IntType), defaultValue = 20)
  val OffsetArg: Argument[Int] = Argument("offset", OptionInputType(IntType), defaultValue = 0)

  val NameArg: Argument[String] = Argument("name", StringType,
    description = "Name of the human")
  val FriendsArg = Argument("friends", StringType)
  val AppearsInArg = Argument("appearIns", StringType)
  val HomePlanetArg: Argument[Option[String]] = Argument("homePlanet", OptionInputType(StringType))

  val Query: ObjectType[CharacterRepo, Unit] = ObjectType(
    "Query", fields[CharacterRepo, Unit](
      Field("hero", OptionType(Character),
        arguments = EpisodeArg :: Nil,
        deprecationReason = Some("Use `human` or `droid` fields instead"),
        resolve = ctx => ctx.ctx.getHero(ctx arg EpisodeArg)
      ),
      Field("human", OptionType(Human),
        arguments = ID :: Nil,
        resolve = ctx => ctx.ctx.getHuman(ctx arg ID)
      ),
      Field("droid", Droid,
        arguments = ID :: Nil,
        resolve = ctx => ctx.ctx.getDroid(ctx arg ID).get
      ),
      Field("humans", ListType(Human),
        arguments = LimitArg :: OffsetArg :: Nil,
        resolve = ctx => ctx.ctx.getHumans(ctx arg LimitArg, ctx arg OffsetArg)
      ),
      Field("droids", ListType(Droid),
        arguments = LimitArg :: OffsetArg :: Nil,
        resolve = ctx => ctx.ctx.getDroids(ctx arg LimitArg, ctx arg OffsetArg)
      )
    )
  )

  val Mutation: ObjectType[CharacterRepo, Unit] = ObjectType(
    "Mutation", fields[CharacterRepo, Unit](
      Field("addHuman", Human,
        arguments = ID :: NameArg :: FriendsArg :: AppearsInArg :: HomePlanetArg :: Nil,
        resolve = req => req.ctx.addHuman(req.arg(ID), req.arg(NameArg), req.arg(FriendsArg), req.arg(AppearsInArg), req.arg(HomePlanetArg))
      )
    )
  )

  val StarWarsSchema: Schema[CharacterRepo, Unit] = Schema(Query, Some(Mutation))
}
