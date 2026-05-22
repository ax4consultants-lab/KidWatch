# High-risk permission to code-path mapping

This document maps every high-risk permission declared in `AndroidManifest.xml` to the runtime path that requests/validates/enforces it.

## `android.permission.SYSTEM_ALERT_WINDOW`
- Request path: `ChildSetupActivity.requestOverlay()` launches `Settings.ACTION_MANAGE_OVERLAY_PERMISSION` for this package.
- Validation path: `ChildSetupActivity.checkAllPermissions()` requires `Settings.canDrawOverlays(this)` before setup can complete.
- Enforcement use: `MonitoringService.checkForegroundApp()` launches `BlockedScreenActivity`, which depends on overlay capability for app-block UX.

## `android.permission.PACKAGE_USAGE_STATS`
- Request path: `ChildSetupActivity.requestUsageStats()` launches `Settings.ACTION_USAGE_ACCESS_SETTINGS`.
- Validation path: `ChildSetupActivity.isUsageStatsAllowed()` checks `AppOpsManager.OPSTR_GET_USAGE_STATS` and is required by `checkAllPermissions()` before setup completion.
- Enforcement use: app-blocking flow requires access to foreground-app usage events surfaced by accessibility/usage monitoring.

## `android.permission.QUERY_ALL_PACKAGES`
- Enforcement use: `MonitoringService.fetchAndUploadInstalledApps()` calls `PackageManager.getInstalledApplications(...)` and uploads discovered installed apps.
- Setup gating: same service is started from `ChildSetupActivity` only after required setup steps are completed.
