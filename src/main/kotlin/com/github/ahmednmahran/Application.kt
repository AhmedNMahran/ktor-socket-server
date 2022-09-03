package com.github.ahmednmahran

import io.ktor.server.application.*
import com.github.ahmednmahran.plugins.*
import com.github.ahmednmahran.plugins.configureRouting
import com.github.ahmednmahran.plugins.configureSockets
import io.ktor.server.auth.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureAuth()
    configureSockets()
    configureRouting()
}
lateinit var chatCredential: UserPasswordCredential
