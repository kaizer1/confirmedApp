package app.confirmer

import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import app.confirmer.viewlistapps.CustomAdapter
import app.confirmer.viewlistapps.pInfo

class ActivityChangeAppSends : AppCompatActivity() {

    lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences  ( applicationContext )

        setContentView(R.layout.activity_com_change)
        hideSystemUI()

        val boxAdapter = CustomAdapter(this, getPackages())
        findViewById<ListView>(R.id.id_list).adapter = boxAdapter

    }

        fun getPackages() : ArrayList<pInfo> {
      getInstalledApps(false).forEach {
            it.prettyPrint()
        }

             val apps = getInstalledApps(false)
              apps.sortBy { it.appName }
              apps.sortBy { !it.checkb }

            return apps

    }



    private fun getInstalledApps(getSysPackages : Boolean) : ArrayList<pInfo> {
    val res = ArrayList<pInfo>()

        applicationContext.packageManager.getInstalledPackages(PackageManager.GET_META_DATA).forEach {

        if (it.versionName.isNotEmpty()) {

             if(!isSystemPackage(it)){


                 val nameAppld = it.applicationInfo.loadLabel(applicationContext.packageManager).toString()

           var existsCheck = false
           if(prefs.contains("filter")){
           val mainStringFilter = prefs.getString("filter", "")

            if (mainStringFilter!!.contains(nameAppld)){
                println(" my name is ok == $nameAppld")
                existsCheck = true
            }
        }

            res.add(
                pInfo(
                    nameAppld,
                    it.packageName,
                    it.versionName,
                    it.versionCode,
                    it.applicationInfo.loadIcon(applicationContext.packageManager),
                    existsCheck ) )
                    }
              }
         }

    return res
    }


      private fun isSystemPackage(packCheck : PackageInfo) : Boolean{

          if((packCheck.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
          {
            return true
          }

          return false
      }


        private fun hideSystemUI() {
         WindowCompat.setDecorFitsSystemWindows(window, false)
         WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

}