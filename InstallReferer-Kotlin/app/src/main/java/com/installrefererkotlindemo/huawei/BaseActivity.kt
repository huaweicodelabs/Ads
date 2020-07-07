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

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.MenuItem

open class BaseActivity : Activity() {

    private val TAG = BaseActivity::class.java.simpleName

    protected open fun init() {
        val mActionBar : ActionBar? = actionBar
        mActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                finish() // back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun getIntExtra(mName: String?, mDefaultValue: Int): Int {
        val mIntent : Intent = intent
        mIntent.let {
            try {
                return it.getIntExtra(mName, mDefaultValue)
            } catch (e: Exception) {
                Log.e(TAG, "getIntExtra Exception")
            }
        }.xor(Log.e(TAG, "getIntExtra intent is null"))
        return mDefaultValue
    }
}