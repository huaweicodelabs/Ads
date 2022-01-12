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
    data() {
        return {
            adShow: true
        }
    },
    computed: {
        isBigImg() {
            return this.adData && (this.adData.creativeType == 3 ||
            this.adData.creativeType == 2 ||
            this.adData.creativeType == 4 ||
            this.adData.creativeType == 102 ||
            this.adData.creativeType == 103 ||
            this.adData.creativeType == 104);
        },
        isSmallImg() {
            return this.adData && (this.adData.creativeType == 7 ||
            this.adData.creativeType == 107 ||
            this.adData.creativeType == 10 ||
            this.adData.creativeType == 110);
        },
        isThreeImg() {
            return this.adData && (this.adData.creativeType == 8 || this.adData.creativeType == 108);
        },
        isVideo() {
            return this.adData && (this.adData.creativeType == 9 ||
            this.adData.creativeType == 6 ||
            this.adData.creativeType == 106 ||
            this.adData.creativeType == 109);
        }
    },
    onAdClose() {
        this.adShow = false;
    }
}
