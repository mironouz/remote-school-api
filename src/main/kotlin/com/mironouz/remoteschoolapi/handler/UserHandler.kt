package com.mironouz.remoteschoolapi.handler

import com.mironouz.remoteschoolapi.model.User
import com.mironouz.remoteschoolapi.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.buildAndAwait
import reactor.kotlin.core.publisher.switchIfEmpty

class UserHandler(private val repository: UserRepository, private val userService: MapReactiveUserDetailsService) {
    private class Auth(val email: String, val password: String)

    suspend fun registerUser(request: ServerRequest) : ServerResponse {
        val user = request.awaitBody<User>()
        var status = HttpStatus.ACCEPTED
        userService
                .findByUsername(user.email)
                .doOnNext {
                    status = HttpStatus.CONFLICT
                }
                .switchIfEmpty {
                    val userDetails = org.springframework.security.core.userdetails.User
                            .withDefaultPasswordEncoder()
                            .username(user.email)
                            .password("")
                            .roles("USER")
                            .build()
                    userService.updatePassword(userDetails, "{noop}" + user.password)
                }
                .subscribe()
        if (status == HttpStatus.ACCEPTED) {
            repository.save(user)
        }
        return ServerResponse.status(status).buildAndAwait()
    }

    suspend fun checkUser(request: ServerRequest) : ServerResponse {
        val auth = request.awaitBody<Auth>()
        var status = HttpStatus.BAD_REQUEST
        userService
                .findByUsername(auth.email)
                .doOnNext {
                    status = if ("{noop}${auth.password}" == it.password) HttpStatus.OK else HttpStatus.CONFLICT
                }
                .subscribe()
        return ServerResponse.status(status).buildAndAwait()
    }
}
