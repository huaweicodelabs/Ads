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

var AdManager = function () {};

AdManager.prototype.loadAd = function (slotId) {
  this.slotId = slotId;
  if (this.nativeAd) {
    this.nativeAd.destroy();
    this.nativeAd = null;
  }
  window.ppsads.ready(() => {
    this.nativeAd = window.ppsads.createNativeAd({
      slotId: this.slotId,
    });
    this.nativeAd.onLoad((ret) => {
      if (this.onLoadHandler) {
        this.onLoadHandler.call(null);
      }
      this.renderAd(ret);
    });

    this.nativeAd.onError((errorCode) => {
      if (this.onErrorHandler) {
        this.onErrorHandler.call(null, errorCode);
      }
    });
    this.nativeAd.load();
  });
};

// Load ad templates according to creative type
AdManager.prototype.renderAd = function (adList) {
  for (let index = 0; index < adList.length; index++) {
    const item = adList[index];
    if (item && (item.creativeType == 103 || item.creativeType == 3)) {
      this.createBigImgTemplate(item);
    }

    if (item && (item.creativeType == 7 || item.creativeType == 107)) {
      this.createSmallImgTemplate(item);
    }

    if (item && item.creativeType == 6) {
      this.createVideoTemplate(item);
    }

    if (item && item.creativeType == 106) {
      this.createVideoWithAppDownloadBtnTemplate(item);
    }
  }
};

AdManager.prototype.onLoad = function (callBack) {
  this.onLoadHandler = callBack;
};

AdManager.prototype.onError = function (callBack) {
  this.onErrorHandler = callBack;
};

AdManager.prototype.destroy = function () {
  if (this.nativeAd) {
    this.nativeAd.destroy();
    this.nativeAd = null;
  }
  this.onLoadHandler = null;
  this.onErrorHandler = null;
  this.slotId = null;
};

AdManager.prototype.showAdView = function (adView) {
  var adViews = document.getElementsByClassName("tmpl");

  Array.from(adViews).forEach(function (view) {
    if (!view.classList.contains("hidden")) {
      view.classList.add("hidden");
    }
  });
  adView.classList.remove("hidden");
};

AdManager.prototype.createBigImgTemplate = function (item) {
  let content = document.getElementById("content");
  let bigImgTmpl = document.getElementById("native-big-img-template");
  let clone = document.importNode(bigImgTmpl.content, true);
  content.innerHTML = "";
  content.appendChild(clone);
  const nativeView = document.querySelector(".native");
  const titleView = document.querySelector(".native__title");
  const sourceView = document.querySelector(".native__ad-source");
  const flagView = document.querySelector(".native__ad-flag");
  const imgView = document.querySelector(".native__img");
  const btnView = document.querySelector(".native__cta-btn");
  customElements.whenDefined("ads-native").then(() => {
    nativeView.registerNativeAd(item);
  });

  titleView.textContent = item.title;
  sourceView.textContent = item.source;
  flagView.textContent = "AD";
  btnView.textContent = item.clickBtnTxt;
  imgView.src = item.imgList[0].url;
  imgView.onload = () => {
    this.showAdView(nativeView);
  };
};

AdManager.prototype.createSmallImgTemplate = function (adData) {
  let content = document.getElementById("content");
  let smallImgTmpl = document.getElementById("native-small-img-template");
  let clone = document.importNode(smallImgTmpl.content, true);
  content.innerHTML = "";
  content.appendChild(clone);
  const nativeView = document.querySelector(".native");
  const titleView = document.querySelector(".native__title");
  const sourceView = document.querySelector(".native__ad-source");
  const flagView = document.querySelector(".native__ad-flag");
  const imgView = document.querySelector(".native__img");
  const btnView = document.querySelector(".native__cta-btn");
  customElements.whenDefined("ads-native").then(() => {
    nativeView.registerNativeAd(adData);
  });
  titleView.textContent = adData.title;
  sourceView.textContent = adData.source;
  flagView.textContent = "AD";
  btnView.textContent = adData.clickBtnTxt;
  imgView.src = adData.imgList[0].url;
  imgView.onload = () => {
    this.showAdView(nativeView);
  };
};

AdManager.prototype.createVideoTemplate = function (adData) {
  let content = document.getElementById("content");
  let videoBtnTmpl = document.getElementById("native-video-template");
  let clone = document.importNode(videoBtnTmpl.content, true);
  content.innerHTML = "";
  content.appendChild(clone);
  const nativeView = document.querySelector(".native");
  const titleView = document.querySelector(".native__title");
  const sourceView = document.querySelector(".native__ad-source");
  const flagView = document.querySelector(".native__ad-flag");
  const videoView = document.querySelector(".native__video-view");
  const btnView = document.querySelector(".native__cta-btn");
  customElements.whenDefined("ads-native").then(() => {
    nativeView.registerNativeAd(adData, null, videoView);
  });
  titleView.textContent = adData.title;
  sourceView.textContent = adData.source;
  btnView.textContent = adData.clickBtnTxt;
  flagView.textContent = "AD";

  this.showAdView(nativeView);
};

AdManager.prototype.createVideoWithAppDownloadBtnTemplate = function (adData) {
  let content = document.getElementById("content");
  let videoBtnTmpl = document.getElementById("native-video-with-btn-template");
  let clone = document.importNode(videoBtnTmpl.content, true);
  content.innerHTML = "";
  content.appendChild(clone);
  const nativeView = document.querySelector(".native");
  const titleView = document.querySelector(".native__title");
  const sourceView = document.querySelector(".native__ad-source");
  const flagView = document.querySelector(".native__ad-flag");
  const videoView = document.querySelector(".native__video-view");
  const btnView = document.querySelector(".native__app-btn");
  customElements.whenDefined("ads-native").then(() => {
    nativeView.registerNativeAd(adData, null, videoView);
    nativeView.registerAppButton(btnView);
  });
  titleView.textContent = adData.title;
  sourceView.textContent = adData.source;
  flagView.textContent = "AD";

  this.showAdView(nativeView);
};
