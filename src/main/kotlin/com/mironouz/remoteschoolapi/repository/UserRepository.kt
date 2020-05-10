package com.mironouz.remoteschoolapi.repository

import com.mironouz.remoteschoolapi.model.User
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.oneAndAwait

class UserRepository(private val mongo: ReactiveMongoOperations) {
    suspend fun save(user: User): User = mongo.insert(User::class.java).oneAndAwait(user)
    fun getUserByEmail(email: String): User? = mongo.findById(email, User::class.java).block()
}