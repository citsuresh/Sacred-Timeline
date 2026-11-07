# Project State

**Phase**: Stable (Phase 10 Complete - Multi-Factor Scoring Engine & 24-Hour Score Heatmap Column)

## 1. Recently Implemented
- **Phase 3 Adaptive Layouts**: Implemented Material 3 Adaptive `ListDetailPaneScaffold` (`AdaptiveTimelineDashboard.kt`) supporting master-detail side-by-side views for wide screens (tablets and foldables) with strict bilingual localization parity.

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3.
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha, Vimsottari Dasa Engine.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache, Reminders, Birth Profiles JSON).
- **Background**: Jetpack WorkManager (`ReminderWorker`, `WidgetUpdateWorker`, `WatchdogWorker`).

## 3. Next Focus
- **Adaptive Layouts**: List-Detail patterns for Tablets/Foldables.
- **Wear OS**: Complications and Tiles support.
- **Hybrid Ephemeris Caching Exploration**: Granular per-day file caching versus SQLite Room database evaluation.
