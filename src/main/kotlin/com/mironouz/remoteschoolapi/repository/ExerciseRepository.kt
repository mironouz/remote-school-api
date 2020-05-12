package com.mironouz.remoteschoolapi.repository

import com.mironouz.remoteschoolapi.model.Exercise
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.oneAndAwait

class ExerciseRepository(private val mongo: ReactiveMongoOperations) {
    suspend fun save(exercise: Exercise): Exercise = mongo.insert(Exercise::class.java).oneAndAwait(exercise)
    fun findAll() = mongo.findAll(Exercise::class.java)
    fun findById(id: Long) = mongo.findById(id, Exercise::class.java)
}