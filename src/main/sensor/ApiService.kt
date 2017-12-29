package main.sensor

import com.squareup.okhttp.OkHttpClient
import main.sensor.response.BaseResponse
import main.sensor.response.CalibrationResponse
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.GET
import retrofit.http.POST
import retrofit.http.Path
import retrofit.http.Query
import rx.Observable


interface ApiService {

    @GET("calibrate/ref_point/{point}/")
    fun calibrateRef(@Path("point") point: Int): Observable<BaseResponse>

    @GET("calibrate/ref_point/{point}/value/")
    fun getReferencePoint(@Path("point") point: Int): Observable<CalibrationResponse>

    @POST("calibrate/ref_point/{point}/value/")
    fun setReferencePoint(@Path("point") point: Int, @Query("x") x: Float, @Query("y") y: Float): Observable<BaseResponse>

    /**
     * Factory class for convenient creation of the Api Service interface
     */
    object Factory {

        fun create(ip: String): ApiService {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient()
            client.interceptors().add(logging)

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl("http://$ip:8080/")
                    .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}