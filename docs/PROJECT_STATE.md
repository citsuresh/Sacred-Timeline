# Project State

**Phase**: Stable (Phase 10 Complete - Multi-Factor Scoring Engine & 24-Hour Score Heatmap Column)

## 1. Recently Implemented
- **Multi-Factor Scoring Engine & Personal Power Meter (Phase 10 Complete)**: Implemented the scoring engine core (`ScoringEngine.kt`, `ScoringWeightsConfig.kt`) supporting 6 weighted astrological factors, malefic penalty overrides (Rahu Kalam, Yamagandam, Chandrashtamam), Hora scoring mode toggle (Natal vs. General), Power Meter Row with live mini-gauge (`SpeedometerCanvas`), modal `ScoreBreakdownSheet`, and the 24-hour toggleable `ScoreHeatmapColumn` with complete interval transition coverage (including Maitra Muhurtham). Refined UI by omitting redundant interactive preview line in favor of the persistent at-a-glance heatmap column.

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3.
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha, Vimsottari Dasa Engine.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache, Reminders, Birth Profiles JSON).
- **Background**: Jetpack WorkManager (`ReminderWorker`, `WidgetUpdateWorker`, `WatchdogWorker`).

## 3. Next Focus
- **Adaptive Layouts**: List-Detail patterns for Tablets/Foldables.
- **Wear OS**: Complications and Tiles support.
- **Hybrid Ephemeris Caching Exploration**: Granular per-day file caching versus SQLite Room database evaluation.
