package com.github.ahmednmahran.domain

import com.github.ahmednmahran.model.ChatUser

/**
 * Repository to access and make database operations
 */
object DatabaseRepository {
    fun getUsers() = dummyUsernames.map {
        ChatUser(it,"123456", dummyUrls[dummyUsernames.indexOf(it)])
    }
    private val dummyUsernames = listOf("Ahmed","Mohamed", "Hoda","Abdullah","Aly", )
    private val dummyUrls = listOf("https://www.kasandbox.org/programming-images/avatars/cs-hopper-cool.png",
        "https://www.kasandbox.org/programming-images/avatars/marcimus-purple.png",
        "https://www.kasandbox.org/programming-images/avatars/marcimus-purple.png",
        "https://www.kasandbox.org/programming-images/avatars/spunky-sam-green.png",
        "https://www.kasandbox.org/programming-images/avatars/cs-hopper-happy.png",
        )
}