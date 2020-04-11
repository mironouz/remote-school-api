package com.mironouz.remoteschoolapi.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(val name: String, val surname: String, val grade: Grade) {}