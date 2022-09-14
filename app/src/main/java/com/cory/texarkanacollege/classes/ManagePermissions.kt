package com.cory.texarkanacollege.classes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cory.texarkanacollege.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
    @SuppressLint("InflateParams")
    fun showAlert(context: Context) {
        val builder = MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle).create()
        val layout =
            LayoutInflater.from(context).inflate(R.layout.need_app_permissions_dialog_layout, null)
        builder.setView(layout)
        builder.setCancelable(false)
        val okButton = layout.findViewById<Button>(R.id.needPermissionsDialogOKButton)
        val cancelButton = layout.findViewById<Button>(R.id.cancelNeedPermissionsDialog)
        okButton.setOnClickListener {
            builder.dismiss()
            requestPermissions(context)
        }
        cancelButton.setOnClickListener {
            builder.dismiss()
            Toast.makeText(context, context.getString(R.string.permission_not_granted), Toast.LENGTH_SHORT)
                    .show()
        }
        builder.show()
        }

    // Request the permissions at run time
    private fun requestPermissions(context: Context) {
        val permission = deniedPermission(context)
        ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)

    }
}