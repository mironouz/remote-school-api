package com.mironouz.remoteschoolapi.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Exercise(@JsonProperty(access=JsonProperty.Access.READ_ONLY) @Id var id: Long,
                    val title: String,
                    val description: String) {
    companion object {
        private var current: Long = 1
        fun getNextId(): Long = current++
    }
}