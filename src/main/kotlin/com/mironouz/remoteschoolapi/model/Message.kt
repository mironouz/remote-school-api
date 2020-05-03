package com.mironouz.remoteschoolapi.model

import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document
data class Message(val user: User, val text: String, val timestamp: Date)