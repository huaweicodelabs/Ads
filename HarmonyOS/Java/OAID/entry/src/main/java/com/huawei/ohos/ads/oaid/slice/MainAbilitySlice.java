/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.huawei.ohos.ads.oaid.slice;

import com.huawei.hms.ads.identifier.AdvertisingIdClient;
import com.huawei.ohos.ads.oaid.OaidCallback;
import com.huawei.ohos.ads.oaid.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Text;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.IOException;

public class MainAbilitySlice extends AbilitySlice {

    private static final HiLogLabel Label = new HiLogLabel(HiLog.LOG_APP, 0X00201, "MainAbilitySlice");
    private Context mContext;

    private Button oaidBtn;

    private Text oaidTv;

    private Text limitTrackTv;

    @Override
    public void onStart(Intent intent) {

        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        mContext = getApplicationContext();
        initView();
        setOaidBTnListener();
    }

    private void initView() {
        oaidBtn = (Button) findComponentById(ResourceTable.Id_get_id_limit_btn);
        oaidTv = (Text) findComponentById(ResourceTable.Id_adid_tv);
        limitTrackTv = (Text) findComponentById(ResourceTable.Id_limit_tv);
    }

    private void setOaidBTnListener() {
        if (null == oaidBtn) {
            return;
        }
        oaidBtn.setClickedListener(component -> getOaid(this, new OaidCallback() {
            @Override
            public void onSuccess(String id, boolean isLimitAdTrackingEnabled) {
                // 更新ui
                getUITaskDispatcher().asyncDispatch(() -> setOaid(id, isLimitAdTrackingEnabled));
            }

            @Override
            public void onFail(String content) {
                getUITaskDispatcher().asyncDispatch(() -> new ToastDialog(getApplicationContext()).setText(content).show());
            }
        }));
    }

    private void getOaid(Context context, final OaidCallback callback) {
        if (null == context || null == callback) {
            HiLog.error(Label, "invalid input param");
            return;
        }
        new Thread(() -> {
            try {
                // 获取OAID信息，请勿在主线程中调用该方法
                AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(context);
                if (null != info) {
                    callback.onSuccess(info.getId(), info.isLimitAdTrackingEnabled());
                } else {
                    callback.onFail("oaid is null");
                }
            } catch (IOException e) {
                HiLog.error(Label, "getAdvertisingIdInfo IOException");
                callback.onFail("getAdvertisingIdInfo IOException");
            }
        }).start();
    }


    private void setOaid(String id, boolean limitAdTrackingEnabled) {
        oaidTv.setText(id);
        limitTrackTv.setText(String.valueOf(limitAdTrackingEnabled));
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
