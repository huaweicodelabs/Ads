/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.huawei.ohos.ads.banner;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.DependentLayout;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        // 初始化HUAWEI Ads SDK
        HwAds.init(this);

        // 初始化HUAWEI Ads Ohos SDK
        HwAds.init(this);
        // 获取XML中配置的BannerView
        BannerView bottomBannerView = findComponentById(ResourceTable.Id_hw_banner_view);
        AdParam adParam = new AdParam.Builder().build();
        bottomBannerView.loadAd(adParam);

        // 通过编程的方式添加
        BannerView topBannerView = new BannerView(this);
        topBannerView.setAdId("testw6vs28auh3");
        topBannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_SMART);
        topBannerView.loadAd(adParam);

        DependentLayout rootView = findComponentById(ResourceTable.Id_root_view);
        rootView.addComponent(topBannerView);
    }
}
