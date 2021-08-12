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

let score = 1;
const PLUS_SCORE = 1;
const MINUS_SCORE = 5;
const defaultScore = 10;

const playBtn = document.querySelector(".play-btn");
const showRewardBtn = document.querySelector(".reward-btn");
const scoreView = document.querySelector(".score-area");

let rewardAd;

loadRewardAd();
setScore(score);

playBtn.addEventListener(
  "click",
  () => {
    if (score == 0) {
      $Toast.show("Watch video ad to add score");
      return;
    }

    let random = Math.floor(Math.random() * 2);
    if (random == 1) {
      score += PLUS_SCORE;
      $Toast.show("You win！");
    } else {
      score -= MINUS_SCORE;
      score = score < 0 ? 0 : score;
      $Toast.show("You lose！");
    }
    setScore(score);
  },
  false
);

showRewardBtn.addEventListener(
  "click",
  () => {
    showRewardAd();
  },
  false
);

function showRewardAd() {
  if (rewardAd && rewardAd.isLoaded()) {
    rewardAd.show();
  } else {
    $Toast.show("Ad did not load");
  }
}

function setScore(score) {
  scoreView.innerHTML = "Score:" + score;
}

function loadRewardAd() {
  window.ppsads.ready(() => {
    rewardAd = window.ppsads.createRewardAd({
      slotId: "testx9dtjwj8hp",
    });
    rewardAd.onLoad(() => {
      $Toast.show("onLoad callback");
    });
    rewardAd.onError((errorCode) => {
      $Toast.show("onError: " + errorCode);
    });
    rewardAd.onShow(() => {
      $Toast.show("onShow callback");
    });

    rewardAd.onComplete(() => {
      $Toast.show("onComplete");
    });

    rewardAd.onClose(() => {
      $Toast.show("onClose callback");
    });

    rewardAd.onReward((rewardData) => {
      // You are advised to grant a reward immediately and at the same time, check whether the reward takes effect on the server.
      // If no reward information is configured, grant a reward based on the actual scenario.
      let addScore = rewardData.amount == 0 ? defaultScore : rewardData.amount;
      $Toast.show("Watch video show finished, add " + addScore + " scores");
      setScore(addScore);
      loadRewardAd();
    });
    rewardAd.load();
  });
}
