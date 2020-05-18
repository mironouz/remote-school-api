package com.mironouz.remoteschoolapi.handler

import com.mironouz.remoteschoolapi.model.Message
import com.mironouz.remoteschoolapi.repository.MessageRepository
import com.mironouz.remoteschoolapi.repository.UserRepository
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.awaitPrincipal
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.sse
import java.util.Date

class MessageHandler(private val messageRepository: MessageRepository,
                     private val userRepository: UserRepository) {

    private class AdHocMessage(val text: String, val timestamp: Date)

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
                    .body(messageRepository.findAll()
                            .filter {
                                it.timestamp.time > request
                                        .headers()
                                        .header("Last-Event-ID")
                                        .getOrElse(0) { "0" }.toLong()
                            }
                            .map {
                                ServerSentEvent.builder<Message>()
                                        .id(it.timestamp.time.toString())
                                        .data(it)
                                        .build()
                            }, Message::class.java)
                    .awaitLast()
}