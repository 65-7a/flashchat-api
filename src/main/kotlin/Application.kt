package com.callumwong

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import java.util.concurrent.ConcurrentHashMap

val rooms = ConcurrentHashMap<Int, ChatRoom>()

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureRouting()
    install(CORS) {
        allowHost("*")
        allowHeader("Content-Type")
    }
}
