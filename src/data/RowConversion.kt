package com.restapiexaple.data

import com.restapiexaple.models.News
import com.restapiexaple.models.Category
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toCategory() = Category(
    id = this[CategoryTable.id],
    name = this[CategoryTable.name]
)

fun ResultRow.toNews() = News(
    id = this[NewsTable.id],
    category_id = this[NewsTable.category_id],
    date = this[NewsTable.date],
    title = this[NewsTable.title],
    shortDescription = this[NewsTable.shortDescription],
    fullDescription = this[NewsTable.fullDescription]
)
