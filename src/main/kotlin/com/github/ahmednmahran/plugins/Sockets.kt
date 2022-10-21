package com.github.ahmednmahran.plugins

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.util.*
import kotlin.collections.LinkedHashSet
import com.github.ahmednmahran.Connection
import com.github.ahmednmahran.chatCredential
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(1000)
        timeout = Duration.ofSeconds(1000)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

        webSocket("/chat") {

            println("Adding user!")
            val thisConnection = Connection(this)
            try {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                println("userNameBB:$userName")
                val userSession = call.sessions.get<UserSession>()

                thisConnection.name = chatCredential.name
                connections += thisConnection
                connections.forEach {
                    it.session.send("You are connected! There are ${connections.count()} users here.")
                }
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedMessage = frame.readText()
                    println(receivedMessage)
                    connections.forEach {
                        it.session.send(receivedMessage)
                    }
                }
            }
            catch (e: UninitializedPropertyAccessException){
                call.respond(HttpStatusCode.Unauthorized,"you are logged out!")
            }
            catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection
            }
        }
    }
}
