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

const PLUS_SCORE = 1;
const MINUS_SCORE = 5;
const defaultScore = 10;

export default {
    data: {
        adId: "testx9dtjwj8hp",
        score: 1,
        rewardAd: null,
    },
    onShow() {
        this.loadAd();
    },
    loadAd() {
        // 创建广告对象并设置广告位
        this.rewardAd = ads.createRewardAd({
            adId: this.adId
        });

        // 设置广告加载成功回调函数
        this.rewardAd.onLoad(() => {
            prompt.showToast({
                message: 'Ad load success',
                duration: 2000,
            });
        });
        this.rewardAd.onRequestError((errorCode) => {
            prompt.showToast({
                message: 'Ad load failed' + errorCode,
                duration: 2000,
            });
        });
        this.rewardAd.onReward((rewardData) => {
            // You are advised to grant a reward immediately and at the same time, check whether the reward takes effect on the server.
            // If no reward information is configured, grant a reward based on the actual scenario.
            let addScore = rewardData.amount == 0 ? defaultScore : rewardData.amount;
            prompt.showToast({
                message: "Watch video show finished, add " + addScore + " scores",
                duration: 2000,
            });
            this.setScore(addScore);
            this.loadAd();
        });
        this.rewardAd.onError((errorCode) => {
            prompt.showToast({
                message: 'Ad load failed:' + errorCode,
                duration: 2000,
            });
        })
        this.rewardAd.onShow(() => {
            prompt.showToast({
                message: 'Ad onShow',
                duration: 2000,
            });
        });
        this.rewardAd.onComplete(() => {
            prompt.showToast({
                message: 'Ad onComplete',
                duration: 2000,
            });
        });
        this.rewardAd.onClose(() => {
            prompt.showToast({
                message: 'Ad onClose',
                duration: 2000,
            });
        });
        this.rewardAd.load();
    },
    showAd() {
        if (this.rewardAd && this.rewardAd.isLoaded()) {
            this.rewardAd.show();
        }
    },
    onPlayBtnClickHandler() {
        let curScore = this.score;
        if (curScore == 0) {
            prompt.showToast({
                message: 'Watch video ad to add score',
                duration: 2000,
            });
            return;
        }

        let random = Math.floor(Math.random() * 2);
        if (random == 1) {
            curScore += PLUS_SCORE;
            prompt.showToast({
                message: 'You win！',
                duration: 2000,
            });
        } else {
            curScore -= MINUS_SCORE;
            curScore = curScore < 0 ? 0 : curScore;
            prompt.showToast({
                message: 'You lose！',
                duration: 2000,
            });
        }
        this.setScore(curScore);
    },
    onWatchBtnClickHandler() {
        this.showAd();
    },
    setScore(score) {
        this.score = score;
    }
}
