package com.mironouz.remoteschoolapi.handler

import com.mironouz.remoteschoolapi.model.Exercise
import com.mironouz.remoteschoolapi.repository.ExerciseRepository
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.buildAndAwait

class ExerciseHandler(private val repository: ExerciseRepository) {
    suspend fun postExercise(request: ServerRequest) : ServerResponse {
        val exercise = request.awaitBody<Exercise>()
        exercise.id = Exercise.getNextId()
        repository.save(exercise)
        return ok().buildAndAwait()
    }

    suspend fun listExercises(@Suppress("UNUSED_PARAMETER") request: ServerRequest) : ServerResponse =
            ok().body(repository.findAll(), Exercise::class.java).awaitLast()

    suspend fun listExercise(request: ServerRequest) : ServerResponse {
        val id = request.pathVariable("id").toLong()
        return repository.findById(id)
                .flatMap { ok().body(fromValue(it)) }
                .switchIfEmpty(status(HttpStatus.NOT_FOUND).build())
                .awaitSingle()
    }
}