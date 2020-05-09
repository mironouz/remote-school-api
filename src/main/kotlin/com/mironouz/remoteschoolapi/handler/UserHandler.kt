package com.mironouz.remoteschoolapi.handler

import com.mironouz.remoteschoolapi.model.User
import com.mironouz.remoteschoolapi.repository.UserRepository
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.buildAndAwait
import reactor.kotlin.core.publisher.switchIfEmpty

class UserHandler(private val repository: UserRepository, private val userService: MapReactiveUserDetailsService) {
    suspend fun registerUser(request: ServerRequest) : ServerResponse {
        val user = request.awaitBody<User>()
        repository.save(user)
        userService
                .findByUsername(user.password)
                .switchIfEmpty {
                    val userDetails = org.springframework.security.core.userdetails.User
                            .withDefaultPasswordEncoder()
                            .username(user.email)
                            .password("")
                            .roles("USER")
                            .build()
                    userService.updatePassword(userDetails, "{noop}" + user.password)
                }
                .block()
        return ServerResponse.accepted().buildAndAwait()
    }
}