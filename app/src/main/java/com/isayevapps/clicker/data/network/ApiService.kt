package com.isayevapps.clicker.data.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url


data class Login(
    val c: Int = 0
)

data class Dot(
    val d: Int,
    val x: Int,
    val y: Int,
    val t: Int,
    val i: Int,
    val h: Int,
    val m: Int,
    val s: Int,
    val ms: Int,
    val n: Int,
    val c: Int = 2
)

data class DeleteDot(
    val d: Int,
    val c: Int = 3
)

data class DeleteAll(
    val c: Int = 4
)

data class Move(
    val x: Int,
    val y: Int,
    val c: Int = 1
)

data class Response(
    val status: String,
)

interface ApiService {
    @POST
    suspend fun login(
        @Url url: String,
        @Body body: Login
    ): Response

    @POST
    suspend fun dot(
        @Url url: String,
        @Body body: Dot
    ): Response

    @POST
    suspend fun deleteDot(
        @Url url: String,
        @Body body: DeleteDot
    ): Response

    @POST
    suspend fun deleteAll(
        @Url url: String,
        @Body body: DeleteAll
    ): Response

    @POST
    suspend fun move(
        @Url url: String,
        @Body body: Move
    ): Response
}