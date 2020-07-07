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
package com.installrefererkotlindemo.huawei.installreferrer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.installrefererkotlindemo.huawei.BaseActivity
import com.installrefererkotlindemo.huawei.Constants
import com.installrefererkotlindemo.huawei.R
import kotlinx.android.synthetic.main.activity_install_referrer_write.*
import org.json.JSONException
import org.json.JSONObject

class InstallReferrerWriteActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "InstallReferrerWrite"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_install_referrer_write)
        init()
    }

    override fun init() {
        super.init()
        // Create the "delete" button, which tries to delete existed install referrer according package name.
        delete_btn.setOnClickListener(this)
        // Create the "save" button, which tries to save what just typed.
        save_btn.setOnClickListener(this)
    }

    override fun onClick(mView: View?) {
        when (mView?.id) {
            R.id.delete_btn -> deleteInstallReferrer()
            R.id.save_btn -> saveInstallReferrer()
        }
    }

    private fun saveInstallReferrer() {
        if (isInvalid(package_name_et)) {
            Log.e(TAG, "invalid package name")
            showToast(getString(R.string.invalid_package_name))
            return
        }
        if (isInvalid(install_referrer_et)) {
            Log.e(TAG, "invalid install referrer")
            showToast(getString(R.string.invalid_install_referrer))
            return
        }
        val mPkgName = package_name_et.text.toString()
        val mInstallReferrer = install_referrer_et.text.toString()
        saveOrDelete(mPkgName, mInstallReferrer, false)
    }

    private fun deleteInstallReferrer() {
        if (isInvalid(package_name_et)) {
            Log.e(TAG, "invalid package name")
            showToast(getString(R.string.invalid_package_name))
            return
        }
        val mPkgName = package_name_et.text.toString()
        saveOrDelete(mPkgName, "", true)
    }

    private fun isInvalid(editText: EditText?): Boolean {
        return null == editText?.text || TextUtils.isEmpty(editText.text.toString())
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveOrDelete(
        mPkgName: String,
        mInstallReferrer: String,
        mIsDelete: Boolean
    ) {
        Log.i(TAG, "saveOrDelete isDelete=$mIsDelete")
        val sp = getSharedPreferences(
            Constants.INSTALL_REFERRER_FILE,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor?.let {
            if (mIsDelete) {
                // Delete existed install referrer according package name
                it.remove(mPkgName)
                it.apply()
                showToast(getString(R.string.delete_install_referrer_success))
            } else {
                // Save the typed install referrer.
                val mJsonObject = JSONObject()
                try {
                    mJsonObject.apply {
                        put("channelInfo", mInstallReferrer)
                        put("clickTimestamp", System.currentTimeMillis() - 123456L)
                        put("installTimestamp", System.currentTimeMillis())
                    }
                    it.putString(mPkgName, mJsonObject.toString())
                    it.apply()
                    showToast(getString(R.string.save_install_referrer_success))
                } catch (e: JSONException) {
                    Log.e(
                        TAG,
                        "saveOrDelete JSONException"
                    )
                    showToast(getString(R.string.save_install_referrer_fail))
                }
            }
        }
    }

    private fun showToast(mToastMessage:String){
        Toast.makeText(this, mToastMessage, Toast.LENGTH_SHORT)
            .show()
    }
}
