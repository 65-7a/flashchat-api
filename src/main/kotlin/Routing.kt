package com.callumwong

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

var rooms = ArrayList<Room>();

fun Application.configureRouting() {
    routing {
        post("/create") {
            val room = Room(code = Random.nextInt(1000, 10000));
            rooms.add(room) // ensure uniqueness
            call.respondText(room.code.toString())
        }
        post("/join") {
            val code = call.request.queryParameters["code"] ?: return@post
            if (rooms.none { r -> r.code == code.toInt() }) {
                call.respondText("no room with code")
            } else {
                call.respondText(code)
            }
        }
    }
}
