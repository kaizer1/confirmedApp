package com.confirmer.permissionsandserver

import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat
import java.util.Calendar
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

        val extras = sbn!!.notification.extras
        val formatter = SimpleDateFormat("dd.MM.yyyy hh:mm:ss")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = sbn.postTime
        val docuI = extras.getCharSequence("android.text").toString()

        println(" notification posted is == ! $docuI" )

    }


    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
          println(" on notifi Removed ")
    }

}