package app.confirmer.viewlistapps

import android.graphics.drawable.Drawable

data class pInfo(
         val appName: String,
         val pName: String,
         val verName: String,
         val verCode: Int,
         val iconD: Drawable,
         val checkb : Boolean
     ) {


     var appname : String = appName
     private var pname   : String = pName
     private var versionName : String  = verName
     private var versionCode : Int = verCode
     private var icon : Drawable = iconD
     private var check : Boolean = checkb
         fun prettyPrint() {
        println("dsf +${appname} + $pname + $versionName + $versionCode" )
     }
 }
