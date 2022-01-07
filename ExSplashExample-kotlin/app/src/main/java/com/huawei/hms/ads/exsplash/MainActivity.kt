/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.huawei.hms.ads.exsplash

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.ads.exsplash.MainActivity
import com.huawei.hms.ads.exsplash.ProtocolDialog.ProtocolDialogCallback

class MainActivity : AppCompatActivity() {
    private var exSplashService: ExSplashServiceManager? = null
    private var exSplashBroadcastReceiver: ExSplashBroadcastReceiver? = null
    private var showDialog: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        exSplashBroadcastReceiver = ExSplashBroadcastReceiver()
        val filter = IntentFilter(ACTION_EXSPLASH_DISPLAYED)
        registerReceiver(exSplashBroadcastReceiver, filter)
        exSplashService = ExSplashServiceManager(this)
        showDialog = findViewById(R.id.show_dialog)
        showDialog!!.setOnClickListener(View.OnClickListener { showProtocolDialog() })

        // Checking user consent status.
        checkUserConsent()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exSplashBroadcastReceiver != null) {
            unregisterReceiver(exSplashBroadcastReceiver)
            exSplashBroadcastReceiver = null
        }
    }

    /**
     * You should show the user protocol dialog and receive user's selection results.
     */
    private fun checkUserConsent() {
        val preferences: SharedPreferences = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val status = preferences.getInt(SP_PROTOCOL_KEY, -1)
        if (status == -1) { // First launch App
            showProtocolDialog()
        } else if (status == 0) { // The user does not consent agreement.
            exSplashService!!.enableUserInfo(false)
        } else { // The user consent agreement.
            exSplashService!!.enableUserInfo(true)
        }
    }

    /**
     * Display a protocol dialog.
     */
    private fun showProtocolDialog() {
        val dialog = ProtocolDialog(this)
        dialog.setCallback(object : ProtocolDialogCallback {
            override fun agree() {
                exSplashService!!.enableUserInfo(true)
                Toast.makeText(this@MainActivity, "Try restart app and check the exsplash ad.", Toast.LENGTH_SHORT).show()
            }

            override fun cancel() {
                exSplashService!!.enableUserInfo(false)
                // Exit app.
                finish()
            }
        })
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    companion object {
        /**
         * Action of ExSplash displayed.
         */
        private const val ACTION_EXSPLASH_DISPLAYED = "com.huawei.hms.ads.EXSPLASH_DISPLAYED"
        private const val SP_NAME = "ExSplashSharedPreferences"
        private const val SP_PROTOCOL_KEY = "user_consent_status"
    }
}
