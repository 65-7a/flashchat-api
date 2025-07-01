package com.callumwong

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random

fun Application.configureRouting() {
    routing {
        post("/create") {
            val name = call.request.queryParameters["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "no name provided")
            val code = Random.nextInt(1000, 10000) // ensure uniqueness
            rooms[code] = ChatRoom()
            call.respondText(code.toString())
            println("\"$name\" created room $code")
        }
        post("/join") {
            val code = call.request.queryParameters["code"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid code")
            if (rooms.containsKey(code)) {
                call.respondText(code.toString())
            } else {
                call.respondText("no room with code", status = HttpStatusCode.BadRequest)
            }
        }
    }
}
