package com.restapiexaple

import com.restapiexaple.data.CategoryRepository
import com.restapiexaple.data.SetupDB
import com.restapiexaple.models.Category
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.gson.*
import io.ktor.util.pipeline.PipelineContext
import java.lang.Exception

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@UseExperimental(KtorExperimentalLocationsAPI::class)
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    SetupDB()

    install(Locations) {
    }

    install(AutoHeadResponse)

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ConditionalHeaders)

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(DataConversion)

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        get("/check") {
            call.respond(hashMapOf("code" to "0"))
        }
        route("/categories"){
            val repo = CategoryRepository()
            get("/"){
                errorAware{
                    call.respond(repo.findAll())
                }

            }

            post("/"){
                errorAware{
                    val receive = call.receive<Category>()
                    println("Received Post Request: $receive")
                    repo.insert(receive)
                    call.respond(receive)
                }
            }

            get("/{id}"){
                errorAware {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter not found")
                    call.respond(repo.findById(id))
                }
            }

            patch("/{id}"){
                errorAware {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter not found")
                    call.respond(repo.findById(id))
                }
            }

            delete("/{id}"){
                errorAware {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter not found")
                    call.respond(mapOf("success" to repo.deleteById(id)))
                }
            }
        }


        get<MyLocation> {
            call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
        }
        // Register nested routes
        get<Type.Edit> {
            call.respondText("Inside $it")
        }
        get<Type.List> {
            call.respondText("Inside $it")
        }

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }

        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

@KtorExperimentalLocationsAPI
@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")

@KtorExperimentalLocationsAPI
@Location("/type/{name}") data class Type(val name: String) {
    @Location("/edit")
    data class Edit(val type: Type)

    @Location("/list/{page}")
    data class List(val type: Type, val page: Int)
}

private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respondText(
            """{"error":"${e.localizedMessage}"}""",
            ContentType.parse("applicattion/json")
//            HttpStatusCode.InternalServerError
        )
        null
    }
}

data class MySession(val count: Int = 0)

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

