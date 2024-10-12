package app.confirmer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.PowerManager
import android.preference.PreferenceManager
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import app.confirmer.permissionsandserver.PermissionOn
import app.confirmer.permissionsandserver.ServesNotify
import android.provider.Settings;
import android.provider.Telephony.Sms
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import app.confirmer.permissionsandserver.PingService
import app.confirmer.permissionsandserver.SmsProcessService
import app.confirmer.R
import app.confirmer.internet.AsynchronousGet
import app.confirmer.internet.CallbackData
import org.json.JSONObject


class ScreenMainApp : AppCompatActivity(), CallbackData {



    lateinit var prefc : SharedPreferences
    val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    val ACTION_NOTIFICATION_LISTENER_SETTINGS =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

    lateinit var mContext: Context
    private var enableNotificationListenerAlertDialog: AlertDialog? = null
    private val df = PermissionOn(this, this)

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = getPackageName()
        //val ENABLED_NOTIFICATION_LISTENERS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
        val flat = Settings.Secure.getString(contentResolver, ENABLED_NOTIFICATION_LISTENERS)
        if (!TextUtils.isEmpty(flat)) {
            //final String[] names = flat.split(":");
            val names = flat.split(":")

            for (i in names) {
                val cn = ComponentName.unflattenFromString(i)
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName))
                        return true
                }
            }

//            for (int i = 0; i < names.length; i++) {
//                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
//                if (cn != null) {
//                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
//                        return true;
//                    }
//                }
//            }
        }
        return false
    }


    override fun onRestart() {
        super.onRestart()
       println(" my restart ! 1 ")
       // notiCheck()
    }

    override fun onResume() {
        super.onResume()
        println(" my restart ! 2")

          notiCheck()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_app_screen)

        hideSystemUI()


        prefc = PreferenceManager.getDefaultSharedPreferences(applicationContext)
       val RadioCurrent =  prefc.getInt("current_radio", 1)


        findViewById<Button>(R.id.savve).setOnClickListener {

           Toast.makeText(this, R.string.all_work, Toast.LENGTH_LONG).show()
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)


        when(RadioCurrent)
        {
            1 ->  radioGroup.check(R.id.radio_01)
            2 ->  radioGroup.check(R.id.radio_02)
            3 ->   radioGroup.check(R.id.radio_03)
        }


        radioGroup.setOnCheckedChangeListener { radioGroup, i ->

            val radio: RadioButton = findViewById(i)
            Toast.makeText(applicationContext," On checked change : ${radio.text}",
                Toast.LENGTH_SHORT).show()



        val editor = prefc.edit()



            when(radio.text){
                "SMS" -> {
                    println(" los call SMS ")

                    val myService = Intent(this, ServesNotify::class.java)
                    stopService(myService)
                    editor.putInt("current_radio", 1)
                }
                "PUSH" -> {
                    println(" los call PUSH ")
                    val myService = Intent(this, SmsProcessService::class.java)
                    stopService(myService)
                    editor.putInt("current_radio", 2)
                }

                "SMS + PUSH" -> {
                    println(" sms + los push ")

                    val myService1 = Intent(this, ServesNotify::class.java)
                    stopService(myService1)


                    val myService = Intent(this, SmsProcessService::class.java)
                    stopService(myService)

                     Thread.sleep(1000)

        val requl = Intent(this, ServesNotify::class.java)
        requl.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startService(requl)

        val smsServiceIntent = Intent(this, SmsProcessService::class.java)
        smsServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startService(smsServiceIntent)

                    editor.putInt("current_radio", 3)

                }
            }


             editor.clear()
             editor.apply()

            println(" my number is == ${radioGroup.checkedRadioButtonId}")
            println(" my number is2 == ${radioGroup.id}")
        }



        findViewById<Button>(R.id.disable_opti).setOnClickListener {
         println(" disable optimisation ! ")
            df.requesrPermissionDisableBatteryOptimisation()

            //val df = PowerManager as PowerManager
            val PowerManagerL = getSystemService(Context.POWER_SERVICE) as PowerManager

            val isIgnoringBatteryOptimizations = PowerManagerL.isIgnoringBatteryOptimizations("app.confirmer")

            println(" isIgnoringBatteryOptimizations = $isIgnoringBatteryOptimizations")

            val intent = Intent()

            if(isIgnoringBatteryOptimizations){
                println(" ok ignore optimisations ")
                   //     intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)

                Toast.makeText(this, "Optimisation is disabled", Toast.LENGTH_SHORT).show()

            }else {
                println(" not ignore optimisations ")
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:$packageName"))
                startActivity(intent)
            }





        }




        findViewById<Button>(R.id.check_app_verid).setOnClickListener {

               val json1=  JSONObject()

                json1.put("version", "1.0")

                println(" in check app update ")
               val sdf =  AsynchronousGet(prefc.getString("api_k", "")!!, 4, json1)
                sdf.dataReturn = this
                sdf.run()

            }




        if (!isNotificationServiceEnabled()) {

            println(" press this 2")
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog()
            println(" press this 3")
            enableNotificationListenerAlertDialog!!.show()
        } else {
            println( " asdkfj press 4")
            if (df.allPermissionsGranted()) {

                println("press 55 ")
                callServersALL()
                // mContext.startActivity(Intent(mContext, testTabCompose::class.java))

            } else {
                df.requestPermissiong();
            }
        }
        // if not first
    }


    private fun notiCheck() {
//
//         if (!isNotificationServiceEnabled()) {
//
//            println(" press this 2")
//            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog()
//            println(" press this 3")
//            enableNotificationListenerAlertDialog!!.show()
//        } else {
        println(" asdkfj press 4")
        if (df.allPermissionsGranted()) {

            println("press 55 ")

            callServersALL()

            // mContext.startActivity(Intent(mContext, testTabCompose::class.java))

        } else {
            df.requestPermissiong();
        }
    }
//        //}
//
//    }


    private fun buildNotificationServiceAlertDialog(): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.notification_listener_service)
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation)
        alertDialogBuilder.setPositiveButton(R.string.yes) { dialogInterface, i ->
            println(" in this pre error ")
            startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
        alertDialogBuilder.setNegativeButton(R.string.no, { dialogInterface, i ->

        })

        return (alertDialogBuilder.create())
    }


    private fun callServersALL() {
        //val android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        println(" in a start servers ")
        val requl = Intent(this, ServesNotify::class.java)
        requl.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startService(requl)

        val smsServiceIntent = Intent(this, SmsProcessService::class.java)
        smsServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startService(smsServiceIntent)



              val intentFore = Intent(this, PingService::class.java)
              intentFore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              startService(intentFore)
    }


    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

//        println(" in a log permissions ")
//
//        if (df.allPermissionsGranted()) {
//
//            callServersALL()
//            // mContext.startActivity(Intent(mContext, testTabCompose::class.java))
//
//        } else {
//            df.requestPermissiong()
//        }




            when (requestCode) {
                11 -> {
                    // If request is cancelled, the result arrays are empty.
                    if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ) {
                        println(" all request  is granted ! ")

                        callServersALL()

                            // mContext.startActivity(Intent(mContext, testTabCompose::class.java))

                        // Permission is granted. Continue the action or workflow
                        // in your app.
                    } else {
                        // Explain to the user that the feature is unavailable because
                        // the feature requires a permission that the user has denied.
                        // At the same time, respect the user's decision. Don't link to
                        // system settings in an effort to convince the user to change
                        // their decision.
                    }
                    return
                }
            }
        }


       override fun onBackPressed() {

//     if(ok){
//      super.onBackPressed()
//
//     }else {
//
//     }


    }


    override fun returnServerAnswer(jsonString: String) {
        println(" my return json = ${jsonString.toString()}")


        // Can't toast on a thread that has not called Looper.prepare()
        Looper.prepare()

              val sdf = JSONObject(jsonString)

           if(sdf.get("status".toString()).equals("error")){

           }else {

                    if(sdf.get("version".toString()).equals("latest-version")){
                Toast.makeText(this, sdf.get("text").toString(), Toast.LENGTH_SHORT).show()


                 }else {

       Toast.makeText(this, sdf.get("text").toString(), Toast.LENGTH_SHORT).show()
       Toast.makeText(this, sdf.get("link").toString(), Toast.LENGTH_LONG).show()



                }

           }



     }


}