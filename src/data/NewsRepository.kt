package com.restapiexaple.data

import com.restapiexaple.application.INewsRepository
import com.restapiexaple.models.Category
import com.restapiexaple.models.News

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class NewsRepository : INewsRepository<News> {
    override fun findAll() = transaction { NewsTable.selectAll().map { x -> x.toNews() } }

    override fun findById(id: Int): News = transaction { findNews(byId(id)) }
    override fun findById(id: String): News = transaction { findNews(byId(id.toInt())) }

    private fun findNews(where: Op<Boolean>) = transaction {
        NewsTable.select(where)
            .checkNull()
            .let(ResultRow::toNews)
    }

    private fun byId(id: Int): Op<Boolean> = transaction { NewsTable.id eq id }

    override fun insert(data: News) = transaction {
        data.id = NewsTable.insert {
            it[title] = data.title
            it[date] = data.date
            it[shortDescription] = data.shortDescription
            it[fullDescription] = data.fullDescription
        } get NewsTable.id
    }

    override fun deleteById(id: Int) = transaction {
        CategoryTable.deleteWhere { NewsTable.id eq id }
    }

    override fun deleteById(id: String) = transaction {
        CategoryTable.deleteWhere { NewsTable.id eq id.toInt() }
    }

    override fun update(id: Int, data: News) = transaction {
        findNews(byId(id))
    }

    override fun update(id: Int, data: Map<String, String>) = transaction {
        findNews(byId(id))
    }
//

    private fun Query.checkNull(): ResultRow = firstOrNull() ?: throw Exception("News not found")
}

class CategoryRepository : INewsRepository<Category> {
    //    ToDo: get this values from model class, check nullable from it
    private val fields: Map<String, Boolean> = mapOf(
        "name" to true
    )

    override fun findAll() = transaction { CategoryTable.selectAll().map { x -> x.toCategory() } }

    override fun findById(id: Int): Category = transaction { findCategory(byId(id)) }
    override fun findById(id: String): Category = transaction { findCategory(byId(id.toInt())) }

    private fun findCategory(where: Op<Boolean>) = transaction {
        CategoryTable.select(where)
            .checkNull()
            .let(ResultRow::toCategory)
    }

    private fun byId(id: Int): Op<Boolean> = transaction { CategoryTable.id eq id }

    override fun insert(data: Category) = transaction {
        data.id = CategoryTable.insert {
            it[name] = data.name
        } get CategoryTable.id
    }

    override fun deleteById(id: Int) = transaction {
        CategoryTable.select(byId(id)).checkNull()
        CategoryTable.deleteWhere { byId(id) }
    }

    override fun deleteById(id: String) = transaction {
        deleteById(id.toInt())
    }

    override fun update(id: Int, data: Category) = transaction {
        CategoryTable.select(byId(id)).checkNull()
        CategoryTable.update({ byId(id) }) {
            it[name] = data.name
        }
//        data.id = id
        findCategory(byId(id))
    }

    override fun update(id: Int, data: Map<String, String>) = transaction {
        CategoryTable.select(byId(id)).checkNull()
        for (field in fields) {
            if (!data.containsKey(field.key) && field.value) {
                throw IllegalArgumentException("Field <${field.key}> must be provided in request")
            }
            if (data.containsKey(field.key)) {
                CategoryTable.update({ byId(id) }) {
                    checkProperty(CategoryTable, field.key)
                    val column = readProperty(CategoryTable, field.key)
                    it[column] = data[field.key] ?:
                            throw IllegalArgumentException("${field.key} must be provided in request")
                }
            }
        }
        findCategory(byId(id))
    }

    private fun Query.checkNull(): ResultRow = firstOrNull() ?: throw IllegalArgumentException("Categories not found")
}

fun readProperty(instance: Any, propertyName: String): Column<Any> {

    val property = instance::class.members.first { it.name == propertyName }
    return property.call(instance) as Column<Any>
}

fun checkProperty(instance: Any, propertyName: String) {
    if (instance::class.members.none { it.name == propertyName }) {
        throw IllegalArgumentException("$propertyName not in class members")
    }
}