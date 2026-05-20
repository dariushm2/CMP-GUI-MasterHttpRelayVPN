package com.darius.relay_vpn.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class ApiService(
    private val client: HttpClient,
) {

    suspend fun query(
        path: String,
    ): HttpResponse {
        val normalizedPath = path.removePrefix("/")
        val response = client.get(normalizedPath)

        if (!response.status.isSuccess()) {
            throw HttpException(
                statusCode = response.status,
                message = "HTTP ${response.status.value}: ${response.status.description}"
            )
        }
        return response
    }
}

class HttpException(
    val statusCode: HttpStatusCode,
    override val message: String,
) : Exception(message)
