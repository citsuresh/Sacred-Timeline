# Project State

**Phase**: Alpha/Stable (Phase 10 Complete - Advanced Reminders & Notifications)

## 1. Recently Implemented
- **Advanced Timeline Event Reminders**: Full-stack reminder system supporting relative offsets (start/end) and custom absolute times ($N$ days before at specific time). Features multiple reminders per event, recurring reminders ("All Occurrences" with automatic next-occurrence calculation for Nakshatras and Tamil dates), and direct creation/management from Settings.
- **WorkManager Background Reliability (`ReminderWorker`)**: Migrated alarm scheduling to WorkManager with built-in WakeLocks to ensure reliable background notification delivery across all OEM devices (including Tecno HiOS).
- **Dedicated 1-to-1 Notification Channels**: Established 12 dedicated notification channels pre-created on app startup, allowing independent user customization of sound, vibration, and banner settings per category.
- **Interactive Solar Cards & Reminders**: Added first-class `SolarTiming` cards for Sunrise and Sunset on the timeline header, allowing users to view details and set solar event reminders.
- **Strict Bilingual Localization Parity**: Completed a comprehensive localization audit across English and Tamil (`values-ta/strings.xml`), ensuring all Nakshatra descriptions (`desc_star_1` to `desc_star_27`), Tithi descriptions (`tithi_desc_...`), Gowri descriptions, Hora planet qualities, guidance, and strategic activities are fully translated.
- **Navigation & Back Button Fix**: Hoisted navigation backstack state to `MainActivity` and overridden `onBackPressed()` to properly support hardware back button navigation across all settings sub-screens.

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3.
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache & Reminders JSON).
- **Background**: Jetpack WorkManager (`ReminderWorker`, `WidgetUpdateWorker`).

## 3. Next Focus
- **Adaptive Layouts**: List-Detail patterns for Tablets/Foldables.
- **Wear OS**: Complications and Tiles support.
- **Traditional Assets**: Sourcing high-fidelity spiritual SVGs (Nandi, Shiva, Ganesha).
