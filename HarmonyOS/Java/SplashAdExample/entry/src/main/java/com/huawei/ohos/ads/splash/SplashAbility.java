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

package com.huawei.ohos.ads.splash;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.AudioFocusType;
import com.huawei.hms.ads.splash.SplashAdDisplayListener;
import com.huawei.hms.ads.splash.SplashView;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.Revocable;
import ohos.bundle.AbilityInfo;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class SplashAbility extends Ability {

    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0X10201, SplashAbility.class.getSimpleName());

    // Ad display timeout interval, in milliseconds.
    private static final int AD_TIMEOUT = 10000;

    /**
     * Pause flag.
     * On the splash ad screen:
     * Set this parameter to true when exiting the app to ensure that the app home screen is not displayed.
     * Set this parameter to false when returning to the splash ad screen from another screen to ensure that the app home screen can be displayed properly.
     */
    private boolean hasPaused = false;

    private boolean hasBackPressed;

    private Revocable revocable;

    private SplashView splashView;

    private SplashView.SplashAdLoadListener splashAdLoadListener = new SplashView.SplashAdLoadListener() {
        @Override
        public void onAdLoaded() {
            // Call this method when an ad is successfully loaded.
            HiLog.debug(LABEL, "SplashAdLoadListener onAdLoaded.");
            showToast(getString(ResourceTable.String_status_load_ad_success));
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            // Call this method when an ad fails to be loaded.
            HiLog.debug(LABEL, "SplashAdLoadListener onAdFailedToLoad, errorCode: " + errorCode);
            showToast(getString(ResourceTable.String_status_load_ad_fail) + errorCode);
            jump();
        }

        @Override
        public void onAdDismissed() {
            // Call this method when the ad display is complete.
            HiLog.debug(LABEL, "SplashAdLoadListener onAdDismissed.");
            showToast(getString(ResourceTable.String_status_ad_dismissed));
            jump();
        }
    };

    private SplashAdDisplayListener adDisplayListener = new SplashAdDisplayListener() {
        @Override
        public void onAdShowed() {
            // Call this method when an ad is displayed.
            HiLog.debug(LABEL, "SplashAdDisplayListener onAdShowed.");
        }

        @Override
        public void onAdClick() {
            // Call this method when an ad is clicked.
            HiLog.debug(LABEL, "SplashAdDisplayListener onAdClick.");
        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_slice_splash);
        loadAd();
    }

    private void loadAd() {
        HiLog.debug(LABEL, "Start to load ad");

        AdParam adParam = new AdParam.Builder().build();
        splashView = (SplashView)findComponentById(ResourceTable.Id_plash_ad_view);
        HiLog.debug(LABEL, "splashView:"+splashView);

        splashView.setAdDisplayListener(adDisplayListener);

        // Set a default app launch image.
        splashView.setSloganView(findComponentById(ResourceTable.Id_splash_slogan_view));
        splashView.setSloganResId(ResourceTable.Media_default_slogan);
        splashView.setWideSloganResId(ResourceTable.Media_default_slogan_landscape);
        splashView.setLogo(findComponentById(ResourceTable.Id_logo_area));

        // Set a logo image.
        splashView.setLogoResId(ResourceTable.Media_ic_launcher);
        // Set logo description.
        splashView.setMediaNameResId(ResourceTable.String_media_name);
        // Set the audio focus type for a video splash ad.
        splashView.setAudioFocusType(AudioFocusType.NOT_GAIN_AUDIO_FOCUS_WHEN_MUTE);

        splashView.load(getString(ResourceTable.String_ad_id_splash), AbilityInfo.DisplayOrientation.PORTRAIT.ordinal(), adParam, splashAdLoadListener);
        HiLog.debug(LABEL, "End to load ad");

        if (null != revocable) {
            revocable.revoke();
        }

        TaskDispatcher dispatcher = getUITaskDispatcher();
        revocable = dispatcher.delayDispatch(new Runnable() {
            @Override
            public void run() {
                if (!isTerminating() && hasWindowFocus()) {
                    jump();
                }
            }
        }, AD_TIMEOUT);
    }

    /**
     * Switch from the splash ad screen to the app home screen when the ad display is complete.
     */
    private void jump() {
        HiLog.debug(LABEL, "jump hasPaused: " + hasPaused);
        if (!hasPaused && !hasBackPressed) {
            hasPaused = true;
            HiLog.debug(LABEL, "jump into application");
            try {
                Intent intent = new Intent();
                Operation operation = new Intent.OperationBuilder()
                        .withBundleName(getBundleName())
                        .withAbilityName(MainAbility.class.getName())
                        .build();
                intent.setOperation(operation);
                startAbility(intent);
            } catch (Throwable throwable) {
                HiLog.error(LABEL, "start ability error");
            }

            getUITaskDispatcher().delayDispatch(new Runnable() {
                @Override
                public void run() {
                    if (!isTerminating()) {
                        terminateAbility();
                    }
                }
            }, 1000);
        }
    }


    /**
     * Set this parameter to true when exiting the app to ensure that the app home screen is not displayed.
     */
    @Override
    protected void onBackground() {
        HiLog.debug(LABEL, "SplashAbilitySlice onBackground.");
        // Remove the timeout message from the message queue.
        if (null != revocable) {
            revocable.revoke();
            revocable = null;
        }
        hasPaused = true;
        super.onBackground();
    }

    /**
     * Call this method when returning to the splash ad screen from another screen to access the app home screen.
     */
    @Override
    public void onForeground(Intent intent) {
        HiLog.debug(LABEL, "SplashAbilitySlice onForeground.");
        super.onForeground(intent);
        hasPaused = false;
        jump();
    }

    @Override
    protected void onStop() {
        HiLog.debug(LABEL, "SplashAbilitySlice onStop.");
        super.onStop();
        if (splashView != null) {
            splashView.destroyView();
        }
    }

    @Override
    protected void onInactive() {
        HiLog.debug(LABEL, "SplashAbilitySlice onInactive.");
        super.onInactive();
        if (splashView != null) {
            splashView.pauseView();
        }
    }

    @Override
    public void onActive() {
        HiLog.debug(LABEL, "SplashAbilitySlice onActive.");
        super.onActive();
        if (splashView != null) {
            splashView.resumeView();
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

    @Override
    protected void onBackPressed() {
        HiLog.debug(LABEL, "onBackPressed");
        hasBackPressed = true;
        if (!isTerminating()) {
            terminateAbility();
        }
        super.onBackPressed();
    }
}
