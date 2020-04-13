package com.mironouz.remoteschoolapi.repository

import com.mironouz.remoteschoolapi.model.User
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.repository.Tailable

class UserRepository(private val mongo: ReactiveMongoOperations){
    suspend fun save(user: User): User = mongo.insert(User::class.java).oneAndAwait(user)

    @Tailable
    fun findAll() = mongo.query(User::class.java).tail()

    fun recreateCollection() {
        mongo.dropCollection(User::class.java)
                .then(mongo.createCollection(User::class.java,
                        CollectionOptions
                                .empty()
                                .size(1_000_000)
                                .maxDocuments(100)
                                .capped()))
                .then()
                .block()
    }
}