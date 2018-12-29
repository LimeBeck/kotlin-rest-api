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

    override fun update(id: Int, data: News) {
    }

    private fun Query.checkNull(): ResultRow = firstOrNull() ?: throw Exception("News not found")
}

class CategoryRepository : INewsRepository<Category> {
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

    override fun update(id: Int, data: Category) {
//        CategoryTable.select(byId(id)).checkNull()
//        CategoryTable.update ({ byId(id) }){
//            with(SqlExpressionBuilder){
//                it.update(CategoryTable.name, data.name)
//            }
//        }
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun Query.checkNull(): ResultRow = firstOrNull() ?: throw IllegalArgumentException("Categories not found")
}


