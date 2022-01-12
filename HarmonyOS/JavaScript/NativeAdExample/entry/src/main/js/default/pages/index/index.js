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

import prompt from '@system.prompt';
// 导入广告API
import ads from '@hw-ads/ohos-ads';

export default {
    data: {
        adId: "testu7m3hc4gvm",
        status: 'init',
        nativeAd: null, // 原生广告对象
        adList: [] // 原生广告素材对象，填充原生广告视图组件
    },
    onRequestAdBtnClickHandler() {
        if (this.status === 'loading') {
            prompt.showToast({
                message: 'Ad loading',
                duration: 2000,
            });
            return;
        } else {
            this.loadAd();
        }
    },
    loadAd() {
        if (!this.adId) {
            prompt.showToast({
                message: 'plead select one display type.',
                duration: 2000,
            });
            return;
        }

        // 创建广告对象并设置广告位等自定义属性
        this.nativeAd = ads.createNativeAd({
            adIds: [this.adId]
        });

        // 设置广告加载成功回调函数，在此回调函数中设置广告素材对象
        this.nativeAd.onLoad((adMap) => {
            this.status = 'loaded';
            this.adList = [];
            prompt.showToast({
                message: 'Ad load success',
                duration: 2000,
            });
            if (adMap) {
                for (let key in adMap) {
                    if (adMap[key]) {
                        this.adList = this.adList.concat(adMap[key]);
                    }
                }
            }
        });

        this.nativeAd.onError((errorCode) => {
            prompt.showToast({
                message: 'Ad load failed:' + errorCode,
                duration: 2000,
            });
            this.status = 'error';
        })
        this.status = 'loading';
        this.nativeAd.load();
    },
    onRadioChange(event) {
        this.adId = event.value;
    }
}
