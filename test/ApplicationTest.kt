package com.restapiexaple

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.restapiexaple.data.CategoryRepository
import com.restapiexaple.data.NewsRepository
import com.restapiexaple.data.SetupDB
import com.restapiexaple.models.DateTimeSerializer
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import org.joda.time.DateTime
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("😱Rest api test")
class ApplicationTest {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
        .create()

    private var category_id: Int = -1
    private var news_id: Int = -1


    @BeforeAll
    fun prepare() {
        SetupDB()
    }

    @Test
    fun complexTest() {
        `1 Test Check URL`()
        `2 Test POST Category URL`()
        `3 Test POST News URL`()
    }

    private fun `1 Test Check URL`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/check").apply {
                assertEquals(HttpStatusCode.OK, response.status(), "HTTP Status code not is 200")
                println(response.content)
                val json = getJson(response.content!!)
                assertEquals(0.0, json["code"], "Code not is 0")
                assertEquals("ok", json["status"], "Status not is OK")
            }
        }
    }

    private fun `2 Test POST Category URL`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/news/categories") {
                setBody(makeJson(mapOf("name" to "test category")))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status(), "HTTP Status code not is 200")
                println(response.content)
                val categoryResponse = getJson(response.content!!)
                assertEquals(0.0, categoryResponse["code"], "Code not is 0")
                assertEquals(true, categoryResponse.containsKey("category"), "Category not is in response")
                val category = categoryResponse["category"]
                if (category is Map<*, *>) {
                    val id = category["id"]
                    if (id is Double) {
                        category_id = id.toInt()
                    }
                    assertEquals("test category", category["name"], "Name not is in category data")
                }
            }
        }
    }

    private fun `3 Test POST News URL`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/news") {
                setBody(
                    makeJson(
                        mapOf(
                            "title" to "test news",
                            "category_id" to category_id,
                            "shortDescription" to "Description",
                            "fullDescription" to "Full description",
                            "date" to "2018.12.31 12:12:12"
                        )
                    )
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status(), "HTTP Status code not is 200")
                println(response.content)
                val newsResponse = getJson(response.content!!)
                assertEquals(0.0, newsResponse["code"], "Code not is 0")
                assertTrue(newsResponse.containsKey("news"), "News is not in response")
                val news = newsResponse["news"]
                if (news is Map<*, *>) {
                    val id = news["id"]
                    if (id is Double) {
                        news_id = id.toInt()
                    }
                    assertEquals("test news", news["title"], "Title not is in news data")
                    assertEquals("Description", news["shortDescription"], "Description not is in news data")
                    assertEquals("Full description", news["fullDescription"], "Full description not is in news data")
                }
            }
        }
    }

    @AfterAll
    fun `Delete Test Data`() {
        NewsRepository().deleteById(news_id)
        CategoryRepository().deleteById(category_id)
    }

    private fun getJson(content: String): Map<String, Any> {
        return gson.fromJson<Map<String, Any>>(content, object : TypeToken<Map<String, Any>>() {}.type)
    }

    private fun makeJson(data: Map<String, Any>): String {
        return gson.toJson(data)
    }
}
