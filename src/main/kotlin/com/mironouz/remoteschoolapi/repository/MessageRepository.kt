package com.mironouz.remoteschoolapi.repository

import com.mironouz.remoteschoolapi.model.Grade
import com.mironouz.remoteschoolapi.model.Message
import com.mironouz.remoteschoolapi.model.User
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.repository.Tailable
import java.util.Date

class MessageRepository(private val mongo: ReactiveMongoOperations){
    private val fakeMessage = Message(
            User("", "", Grade.FIFTH, "heartbeat", ""),
            "",
            Date())

    suspend fun save(message: Message): Message = mongo.insert(Message::class.java).oneAndAwait(message)

    @Tailable
    fun findAll() = mongo.query(Message::class.java).tail()

    fun recreateCollection() {
        mongo.dropCollection(Message::class.java)
                .then(mongo.createCollection(Message::class.java,
                        CollectionOptions
                                .empty()
                                .size(1_000_000)
                                .maxDocuments(100)
                                .capped()))
                .then(mongo.insert(Message::class.java).one(fakeMessage))
                .block()
    }
}