# Project State

**Phase**: Stable (Phase 3 & Phase 5 Complete - Adaptive Layouts, Search Suite, Custom Reminders & Scoring Boosters)

## 1. Recently Implemented
- **Search Feature (Phase 5 Complete)**: Implemented dedicated Search screen with default 60-day instant prefill (Holidays, Muhurthams, Custom Events, and user-selected Tithis/Nakshatras/Chandrashtamams), global search query expansion, horizontal scrollable filter chips, chronological sorting, start-date-only multi-day span filtering, dynamic headers ("Upcoming Events" / "Search Results"), and authentic moon phase icons (`MoonPhaseIcon`).
- **Custom Reminders Date Selection & Persistence**: Added flexible date picking (English Date vs. Tamil Calendar Date with Month and Star/Tithi), annual recurrence (`plusYears(1)`), search integration, and atomic file persistence (`RemindersRepository`, `CacheManager`, `BirthProfilesRepository`).
- **Nalla Neram Scoring Boost**: Added positive score weightage multipliers for Nalla Neram (`+20%`) and auspicious Gowri Neram (`+10%`).
- **MainActivity Modularization**: Refactored `MainActivity.kt` into modular components (`MainDrawer.kt` and `NavGraph.kt`).

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3, Material 3 Adaptive (`ListDetailPaneScaffold`).
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha, Vimsottari Dasa Engine.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache, Reminders, Birth Profiles JSON).
- **Background**: Jetpack WorkManager (`ReminderWorker`, `WidgetUpdateWorker`, `WatchdogWorker`).

## 3. Next Focus
- **Wear OS Support**: Complications and Tiles support.
- **Global Search Expansion & Accessibility Suite**: Screen reader semantics and high-contrast refinements.
- **Hybrid Ephemeris Caching Exploration**: Granular per-day file caching versus SQLite Room database evaluation.
