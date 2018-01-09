package main.controllers.bot

import com.squareup.okhttp.OkHttpClient
import main.controllers.Const
import main.sensor.HttpLoggingInterceptor
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.GET
import rx.Observable
import java.util.concurrent.TimeUnit


class BotControlManager {
    companion object {
        private var instance: BotControlManager? = null

        fun get(): BotControlManager {
            if (instance == null)
                instance = BotControlManager()
            return instance!!
        }
    }
}