package com.example.back_cam

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(TAG, "onReceive")
        notifyService(context)
    }

    private fun notifyService(context:Context){
        Log.i(TAG, "startingService")
        val intent = Intent(context, CamService::class.java)
        intent.action = CamService.ACTION_START

        //context.startService(intent)

        context.startForegroundService(intent)
    }

}