/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.huawei.ohos.ads.nativeads;

import com.huawei.hms.ads.HwAds;
import com.huawei.ohos.ads.nativeads.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        // Initialize the HUAWEI Ads Ohos SDK.
        HwAds.init(this);
    }
}
