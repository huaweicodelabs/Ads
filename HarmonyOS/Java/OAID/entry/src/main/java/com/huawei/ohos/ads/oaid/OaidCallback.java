/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.huawei.ohos.ads.oaid;

public interface OaidCallback {

    void onSuccess(String id,boolean isLimitAdTrackingEnabled);

    void onFail(String content);
}
