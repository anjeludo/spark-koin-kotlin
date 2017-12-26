package prueba


import spark.Spark
import spark.Spark.after
import spark.Spark.port


fun start(port: Int = 4567, controllersInit: () -> Unit): Int {

    port(port)

    // Logging filter
    after ("*") { request, response ->
        println(request.requestMethod() + " " + request.pathInfo() + " - " + response.raw().status)
    }

    // launch controllers initialization
    controllersInit()

    // This is the important line. It must be *after* creating the routes and *before* the call to port()
    Spark.awaitInitialization()

    // Will return the automatically defined port if requested port was 0 (useful for testing)
    return port()
}
