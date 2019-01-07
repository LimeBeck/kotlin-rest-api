package com.restapiexaple


import com.restapiexaple.data.CategoryRepository
import com.restapiexaple.data.NewsRepository
import com.restapiexaple.data.SetupDB
import com.restapiexaple.models.Category
import com.restapiexaple.models.News
import kotlin.test.*
import org.junit.FixMethodOrder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Functional test")
class FunctionalTest {
    val newsRepo = NewsRepository()
    val categoryRepo = CategoryRepository()

    var category_id: Int? = null
    var news_id: Int? = null

    @BeforeAll
    fun prepare() {
        SetupDB()
    }

    @Test
    fun complexTest() {
        `1 Create category with object`()
        `2 Create news with object`()
    }

    private fun `1 Create category with object`() {
        val category = Category(name = "Test category", id = null)
        categoryRepo.insert(category)
        assertTrue(category.id != null, "Category with id = ${category.id} is added to repository ")
        category_id = category.id!!
    }

    private fun `2 Create news with object`() {
        val news = News(id = null, category_id = category_id!!)
        newsRepo.insert(news)
        assertTrue(news.id != null, "News with id = ${news.id} is added to repository ")
        news_id = news.id!!
    }

    @AfterAll
    fun deleteData() {
        if (news_id != null) {
            newsRepo.deleteById(news_id!!)
        }
        if (category_id != null) {
            categoryRepo.deleteById(category_id!!)
        }
    }

}

