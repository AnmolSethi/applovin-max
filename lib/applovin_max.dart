import 'dart:async';

import 'package:flutter/services.dart';

class ApplovinMax {
  static const MethodChannel _channel = MethodChannel('applovin_max');
  static const MethodChannel _loadRewardedChannel = MethodChannel('rewardedAd');

  /// Initialize ApplovinMax SDK
  static Future<bool?> initialize() async {
    try {
      await _channel.invokeMethod("init");
    } on PlatformException {
      return false;
    }
  }

  /// Load Rewarded Ad
  ///
  /// [adUnit] placement identifier, as defined in Unity Ads Dashboard
  /// [adState] - current state of the ad
  /// [listener] - callback for ad state changes
  static Future<void> loadRewardedAd({
    required String adUnit,
    required Function(MAXADState, dynamic) listener,
  }) async {
    try {
      _loadRewardedChannel.setMethodCallHandler(
        (call) => _adCallHandler(call, listener),
      );

      await _channel.invokeMethod("loadRewardedAd", {"adUnit": adUnit});
    } on PlatformException {
      return;
    }
  }

  static Future<bool?> isReady() async {
    try {
      final result = await _channel.invokeMethod("isReady");
      return result;
    } on PlatformException {
      return false;
    }
  }

  static Future<dynamic> _adCallHandler(
      MethodCall call, Function(MAXADState, dynamic) listener) {
    print("AppLovinMax: _adCallHandler: ${call.method}");
    switch (call.method) {
      case "onAdLoaded":
        print("AppLovinMax: switchcase: onAdLoaded");
        listener(MAXADState.onAdLoaded, call.arguments);
        break;
      case "onAdLoadFailed":
        listener(MAXADState.onAdLoadFailed, call.arguments);
        break;
      case "onUserRewarded":
        listener(MAXADState.onUserRewarded, call.arguments);
        break;
      case "onAdClicked":
        listener(MAXADState.onAdClicked, call.arguments);
        break;
      case "onAdDisplayFailed":
        listener(MAXADState.onAdDisplayFailed, call.arguments);
        break;
    }
    return Future.value(true);
  }
}

enum MAXADState {
  onRewardedVideoStarted,
  onRewardedVideoCompleted,
  onUserRewarded,
  onAdLoaded,
  onAdDisplayed,
  onAdHidden,
  onAdClicked,
  onAdLoadFailed,
  onAdDisplayFailed
}
