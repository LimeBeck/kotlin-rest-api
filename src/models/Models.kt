package com.restapiexaple.models

import com.google.gson.*
import org.joda.time.DateTime
import java.util.*
import java.lang.reflect.Type
import org.joda.time.format.DateTimeFormat




data class Category(var id: Int?, val name: String = "")

data class News(var id: Int?,
                var category_id: Int,
                val date: DateTime = DateTime(1994, 2, 6, 10, 0, 30),
                val title: String = "Title",
                val shortDescription: String = "shortDescription",
                val fullDescription: String = "<b>fullDescription</b>"
)

class NewsListSerializer : JsonSerializer<News> {
    override fun serialize(src: News, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonNew = JsonObject()

        jsonNew.addProperty("id", src.id)
        jsonNew.addProperty("title", src.title)
        if (context != null) {
            jsonNew.add("date", context.serialize(src.date))
        }
        jsonNew.addProperty("shortDescription", src.shortDescription)

        return jsonNew
    }
}

class DateTimeSerializer : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
    override fun serialize(src: DateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DateTime {
        val dateParser = DateTimeFormat.forPattern("yyyy.MM.dd HH:mm:ss")
        json ?: throw IllegalArgumentException("date must be not null")
        return dateParser.parseDateTime(json.asString)
    }

}