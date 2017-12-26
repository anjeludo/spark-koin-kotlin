package prueba

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Spark.*
import java.util.concurrent.atomic.AtomicInteger

fun main(args: Array<String>) {
    val playersDAO = FootballPlayerDAO()
    path("/players") {

        get("") { req, res ->
            jacksonObjectMapper().writeValueAsString(playersDAO.players)
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

    playersDAO.players.forEach(::println)

}


data class FootballPlayer(val name: String, val passSkill: Double,val kickSkill: Double, val id: Int)

class FootballPlayerDAO {

    val players = hashMapOf(
            1 to FootballPlayer("Messi",99.9,99.9,1),
            1 to FootballPlayer("Deco",79.9,83.9,2),
            2 to FootballPlayer("Jordi Alba",88.3,79.0,3)
    )

    var lastId: AtomicInteger = AtomicInteger(players.size)

    fun save(name: String, passSkill: Double, kickSkill: Double) {
        val id = lastId.incrementAndGet()
        players.put(id, FootballPlayer(name, passSkill, kickSkill, id))
    }

    fun find(id:Int): FootballPlayer? {
        return players[id]
    }

    fun findByName(name: String): FootballPlayer? {
        return players.values.find { it.name == name }
    }

    fun update(id:Int, name: String, passSkill: Double, kickSkill: Double) {
        players.put(id, FootballPlayer(name = name, passSkill = passSkill, kickSkill = kickSkill, id = id))
    }

    fun delete(id:Int) {
        players.remove(id)
    }
}