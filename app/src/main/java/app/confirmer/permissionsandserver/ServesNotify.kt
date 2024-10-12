package app.confirmer.permissionsandserver

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.getSystemService
import app.confirmer.internet.AsynchronousGet
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID

class ServesNotify : NotificationListenerService() {

   private companion object{
       const val push : String =  "PUSH"
      // val uniqueID = ServesNotifyWork.android_id
    }


    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val prefs = PreferenceManager.getDefaultSharedPreferences  ( applicationContext )

        val extras = sbn!!.notification.extras
        val formatter = SimpleDateFormat("yyyy.MM.dd hh:mm:ss")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = sbn.postTime
        val docuI = extras.getCharSequence("android.text").toString()

        println(" notification posted is == ! $docuI" )



       val tymZne = TimeZone.getDefault().rawOffset / 3600000

       val json1  = JSONObject()

        json1.put("from", sbn.packageName.toString())
        json1.put("text", extras.getCharSequence("android.text").toString())
        json1.put("datetime", formatter.format(calendar.time))
        json1.put("timeZoneOffsetInHours", tymZne.toString())
        json1.put("type", "PUSH")


     AsynchronousGet( prefs.getString("api_k", "")!!,3, json1).run()


    }


    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
          println(" on notifi Removed ")
    }

}