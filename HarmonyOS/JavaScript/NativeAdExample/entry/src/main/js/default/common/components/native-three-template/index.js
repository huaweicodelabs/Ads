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
        imgUrls: ["", "", ""]
    },
    onInit() {
        this.initImgs();
    },
    initImgs() {
        if (this.adData && this.adData.imgList && this.adData.imgList.length > 0) {
            for (var index = 0; index < this.adData.imgList.length; index++) {
                this.imgUrls[index] = this.adData.imgList[index].url;
            }
        }
    },
    onAdClose() {
        const nativeView = this.$child("native-view");
        if (nativeView && nativeView.onAdClose) {
            nativeView.onAdClose();
        }
        const footerView = this.$child("footer");
        if (footerView && footerView.onAdClose) {
            footerView.onAdClose();
        }
        this.$emit('adClose');
    }
}