package com.mironouz.remoteschoolapi.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(val name: String,
                val surname: String,
                val grade: Grade,
                @Id @JsonProperty(access=JsonProperty.Access.WRITE_ONLY) val email: String,
                @JsonProperty(access=JsonProperty.Access.WRITE_ONLY) val password: String)