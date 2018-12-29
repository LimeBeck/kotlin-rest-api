package com.restapiexaple.data

import org.jetbrains.exposed.sql.Table

object NewsTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val category_id = integer("category_id").references(CategoryTable.id)
    val date = datetime("date")
    val title = varchar("title", length=300)
    val shortDescription = varchar("short_description",length = 300)
    val fullDescription = text("full_description")
}

object CategoryTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 20)
}