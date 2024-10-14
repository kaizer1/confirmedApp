package app.confirmer.permissionsandserver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import app.confirmer.internet.AsynchronousGet
import app.confirmer.R
import com.google.common.base.Objects
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask





class PingService  : Service() {



      companion object
   {
       private  final const val ID_SERVICE = 101
       private val timer = Timer()
   }

    override fun onBind(p0: Intent?): IBinder? {
        //TODO("Not yet implemented")

        return null
    }

    private fun sendMyPing(){

        println( " my send's ! ")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val prefs = PreferenceManager.getDefaultSharedPreferences  ( applicationContext )



        val json1 = JSONObject()
        json1.put("sdf", " meo")

              val  AsyncG = AsynchronousGet(prefs.getString("api_k","")!!,2, json1)


                timer.schedule(object : TimerTask(){

            override fun run() {
                   sendMyPing()
                AsyncG.run()
            }
        }, 0, 30000) // 30 seconds



        val notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = createNotificationChannel(notificationManager)
        val notificationBuilder =  NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getText(R.string.work_conf))
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()


        startForeground(ID_SERVICE, notification)

        return START_STICKY
    }


    


    override fun onCreate() {
        super.onCreate()


//        val notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channelId = createNotificationChannel(notificationManager)
//        val notificationBuilder =  NotificationCompat.Builder(this, channelId)
//        val notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setPriority(PRIORITY_MIN)
//                .setCategory(NotificationCompat.CATEGORY_SERVICE)
//                .build()
//
//
//        startForeground(ID_SERVICE, notification)

    }

       private fun createNotificationChannel(notificationManager : NotificationManager) : String{
        val channelId = "my_service_channelid";
        val channelName = "My Foreground Service";
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
           channel.importance = NotificationManager.IMPORTANCE_NONE;
           channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE;
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}