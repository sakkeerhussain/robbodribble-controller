package main.sensor

import main.sensor.response.BaseResponse
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.GET
import retrofit.http.Path
import retrofit.http.Query
import rx.Observable


interface ApiService {

    @GET("calibrate/ref_point/{point}/")
    fun calibrateRef(@Path("point") point: Int): Observable<BaseResponse>

    @GET("q={q}")
    fun search(@Query("q") q: String): Observable<String>

    /**
     * Factory class for convenient creation of the Api Service interface
     */
    object Factory {

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://www.google.co.in/")
                    //.baseUrl("http://10.7.170.6:8080/")
                    .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}