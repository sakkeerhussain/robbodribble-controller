package main.controllers.bot

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.RequestBody
import main.controllers.Const
import main.sensor.HttpLoggingInterceptor
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.Body
import retrofit.http.GET
import retrofit.http.POST
import rx.Observable
import java.util.concurrent.TimeUnit


interface BotCommunicationService{
    @GET("/v2/controller/reset/")
    fun reset(): Observable<Response>

    @GET("/v2/controller/stop/")
    fun stop() : Observable<Response>

    @GET("/v2/controller/door/open/")
    fun doorOpen() : Observable<Response>

    @GET("/v2/controller/door/close/")
    fun doorClose() : Observable<Response>

    @POST("/v2/controller/path/")
    fun sendPath(@Body body: List<PathRequestItem>) : Observable<Response>

    object Factory {
        fun create(tag:String): BotCommunicationService {
            val logging = HttpLoggingInterceptor(tag)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient()
            client.interceptors().add(logging)
            client.setReadTimeout(5, TimeUnit.SECONDS)
            client.setWriteTimeout(5, TimeUnit.SECONDS)

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