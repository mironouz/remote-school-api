package com.mironouz.remoteschoolapi.handler

import com.mironouz.remoteschoolapi.model.Message
import com.mironouz.remoteschoolapi.repository.MessageRepository
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.sse

class MessageHandler(private val repository: MessageRepository) {
    suspend fun postMessage(request: ServerRequest) : ServerResponse {
        val message = request.awaitBody<Message>()
        repository.save(message)
        return ServerResponse.accepted().buildAndAwait()
    }

    suspend fun findAll(serverRequest: ServerRequest): ServerResponse =
            ServerResponse.ok().sse()
                    // disable nginx buffering (fix for ssl)
                    // see: https://stackoverflow.com/questions/27898622
                    .header("X-Accel-Buffering", "no")
                    .body(repository.findAll(), Message::class.java)
                    .awaitLast()
}