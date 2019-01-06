package com.restapiexaple

import com.google.gson.reflect.TypeToken
import com.restapiexaple.data.CategoryRepository
import com.restapiexaple.data.NewsRepository
import com.restapiexaple.data.SetupDB
import com.restapiexaple.models.Category
import com.restapiexaple.models.DateTimeSerializer
import com.restapiexaple.models.News
import com.restapiexaple.models.NewsListSerializer
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.gson.*
import io.ktor.util.pipeline.PipelineContext
import java.lang.Exception
import org.joda.time.DateTime
import java.util.ArrayList
import com.google.gson.GsonBuilder
import com.google.gson.Gson


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
            //            setPrettyPrinting()
            setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
            registerTypeAdapter(object : TypeToken<News>() {}.type, NewsListSerializer())
        }
    }

    routing {
        get("/check") {
            call.respond(
                makeResponse(
                    data = "ok",
                    code = 0,
                    elemName = "status"
                )
            )
        }
        route("/news") {
            val repo = NewsRepository()
            get("") {
                errorAware {
                    call.respond(
                        makeResponse(
                            data = repo.findAll(),
                            elemName = "list"
                        )
                    )
                }
            }
            get("/details") {
                errorAware {
                    val id = call.request.queryParameters["id"] ?: throw IllegalArgumentException("id must be not null")
                    errorAware {
                        val gson = GsonBuilder()
                            .setPrettyPrinting()
                            .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
                            .create()
                        call.respondText(
                            gson.toJson(
                                makeResponse(
                                    data = repo.findById(id),
                                    elemName = "news"
                                )
                            ),
                            ContentType.parse("applicattion/json")
                        )
                    }
                }
            }
            put("/details") {
                errorAware {
                    val id = call.request.queryParameters["id"] ?: throw IllegalArgumentException("id must be not null")
                    val receive = call.receive<News>()
                    call.respond(
                        makeResponse(
                            data = repo.update(id.toInt(), receive),
                            elemName = "news"
                        )
                    )
                }
            }
            patch("/details") {
                errorAware {
                    val id = call.request.queryParameters["id"] ?: throw IllegalArgumentException("id must be not null")
                    val receive = call.receive<Map<String, String>>()
                    call.respond(
                        makeResponse(
                            data = repo.update(id.toInt(), receive),
                            elemName = "news"
                        )
                    )
                }
            }
            delete("/details") {
                errorAware {
                    val id = call.request.queryParameters["id"] ?: throw IllegalArgumentException("id must be not null")
                    call.respond(
                        makeResponse(
                            data = repo.deleteById(id),
                            elemName = "deleted",
                            code = 1
                        )
                    )
                }
            }

            post("") {
                errorAware {
                    val receive = call.receive<News>()
                    println("Received Post Request: $receive")
                    repo.insert(receive)
                    val gson = GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
                        .create()
                    call.respondText(
                        gson.toJson(
                            makeResponse(
                                data = receive,
                                elemName = "news"
                            )
                        ),
                        ContentType.parse("applicattion/json")
                    )
                }
            }

            route("/categories") {
                val repo = CategoryRepository()
                get("/") {
                    errorAware {
                        call.respond(
                            makeResponse(
                                data = repo.findAll(),
                                elemName = "list"
                            )
                        )
                    }
                }

                post("/") {
                    errorAware {
                        val receive = call.receive<Category>()
                        println("Received Post Request: $receive")
                        repo.insert(receive)
                        call.respond(
                            makeResponse(
                                data = receive,
                                elemName = "category"
                            )
                        )
                    }
                }

                get("/{id}") {
                    errorAware {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter not found")
                        call.respond(
                            makeResponse(
                                data = repo.findById(id),
                                elemName = "category"
                            )
                        )
                    }
                }

                get("/{id}/news") {
                    errorAware {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter not found")
                        val page = call.request.queryParameters["page"] ?: "1"
                        call.respond(
                            makeResponse(
                                data = NewsRepository().findByCategoryId(id, page),
                                elemName = "list"
                            )
                        )
                    }
                }

                put("/{id}") {
                    errorAware {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter not found")
                        val receive = call.receive<Category>()
                        call.respond(
                            makeResponse(
                                data = repo.update(id.toInt(), receive),
                                elemName = "category"
                            )
                        )
                    }
                }

                patch("/{id}") {
                    errorAware {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter not found")
                        val receive = call.receive<Map<String, String>>()
                        call.respond(
                            makeResponse(
                                data = repo.update(id.toInt(), receive),
                                elemName = "category"
                            )
                        )
                    }
                }

                delete("/{id}") {
                    errorAware {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter not found")
                        call.respond(
                            makeResponse(
                                data = repo.deleteById(id),
                                elemName = "deleted",
                                code = 1
                            )
                        )
                    }
                }
            }
        }

        install(StatusPages) {
            status(HttpStatusCode.NotFound) {
                call.respond(
                    makeResponse(
                        data = "not found",
                        code = 4,
                        elemName = "error"
                    )
                )
            }

        }
    }
}

private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respond(
            makeResponse(
                data = e.localizedMessage,
                code = 5,
                elemName = "error"
            )
        )
        null
    }
}

fun makeResponse(data: Any, code: Int = 0, elemName: String = "data"): Map<String, Any> {
    return mapOf("code" to code, elemName to data)
}
