package com.huawei.ohos.ads.reward;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.reward.Reward;
import com.huawei.hms.ads.reward.RewardAd;
import com.huawei.hms.ads.reward.RewardAdLoadListener;
import com.huawei.hms.ads.reward.RewardAdStatusListener;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Text;
import ohos.agp.window.dialog.ToastDialog;

import java.util.Random;

public class MainAbility extends Ability {

    private static final int PLUS_SCORE = 1;

    private static final int MINUS_SCORE = 5;

    private static final int RANGE = 2;

    private Text rewardedTitle;

    private Text scoreView;

    private Button reStartButton;

    private Button watchAdButton;

    private RewardAd rewardedAd;

    private int score = 1;

    private final int defaultScore = 10;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        // Initialize the HUAWEI Ads Ohos SDK.
        HwAds.init(this);
        rewardedTitle = (Text) findComponentById(ResourceTable.Id_text_reward);
        rewardedTitle.setText(ResourceTable.String_reward_ad_title);
        // Load a rewarded ad.
        loadRewardAd();
        // Load a score view.
        loadScoreView();
        // Load the button for watching a rewarded ad.
        loadWatchButton();
        // Load the button for starting a game.
        loadPlayButton();
    }

    /**
     * Load a rewarded ad.
     */
    private void loadRewardAd() {
        if (rewardedAd == null) {
            rewardedAd = new RewardAd(getContext(), getString(ResourceTable.String_ad_id_reward));
        }

        RewardAdLoadListener rewardAdLoadListener = new RewardAdLoadListener() {
            @Override
            public void onRewardAdFailedToLoad(int errorCode) {
                showToast("onRewardAdFailedToLoad " + "errorCode is :" + errorCode);
            }

            @Override
            public void onRewardedLoaded() {
                showToast("onRewardedLoaded");
            }
        };

        rewardedAd.loadAd(new AdParam.Builder().build(), rewardAdLoadListener);
    }

    /**
     * Display a rewarded ad.
     */
    private void rewardAdShow() {
        if (rewardedAd.isLoaded()) {
            rewardedAd.show(this, new RewardAdStatusListener() {
                @Override
                public void onRewardAdClosed() {
                    showToast("onRewardAdClosed");

                    loadRewardAd();
                }

                @Override
                public void onRewardAdFailedToShow(int errorCode) {
                    showToast("onRewardAdFailedToShow " + "errorCode is :" + errorCode);
                }

                @Override
                public void onRewardAdOpened() {
                    showToast("onRewardAdOpened");
                }

                @Override
                public void onRewarded(Reward reward) {
                    // You are advised to grant a reward immediately and at the same time, check whether the reward
                    // takes effect on the server. If no reward information is configured, grant a reward based on the
                    // actual scenario.
                    int addScore = reward.getAmount() == 0 ? defaultScore : reward.getAmount();
                    showToast("Watch video show finished , add " + addScore + " scores");
                    score += addScore;
                    setScore(score);
                    loadRewardAd();
                }
            });
        }
    }

    /**
     * Set a score.
     *
     * @param score
     */
    private void setScore(int score) {
        scoreView.setText("Score:" + score);
    }

    /**
     * Load the button for watching a rewarded ad.
     */
    private void loadWatchButton() {
        watchAdButton = findComponentById(ResourceTable.Id_show_video_button);
        watchAdButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                rewardAdShow();
            }
        });
    }

    /**
     * Load the button for starting a game.
     */
    private void loadPlayButton() {
        reStartButton = findComponentById(ResourceTable.Id_play_button);
        reStartButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                play();
            }
        });
    }

    private void loadScoreView() {
        scoreView = findComponentById(ResourceTable.Id_score_count_text);
        scoreView.setText("Score:" + score);
    }

    /**
     * Used to play a game.
     */
    private void play() {
        // If the score is 0, a message is displayed, asking users to watch the ad in exchange for scores.
        if (score == 0) {
            showToast("Watch video ad to add score");
            return;
        }

        // The value 0 or 1 is returned randomly. If the value is 1, the score increases by 1. If the value is 0, the
        // score decreases by 5. If the score is a negative number, the score is set to 0.
        int random = new Random().nextInt(RANGE);
        if (random == 1) {
            score += PLUS_SCORE;
            showToast("You win！");
        } else {
            score -= MINUS_SCORE;
            score = score < 0 ? 0 : score;
            showToast("You lose！");
        }
        setScore(score);
    }

    private void showToast(final String text) {
        getUITaskDispatcher().asyncDispatch(new Runnable() {
            @Override
            public void run() {
                new ToastDialog(getApplicationContext()).setText(text).show();
            }
        });
    }
}
