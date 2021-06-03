package com.example.yummymeals.util

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog


class ConnectionReportAlertDialog {
    fun buildDialog(context: Context) {
        val dialogue = AlertDialog.Builder(context)
        dialogue.setTitle("You're offline")
        dialogue.setMessage("Please Check Your Internet Connection")
        dialogue.setPositiveButton("Setting") { _, _ ->
            context.startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS))
        }
        dialogue.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        dialogue.create()
        dialogue.show()
    }
}