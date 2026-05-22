package com.yousafdev.KidShield.Utils;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    private static final String TAG = "MyDeviceAdminReceiver";

    private void logEnforcementState(@NonNull Context context, @NonNull String event) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

        boolean isAdminActive = dpm != null && dpm.isAdminActive(adminComponent);
        boolean isDeviceOwner = dpm != null && dpm.isDeviceOwnerApp(context.getPackageName());
        boolean isProfileOwner = dpm != null && dpm.isProfileOwnerApp(context.getPackageName());

        Log.i(TAG, event
                + " | adminActive=" + isAdminActive
                + " | deviceOwner=" + isDeviceOwner
                + " | profileOwner=" + isProfileOwner
                + " | sdk=" + Build.VERSION.SDK_INT);
    }

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        logEnforcementState(context, "onEnabled");
        Toast.makeText(context, "Device Admin: Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        logEnforcementState(context, "onDisabled");
        Toast.makeText(context, "Device Admin: Disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordChanged(@NonNull Context context, @NonNull Intent intent) {
        super.onPasswordChanged(context, intent);
        logEnforcementState(context, "onPasswordChanged");
    }

    @Override
    public void onPasswordFailed(@NonNull Context context, @NonNull Intent intent) {
        super.onPasswordFailed(context, intent);
        logEnforcementState(context, "onPasswordFailed");
    }

    @Override
    public void onPasswordSucceeded(@NonNull Context context, @NonNull Intent intent) {
        super.onPasswordSucceeded(context, intent);
        logEnforcementState(context, "onPasswordSucceeded");
    }
}
