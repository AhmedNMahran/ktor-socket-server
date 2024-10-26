package com.github.ahmednmahran.plugins


import com.github.ahmednmahran.chatCredential
import com.github.ahmednmahran.domain.DatabaseRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class UserSession(val name: String, val count: Int) : Principal

fun Application.configureAuth() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60
        }
    }
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->

                chatCredential = credentials
                val user = DatabaseRepository.getUsers().find {
                    it.username == credentials.name && it.password == credentials.password
                }

                if (user != null) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
        session<UserSession>("auth-session") {
            validate { session ->
                if(session.name.isNotBlank()) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }

    routing {
        authenticate("auth-basic") {
            post("/login") {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                if(call.sessions.get<UserSession>()?.name != userName) {
                    call.sessions.set(UserSession(name = userName, count = 1))
                }
                call.respond(Json.encodeToString(DatabaseRepository.getUsers().find { it.username == userName }))
            }
        }
    }
}
