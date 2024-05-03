package com.example.miseyaexercise01

import com.google.gson.annotations.SerializedName

data class Dust(val response: DustResponse)
//Dust라는 이름의 data class를 만들거고, 거기에 DustResponse라는 클래스 이름으로 body와 header가 들어감(아래코드)

data class DustResponse(
    //json...
    @SerializedName("body")
    val dustBody: DustBody,
    @SerializedName("header")
    val dustHeader: DustHeader
)

data class DustBody(
    val totalCount: Int,
    @SerializedName("items")
    //json으로 들어올 이름을 따온 것...
    //  리스트
    val dustItem: MutableList<DustItem>?,
    val pageNo: Int,
    val numOfRows: Int
)

data class DustHeader(
    val resultCode: String,
    val resultMsg: String
)

data class DustItem(
    val so2Grade: String,
    val coFlag: String?,
    val khaiValue: String,
    val so2Value: String,
    val coValue: String,
    val pm25Flag: String?,
    val pm10Flag: String?,
    val o3Grade: String,
    val pm10Value: String,
    val khaiGrade: String,
    val pm25Value: String,
    val sidoName: String,
    val no2Flag: String?,
    val no2Grade: String,
    val o3Flag: String?,
    val pm25Grade: String,
    val so2Flag: String?,
    val dataTime: String,
    val coGrade: String,
    val no2Value: String,
    val stationName: String,
    val pm10Grade: String,
    val o3Value: String
)