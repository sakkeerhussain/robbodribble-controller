package main.sensor

import com.squareup.okhttp.OkHttpClient
import main.sensor.response.BallsResponse
import main.sensor.response.BaseResponse
import main.sensor.response.BotLocationResponse
import main.sensor.response.CalibrationResponse
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.GET
import retrofit.http.POST
import retrofit.http.Path
import retrofit.http.Query
import rx.Observable
import java.util.concurrent.TimeUnit


interface ApiService {

    @GET("calibrate/ref_point/{point}/")
    fun calibrateRef(@Path("point") point: Int): Observable<BaseResponse>

    @GET("calibrate/ref_point/{point}/value/")
    fun getReferencePoint(@Path("point") point: Int): Observable<CalibrationResponse>

    @POST("calibrate/ref_point/{point}/value/")
    fun setReferencePoint(@Path("point") point: Int, @Query("xImage") xImage: Float,
                          @Query("yImage") yImage: Float, @Query("xBoard") xBord: Float,
                          @Query("yBoard") yBord: Float) : Observable<BaseResponse>

    @GET("balls/")
    fun getBalls() : Observable<BallsResponse>

    @GET("bot/")
    fun getBotLocation() : Observable<BotLocationResponse>

    /**
     * Factory class for convenient creation of the Api Service interface
     */
    object Factory {

        fun create(tag: String, ip: String, port: String): ApiService {

            val logging = HttpLoggingInterceptor(tag)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient()
            client.interceptors().add(logging)
            client.setReadTimeout(650, TimeUnit.SECONDS)
            client.setWriteTimeout(650, TimeUnit.SECONDS)

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl("http://$ip:$port/")
                    .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}