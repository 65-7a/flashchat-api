package com.callumwong

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = JacksonWebsocketContentConverter()
    }

    routing {
        webSocket("/ws/{code?}") {
            val code = call.parameters["code"]?.toIntOrNull()
            if (code == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "No room code specified"))
                return@webSocket
            }

            val room = rooms.getOrElse(code) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "No such room"))
                return@webSocket
            }

            val nameFrame = incoming.receiveCatching().getOrNull()
            if (nameFrame !is Frame.Text) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "No name"))
                return@webSocket
            }
            val name = nameFrame.readText()
            room.clients += this

            val job = launch {
                room.sharedFlow.collect { message ->
                    sendSerialized(message)
                }
            }

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val msg = frame.readText()
                        val messageResponse = ChatMessage(name = name, message = msg)
                        room.messageFlow.emit(messageResponse)
                    }
                }
            }.onFailure { exception ->
                println("WebSocket exception: ${exception.localizedMessage}")
            }.also {
                job.cancel()
                room.clients -= this

                // If no clients remain, delete the room
                if (room.clients.isEmpty()) {
                    rooms.remove(code.toInt())
                    println("Room $code deleted (no active clients).")
                }
            }
        }
    }
}
