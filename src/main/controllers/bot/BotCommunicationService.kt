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


interface BotCommunicationService{
    @GET("/v2/controller/reset/")
    fun reset(): Observable<Response>

    @GET("/v2/controller/stop/")
    fun stop() : Observable<Response>

    object Factory {
        fun create(): BotCommunicationService {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient()
            client.interceptors().add(logging)
            client.setReadTimeout(125, TimeUnit.SECONDS)
            client.setWriteTimeout(125, TimeUnit.SECONDS)

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl("http://${Const.BOT_ADDRESS}/")
                    .build()

            return retrofit.create(BotCommunicationService::class.java)
        }
    }
}