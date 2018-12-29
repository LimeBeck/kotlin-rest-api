package com.restapiexaple.models

import org.joda.time.DateTime
import java.util.*

data class Category(var id: Int?, val name: String = "")

data class News(var id: Int?,
                var category_id: Int,
                val date: DateTime = DateTime(1994, 2, 6, 10, 0, 30),
                val title: String = "Title",
                val shortDescription: String = "shortDescription",
                val fullDescription: String = "<b>fullDescription</b>"
)