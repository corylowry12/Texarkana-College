package com.cory.texarkanacollege

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ManagePermissions(private val activity: Activity, private val list: List<String>, private val code:Int) {

    // Check permissions at runtime
    fun checkPermissions(context: Context) : Boolean {
        return isPermissionsGranted() == PackageManager.PERMISSION_GRANTED
    }

    // Check permissions status
    private fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }


    // Find the first denied permission
    private fun deniedPermission(context : Context): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) return permission
        }
        return ""
    }

    // Show alert dialog to request permissions
    fun showAlert(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.need_permissions)
        builder.setMessage(R.string.some_permissions_are_required_to_do_the_task)
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            requestPermissions(context)
        }
        builder.setNeutralButton(R.string.cancel) { _, _ ->
            Toast.makeText(context, context.getString(R.string.permission_not_granted), Toast.LENGTH_SHORT)
                    .show()
        }
        val dialog = builder.create()
        dialog.show()
        }

    // Request the permissions at run time
    private fun requestPermissions(context: Context) {
        val permission = deniedPermission(context)
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        } else {
            Toast.makeText(context, "Go to settings and manually change the permission", Toast.LENGTH_SHORT).show()
        }
    }

    fun processPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }
        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }
}