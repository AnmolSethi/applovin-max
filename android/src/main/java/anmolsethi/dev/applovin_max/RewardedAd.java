package anmolsethi.dev.applovin_max;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;

public class RewardedAd implements MaxRewardedAdListener {
    private static final String TAG = "MAXRewardedAd";
    private final Activity activity;
    private final MethodChannel adChannel;

    private MaxRewardedAd rewardedAd;
    private int retryAttempt = 0;

    public RewardedAd(BinaryMessenger messenger, Activity activity) {
        this.activity = activity;
        this.adChannel = new MethodChannel(messenger, "rewardedAd");
    }

    public boolean loadAndShowAd(Map<?, ?> args) {
        final String adUnit = (String) args.get(Constants.AD_UNIT);
        if (adUnit == null) {
            return false;
        }

        Log.i(TAG, "Loading MAXRewardedAd with placement id: " + adUnit);
        rewardedAd = MaxRewardedAd.getInstance(adUnit, activity);
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
        return true;
    }

    @Override
    public void onRewardedVideoStarted(MaxAd ad) {
        Log.i(TAG, "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {
        Log.i(TAG, "onRewardedVideoCompleted");
    }

    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {
        Log.i(TAG, "User Rewarded: " + ad.toString());
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put(Constants.AD_UNIT, rewardedAd.getAdUnitId());
        adChannel.invokeMethod("onUserRewarded", arguments);
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        Log.i(TAG, "Ad Loaded: " + ad.toString());
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put(Constants.AD_UNIT, rewardedAd.getAdUnitId());
        adChannel.invokeMethod("onAdLoaded", arguments);

        rewardedAd.showAd();
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        Log.i(TAG, "onAdDisplayed");
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put(Constants.AD_UNIT, rewardedAd.getAdUnitId());
        adChannel.invokeMethod("onAdHidden", arguments);
    }

    @Override
    public void onAdClicked(MaxAd ad) {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put(Constants.AD_UNIT, rewardedAd.getAdUnitId());
        adChannel.invokeMethod("onAdClicked", arguments);
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        Log.e(TAG, "Error: " + error.toString());
        int maxLimit = 2;
        if (retryAttempt <= maxLimit) {
            Log.i(TAG, "Ad retrying after : " + retryAttempt);
            retryAttempt++;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rewardedAd.loadAd();
                }
            }, 300);
        } else {
            Map<String, Object> arguments = new HashMap<>();
            arguments.put(Constants.AD_UNIT, rewardedAd.getAdUnitId());
            arguments.put("errorCode", error.toString());
            adChannel.invokeMethod("onAdLoadFailed", arguments);
        }
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        Log.e(TAG, "onAdDisplayFailed");
    }
}