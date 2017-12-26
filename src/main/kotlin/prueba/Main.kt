package prueba

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Spark.*
import java.util.concurrent.atomic.AtomicInteger
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject

val footballPlayerModule = applicationContext {
    bean { FootballPlayerDAO() as IFootballPlayerDAO }
}

fun main(args: Array<String>) {
    // Spark
    start {
        // Koin
        startKoin(listOf(footballPlayerModule))
        // Controllers
        FootballPlayerController()
    }
}

// CONTROLLER

class FootballPlayerController : KoinComponent {

    private val playersDAO: IFootballPlayerDAO by inject()

    init {
        path("/players") {
            get("") { req, res ->
                jacksonObjectMapper().writeValueAsString(playersDAO.getFootballPlayers())
            }

            get("/:id") { req, res ->
                playersDAO.find(req.params("id").toInt())
            }

            get("/name/:name") { req, res ->
                playersDAO.findByName(req.params("name"))
            }

            post("/create") { req, res ->
                playersDAO.save(name = req.queryParams("name"),
                        passSkill = req.queryParams("passSkill").toDouble(),
                        kickSkill = req.queryParams("kickSkill").toDouble()
                )
                res.status(201)
                "ok"
            }

            patch("/update/:id") { req, res ->
                playersDAO.update(
                        id = req.params("id").toInt(),
                        name = req.queryParams("name"),
                        passSkill = req.queryParams("passSkill").toDouble(),
                        kickSkill = req.queryParams("kickSkill").toDouble()
                )
                "ok"
            }

            delete("/delete/:id") { req, res ->
                playersDAO.delete(req.params("id").toInt())
                "ok"
            }

        }
    }
}

// DOMAIN
data class FootballPlayer(val name: String, val passSkill: Double,val kickSkill: Double, val id: Int)


// DAO
interface IFootballPlayerDAO {
    fun getFootballPlayers(): HashMap<Int, FootballPlayer>
    fun save(name: String, passSkill: Double, kickSkill: Double)
    fun find(id:Int): FootballPlayer?
    fun findByName(name: String): FootballPlayer?
    fun update(id:Int, name: String, passSkill: Double, kickSkill: Double)
    fun delete(id:Int)
}

class FootballPlayerDAO: IFootballPlayerDAO {

    private val players = hashMapOf(
            1 to FootballPlayer("Messi",99.9,99.9,1),
            1 to FootballPlayer("Deco",79.9,83.9,2),
            2 to FootballPlayer("Jordi Alba",88.3,79.0,3)
    )

    private var lastId: AtomicInteger = AtomicInteger(players.size)

    override fun getFootballPlayers(): HashMap<Int, FootballPlayer> {
        return players
    }

    override fun save(name: String, passSkill: Double, kickSkill: Double) {
        val id = lastId.incrementAndGet()
        players.put(id, FootballPlayer(name, passSkill, kickSkill, id))
    }

    override fun find(id:Int): FootballPlayer? {
        return players[id]
    }

    override fun findByName(name: String): FootballPlayer? {
        return players.values.find { it.name == name }
    }

    override fun update(id:Int, name: String, passSkill: Double, kickSkill: Double) {
        players.put(id, FootballPlayer(name = name, passSkill = passSkill, kickSkill = kickSkill, id = id))
    }

    override fun delete(id:Int) {
        players.remove(id)
    }
}