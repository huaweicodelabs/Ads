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

const requestBtn = document.querySelector(".request-btn");
let slotRadios = document.getElementsByName("creativeType");

let interstitialAd;

requestBtn.addEventListener(
  "click",
  () => {
    loadInterstitialAd(getSlotId());
  },
  false
);

function getSlotId() {
  for (i = 0; i < slotRadios.length; i++) {
    if (slotRadios[i].checked) {
      return slotRadios[i].value;
    }
  }
  return null;
}

function loadInterstitialAd(slotId) {
  window.ppsads.ready(() => {
    interstitialAd = window.ppsads.createInterstitialAd({
      slotId: slotId,
    });

    interstitialAd.onLoad(() => {
      $Toast.show("onLoad callback");
      showInterstitialAd();
    });
    interstitialAd.onError((errorCode) => {
      $Toast.show("onError: " + errorCode);
    });
    interstitialAd.onShow(() => {
      $Toast.show("onShow callback");
    });
    interstitialAd.onClose(() => {
      $Toast.show("onClose callback");
    });
    interstitialAd.load();
  });
}

function showInterstitialAd() {
  if (interstitialAd && interstitialAd.isLoaded()) {
    interstitialAd.show();
  } else {
    $Toast.show("Ad did not load");
  }
}
