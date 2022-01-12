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

export default {
    props: ["adData"],
    data: {
        isAdSign: false
    },
    onInit() {
        if (this.adData && this.adData.adSign == 2) {
            return this.isAdSign = true;
        } else {
            return false;
        }
    },
    computed: {
        isAppDownload() {
            if ((this.adData && this.adData.creativeType > 100)
            || (this.adData && this.adData.app)) {
                return true;
            }
            return false;
        }
    },
    onReady() {
        const nativeView = this.$parent().$child("native-view");
        const appBtn = this.$child("app-btn");
        if (nativeView && nativeView.setAppDownloadButton && appBtn) {
            nativeView.setAppDownloadButton(appBtn);
        }
    },
    cancelDownload() {
        const appBtn = this.$child("app-btn");
        if (appBtn && appBtn.cancel) {
            appBtn.cancel();
        }
    },
    onAdClose() {
        this.cancelDownload();
    },
    closeAd() {
        this.$emit('adClose');
    }
}
