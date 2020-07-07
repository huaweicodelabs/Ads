/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.installrefererkotlindemo.huawei

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.installrefererkotlindemo.huawei.installreferrer.InstallReferrerActivity
import com.installrefererkotlindemo.huawei.installreferrer.InstallReferrerWriteActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var mCallMode: Int = CallMode.SDK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        // Create the "install_referrer" TextView, which tries to show the obtained install referrer.
        enter_install_referrer_rl.setOnClickListener(this)
        // Create the "write_install_referrer" view, which tries to enter the page where user can set service package name and install referer.
        write_install_referrer_rl.setOnClickListener(this)

        call_mode_rg.setOnCheckedChangeListener { _, mCheckedId -> getCallMode(mCheckedId) }
    }

    private fun getCallMode(mCheckedId: Int) {
        mCallMode = if (R.id.mode_sdk_rb == mCheckedId) CallMode.SDK else CallMode.AIDL
    }

    override fun onClick(mView: View?) {
        mView?.let {
            when (it.id) {
                R.id.enter_install_referrer_rl -> startActivity(InstallReferrerActivity::class.java)
                R.id.write_install_referrer_rl -> startActivity(InstallReferrerWriteActivity::class.java)
            }
        }
    }

    private fun startActivity(activity: Class<*>) {
        try {
            val intent = Intent(this, activity)
            intent.putExtra("mode", mCallMode)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "startActivity Exception: $e")
        }
    }
}
