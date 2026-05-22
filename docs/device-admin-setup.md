# Device Admin / Device Owner Setup

This project supports two setup tracks for policy enforcement depending on what you need to validate.

## Track A: Device Admin activation from `ChildSetupActivity` UI

Use this for normal app-level onboarding (no factory reset required).

1. Open the child app and navigate to setup (`ChildSetupActivity`).
2. Tap the **Device Admin** permission step.
3. The app launches `DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN` with
   `MyDeviceAdminReceiver` as `EXTRA_DEVICE_ADMIN`.
4. Accept activation in Android's system confirmation screen.
5. Return to setup; the Device Admin step should show as granted.

### Expected scope in Device Admin mode

- `force-lock` is available.
- Password/login-related callbacks are logged (`watch-login`, password event hooks).
- Device Owner-only policies are **not** available in this mode.

## Track B: Device Owner provisioning via ADB (factory-reset test device)

Use this only on dedicated test hardware/emulators when you need Device Owner controls.

### Preconditions (strict)

- Device must be in a **fresh/factory-reset state**.
- Run before normal account provisioning/enrollment is completed.
- USB debugging must be enabled for ADB access.

### Provisioning commands

Use package/component values as needed for your build variant.

```bash
adb devices
adb shell dpm set-device-owner com.yousafdev.KidShield/.Utils.MyDeviceAdminReceiver
```

Optional verification:

```bash
adb shell dpm list device-owners
adb shell dumpsys device_policy
```

## Constraints, failure modes, and cleanup for test loops

### Common failure modes

- `set-device-owner` fails if the device is not fresh (already provisioned or has existing accounts).
- Fails if another app is already Device Owner.
- Fails if the receiver component/package name does not match the installed APK.

### Cleanup / reset options

For repeatable testing, use one of these:

1. **Factory reset** (recommended for reliable DO retests).
2. Remove test users/profiles and clear provisioning state (device/OS dependent).
3. If an owner is already present, remove that owner before retrying when possible:

```bash
adb shell dpm remove-active-admin com.yousafdev.KidShield/.Utils.MyDeviceAdminReceiver
```

> Note: On many builds, full Device Owner reprovisioning still requires a factory reset even after admin removal.
