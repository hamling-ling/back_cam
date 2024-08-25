package com.example.back_cam

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.back_cam.R
import eu.sisik.backgroundcam.isServiceRunning


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.action) {
                CamService.ACTION_STOPPED -> flipButtonVisibility(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initView()

        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.SYSTEM_ALERT_WINDOW)
        for(permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // We don't have camera permission yet. Request it from the user.
                ActivityCompat.requestPermissions(this, permissions, CODE_PERM_CAMERA)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        registerReceiver(receiver, IntentFilter(CamService.ACTION_STOPPED))
        registerReceiver(receiver, IntentFilter(CamService.ACTION_START_WITH_PREVIEW))
        registerReceiver(receiver, IntentFilter(CamService.ACTION_START))

        val running = isServiceRunning(this, CamService::class.java)
        flipButtonVisibility(running)
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(receiver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CODE_PERM_CAMERA -> {
                if (grantResults?.firstOrNull() != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.err_no_cam_permission), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun initView() {

        val butStart = findViewById<Button>(R.id.butStart)
        butStart.setOnClickListener {

            if (!isServiceRunning(this, CamService::class.java)) {
                notifyService(CamService.ACTION_START)
                finish()
            }
        }

        val butStartPreview = findViewById<Button>(R.id.butStartPreview)
        butStartPreview.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

                // Don't have permission to draw over other apps yet - ask user to give permission
                val settingsIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivityForResult(settingsIntent, CODE_PERM_SYSTEM_ALERT_WINDOW)
                return@setOnClickListener
            }

            if (!isServiceRunning(this, CamService::class.java)) {
                notifyService(CamService.ACTION_START_WITH_PREVIEW)
                finish()
            }
        }

        val butStop = findViewById<Button>(R.id.butStop)
        butStop.setOnClickListener {
            stopService(Intent(this, CamService::class.java))
        }
    }

    private fun notifyService(action: String) {

        val intent = Intent(this, CamService::class.java)
        intent.action = action
        startService(intent)
    }

    private fun flipButtonVisibility(running: Boolean) {

        val butStart = findViewById<Button>(R.id.butStart)
        val butStartPreview = findViewById<Button>(R.id.butStartPreview)
        val butStop = findViewById<Button>(R.id.butStop)

        butStart.visibility =  if (running) View.GONE else View.VISIBLE
        butStartPreview.visibility =  if (running) View.GONE else View.VISIBLE
        butStop.visibility =  if (running) View.VISIBLE else View.GONE
    }

    companion object {

        val CODE_PERM_SYSTEM_ALERT_WINDOW = 6111
        val CODE_PERM_CAMERA = 6112

    }
}
