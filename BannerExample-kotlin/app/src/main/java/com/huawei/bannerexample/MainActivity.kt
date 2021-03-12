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
package com.huawei.bannerexample

import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.banner.BannerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this)

        // Obtain BannerView based on the configuration in layout/ad_fragment.xml.
        val bottomBannerView = findViewById<BannerView>(R.id.hw_banner_view)
        val adParam = AdParam.Builder().build()
        bottomBannerView.loadAd(adParam)

        // Call new BannerView(Context context) to create a BannerView class.
        val topBannerView = BannerView(this)
        topBannerView.adId = getString(R.string.banner_ad_id)
        topBannerView.bannerAdSize = BannerAdSize.BANNER_SIZE_SMART
        topBannerView.loadAd(adParam)
        val rootView = findViewById<RelativeLayout>(R.id.root_view)
        rootView.addView(topBannerView)
    }
}