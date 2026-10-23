# Project State

**Phase**: Alpha/Stable (Phase 12 Complete - Progressive Startup Sequencing, Granular Loading Feedback, and 100% Localization Parity)

## 1. Recently Implemented
- **Progressive Startup Sequencing & Granular Loading Feedback**: Refactored `TimelineViewModel` and `MainActivity` to enforce strict sequential loading (Location resolution first -> Ephemeris & Panchangam calculation second), eliminating race conditions and double-refreshes. Added descriptive localized loading status messages (`"Loading location…"`, `"Calculating Panchangam & Ephemeris…"`, `"Loading preferences…"`) alongside spinners during cold starts and uncached date selections.
- **Hora Compatibility Card Text Bug Fix**: Fixed a bug in `DashboardDetailSheet.kt` where conflicting Horas displayed the correct red conflict icon/tint but hardcoded "Favorable for your birth star" text. Now dynamically maps to favorable, conflicting, or neutral string resources.
- **Strict Bilingual Localization Parity Audit**: Externalized all remaining hardcoded strings in dialogs, sheets, and setup flows (`ProfileAddDialog`, `ReminderDialog`, `DashboardDetailSheet`) into complete non-fallback English (`values/strings.xml`) and Tamil (`values-ta/strings.xml`) string resources.
- **Background Watchdog & Health Check Dashboard (`WatchdogWorker` & Settings Dashboard)**: Implemented an always-running background monitor for `WidgetUpdateWorker` and `ReminderWorker`, featuring circuit-breaker crash-loop protection, dedicated notification channels, and real-time status indicators.
- **Active vs. Primary Profile Separation & Natal Hora Compatibility**: Side-by-side general and personal natal compatibility indicators on timeline cards and detail sheets inside legibility containers.

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3.
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha, Vimsottari Dasa Engine.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache, Reminders, Birth Profiles JSON).
- **Background**: Jetpack WorkManager (`ReminderWorker`, `WidgetUpdateWorker`, `WatchdogWorker`).

## 3. Next Focus
- **Adaptive Layouts**: List-Detail patterns for Tablets/Foldables.
- **Wear OS**: Complications and Tiles support.
- **Hybrid Ephemeris Caching Exploration**: Granular per-day file caching versus SQLite Room database evaluation.
