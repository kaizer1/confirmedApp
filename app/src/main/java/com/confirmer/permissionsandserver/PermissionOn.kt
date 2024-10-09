package com.confirmer.permissionsandserver

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionOn(context: Context, active: Activity) {


    companion object {
                private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.QUERY_ALL_PACKAGES)
    }
    var conte : Context = context

    public fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            conte, it) == PackageManager.PERMISSION_GRANTED

    }

    public fun requestPermissiong(){
        ActivityCompat.requestPermissions(conte as Activity, REQUIRED_PERMISSIONS, 11)
    }

}