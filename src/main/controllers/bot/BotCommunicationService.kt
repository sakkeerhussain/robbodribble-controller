package main.controllers.bot

import com.squareup.okhttp.OkHttpClient
import main.controllers.Const
import main.sensor.HttpLoggingInterceptor
import main.sensor.response.BotLocationResponse
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.GET
import rx.Observable
import java.util.concurrent.TimeUnit


interface BotCommunicationService{
    @GET("bot/")
    fun getBotLocation() : Observable<BotLocationResponse>

    /**
     * Factory class for convenient creation of the Api Service interface
     */
    object Factory {

        fun create(): BotCommunicationService {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient()
            client.interceptors().add(logging)
            client.setReadTimeout(650, TimeUnit.SECONDS)
            client.setWriteTimeout(650, TimeUnit.SECONDS)

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl("http://${Const}.BOT_ADDRESS/")
                    .build()

            return retrofit.create(BotCommunicationService::class.java)
        }
    }
}