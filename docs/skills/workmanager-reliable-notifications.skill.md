---
name: workmanager-reliable-notifications
description: Provides architectural guidelines and implementation patterns for building robust, OEM-resilient background notifications and timed alerts on Android using Jetpack WorkManager (CoroutineWorker), atomic JSON queue storage, WakeLocks, auto-rescheduling recurrence engines, and 1-to-1 pre-created notification channels.
---

# WorkManager Reliable Notifications & Background Alarms

## Overview
When building notification and alarm systems on Android, raw `AlarmManager` broadcasts frequently fail on aggressive OEM ROMs (like Tecno HiOS, Xiaomi MIUI, or Samsung OneUI) due to background power management and missing exact alarm permissions. This skill outlines the production-ready pattern for 100% reliable background notifications using Jetpack WorkManager.

## Core Architecture

### 1. WorkManager Worker (`CoroutineWorker`)
- Instead of raw `BroadcastReceiver` alarms, schedule background tasks using WorkManager's `OneTimeWorkRequestBuilder` with initial delay.
- WorkManager automatically provides built-in CPU WakeLocks, ensuring the notification is posted successfully even in Doze mode or when the app is in the background.

### 2. Pre-Created 1-to-1 Notification Channels
- Pre-create dedicated notification channels on app startup (`Application.onCreate()`) mapped 1-to-1 to each event category (e.g. `channel_nakshatra`, `channel_chandrashtamam`, `channel_default`).
- Ensures channels exist and have `NotificationManager.IMPORTANCE_HIGH` before any worker fires.

### 3. Recurrence Engine
- For recurring notifications ("All Occurrences" / daily / monthly / annual), handle auto-rescheduling directly inside `CoroutineWorker.doWork()` by calculating the next occurrence date/time and queueing the next unique work request.
