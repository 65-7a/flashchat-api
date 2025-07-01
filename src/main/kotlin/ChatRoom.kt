package com.callumwong

import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChatRoom() {
    val messageFlow = MutableSharedFlow<ChatMessage>()
    val sharedFlow = messageFlow.asSharedFlow()
    val clients = mutableSetOf<DefaultWebSocketServerSession>()
}