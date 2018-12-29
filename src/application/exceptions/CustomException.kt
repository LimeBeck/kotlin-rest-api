package com.restapiexaple.application.exceptions

import io.ktor.http.HttpStatusCode

class CustomException(status: HttpStatusCode, customMessage: String) : Exception() {
    val status = HttpStatusCode.Forbidden
    val customMessage = "Forbidden"
}