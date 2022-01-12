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
        adId: "teste9ih9j0rc3",
        status: 'init',
        interstitialAd: null, // 原生广告对象
    },
    onRequestAdBtnClickHandler() {
        if (this.status === 'loading') {
            prompt.showToast({
                message: 'Ad loading',
                duration: 2000,
            });
            return;
        } else if (this.status === 'loaded') {
            this.showAd();
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

        // 创建广告对象并设置广告位
        this.interstitialAd = ads.createInterstitialAd({
            adId: this.adId
        });

        // 设置广告加载成功回调函数，在此回调函数中设置广告素材对象
        this.interstitialAd.onLoad(() => {
            this.status = 'loaded';
            prompt.showToast({
                message: 'Ad load success',
                duration: 2000,
            });
        });
        this.interstitialAd.onRequestError((errorCode) => {
            this.status = 'error';
            prompt.showToast({
                message: 'Ad load failed' + errorCode,
                duration: 2000,
            });
        });

        this.interstitialAd.onError((errorCode) => {
            prompt.showToast({
                message: 'Ad load failed:' + errorCode,
                duration: 2000,
            });
            this.status = 'error';
        })
        this.interstitialAd.onShow(() => {
            this.status = 'shown';
            prompt.showToast({
                message: 'Ad onShow',
                duration: 2000,
            });
        });
        this.interstitialAd.onComplete(() => {
            prompt.showToast({
                message: 'Ad onComplete',
                duration: 2000,
            });
        });
        this.interstitialAd.onClose(() => {
            prompt.showToast({
                message: 'Ad onClose',
                duration: 2000,
            });
        });
        this.status = 'loading';
        this.interstitialAd.load();
    },
    onRadioChange(event) {
        this.adId = event.value;
    },
    showAd() {
        if (this.interstitialAd && this.interstitialAd.isLoaded()) {
            this.interstitialAd.show();
        }
    }
}
