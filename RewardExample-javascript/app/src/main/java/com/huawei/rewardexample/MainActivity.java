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

package com.huawei.rewardexample;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.ads.jsb.JsbConfig;
import com.huawei.hms.ads.jsb.PPSJsBridge;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private PPSJsBridge mJsBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWebView();
    }


    private void initWebView() {
        mWebView = findViewById(R.id.mWebView);

        // Initialize the JSBridge configuration.
        PPSJsBridge.init(new JsbConfig.Builder().enableUserInfo(true).enableLog(true).build());

        // Inject the PPSJsBridge Object into this WebView
        mJsBridge = new PPSJsBridge(mWebView);

        mWebView.loadUrl("file:///android_asset/reward.html");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }

        WebSettings websettings = mWebView.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setMediaPlaybackRequiresUserGesture(false);
        websettings.setDomStorageEnabled(true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Destroy the PPSJsBridge Object
        mJsBridge.destroy();
        mWebView.destroy();
        mWebView = null;
    }
}
