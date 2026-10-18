# Implementation Plan - Phase 3: Background Watchdog & Health Check Component

## User Review Required
> [!IMPORTANT]
> This plan implements an always-running background watchdog (`WatchdogWorker`), circuit-breaker crash-loop protection (max 3 consecutive failures), dedicated watchdog notification channel (`channel_watchdog`), and a bottom Settings health check dashboard with manual stop protection and confirmation dialogs.

## Proposed Phased Breakdown

### Part 1: Watchdog Worker & State Tracking (`WatchdogWorker.kt`, `SettingsRepository.kt`, `SacredTimelineApp.kt`)
- **Scope**:
  - Add `channel_watchdog` notification channel in `SacredTimelineApp.kt`.
  - Add manual stop flags and consecutive failure counters in `SettingsRepository.kt`.
  - Build `WatchdogWorker.kt` to query WorkManager work states (`WidgetUpdateWorker`, `ReminderWorker`), auto-restart failed tasks (unless manually stopped), enforce circuit breaker (max 3 failures), and dispatch notifications on recovery or critical failure.

### Part 2: Settings Health Check Dashboard UI (`SettingsScreen.kt`)
- **Scope**:
  - Add a Health Check section at the bottom of `SettingsScreen.kt`.
  - Display Primary Profile name at the top.
  - Render component status cards (`WidgetUpdateWorker`, `ReminderWorker`) with live state badges and **Restart** / **Stop** / **Resume** buttons with confirmation dialogs.

### Part 3: Verification & Build
- **Scope**:
  - Run unit tests: `gradle_build(":app:testDebugUnitTest")`.
  - Assemble debug APK: `gradle_build(":app:assembleDebug")`.
