---
name: background-watchdog-and-health-check
description: Provides instructions and patterns for implementing robust WorkManager background watchdogs, health check monitors, circuit-breaker crash-loop protection, manual stop protection, and settings dashboard status integrations. Use this skill when building self-healing background workers, supervisory workers that monitor other workers, failure-count threshold mechanisms, or settings health panels.
---

# Background Watchdog & Health Check Component

## Overview
This skill outlines the pattern for implementing an autonomous background watchdog (`WatchdogWorker`) that monitors critical background workers (such as widget updaters and reminder engines), protects against infinite crash loops via circuit-breaker logic, and integrates real-time status panels into Settings.

## Key Architectural Patterns

### 1. Supervisory Watchdog Worker (`WatchdogWorker`)
- Periodically queries WorkManager work states (`ENQUEUED`, `RUNNING`, `SUCCEEDED`, `FAILED`, `CANCELLED`).
- Automatically recovers and restarts failed background tasks unless explicitly stopped by the user.

### 2. Circuit-Breaker Crash-Loop Protection
- Tracks consecutive failure counts in persistent storage (DataStore / SharedPreferences).
- If a component fails consecutively beyond a threshold (e.g., 3 attempts), the circuit breaker trips, pausing auto-restarts to protect battery and CPU, and posts a critical alert notification.

### 3. Manual Stop Protection & Settings Dashboard
- Records manual user stoppages (`STOPPED_BY_USER`), ensuring the watchdog never overrides user intent.
- Exposes real-time status cards in Settings with interactive **Restart**, **Stop**, and **Resume** buttons.
