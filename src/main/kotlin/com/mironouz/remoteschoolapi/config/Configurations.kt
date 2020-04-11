package com.mironouz.remoteschoolapi.config

import com.mironouz.remoteschoolapi.handler.UserHandler
import com.mironouz.remoteschoolapi.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.configuration
import org.springframework.web.reactive.function.server.coRouter


val appConfig = configuration {
    beans {
        bean<UserRepository>()
        bean<UserHandler>()
        bean(::route)
    }
    listener<ApplicationReadyEvent> {
        runBlocking {
            ref<UserRepository>().recreateCollection()
            print("User collection created")
        }
    }
}

fun route(userHandler: UserHandler) = coRouter {
    POST("/register", userHandler::registerUser)
    GET("/users", userHandler::findAll)
}