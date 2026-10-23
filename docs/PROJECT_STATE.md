# Project State

**Phase**: Alpha/Stable (Phase 12 Complete - Background Watchdog, Health Check Dashboard, Comprehensive Localization, and UI/UX Polish)

## 1. Recently Implemented
- **Background Watchdog & Health Check Dashboard (`WatchdogWorker` & Settings Dashboard)**: Implemented an always-running background monitor for `WidgetUpdateWorker` and `ReminderWorker`, featuring circuit-breaker crash-loop protection (max 3 consecutive retries), manual stop protection, dedicated notification channels (`channel_watchdog`), real-time status indicators (`RUNNING`, `ENQUEUED`, `FAILED`, `STOPPED_BY_USER`), and formatted last update/reminder timestamps matching user date & time preferences.
- **Profile Avatars & Initials Badges**: Profile photo picker, background color palette, and initials customization with circular preview badges.
- **Active vs. Primary Profile Separation**: Active session profile drives Dashboard UI (Thara Balam, Chandrashtamam, Hora compatibility, charts), while Primary profile powers background workers and Glance widgets with dedicated header title (`[Name] - Primary`).
- **Natal Hora Compatibility & Dual Compatibility UI**: Side-by-side general weekday and personal natal compatibility indicators on timeline cards and detail sheets inside white circular legibility containers.
- **Rasi Chart Sign Lords & Lagna Lordships Aspects Breakdown**: Complete Lagna lordship analysis (Lagnadipathi, Yogakaraka, Trikona, Dusthana, Kendra, Upachaya, Maraka, Badhaka) with info dialog and 100% Tamil/English localization parity.
- **Midnight-Crossing Sub-Hora Calculation Fix**: Upgraded `calculateSubHoras()` in `PanchangamCalculator` with cross-day duration math and nano-of-day time wrapping, ensuring all 24 horas (including midnight-crossing night horas) correctly populate sub-horas.
- **Scroll-to-Now Precision**: Implemented exact pixel coordinate math (`yNow - centerOffset` and `xNow - centerOffset`) for both the dashboard and Dasa/Bhukti Visual Track, with layout-aware state observation (`maxValue > 0`) ensuring instant and reliable "Go to Now" execution on launch and button click.
- **Strict Bilingual Localization Parity**: Externalized all user-facing strings and completed fluent Tamil translations across settings, profiles, Dasa/Bhukti timelines, and visual track canvas labels.

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3.
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha, Vimsottari Dasa Engine.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache, Reminders, Birth Profiles JSON).
- **Background**: Jetpack WorkManager (`ReminderWorker`, `WidgetUpdateWorker`, `WatchdogWorker`).

## 3. Next Focus
- **Adaptive Layouts**: List-Detail patterns for Tablets/Foldables.
- **Wear OS**: Complications and Tiles support.
- **Traditional Assets**: Sourcing high-fidelity spiritual SVGs (Nandi, Shiva, Ganesha).
