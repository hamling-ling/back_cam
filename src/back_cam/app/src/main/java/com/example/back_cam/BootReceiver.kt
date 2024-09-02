package com.example.back_cam

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    companion object {
        val TAG = "BootReceiver"
        val IS_NEWER_VERSION_BOOT = "is_newer_version_boot"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(TAG, "onReceive")
        notifyService(context)
    }

    private fun notifyService(context:Context){
        Log.i(TAG, "startingService")

        if (android.os.Build.VERSION.SDK_INT < 11){
            Log.i(TAG, "boot sequence for Old Android")

            val intent = Intent(context, CamService::class.java)
            intent.action = CamService.ACTION_START
            context.startForegroundService(intent)
        } else {
            Log.i(TAG, "boot sequence for New Android")
            Intent(context, MainActivity::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                this.putExtra(IS_NEWER_VERSION_BOOT, true)
                context.startActivity(this)
            }
        }
    }
}