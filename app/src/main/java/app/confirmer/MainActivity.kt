package app.confirmer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import app.confirmer.internet.AsynchronousGet
import app.confirmer.internet.CallbackData
import com.confirmer.R
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

              println(" in this 2232323 ${inpLogin.text.toString()} and ${inpPassword.text.toString()}")
             val json1 = JSONObject()
              json1.put("username", inpLogin.text.toString())
              json1.put("password", inpPassword.text.toString())


            async = AsynchronousGet("", 1, json1)
            async.dataReturn = this
            async.run()
           }
        }

    }

    override fun returnServerAnswer(jsonString: String) {
        println(" my string in main  = $jsonString")

        if(!jsonString.isNullOrEmpty()){
            val d = JSONObject(jsonString)

            // d.get("access_token").toString()


            val ersd = prefs.edit()

            ersd.putString("api_k", d.get("access_token").toString()).apply()

           startActivity(Intent(this, ScreenMainApp::class.java))

        }

    }

    // val df = Intent(this, ScreenMainApp::class.java)
    //        startActivity(df)

     private fun hideSystemUI() {
         WindowCompat.setDecorFitsSystemWindows(window, false)
         WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}