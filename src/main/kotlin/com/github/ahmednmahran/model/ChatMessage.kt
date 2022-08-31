package com.github.ahmednmahran.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(val body: String="", val sender : String="",)
