package com.mironouz.remoteschoolapi.handler

import com.mironouz.remoteschoolapi.model.User
import com.mironouz.remoteschoolapi.repository.UserRepository
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.sse
import reactor.kotlin.core.publisher.switchIfEmpty

class UserHandler(private val repository: UserRepository, private val userService: MapReactiveUserDetailsService) {
    suspend fun registerUser(request: ServerRequest) : ServerResponse {
        val user = request.awaitBody<User>()
        repository.save(user)
        userService
                .findByUsername(user.name)
                .switchIfEmpty {
                    val userDetails = org.springframework.security.core.userdetails.User
                            .withDefaultPasswordEncoder()
                            .username(user.name)
                            .password("{noop}" + user.surname)
                            .roles("USER")
                            .build()
                    userService.updatePassword(userDetails, "{noop}" + user.surname)
                }
                .block()
        return ServerResponse.accepted().buildAndAwait()
    }

    suspend fun findAll(serverRequest: ServerRequest): ServerResponse =
            ok().sse()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(repository.findAll(), User::class.java)
                    .awaitLast()
}