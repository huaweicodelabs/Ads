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

loadBannerAd();

loadBannerAdByApi();

function loadBannerAd() {
  customElements.whenDefined("ads-banner").then(() => {
    const bannerView = document.getElementById("banner-view");
    bannerView.load();
    bannerView.onAdLoad(() => {
      $Toast.show("Ad loaded");
    });
    bannerView.onAdError((errorCode) => {
      $Toast.show("Ad failed: " + errorCode);
    });
  });
}

function loadBannerAdByApi() {
  const bannerContainer = document.getElementById("bottom");
  window.ppsads.ready(() => {
    const bannerAd = window.ppsads.createBannerAd({
      slotId: "testw6vs28auh3",
      width: 360,
      height: 57,
      container: bannerContainer,
    });
    bannerAd.load();
    bannerAd.onLoad(() => {
      $Toast.show("Ad loaded");
    });
    bannerAd.onError((errorCode) => {
      $Toast.show("Ad failed: " + errorCode);
    });
  });
}