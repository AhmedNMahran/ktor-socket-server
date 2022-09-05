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
import com.github.ahmednmahran.model.ChatMessage
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
                send("You are connected! There are ${connections.count()} users here.")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val textWithUsername = Json.encodeToString(ChatMessage(receivedText,thisConnection.name))
                    println(textWithUsername)
                    connections.forEach {
                        it.session.send(textWithUsername)
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
