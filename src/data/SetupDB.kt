package com.restapiexaple.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class SetupDB{
    init {
        Database.connect("jdbc:postgresql://localhost:5432/test", driver = "org.postgresql.Driver",
            user = "postgres", password = "tO8Qa3ng")

        transaction {
            SchemaUtils.create(NewsTable,CategoryTable)
//            SchemaUtils.create(CategoryTable)
        }
    }
}