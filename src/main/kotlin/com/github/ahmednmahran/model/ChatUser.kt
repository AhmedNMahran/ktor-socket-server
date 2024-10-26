package com.github.ahmednmahran.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatUser(val username: String,val password: String, val profileImageUrl: String?=null,)
