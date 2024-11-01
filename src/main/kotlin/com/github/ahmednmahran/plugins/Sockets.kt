package com.github.ahmednmahran.plugins

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.util.*
import kotlin.collections.LinkedHashSet
import com.github.ahmednmahran.Connection
import com.github.ahmednmahran.chatCredential
import com.github.ahmednmahran.domain.DatabaseRepository
import com.github.ahmednmahran.model.ChatMessage
import io.ktor.http.*
import io.ktor.network.sockets.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.parse("PT1000S")
        timeout = Duration.parse("PT1000S")
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
                connections.distinct().forEach {
                    it.session.send("You are connected! There are ${connections.count()} users here.")
                }
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedMessage = frame.readText()
                    val message : ChatMessage = Json.decodeFromString(receivedMessage)
                    println(receivedMessage)
                    message.to?.let { to ->
                        connections.filter { it.name == to }
                    } ?: connections.forEach {
                        println("sending $receivedMessage")
                        it.session.send(receivedMessage)
                    }
                }
            }
            catch (e: UninitializedPropertyAccessException){
                call.respond(HttpStatusCode.Unauthorized,"you are logged out!")
                println("logged out!")
            }
            catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection
            }

            // HTTP endpoint to get all connected users
            get("/connected-users") {
                val userList = DatabaseRepository.getUsers().filter {
                    connections.filterNotNull().map { connection -> connection.name }.contains(it.username)
                }   // Get all usernames
                call.respond(Json.encodeToString(userList))  // Respond with the list of connected users

            }
        }
    }
}
