package com.mironouz.remoteschoolapi.integration

import com.mironouz.remoteschoolapi.app
import com.mironouz.remoteschoolapi.model.Grade
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

class IntegrationTests {
    private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()
    private lateinit var context: ConfigurableApplicationContext
    private class User(val name: String, val surname: String, val grade: Grade, val email: String, val password: String)
    private data class Auth(val email: String, val password: String)

    @BeforeAll
    fun beforeAll() {
        context = app.run()
    }

    @Test
    fun `Check application flow`() {
        // given - application without any registered user
        val testUser =
                User("testName", "testSurname", Grade.FIFTH, "testEmail", "testPassword")
        val testAuth = Auth("testEmail", "testPassword")

        // when-then - check user which does not exist
        client.post()
                .uri("/api/checkUser")
                .bodyValue(testAuth)
                .exchange()
                .expectStatus().isBadRequest

        // when - user has registered
        client.post()
                .uri("/api/register")
                .bodyValue(testUser)
                .exchange()
                .expectStatus().isAccepted

        // then - it is possible to check him successfully
        client.post()
                .uri("/api/checkUser")
                .bodyValue(testAuth)
                .exchange()
                .expectStatus().isOk

        // when-then - check existed user with wrong password
        client.post()
                .uri("/api/checkUser")
                .bodyValue(Auth("testEmail", "wrong"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }
    
    @AfterAll
    fun afterAll() {
        context.close()
    }
}