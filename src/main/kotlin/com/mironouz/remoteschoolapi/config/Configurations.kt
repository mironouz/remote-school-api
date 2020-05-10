package com.mironouz.remoteschoolapi.config

import com.mironouz.remoteschoolapi.handler.MessageHandler
import com.mironouz.remoteschoolapi.handler.UserHandler
import com.mironouz.remoteschoolapi.repository.MessageRepository
import com.mironouz.remoteschoolapi.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.fu.kofu.configuration
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.WebFilterChainProxy
import org.springframework.web.reactive.function.server.coRouter
import java.util.concurrent.ConcurrentHashMap


val appConfig = configuration {
    beans {
        bean<UserRepository>()
        bean<MessageRepository>()
        bean<UserHandler>()
        bean<MessageHandler>()
        bean(::route)
    }
    listener<ApplicationReadyEvent> {
        runBlocking {
            ref<MessageRepository>().recreateCollection()
            print("Messages collection created")
        }
    }
}

val securityConfig = configuration {
    beans {
        bean {
            WebFilterChainProxy(ref<ServerHttpSecurity>().build())
        }
        bean(scope = BeanDefinitionDsl.Scope.PROTOTYPE) {
            ServerHttpSecurity.http()
                    .authenticationManager(
                            UserDetailsRepositoryReactiveAuthenticationManager(ref<MapReactiveUserDetailsService>()))
                    .csrf().disable()
                    .authorizeExchange()
                    .pathMatchers("/api/register").permitAll()
                    .anyExchange().authenticated()
                    .and()
                    .httpBasic().and()
        }
        bean {
            MapReactiveUserDetailsService(ConcurrentHashMap())
        }
    }
}

fun route(userHandler: UserHandler, messageHandler: MessageHandler) = coRouter {
    "/api".nest {
        POST("/register", userHandler::registerUser)
        POST("/message", messageHandler::postMessage)
        GET("/messages", messageHandler::findAll)
        GET("/checkUser", userHandler::checkUser)
    }
}