/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.huawei.ohos.ads.instream;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.MediaMuteListener;
import com.huawei.hms.ads.instreamad.InstreamAd;
import com.huawei.hms.ads.instreamad.InstreamAdLoadListener;
import com.huawei.hms.ads.instreamad.InstreamAdLoader;
import com.huawei.hms.ads.instreamad.InstreamMediaStateListener;
import com.huawei.hms.ads.instreamad.InstreamView;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.utils.TextTool;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.bundle.AbilityInfo;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.KeyEvent;
import ohos.utils.net.Uri;

import java.util.Iterator;
import java.util.List;

public class MainAbility extends Ability implements MediaMuteListener {
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0X00201, "MainAbility");

    private Context context;

    private final static int totalDuration = 60;

    private final static int maxCount = 4;

    private InstreamView instreamView;

    private Text timerTv;

    private Text adTipTv;

    private Button callToAction;

    private Button skipBtn;

    private Button playBt;
    private Button muteBt;

    private Image whyThisAdIcon;

    private String whyThisAdUrl;

    private int maxAdDuration;

    private boolean isMuted = false;

    private boolean isAdPlaying = false;

    private final EventHandler delayHandler = new EventHandler(EventRunner.create(false));

    private long reqStartTime = -1;


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        // 初始化HUAWEI Ads SDK
        HwAds.init(this);

        setUIContent(ResourceTable.Layout_ability_main);
        this.context = getApplicationContext();
        initViews();
        requestAd();
    }

    private void initViews() {
        instreamView = findComponentById(ResourceTable.Id_instream_view);
        instreamView.setInstreamMediaChangeListener(ad -> {
            whyThisAdUrl = null;
            if (ad != null) {
                whyThisAdUrl = ad.getWhyThisAd();
            }
            HiLog.info(LABEL, "onSegmentMediaChange, whyThisAd: " + whyThisAdUrl);
            if (!TextTool.isNullOrEmpty(whyThisAdUrl)) {
                whyThisAdIcon.setVisibility(Component.VISIBLE);
                HiLog.info(LABEL, "whyThisAdIcon  VISIBLE");
                whyThisAdIcon.setClickedListener(component -> {
                    try {
                        HiLog.info(LABEL, "on whyThisAd clicked");
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri contentUrl = Uri.parse(whyThisAdUrl);
                        intent.setUri(contentUrl);
                        intent.setFlags(Intent.FLAG_ABILITY_NEW_MISSION | Intent.FLAG_ABILITY_CLEAR_MISSION);
                        startAbility(intent, 0);
                    } catch (Throwable e) {
                        HiLog.error(LABEL, "open whyThisAd error" + e.getClass().getSimpleName());
                    }
                });
            } else {
                whyThisAdIcon.setVisibility(Component.HIDE);
                HiLog.info(LABEL, "whyThisAdIcon  HIDE");

            }

            String cta = ad.getCallToAction();
            HiLog.info(LABEL, "onSegmentMediaChange, cta: " + cta);
            if (!TextTool.isNullOrEmpty(cta)) {
                callToAction.setVisibility(Component.VISIBLE);
                callToAction.setText(cta);
            }
        });

        instreamView.setInstreamMediaStateListener(new InstreamMediaStateListener() {
            @Override
            public void onMediaProgress(int percent, final int playTime) {
                getUITaskDispatcher().asyncDispatch(() -> updateCountDown(playTime));
            }

            @Override
            public void onMediaStart(final int playTime) {
                timerTv.setVisibility(Component.VISIBLE);

                getUITaskDispatcher().asyncDispatch(() -> {
                    HiLog.info(LABEL, "onMediaStart: " + playTime);
                    playBt.setText(getString(ResourceTable.String_instream_pause));
                    updateCountDown(playTime);
                });

            }

            @Override
            public void onMediaPause(final int playTime) {

                getUITaskDispatcher().asyncDispatch(() -> {
                    HiLog.info(LABEL, "onMediaPause: " + playTime);
                    updateCountDown(playTime);
                });
            }

            @Override
            public void onMediaStop(final int playTime) {

                getUITaskDispatcher().asyncDispatch(() -> {
                    HiLog.info(LABEL, "onMediaStop: " + playTime);
                    updateCountDown(playTime);
                });
            }

            @Override
            public void onMediaCompletion(final int playTime) {

                getUITaskDispatcher().asyncDispatch(() -> {
                    HiLog.info(LABEL, "onMediaCompletion: " + playTime);
                    updateCountDown(playTime);
                    instreamView.destroy();
                    playVideo();
                });
            }

            @Override
            public void onMediaError(final int playTime, final int errorCode, final int extra) {

                getUITaskDispatcher().asyncDispatch(() -> {
                    updateCountDown(playTime);
                    HiLog.warn(LABEL, "onMediaError," + "playTime: " + playTime + "errorCode: " + errorCode
                            + ", extra: " + extra);
                });
            }
        });

        instreamView.setMediaMuteListener(this);

        timerTv = findComponentById(ResourceTable.Id_instream_timer_tv);
        whyThisAdIcon = findComponentById(ResourceTable.Id_instream_wta_icon);
        whyThisAdIcon.setVisibility(Component.HIDE);
        adTipTv = findComponentById(ResourceTable.Id_instream_ad_tip_tv);
        callToAction = findComponentById(ResourceTable.Id_instream_call_to_action);
        callToAction.setVisibility(Component.INVISIBLE);
        instreamView.setCallToActionView(callToAction);
        skipBtn = findComponentById(ResourceTable.Id_instream_skip);
        skipBtn.setVisibility(Component.INVISIBLE);
        skipBtn.setClickedListener(component -> {
            timerTv.setVisibility(Component.VISIBLE);
            instreamView.onClose();
            instreamView.destroy();
            playVideo();
        });

        playBt = findComponentById(ResourceTable.Id_instream_play_btn);
        playBt.setClickedListener(component -> {
            if (isAdPlaying) {
                HiLog.info(LABEL, "pause");
                instreamView.pause();
                isAdPlaying = false;
                playBt.setText(getString(ResourceTable.String_instream_play));
            } else {
                HiLog.info(LABEL, "play");
                instreamView.play();
                isAdPlaying = true;
                playBt.setText(getString(ResourceTable.String_instream_pause));
            }
        });

        muteBt = findComponentById(ResourceTable.Id_instream_mute_btn);
        muteBt.setClickedListener(component -> {
            if (isMuted) {
                HiLog.info(LABEL, "unmute");
                muteBt.setText(getString(ResourceTable.String_instream_mute));
                instreamView.unmute();
            } else {
                HiLog.info(LABEL, "mute");
                muteBt.setText(getString(ResourceTable.String_instream_unmute));
                instreamView.mute();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEY_BACK) {    // 返回键
            HiLog.debug(LABEL, "back--->");
            if (isAdPlaying) {
                skipBtn.simulateClick();
                return true; // 这里由于break会退出，所以要处理掉 不返回上一层
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void requestAd() {
        InstreamAdLoader.Builder builder = new InstreamAdLoader.Builder(context, getString(ResourceTable.String_instream_ad_id));
        builder.setTotalDuration(totalDuration)
                .setMaxCount(maxCount)
                .setInstreamAdLoadListener(new MyInstreamAdLoadListener());
        InstreamAdLoader adLoader = builder.build();
        reqStartTime = System.currentTimeMillis();
        adLoader.loadAd(new AdParam.Builder().build());
    }

    private void playInstream(List<InstreamAd> ads) {
        maxAdDuration = getMaxDuration(ads);
        instreamView.setVisibility(Component.VISIBLE);
        instreamView.setInstreamAds(ads);
        adTipTv.setVisibility(Component.VISIBLE);
        String time = String.valueOf(Math.round(maxAdDuration / 1000));
        getUITaskDispatcher().delayDispatch(() -> {
            HiLog.info(LABEL, "set skipbtn visible");
            if (!isTerminating()) {
                skipBtn.setVisibility(Component.VISIBLE);
            }
        }, 4000l);
        timerTv.setText(time + "s");

    }

    private void playVideo() {
        isAdPlaying = false;
        instreamView.setVisibility(Component.HIDE);
        hideAdViews();
    }

    private void hideAdViews() {
        instreamView.setVisibility(Component.HIDE);
        adTipTv.setVisibility(Component.HIDE);
        timerTv.setVisibility(Component.HIDE);
        whyThisAdIcon.setVisibility(Component.HIDE);
        callToAction.setVisibility(Component.HIDE);
        skipBtn.setVisibility(Component.HIDE);
    }

    private void updateCountDown(long playTime) {
        String time = String.valueOf(Math.round((maxAdDuration - playTime) / 1000));
        HiLog.info(LABEL, "updateCountDown, playTime: " + playTime + ", time left: " + time);
        if (timerTv.getVisibility() != Component.VISIBLE) {
            timerTv.setVisibility(Component.VISIBLE);
        }
        timerTv.setText(time + "s");
    }

    private int getMaxDuration(List<InstreamAd> ads) {
        int duration = 0;
        for (InstreamAd ad : ads) {
            duration += ad.getDuration();
        }
        return duration;
    }

    @Override
    public void onMute() {
        HiLog.info(LABEL, "onMute");
        isMuted = true;
    }

    @Override
    public void onUnmute() {
        HiLog.info(LABEL, "onUnmute");
        isMuted = false;
    }


    @Override
    public void onOrientationChanged(AbilityInfo.DisplayOrientation displayOrientation) {
        HiLog.info(LABEL, "displayOrientation==" + displayOrientation);
        super.onOrientationChanged(displayOrientation);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        instreamView.pause();
        isAdPlaying = false;
    }

    @Override
    public void onActive() {
        super.onActive();
        instreamView.resumeView();
        isAdPlaying = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        instreamView.removeInstreamMediaStateListener();
        instreamView.removeInstreamMediaChangeListener();
        instreamView.removeMediaMuteListener();
        instreamView.destroyView();
        delayHandler.removeAllEvent();
    }

    private class MyInstreamAdLoadListener implements InstreamAdLoadListener {
        @Override
        public void onAdLoaded(final List<InstreamAd> ads) {
            HiLog.warn(LABEL, "onAdLoaded: ");

            if (null == ads || ads.size() == 0) {
                playVideo();
                return;
            }

            if (reqStartTime > 0) {
                final long reqTimeCost = System.currentTimeMillis() - reqStartTime;
                showToast("reqTimeCost: " + reqTimeCost);
            }
            Iterator<InstreamAd> it = ads.iterator();
            while (it.hasNext()) {
                InstreamAd ad = it.next();
                if (ad.isExpired()) {
                    it.remove();
                }
            }
            if (ads.size() == 0) {
                playVideo();
                return;
            }
            playInstream(ads);
        }

        @Override
        public void onAdFailed(int errorCode) {
            HiLog.warn(LABEL, "onAdFailed: " + errorCode);
            new ToastDialog(getContext()).setText("onAdFailed: " + errorCode).show();
            playVideo();
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

