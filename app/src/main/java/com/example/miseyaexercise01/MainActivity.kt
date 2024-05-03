package com.example.miseyaexercise01

//import com.android.miseya.data.DustItem
//import com.android.miseya.databinding.ActivityMainBinding
//import com.android.miseya.retrofit.NetWorkClient
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.miseyaexercise01.databinding.ActivityMainBinding
import com.skydoves.powerspinner.IconSpinnerAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //DustDTO 에서 만든 DustItem 타입의 리스트들을 갖고있다
    //
    var items = mutableListOf<DustItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //왼쪽 스피너
        //  왼쪽에는 미리 데이터를 리스트로 넣어뒀으니까, 그걸 선택했는지를 파악.
        //    깃허브 readMe 보면, 3번째가 newIndex, 4번째가 newText.
        binding.spinnerViewSido.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            // 그 text를 파라미터로, setUpDustParameter를 만든다.
            //  setUpDustParameter는 요청 파라미터(serviceKey, returnType 등)을 생성할것임
            //    setUpDustParameter의 리턴값을 가지고 communicateNetWork라는 function을 호출
            communicateNetWork(setUpDustParameter(text))

        }

        //오른쪽 스피너. 지역선택
        binding.spinnerViewGoo.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->

            //text로 "용산구" 이런게 들어올것임
            Log.d("miseya", "selectedItem: spinnerViewGoo selected >  $text")

            //item 안에는 서울시 내의 모든 stationName의 아이템들이 전부 들어있으니 filter.
            //  stationName이 용산구인 것의 아이템을 가져옴... 그 안의 sidoName과 pm10Value 등을 쓸거임
            //    selectedItem은 리스트로 들어옴 (같은 이름이 또 존재할수도 있으니)
            var selectedItem = items.filter { f -> f.stationName == text }
            Log.d("miseya", "selectedItem: sidoName > " + selectedItem[0].sidoName)
            Log.d("miseya", "selectedItem: pm10Value > " + selectedItem[0].pm10Value)

            //실시간 데이터라서 첫번쨰, [0]번째인 것을 사용할것임
            //  ex. 서울시 용산구
            binding.tvCityname.text = selectedItem[0].sidoName + "  " + selectedItem[0].stationName
            binding.tvDate.text = selectedItem[0].dataTime
            // 단위 붙여주기
            binding.tvP10value.text = selectedItem[0].pm10Value + " ㎍/㎥"

            when (getGrade(selectedItem[0].pm10Value)) {
                1 -> {
                    //배경 색도 바꾼다
                    binding.mainBg.setBackgroundColor(Color.parseColor("#9ED2EC"))
                    binding.ivFace.setImageResource(R.drawable.mise1)
                    binding.tvP10grade.text = "좋음"
                }

                2 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#D6A478"))
                    binding.ivFace.setImageResource(R.drawable.mise2)
                    binding.tvP10grade.text = "보통"
                }

                3 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#DF7766"))
                    binding.ivFace.setImageResource(R.drawable.mise3)
                    binding.tvP10grade.text = "나쁨"
                }

                4 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#BB3320"))
                    binding.ivFace.setImageResource(R.drawable.mise4)
                    binding.tvP10grade.text = "매우나쁨"
                }
            }
        }
    }

    //해시맵으로 요청 파라미터가 들어온다
    //  이건 http 통신을 해야해서, 메인 스레드에서는 돌지 못함(렉 걸릴 경우 등 대비) 별도의 스레드임
    private fun communicateNetWork(param: HashMap<String, String>) = lifecycleScope.launch() {

        //param으로 들어온 것을 가지고 인터페이스의 getDust를 호출함
        //getDust 사용할 때 : param으로 요청주소가 들어갔고,
        // 이게 실행되면 NetWorkClient 페이지 안의 retrofit이 쭉 실행되고, responseData가 리턴됨
        //   이 때, 전체 다 string형태로 쭉 나열된 형태가 아니라, 우리가 정의해 둔 DustDTO 클래스의 상태로 들어옴
        val responseData = NetWorkClient.dustNetWork.getDust(param)
        Log.d("Parsing Dust ::", responseData.toString())

        val adapter = IconSpinnerAdapter(binding.spinnerViewGoo)

        //1개가 아닐테니까, 아까 미리 리스트로 선언해둔 item에 넣음
        //  그럼 items 안에는 서울에 해당하는 여러 구들의 아이템들이 다 들어갈것임
        items = responseData.response.dustBody.dustItem!!

        //항목들 중 측정소명(stationName)
        //  옆 스피너에 해당 지역의 구 리스트를 다 보여주게. 지역명들을 꺼낸다
        val goo = ArrayList<String>()
        items.forEach {
            Log.d("add Item :", it.stationName)
            //아이템에서 stationName을 꺼내서 add해줌
            goo.add(it.stationName)
        }

        //두번째 스피너에 setItem으로 넣어준다
        // 왜 runOnUiThread인가 -> 현재 스레드가 별도 스레드이기 때문
        runOnUiThread {
            binding.spinnerViewGoo.setItems(goo)
        }

    }

    private fun setUpDustParameter(sido: String): HashMap<String, String> {

        //환경공단에서 받은 키
        val authKey = "qHdIjJSe2Oe33WNXA0nY5QACorIeEbg3RNs96r1A0+QJtv6zMP7Yd/FxDrSU2WuPZ1Fv2/bG0tAnfLbvFesDYA=="

        // 그 페이지에 있는 요청변수 항목들과 정확히 일치해야 함
        //   sidoName은 파라미터로 받을것임
        return hashMapOf(
            "serviceKey" to authKey,
            "returnType" to "json",
            "numOfRows" to "100",
            "pageNo" to "1",
            "sidoName" to sido,
            "ver" to "1.0"
        )
    }

    fun getGrade(value: String): Int {
        val mValue = value.toInt()
        var grade = 1
        grade = if (mValue >= 0 && mValue <= 30) {
            1
        } else if (mValue >= 31 && mValue <= 80) {
            2
        } else if (mValue >= 81 && mValue <= 100) {
            3
        } else 4
        return grade
    }
}