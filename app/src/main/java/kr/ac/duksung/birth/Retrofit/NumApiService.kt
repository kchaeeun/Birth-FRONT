package kr.ac.duksung.birth.Retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NumApiService {
    @GET("/number/{serial}")
    fun getBySerial(@Path("serial") serial: String?): Call<Serial?>?
}