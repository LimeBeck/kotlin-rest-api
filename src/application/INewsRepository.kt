package com.restapiexaple.application

interface INewsRepository<T> {

    fun findAll(): List<T>
    fun findById(id: Int): T
    fun findById(id: String): T
    fun deleteById(id: Int): Int
    fun deleteById(id: String): Int
    fun insert(data: T)
    fun update(id: Int, data: T)
}