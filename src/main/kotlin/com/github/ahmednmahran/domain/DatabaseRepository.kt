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
    private val dummyUrls = listOf("https://pbs.twimg.com/profile_images/1560971331595821056/_RXVapxu_200x200.jpg",
        "https://pbs.twimg.com/profile_images/1557475327688982528/KldARPI3_200x200.jpg",
        "https://pbs.twimg.com/profile_images/1399329694340747271/T5fbWxtN_200x200.png",
        "https://pbs.twimg.com/profile_images/1499043529989017602/9FFya-qL_200x200.png",
        "https://pbs.twimg.com/profile_images/1295710773101178880/pZhbLITZ_200x200.png",
        )
}