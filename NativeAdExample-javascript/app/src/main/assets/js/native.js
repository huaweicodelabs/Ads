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
const slotRadios = document.getElementsByName("creativeType");
let isLoading = false;

var adManager = new AdManager();

adManager.onLoad(() => {
  updateStatus("Ad loading state: loaded successfully.", true);
});

adManager.onError((errorCode) => {
  updateStatus(
    "Ad loading state: failed to be loaded. Error code: " + errorCode,
    true
  );
});

// Fire load ad
adManager.loadAd(getSlotId());

// Add a click listener to trigger ad load
requestBtn.addEventListener(
  "click",
  () => {
    if (isLoading) {
      updateStatus("Ad loading state: being loaded.", false);
      return;
    }
    if (adManager) {
      updateStatus("Ad loading state: being loaded.", false);
      adManager.loadAd(getSlotId());
    }
  },
  false
);

/**
 * Update tip and status of the load button.
 *
 * @param text           tip.
 * @param requestBtnEnabled status of the load button.
 */
function updateStatus(text, requestBtnEnabled) {
  if (text) {
    $Toast.show(text);
  }
  isLoading = !requestBtnEnabled;
  if (requestBtnEnabled) {
    requestBtn.classList.remove("disabled");
    requestBtn.disabled = false;
  } else {
    requestBtn.classList.add("disabled");
    requestBtn.disabled = true;
  }
}

/**
 * Initialize ad slot ID.
 *
 * @return ad slot ID
 */
function getSlotId() {
  var radioValue = "";
  for (i = 0; i < slotRadios.length; i++) {
    if (slotRadios[i].checked) {
      radioValue = slotRadios[i].value;
      break;
    }
  }
  return radioValue;
}
