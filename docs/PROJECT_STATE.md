# Project State

**Phase**: Alpha/Stable (Phase 10 In-Progress - Multi-Factor Scoring Engine & Interactive Time Inspector Preview Line)

## 1. Recently Implemented
- **Multi-Factor Scoring Engine & Personal Power Meter (Phase 10 Prototype & Engine)**: Implemented the scoring engine core (`ScoringEngine.kt`, `ScoringWeightsConfig.kt`), Option 1 Header Power Meter Row with quick action buttons (`[🔍 Best Window]` and `[⏱️ Preview]`), Option 2 Floating NOW Gauge with directional touch slop and vertical drag to spawn the Purple Preview Line (Inspector Mode), transparent `ScoreBreakdownDialog`, and the 24-hour toggleable `ScoreHeatmapColumn`.

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3.
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha, Vimsottari Dasa Engine.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache, Reminders, Birth Profiles JSON).
- **Background**: Jetpack WorkManager (`ReminderWorker`, `WidgetUpdateWorker`, `WatchdogWorker`).

## 3. Next Focus
- **Adaptive Layouts**: List-Detail patterns for Tablets/Foldables.
- **Wear OS**: Complications and Tiles support.
- **Hybrid Ephemeris Caching Exploration**: Granular per-day file caching versus SQLite Room database evaluation.
