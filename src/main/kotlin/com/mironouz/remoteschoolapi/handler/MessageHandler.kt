package com.mironouz.remoteschoolapi.handler

import com.mironouz.remoteschoolapi.model.Message
import com.mironouz.remoteschoolapi.repository.MessageRepository
import com.mironouz.remoteschoolapi.repository.UserRepository
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.awaitPrincipal
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.sse
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.Date

class MessageHandler(private val messageRepository: MessageRepository,
                     private val userRepository: UserRepository) {

    private class AdHocMessage(val text: String, val timestamp: Date)
    private val messageStream = messageRepository.findAll().share()
    private val heartbeat = Flux.interval(Duration.ofSeconds(10)).map { "heartbeat" }

    init {
        messageStream.subscribe()
    }

    suspend fun postMessage(request: ServerRequest) : ServerResponse {
        val principal = request.awaitPrincipal()
        val user = principal?.name?.let { userRepository.getUserByEmail(it) }
        val adHocMessage = request.awaitBody<AdHocMessage>()
        messageRepository.save(Message(user!!, adHocMessage.text, adHocMessage.timestamp))
        return ServerResponse.accepted().buildAndAwait()
    }

    suspend fun findAll(@Suppress("UNUSED_PARAMETER") request: ServerRequest): ServerResponse =
            ServerResponse.ok().sse()
                    // disable nginx buffering (fix for ssl)
                    // see: https://stackoverflow.com/questions/27898622
                    .header("X-Accel-Buffering", "no")
                    .body(Flux.merge(messageStream, heartbeat), Message::class.java)
                    .awaitLast()
}