# Project State

**Phase**: Alpha/Stable (Phase 11 Complete - Personalized Astrological Profiles & Vimsottari Dasa Engine)

## 1. Recently Implemented
- **Personalized Astrological Profiles & Storage**: Implemented `BirthProfile` data model and `BirthProfilesRepository` with atomic JSON persistence, primary profile management, dual name fields (English and Regional script), and offline coordinate auto-resolution.
- **Live Global Geocoder Autocomplete (`GeoHelper`)**: Integrated Android's system `Geocoder` (forced to `Locale.ENGLISH` for cross-language consistency) with zero API keys required, enabling live autocomplete for any town, village, or city worldwide.
- **High-Precision Ephemeris & Lagna Engine (`BirthChartCalculator` & `LagnaCalculator`)**: Upgraded Sidereal Ascendant (Lagna) calculations using rigorous Meeus Chapter 12/13 GMST/LST formulas and fractional timezone offset handling (`+05:30`).
- **Visual Astrological Charts (`AstroChartCanvas`)**: Built native Compose grid and diamond chart renderers supporting **South Indian Grid** and **North Indian Diamond** chart styles with traditional Lagna (`Lag`) and Moon Rasi (`Rasi`) markers and localized 3-letter abbreviations.
- **Global Date & Time Settings Parity**: Added Date Format settings (`DD-MM-YYYY`, `MM-DD-YYYY`, `YYYY-MM-DD`) in Settings, synchronized with 12h/24h time formatting across the entire app.
- **Vimsottari Dasa & Bhukti Calculator (`DasaCalculator` & `DasaBhuktiDialog`)**: Implemented complete 120-year Mahadasa and Bhukti calculation engine with exact birth balance slicing, 120-year boundary enforcement, List View with active Dasa auto-focus, and Visual Track with dual-lane Gantt chart, zoom controls, pinch-to-zoom, and "Jump to Now" button.
- **Strict Bilingual Localization Parity**: Completed comprehensive localization audits across English and Tamil (`values-ta/strings.xml`), ensuring all profile screens, dialogs, chart box abbreviations, and 12-house Macro Planetary Insights transit interpretations are fully translated.

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3.
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha, Vimsottari Dasa Engine.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache, Reminders, Birth Profiles JSON).
- **Background**: Jetpack WorkManager (`ReminderWorker`, `WidgetUpdateWorker`).

## 3. Next Focus
- **Adaptive Layouts**: List-Detail patterns for Tablets/Foldables.
- **Wear OS**: Complications and Tiles support.
- **Traditional Assets**: Sourcing high-fidelity spiritual SVGs (Nandi, Shiva, Ganesha).
