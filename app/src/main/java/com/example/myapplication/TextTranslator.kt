import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TextTranslator {
    @FormUrlEncoded
    @POST("v1/papago/n2mt")
    fun post(
        @Field("source") source: String = "ko",
        @Field("target") target: String = "en",
        @Field("text") text: String
    ): Call<ResultTextTranslation>

    companion object {
        private const val BASE_URL = "https://openapi.naver.com/"
        private const val CLIENT_ID = "do3RySUy582vACPlp5KO"
        private const val CLIENT_SECRET = "JQjTPmoYew"

        fun create(): TextTranslator {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .addHeader("X-Naver-Client-Id", CLIENT_ID)
                    .addHeader("X-Naver-Client-Secret", CLIENT_SECRET)
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
                .create(TextTranslator::class.java)
        }
    }
}

data class ResultTextTranslation (
    @SerializedName("message")
    val message: Message
)

data class Message(
    @SerializedName("result")
    val result: Result
)

data class Result (
    @SerializedName("srcLangType")
    val srcLangType: String,
    @SerializedName("tarLangType")
    val tarLangType: String,
    @SerializedName("translatedText")
    val translatedText: String
)