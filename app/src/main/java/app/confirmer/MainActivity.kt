package app.confirmer

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import app.confirmer.internet.AsynchronousGet
import app.confirmer.internet.CallbackData
import app.confirmer.R
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class MainActivity : AppCompatActivity(), CallbackData {

    lateinit var inpLogin : TextInputEditText
    lateinit var inpPassword : TextInputEditText
    lateinit var async : AsynchronousGet
    lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        prefs  = PreferenceManager.getDefaultSharedPreferences  ( applicationContext )
        hideSystemUI()

        inpLogin = findViewById(R.id.input_login)
        inpPassword = findViewById(R.id.input_pass)
        findViewById<Button>(R.id.button_go_next).setOnClickListener {

          if(inpLogin.text.toString().isNotEmpty() && inpPassword.text.toString().isNotEmpty()){

             val json1 = JSONObject()
              json1.put("username", inpLogin.text.toString())
              json1.put("password", inpPassword.text.toString())
            async = AsynchronousGet("", 1, json1)
            async.dataReturn = this
            async.run()
           }
        }

        if (prefs.contains("api_k")) {
            val getExpired = prefs.getLong("expired", 0)
            println(" my contains api_K == ${prefs.getString("api_k", "")}")
            val nowTime = System.currentTimeMillis()
            val timeDiff = nowTime - getExpired
            if (timeDiff > 0) {

                println(" contains a api_k")
                val insdf = Intent(this, ScreenMainApp::class.java)
                startActivity(insdf)

            } else {
                toastLos("Your token expired. Please re-enter again") }
        }
    }


    private fun toastLos(textSee : String){
        Looper.prepare()
        Toast.makeText(this, textSee, Toast.LENGTH_SHORT).show()
    }

    override fun returnServerAnswer(jsonString: String) {

        if(!jsonString.isNullOrEmpty()){
            val d = JSONObject(jsonString)

            if(d.get("status").toString() == "error"){
                toastLos("Error: ${d.get("error").toString()}")
            }else {
             val ersd = prefs.edit()

            ersd.putString("api_k", d.get("access_token").toString()).apply()
            ersd.putLong("expired", d.get("expires_in").toString().toLong())

                 val insdf = Intent(this, ScreenMainApp::class.java)
                insdf.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                insdf.setData(Uri.parse("package:$packageName"))
                startActivity(insdf)  }
        }else {
             toastLos("Error return data from server")
        }
    }


     private fun hideSystemUI() {
         WindowCompat.setDecorFitsSystemWindows(window, false)
         WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    override fun onBackPressed() {

    }

}