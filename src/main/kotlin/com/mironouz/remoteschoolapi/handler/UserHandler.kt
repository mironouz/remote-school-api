package com.mironouz.remoteschoolapi.handler

import com.mironouz.remoteschoolapi.model.User
import com.mironouz.remoteschoolapi.repository.UserRepository
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

class UserHandler(private val repository: UserRepository) {
    suspend fun registerUser(request: ServerRequest) : ServerResponse {
        val user = request.awaitBody<User>()
        repository.save(user)
        return ServerResponse.accepted().buildAndAwait()
    }

    suspend fun findAll(serverRequest: ServerRequest): ServerResponse =
            ok().sse().body(repository.findAll(), User::class.java).awaitLast()
}