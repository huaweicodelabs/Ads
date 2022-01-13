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

package com.huawei.ohos.ads.nativeads;

import coil.Coil;
import com.huawei.hms.ads.AppDownloadButton;
import com.huawei.hms.ads.AppDownloadButtonStyle;
import com.huawei.hms.ads.VideoOperator;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeView;
import ohos.aafwk.ability.Ability;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.Text;
import ohos.agp.components.element.ElementScatter;
import ohos.app.Context;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.IOException;

public class NativeViewFactory {

    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0X10201, NativeViewFactory.class.getSimpleName());

    public static Component createMediumAdView(NativeAd nativeAd, final ComponentContainer parentComponent) {
        LayoutScatter scatter = LayoutScatter.getInstance(parentComponent.getContext());
        Component adRootComponent = scatter.parse(ResourceTable.Layout_native_common_medium_template, null, false);

        final NativeView nativeView = adRootComponent.findComponentById(ResourceTable.Id_native_medium_view);
        nativeView.setTitleView(adRootComponent.findComponentById(ResourceTable.Id_ad_title));
        nativeView.setMediaView((MediaView) adRootComponent.findComponentById(ResourceTable.Id_ad_media));
        nativeView.setAdSourceView(adRootComponent.findComponentById(ResourceTable.Id_ad_source));
        nativeView.setCallToActionView(adRootComponent.findComponentById(ResourceTable.Id_ad_call_to_action));

        // Populate a native ad material view.
        ((Text) nativeView.getTitleView()).setText(nativeAd.getTitle());
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (null != nativeAd.getAdSource()) {
            ((Text) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }

        nativeView.getAdSourceView()
                .setVisibility(null != nativeAd.getAdSource() ? Component.VISIBLE : Component.INVISIBLE);

        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? Component.VISIBLE : Component.INVISIBLE);

        // Obtain a video controller.
        VideoOperator videoOperator = nativeAd.getVideoOperator();

        // Check whether a native ad contains video materials.
        if (videoOperator.hasVideo()) {
            // Add a video lifecycle event listener.
            videoOperator.setVideoLifecycleListener(new VideoOperator.VideoLifecycleListener() {
                @Override
                public void onVideoStart() {
                    HiLog.info(LABEL, "NativeAd video play start.");
                }

                @Override
                public void onVideoPlay() {
                    HiLog.info(LABEL, "NativeAd video playing.");
                }

                @Override
                public void onVideoEnd() {
                    HiLog.info(LABEL, "NativeAd video play end.");
                }
            });
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);

        return nativeView;
    }

    public static Component createSmallImageAdView(NativeAd nativeAd, final ComponentContainer parentComponent) {
        LayoutScatter scatter = LayoutScatter.getInstance(parentComponent.getContext());
        Component adRootView = scatter.parse(ResourceTable.Layout_native_small_image_template, null, false);

        final NativeView nativeView = adRootView.findComponentById(ResourceTable.Id_native_small_view);
        nativeView.setTitleView(adRootView.findComponentById(ResourceTable.Id_ad_title));
        nativeView.setMediaView((MediaView) adRootView.findComponentById(ResourceTable.Id_ad_media));
        nativeView.setAdSourceView(adRootView.findComponentById(ResourceTable.Id_ad_source));
        nativeView.setCallToActionView(adRootView.findComponentById(ResourceTable.Id_ad_call_to_action));

        // Populate a native ad material view.
        ((Text) nativeView.getTitleView()).setText(nativeAd.getTitle());
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (null != nativeAd.getAdSource()) {
            ((Text) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }

        nativeView.getAdSourceView()
                .setVisibility(null != nativeAd.getAdSource() ? Component.VISIBLE : Component.INVISIBLE);

        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? Component.VISIBLE : Component.INVISIBLE);

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);

        return nativeView;
    }

    public static Component createThreeImagesAdView(NativeAd nativeAd, final ComponentContainer parentComponent) {
        LayoutScatter scatter = LayoutScatter.getInstance(parentComponent.getContext());
        Component adRootView = scatter.parse(ResourceTable.Layout_native_three_images_template, null, false);

        final NativeView nativeView = adRootView.findComponentById(ResourceTable.Id_native_three_images);
        nativeView.setTitleView(adRootView.findComponentById(ResourceTable.Id_ad_title));
        nativeView.setAdSourceView(adRootView.findComponentById(ResourceTable.Id_ad_source));
        nativeView.setCallToActionView(adRootView.findComponentById(ResourceTable.Id_ad_call_to_action));

        Image imageView1 = adRootView.findComponentById(ResourceTable.Id_image_view_1);
        Image imageView2 = adRootView.findComponentById(ResourceTable.Id_image_view_2);
        Image imageView3 = adRootView.findComponentById(ResourceTable.Id_image_view_3);

        // Populate a native ad material view.
        ((Text) nativeView.getTitleView()).setText(nativeAd.getTitle());

        if (null != nativeAd.getAdSource()) {
            ((Text) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }

        nativeView.getAdSourceView()
                .setVisibility(null != nativeAd.getAdSource() ? Component.VISIBLE : Component.INVISIBLE);

        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? Component.VISIBLE : Component.INVISIBLE);

        if (nativeAd.getImages() != null && nativeAd.getImages().size() >= 3) {
            Coil.load(imageView1, nativeAd.getImages().get(0).getUri().toString(), (Ability) parentComponent.getContext());
            Coil.load(imageView2, nativeAd.getImages().get(1).getUri().toString(), (Ability) parentComponent.getContext());
            Coil.load(imageView3, nativeAd.getImages().get(2).getUri().toString(), (Ability) parentComponent.getContext());
        }
        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);
        return nativeView;
    }

    public static Component createAppDownloadButtonAdView(NativeAd nativeAd, final ComponentContainer parentComponent) {
        LayoutScatter scatter = LayoutScatter.getInstance(parentComponent.getContext());
        Component adRootView = scatter.parse(ResourceTable.Layout_native_ad_with_app_download_btn_template, null, false);

        final NativeView nativeView = adRootView.findComponentById(ResourceTable.Id_native_app_download_button_view);


        nativeView.setTitleView(adRootView.findComponentById(ResourceTable.Id_ad_title));
        nativeView.setMediaView((MediaView) adRootView.findComponentById(ResourceTable.Id_ad_media));
        nativeView.setAdSourceView(adRootView.findComponentById(ResourceTable.Id_ad_source));
        nativeView.setCallToActionView(adRootView.findComponentById(ResourceTable.Id_ad_call_to_action));

        // Populate a native ad material view.
        ((Text) nativeView.getTitleView()).setText(nativeAd.getTitle());
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        if (null != nativeAd.getAdSource()) {
            ((Text) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }

        nativeView.getAdSourceView()
                .setVisibility(null != nativeAd.getAdSource() ? Component.VISIBLE : Component.INVISIBLE);
        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);

        AppDownloadButton appDownloadButton = nativeView.findComponentById(ResourceTable.Id_app_download_btn);
        appDownloadButton.setAppDownloadButtonStyle(new MyAppDownloadStyle(parentComponent.getContext()));
        if (nativeView.register(appDownloadButton)) {
            appDownloadButton.setVisibility(Component.VISIBLE);
            appDownloadButton.refreshAppStatus();
            nativeView.getCallToActionView().setVisibility(Component.HIDE);
        } else {
            appDownloadButton.setVisibility(Component.HIDE);
            nativeView.getCallToActionView().setVisibility(Component.VISIBLE);
        }

        return nativeView;
    }

    public static Component createImageOnlyAdView(NativeAd nativeAd, final ComponentContainer parentComponent) {
        LayoutScatter scatter = LayoutScatter.getInstance(parentComponent.getContext());
        Component adRootView = scatter.parse(ResourceTable.Layout_native_image_only_template, null, false);

        final NativeView nativeView = adRootView.findComponentById(ResourceTable.Id_native_image_only_view);

        nativeView.setMediaView((MediaView) adRootView.findComponentById(ResourceTable.Id_ad_media));
        nativeView.setCallToActionView(adRootView.findComponentById(ResourceTable.Id_ad_call_to_action));

        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? Component.VISIBLE : Component.INVISIBLE);

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);

        return nativeView;
    }

    /**
     * Custom AppDownloadButton Style
     */
    private static class MyAppDownloadStyle extends AppDownloadButtonStyle {

        public MyAppDownloadStyle(Context context) {
            super(context);
            try {
                ResourceManager resourceManager = context.getResourceManager();
                normalStyle.setTextColor(resourceManager.getElement(ResourceTable.Color_white).getColor());
                normalStyle.setBackground(ElementScatter.getInstance(context).parse(ResourceTable.Graphic_native_button_rounded_corners_shape));
                processingStyle.setTextColor(resourceManager.getElement(ResourceTable.Color_black).getColor());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotExistException e) {
                e.printStackTrace();
            } catch (WrongTypeException e) {
                e.printStackTrace();
            }

        }
    }
}
