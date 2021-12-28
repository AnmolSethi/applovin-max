package anmolsethi.dev.applovin_max;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * ApplovinMaxPlugin
 */
public class ApplovinMaxPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel channel;

    private static final String TAG = "ApplovinMaxPlugin";

    private Activity activity;
    private BinaryMessenger binaryMessenger;

    public RewardedAd rewardedAd;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "applovin_max");
        channel.setMethodCallHandler(this);
        binaryMessenger = flutterPluginBinding.getBinaryMessenger();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "init":
                initialize();
                result.success(true);
                break;
            case "loadRewardedAd":
                rewardedAd = new RewardedAd(binaryMessenger, activity);
                result.success(rewardedAd.loadAndShowAd((Map<String, ?>) call.arguments));
                break;
            default:
                result.notImplemented();
        }

    }

    private void initialize() {
        AppLovinSdk.getInstance(activity).setMediationProvider("max");
        AppLovinSdk.initializeSdk(activity, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                Log.i(TAG, "SDK Initialized: " + configuration.toString());
            }
        });
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivity() {
    }
}
