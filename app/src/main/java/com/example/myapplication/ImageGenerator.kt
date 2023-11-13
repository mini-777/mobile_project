package com.example.myapplication

import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST

interface ImageGenerator {
    @POST("v1/images/generations")
    fun post(
        @Body dto: RequestImageGenerationDto
    ): Call<ResultImageGeneration>

    companion object {
        private const val BASE_URL = "https://api.openai.com/"
        private const val OPENAI_API_KEY = "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"

        fun create(): ImageGenerator {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization",  "Bearer $OPENAI_API_KEY")
                    .build()
                return@Interceptor it.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageGenerator::class.java)
        }
    }
}

data class RequestImageGenerationDto (
    @SerializedName("prompt") val prompt: String,
    @SerializedName("n") val n: Int = 4,
    @SerializedName("size") val size: String = "256x256"
)

data class ResultImageGeneration (
    @SerializedName("created")
    val created: Long,
    @SerializedName("data")
    val data: List<Url>
)

data class Url(
    @SerializedName("url")
    val url: String
)