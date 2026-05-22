package com.yousafdev.KidShield.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;

import com.yousafdev.KidShield.Activities.BlockedScreenActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AppAccessibilityService extends AccessibilityService {

    private static final String TAG = "AppAccessibilityService";
    private static final String APP_PACKAGE = "com.yousafdev.KidShield";
    private static final Set<String> DEFAULT_BLOCKED_PACKAGES = new HashSet<>(Arrays.asList(
            "com.google.android.youtube"
    ));
    public static final String ACTION_FOREGROUND_APP = "com.yousafdev.KidShield.ACTION_FOREGROUND_APP";
    public static final String EXTRA_PACKAGE_NAME = "packageName";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }

        int eventType = event.getEventType();
        if (eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return;
        }

        CharSequence packageNameChar = event.getPackageName();
        if (packageNameChar == null) {
            return;
        }

        String packageName = packageNameChar.toString();
        if (packageName.isEmpty()) {
            return;
        }

        Log.d(TAG, "Foreground App: " + packageName);

        Intent intent = new Intent(ACTION_FOREGROUND_APP);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        sendBroadcast(intent);

        if (isOwnPackage(packageName) || isBlockedScreen(event, packageName)) {
            return;
        }

        if (DEFAULT_BLOCKED_PACKAGES.contains(packageName)) {
            boolean sentHome = performGlobalAction(GLOBAL_ACTION_HOME);
            Log.d(TAG, "Immediate HOME enforcement for package " + packageName + ": " + sentHome);

            Intent blockedIntent = new Intent(getApplicationContext(), BlockedScreenActivity.class);
            blockedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                    | Intent.FLAG_ACTIVITY_NO_HISTORY);
            blockedIntent.putExtra(EXTRA_PACKAGE_NAME, packageName);
            startActivity(blockedIntent);
        }
    }

    private boolean isOwnPackage(String packageName) {
        return APP_PACKAGE.equals(packageName);
    }

    private boolean isBlockedScreen(AccessibilityEvent event, String packageName) {
        if (!APP_PACKAGE.equals(packageName) || event.getClassName() == null) {
            return false;
        }
        String className = event.getClassName().toString();
        return BlockedScreenActivity.class.getName().equals(className);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        Log.d(TAG, "Accessibility Service Connected");
    }
}
