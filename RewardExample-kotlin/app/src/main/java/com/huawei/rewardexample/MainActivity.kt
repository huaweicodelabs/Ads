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
package com.huawei.rewardexample

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.reward.Reward
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener

class MainActivity : AppCompatActivity() {
    private var watchAdButton: Button? = null
    private var rewardAd: RewardAd? = null
    private var scoreView: TextView? = null
    private var score = 1
    private val defaultScore = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this)

        // Load a rewarded ad.
        loadRewardAd()

        // Load the button for watching a rewarded ad.
        loadWatchVideoButton()

        // Load a score view.
        loadScoreView()
    }

    private fun createRewardAd() {
        // testx9dtjwj8hp indicates a test ad unit ID.
        rewardAd = RewardAd(this@MainActivity, getString(R.string.reward_ad_id))
    }

    private fun loadRewardAd() {
        if (rewardAd == null) {
            createRewardAd()
        }
        val rewardAdLoadListener: RewardAdLoadListener = object : RewardAdLoadListener() {
            override fun onRewardAdFailedToLoad(errorCode: Int) {
                Toast.makeText(this@MainActivity, "onRewardAdFailedToLoad errorCode is :$errorCode", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedLoaded() {
                Toast.makeText(this@MainActivity, "onRewardedLoaded", Toast.LENGTH_SHORT).show()
            }
        }
        rewardAd!!.loadAd(AdParam.Builder().build(), rewardAdLoadListener)
    }

    /**
     * Load the button for watching a rewarded ad.
     */
    private fun loadWatchVideoButton() {
        watchAdButton = findViewById(R.id.show_video_button)
        watchAdButton!!.setOnClickListener(View.OnClickListener { rewardAdShow() })
    }

    private fun loadScoreView() {
        scoreView = findViewById(R.id.coin_count_text)
        scoreView!!.setText("Score:$score")
    }

    /**
     * Display a rewarded ad.
     */
    private fun rewardAdShow() {
        if (rewardAd!!.isLoaded) {
            rewardAd!!.show(this@MainActivity, object : RewardAdStatusListener() {
                override fun onRewardAdClosed() {
                    loadRewardAd()
                }

                override fun onRewardAdFailedToShow(errorCode: Int) {
                    Toast.makeText(this@MainActivity, "onRewardAdFailedToShow errorCode is :$errorCode", Toast.LENGTH_SHORT).show()
                }

                override fun onRewardAdOpened() {
                    Toast.makeText(this@MainActivity, "onRewardAdOpened", Toast.LENGTH_SHORT).show()
                }

                override fun onRewarded(reward: Reward) {
                    // You are advised to grant a reward immediately and at the same time, check whether the reward takes effect on the server.
                    // If no reward information is configured, grant a reward based on the actual scenario.
                    val addScore = if (reward.amount == 0) defaultScore else reward.amount
                    Toast.makeText(this@MainActivity, "Watch video show finished, add $addScore scores", Toast.LENGTH_SHORT).show()
                    addScore(addScore)
                    loadRewardAd()
                }
            })
        }
    }

    private fun addScore(addScore: Int) {
        score += addScore
        scoreView!!.text = "Score:$score"
    }
}
