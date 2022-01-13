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

package com.huawei.ohos.ads.interstitial;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.interstitialad.InterstitialAd;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.RadioContainer;
import ohos.agp.window.dialog.ToastDialog;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class MainAbility extends Ability {

    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0X10201, MainAbility.class.getSimpleName());


    private RadioContainer displayRadioGroup;
    private Button loadAdButton;

    private InterstitialAd interstitialAd;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);

        displayRadioGroup = findComponentById(ResourceTable.Id_display_radio_group);
        loadAdButton = findComponentById(ResourceTable.Id_load_ad);
        loadAdButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                loadInterstitialAd();
            }
        });
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            showToast("Ad loaded");

            // Display an interstitial ad.
            showInterstitial();
        }

        @Override
        public void onAdFailed(int errorCode) {
            showToast("Ad load failed with error code: " + errorCode);
            HiLog.debug(LABEL, "Ad load failed with error code: " + errorCode);
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            showToast("Ad closed");
            HiLog.debug(LABEL, "onAdClosed");

        }

        @Override
        public void onAdClicked() {
            HiLog.debug(LABEL, "onAdClicked");
            super.onAdClicked();
        }

        @Override
        public void onAdOpened() {
            HiLog.debug(LABEL, "onAdOpened");

            super.onAdOpened();
        }
    };

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdId(getAdId());
        interstitialAd.setAdListener(adListener);

        AdParam adParam = new AdParam.Builder().build();
        interstitialAd.loadAd(adParam);
    }

    private String getAdId() {
        if (displayRadioGroup.getMarkedButtonId() == 0) {
            return getString(ResourceTable.String_image_ad_id);
        } else {
            return getString(ResourceTable.String_video_ad_id);
        }
    }

    private void showInterstitial() {
        // Display an interstitial ad.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show(this);
        } else {
            showToast("Ad did not load");
        }
    }

    private void showToast(final String text) {
        getUITaskDispatcher().asyncDispatch(new Runnable() {
            @Override
            public void run() {
                new ToastDialog(getApplicationContext()).setText(text).show();
            }
        });
    }
}
