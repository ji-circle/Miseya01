package com.example.miseyaexercise01

import retrofit2.http.GET
import retrofit2.http.QueryMap

interface NetWorkInterface {
    //요청주소의 맨 뒷부분...
    @GET("getCtprvnRltmMesureDnsty") //시도별 실시간 측정정보 조회 주소
    //getDust라는 function을 만들었음...
    //  파라미터로는 hashmap 컬렉션 타입으로 string 2개... 이 안에 요청값들이 들어감. (serviceKey는 뭐고, returnType는 뭐고... etc)
    //    리턴값으로는 Dust (아까 DustDTO에서 만들었던... 타입 그대로 받음)
    suspend fun getDust(@QueryMap param: HashMap<String, String>): Dust
    //사용할 때 : param으로 요청주소가 들어갔고,
    // 이게 실행되면 NetWorkClient 페이지 안의 retrofit이 쭉 실행되고, responseData가 리턴됨
    //   이 때, 전체 다 string형태로 쭉 나열된 형태가 아니라, 우리가 정의해 둔 DustDTO 클래스의 상태로 들어옴
}