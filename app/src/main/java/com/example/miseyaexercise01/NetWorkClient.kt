package com.example.miseyaexercise01

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetWorkClient {

    //서비스 URL
    private const val DUST_BASE_URL = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc"


    private fun createOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()

        //실제로 통신이 잘 안 될 때 디버깅을 위한...
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            //타임아웃 인터벌 줌... ?
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(interceptor)
            .build()
    }

    //아래에서 변수가 create될 때 ... retrofit은 항상 이렇게 적어야 함...
    private val dustRetrofit = Retrofit.Builder()
        //url이 들어가고,
        .baseUrl(DUST_BASE_URL)
        // 그 안에 json 파일을 convert하기 위한 gson을 만들고? (-> data class로 바꿔준다)
        .addConverterFactory(GsonConverterFactory.create())
        //위의 createOkHttpClient를 사용할것임
        .client(createOkHttpClient())
        .build()

    //아까 만든, (getDust하는) NetWorkInterface 타입의 dustNetWork라는 변수를 하나 만들건데,
    //  거기에 인터페이스를 파라미터로 넣어 dustRetrofit을 하나 create할건데, (이어서 바로 위 부분 코드 보기!)
    val dustNetWork: NetWorkInterface = dustRetrofit.create(NetWorkInterface::class.java)

}