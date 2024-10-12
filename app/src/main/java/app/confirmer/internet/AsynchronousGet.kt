package app.confirmer.internet

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class AsynchronousGet internal constructor(private val apiKey: String, private val numberReq: Int, private val jsonSend: JSONObject?,
                                            val cont: Context? = null) {

   // val empty: RequestBody = EMPTY_REQUEST_BODY
    private var client = OkHttpClient()
    public var dataReturn : CallbackData? = null
        // 1 - get-link   (get)
        // 2 - ping check (post)
        // 3 - send data  (post)
        // 4 - check version (post)

    @Throws(Exception::class)
    fun run() {

       val  requestBody  = RequestBody.create( JSON_MEDIA, jsonSend.toString())
       val autho = "Bearer $apiKey"

       val request2 = when(numberReq){
             1 -> {    // getLink || autho
                   println(" send number 1 ")

                     Builder()
                    .url("https://ecosystem-bot.ru/auth.php") // ppsand.esokolov.com
                 //   .addHeader("Authorization", autho)
                      .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                   // .put(requestBody)
                         // .get()
                    .build()
             }

            2 -> {     // ping


                 val formatter = SimpleDateFormat("yyyy.MM.dd hh:mm:ss")
                 val calendar = Calendar.getInstance()


               // formatter.calendar.timeInMillis

                 val json1  = JSONObject()
                val tymZne = TimeZone.getDefault().rawOffset / 3600000

                json1.put("time",formatter.format(calendar.time))
                json1.put("timeZoneOffsetInHours", tymZne)

                val  request3  = RequestBody.create( JSON_MEDIA, json1.toString())


                     Builder()
                    .url("https://ecosystem-bot.ru/ping.php")
                    .addHeader("Authorization", autho)
                     .addHeader("Content-Type", "application/json")
                    .post(request3)  // was EMPTY_REQUEST
                    .build()
            }

            3 -> {     // send Push or Sms
                     Builder()
                    .url("https://ecosystem-bot.ru/sms-push.php")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", autho)

                    .post(requestBody)
                    .build()
            }

           4 -> {
                println(" 4 start async ")
                Builder()
                    .url("https://ecosystem-bot.ru/version.php")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", autho)
                    .post(requestBody)
                    .build()
           }


           else -> {
                  Builder()
                    .url("https://ecosystem-bot.ru/sms-push.php")
                    .addHeader("Authorization", autho)
                    .get()
                    .build()
           }
       }


        client.newCall(request2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("sdf", " eror this ${e.toString()}")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

               // println(" BAD response success los ")


                    if (response.isSuccessful) {
                         println(" response success los ")

                        when (numberReq) {
                            1 -> {

                                println(" 01 in get 01 ")
                                val stringGet: String = response.body!!.string()
                                // println(" sdfsd string = $stringGet")
                                println(" 02 in get 02 ${stringGet.length} ")

                                if(stringGet.isNotEmpty()){
                                println(" string empty ")
                                }else {
                                    println(" string not empty ${stringGet.toString()}")
                               // assert(stringGet.isNotEmpty())
                               val jsonParse = JSONObject(stringGet)
                                }




                                println("03 in get 03")
                                //val pingUrl = jsonParse.get("ping_url")
                                //val messUrl = jsonParse.get("message_url")
                                //println(" my data isUrl = $pingUrl")
                                //println(" my data isMes = $messUrl")
                                dataReturn!!.returnServerAnswer(
                                    stringGet.toString()
                                    //pingUrl.toString(),
                                    //messUrl.toString(),
                                    //7
                                )

                            }

                            2 -> {
                                  println(" my respon code in TWO  == ${response.code}")


                               val numberReq =  if(response.code == 200 || response.code == 201) {
                                  println(" my response is ok (2222)")
                                   // myMutableStateGreenOrRedCircle.postValue(1)
                               }else {
                                    // myMutableStateGreenOrRedCircle.postValue(0)
                               }
                            }

                            3 -> {

                                println(" my response in 3 == ")
                            }

                            4 -> {

                                val stringGet: String = response.body!!.string()

                                if(response.code == 200 || response.code == 201) {
                                    dataReturn!!.returnServerAnswer(
                                    stringGet.toString())   }
                            }


                        }
                    }else {
                         println(" no response is success ${response.code} and ${response.message}")
                    }


//                when(numberReq){
//                    1 -> {}
//                    2 -> {
//
//                        //val viewModelShared22 = ViewModelProviders.of()[SharedViewModel1::class.java]
//                        //val df = ViewModelProviders.of()
//                        //viewModelShared22
//                    }
//                }

//                        if (numberReq == 1) {
//                            val stringGet: String = response.body!!.string()
//                            // println(" sdfsd string = $stringGet")
//                            val jsonParse = JSONObject(stringGet)
//
//                            val pingUrl = jsonParse.get("ping_url")
//                            val messUrl = jsonParse.get("message_url")
//                            //println(" my data isUrl = $pingUrl")
//                            //println(" my data isMes = $messUrl")
//                            dataReturn!!.returnServerAnswer(
//                                pingUrl.toString(),
//                                messUrl.toString(),
//                                7
//                            )
//                        } else if (numberReq == 3) {
//
//
//                        }else if(numberReq == 2){
//                         val intent =  Intent("my.event.calling.circle")
//                         intent.putExtra("message", 0)
//                        // LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//
//                    }else {
//                                 dataReturn!!.returnServerAnswer("", "", 1)
//                        }
//                }

            }
        })
    }

    companion object {
        val JSON_MEDIA : MediaType =    "application/json; charset=utf-8".toMediaType()
        val myMutableStateGreenOrRedCircle = MutableLiveData<Int>()
    }
}