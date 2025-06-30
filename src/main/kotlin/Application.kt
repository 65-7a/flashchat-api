package com.callumwong

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

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
