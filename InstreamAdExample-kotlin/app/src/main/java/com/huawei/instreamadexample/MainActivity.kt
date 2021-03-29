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
package com.huawei.instreamadexample

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.MediaMuteListener
import com.huawei.hms.ads.instreamad.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var videoContent: TextView? = null
    private var skipAd: TextView? = null
    private var countDown: TextView? = null
    private var callToAction: TextView? = null
    private var loadButton: Button? = null
    private var registerButton: Button? = null
    private var muteButton: Button? = null
    private var pauseButton: Button? = null
    private var instreamContainer: RelativeLayout? = null
    private var instreamView: InstreamView? = null
    private var whyThisAd: ImageView? = null
    private var context: Context? = null
    private var maxAdDuration = 0
    private var whyThisAdUrl: String? = null
    private var isMuted = false
    private var adLoader: InstreamAdLoader? = null
    private var instreamAds: MutableList<InstreamAd>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        setTitle(R.string.instream_ad)
        setContentView(R.layout.activity_main)
       
       // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);
       
        initInstreamAdView()
        initButtons()
       
        configAdLoader()
    }

    private val mediaChangeListener = InstreamMediaChangeListener { instreamAd ->
        whyThisAdUrl = null
        whyThisAdUrl = instreamAd.whyThisAd
        Log.i(TAG, "onSegmentMediaChange, whyThisAd: $whyThisAdUrl")
        if (!TextUtils.isEmpty(whyThisAdUrl)) {
            whyThisAd!!.visibility = View.VISIBLE
            whyThisAd!!.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(whyThisAdUrl))) }
        } else {
            whyThisAd!!.visibility = View.GONE
        }
        val cta = instreamAd.callToAction
        if (!TextUtils.isEmpty(cta)) {
            callToAction!!.visibility = View.VISIBLE
            callToAction!!.text = cta
            instreamView!!.callToActionView = callToAction
        }
    }
    private val mediaStateListener: InstreamMediaStateListener = object : InstreamMediaStateListener {
        override fun onMediaProgress(per: Int, playTime: Int) {
            updateCountDown(playTime.toLong())
        }

        override fun onMediaStart(playTime: Int) {
            updateCountDown(playTime.toLong())
        }

        override fun onMediaPause(playTime: Int) {
            updateCountDown(playTime.toLong())
        }

        override fun onMediaStop(playTime: Int) {
            updateCountDown(playTime.toLong())
        }

        override fun onMediaCompletion(playTime: Int) {
            updateCountDown(playTime.toLong())
            removeInstream()
            playVideo()
        }

        override fun onMediaError(playTime: Int, errorCode: Int, extra: Int) {
            updateCountDown(playTime.toLong())
        }
    }
    private val mediaMuteListener: MediaMuteListener = object : MediaMuteListener {
        override fun onMute() {
            isMuted = true
            Toast.makeText(context, "Ad muted", Toast.LENGTH_SHORT).show()
        }

        override fun onUnmute() {
            isMuted = false
            Toast.makeText(context, "Ad unmuted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initInstreamAdView() {
        instreamContainer = findViewById(R.id.instream_ad_container)
        instreamView = InstreamView(applicationContext)
        instreamContainer!!.addView(instreamView, 0)
        videoContent = findViewById(R.id.instream_video_content)
        skipAd = findViewById(R.id.instream_skip)
        skipAd!!.setOnClickListener(View.OnClickListener { removeInstream() })
        countDown = findViewById(R.id.instream_count_down)
        callToAction = findViewById(R.id.instream_call_to_action)
        whyThisAd = findViewById(R.id.instream_why_this_ad)
        instreamView!!.setInstreamMediaChangeListener(mediaChangeListener)
        instreamView!!.setInstreamMediaStateListener(mediaStateListener)
        instreamView!!.setMediaMuteListener(mediaMuteListener)
        instreamView!!.setOnInstreamAdClickListener { Toast.makeText(context, "instream clicked.", Toast.LENGTH_SHORT).show() }
    }

    private fun removeInstream() {
        if (null != instreamView) {
            instreamView!!.onClose()
            instreamView!!.destroy()
            instreamContainer!!.removeView(instreamView)
            instreamContainer!!.visibility = View.GONE
            instreamAds!!.clear()
        }
    }

    private val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.instream_load -> if (instreamView!!.isPlaying) {
                Toast.makeText(context, getString(R.string.instream_ads_playing), Toast.LENGTH_SHORT).show()
            } else if (null != adLoader) {
                initInstreamAdView()
                loadButton!!.text = getString(R.string.instream_loading)
                adLoader!!.loadAd(AdParam.Builder().build())
            }
            R.id.instream_register -> if (null == instreamAds || instreamAds!!.size == 0) {
                playVideo()
            } else if (instreamView!!.isPlaying) {
                Toast.makeText(context, getString(R.string.instream_ads_playing), Toast.LENGTH_SHORT).show()
            } else {
                playInstreamAds(instreamAds!!)
            }
            R.id.instream_mute -> if (isMuted) {
                instreamView!!.unmute()
                muteButton!!.text = getString(R.string.instream_mute)
            } else {
                instreamView!!.mute()
                muteButton!!.text = getString(R.string.instream_unmute)
            }
            R.id.instream_pause_play -> if (instreamView!!.isPlaying) {
                instreamView!!.pause()
                pauseButton!!.text = getString(R.string.instream_play)
            } else {
                instreamView!!.play()
                pauseButton!!.text = getString(R.string.instream_pause)
            }
            else -> {
            }
        }
    }

    private fun initButtons() {
        loadButton = findViewById(R.id.instream_load)
        registerButton = findViewById(R.id.instream_register)
        muteButton = findViewById(R.id.instream_mute)
        pauseButton = findViewById(R.id.instream_pause_play)
        loadButton!!.setOnClickListener(clickListener)
        registerButton!!.setOnClickListener(clickListener)
        muteButton!!.setOnClickListener(clickListener)
        pauseButton!!.setOnClickListener(clickListener)
    }

    private val instreamAdLoadListener: InstreamAdLoadListener = object : InstreamAdLoadListener {
        override fun onAdLoaded(ads: MutableList<InstreamAd>) {
            if (null == ads || ads.size == 0) {
                playVideo()
                return
            }
            val it = ads.iterator()
            while (it.hasNext()) {
                val ad = it.next()
                if (ad.isExpired) {
                    it.remove()
                }
            }
            if (ads.size == 0) {
                playVideo()
                return
            }
            loadButton!!.text = getString(R.string.instream_loaded)
            instreamAds = ads
            Toast.makeText(context, "onAdLoaded, ad size: " + ads.size + ", click REGISTER to play.", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailed(errorCode: Int) {
            Log.w(TAG, "onAdFailed: $errorCode")
            loadButton!!.text = getString(R.string.instream_load)
            Toast.makeText(context, "onAdFailed: $errorCode", Toast.LENGTH_SHORT).show()
            playVideo()
        }
    }

    private fun configAdLoader() {
        /**
         * if the maximum total duration is 60 seconds and the maximum number of roll ads is eight,
         * at most four 15-second roll ads or two 30-second roll ads will be returned.
         * If the maximum total duration is 120 seconds and the maximum number of roll ads is four,
         * no more roll ads will be returned after whichever is reached.
         */
        val totalDuration = 60
        val maxCount = 4
        val builder = InstreamAdLoader.Builder(context, getString(R.string.instream_ad_id))
        adLoader = builder.setTotalDuration(totalDuration)
                .setMaxCount(maxCount)
                .setInstreamAdLoadListener(instreamAdLoadListener)
                .build()
    }

    // play your normal video content.
    private fun playVideo() {
        hideAdViews()
        videoContent!!.setText(R.string.instream_normal_video_playing)
    }

    private fun hideAdViews() {
        instreamContainer!!.visibility = View.GONE
    }

    private fun playInstreamAds(ads: List<InstreamAd>) {
        maxAdDuration = getMaxInstreamDuration(ads)
        instreamContainer!!.visibility = View.VISIBLE
        loadButton!!.text = getString(R.string.instream_load)
        instreamView!!.setInstreamAds(ads)
    }

    private fun updateCountDown(playTime: Long) {
        val time = Math.round((maxAdDuration - playTime) / 1000.toFloat()).toString()
        runOnUiThread { countDown!!.text = time + "s" }
    }

    private fun getMaxInstreamDuration(ads: List<InstreamAd>): Int {
        var duration = 0
        for (ad in ads) {
            duration += ad.duration.toInt()
        }
        return duration
    }

    override fun onPause() {
        super.onPause()
        if (null != instreamView && instreamView!!.isPlaying) {
            instreamView!!.pause()
            pauseButton!!.text = getText(R.string.instream_play)
        }
    }

    override fun onResume() {
        super.onResume()
        if (null != instreamView && !instreamView!!.isPlaying) {
            instreamView!!.play()
            pauseButton!!.text = getText(R.string.instream_pause)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != instreamView) {
            instreamView!!.removeInstreamMediaStateListener()
            instreamView!!.removeInstreamMediaChangeListener()
            instreamView!!.removeMediaMuteListener()
            instreamView!!.destroy()
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
