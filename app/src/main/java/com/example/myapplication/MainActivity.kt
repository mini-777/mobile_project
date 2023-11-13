package com.example.myapplication

import ResultTextTranslation
import TextTranslator
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = "안녕하세요"
        var translatedText: String = ""
        val imgGenerator = ImageGenerator.create()
        var images = listOf<Url>()
        var url: String = ""
        val translator = TextTranslator.create()
        translator.post(text = text).enqueue(object: Callback<ResultTextTranslation> {
            override fun onResponse(
                call: Call<ResultTextTranslation>,
                response: Response<ResultTextTranslation>
            ) {
                Log.d(TAG, "success translation, ${response.body()}")
                translatedText = response.body()!!.message.result.translatedText
                imgGenerator.post(dto = RequestImageGenerationDto(prompt = translatedText)).enqueue(object: Callback<ResultImageGeneration> {
                    override fun onResponse(
                        call: Call<ResultImageGeneration>,
                        response: Response<ResultImageGeneration>
                    ) {
                        Log.d(TAG, "success image generation, ${response.body()}")
                    }
                    override fun onFailure(call: Call<ResultImageGeneration>, t: Throwable) {
                        Log.d(TAG, "fail to generation")
                    }
                })
            }
            override fun onFailure(call: Call<ResultTextTranslation>, t: Throwable) {
                Log.d(TAG, "fail to translation")
            }
        })

        val image = findViewById<ImageView>(R.id.test)
        Glide.with(this).load(url).into(image)

    }
}