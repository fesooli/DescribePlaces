package br.com.describeplaces.describeplaces.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Fellipe Oliveira on 30/03/18.
 */
interface ImageAPI {
    @GET("/ws/{cep}/json")
    fun pesquisar(@Path("cep") cep: String): Call<String>
}