# Code Summary & Structural Map

## 1. Architecture & Data Flow
- **Pattern**: MVI-lite (StateFlow driven). Monolithic `:app` module.
- **Flow**: `Logic (Calculators)` -> `Providers` -> `ViewModel/Widget` -> `UI (Compose/Glance)`.

```mermaid
graph TD
    PCalc["P.Calc (Nalla Neram/Gowri)"] --> Prov["Providers (Mock/Sun)"]
    LCalc["L.Calc (Lagna/Maitra)"] --> Prov
    Prov --> VM["Timeline.VM"]
    VM --> UI["Dashboard UI"]
    Prov --> Widget["Panchangam.Widget"]
    Worker["WidgetUpdate.Worker"] --> Widget
```

## 2. Layer Responsibilities

### `logic/` (Domain)
- `PanchangamCalculator`: Core engine (Nalla Neram, Gowri, Horai). Proportional/Fixed styles.
- `LagnaCalculator`: Sidereal ascendants & Maitra Muhurtham (IAU 1982/Meeus).
- `BirthChartCalculator`: Exact astrological ephemeris calculations for Lagna (D1), Moon Rasi, and planetary positions from birth profile coordinates.
- `DasaCalculator`: Vimsottari Dasa & Bhukti calculation engine with exact birth balance slicing and 120-year boundary enforcement.
- `LunarCalendarUtils`: Ephemeris (ELP-2000), Lahiri Ayanamsha, ritual windows.
- `TamilCalendarUtils`: 60-year cycles, anchored festival detection (`RitualContext`).
- `SunriseSunsetProvider` & `MockPanchangamProvider`: Solar calculations & slot clipping/transitions.

### `ui/` & `widget/` (Presentation)
- `dashboard/`: 24h timeline. `TimelineCore` handles lane-scaling & harmony refinement.
- `profile/`: User Profile Page (`ProfileScreen`), Add/Edit Dialog (`ProfileAddDialog`), Astrological Charts (`AstroChartCanvas`), and Vimsottari Dasa Timeline Dialog (`DasaBhuktiDialog`).
- `navigation/`: State-driven Nav3 routing and custom `ViewMode` logic.
- `settings/`: DataStore-backed config (7 blocks), Custom Timeline and Chandrashtamam management.
- `PanchangamWidget`: Glance-based M3 widget for at-a-glance timings.
- `theme/`: Universal planetary colors (`SunColor`, `MoonColor`, `RahuColor`, `KetuColor`) with high-contrast text foreground colors (`SunFgColor`, `VenusFgColor`, etc.).

### `data/` & `worker/` (Infrastructure)
- `SettingsRepository`: SSOT for config, Custom Timeline presets, Date Format, and Time Format.
- `BirthProfilesRepository`: Atomic disk JSON storage for user birth profiles (`birth_profiles_v1.json`).
- `CacheManager`: Atomic JSON storage for `DayData` (keyed by lat/lng/date).
- `WidgetUpdateWorker`: Background cache maintenance & transition-aware updates.
- `VerifiedHolidays`: Static TN Public Holidays & Muhurthams.

### `model/` (State & Resources)
- `BirthProfile`: Data model for user birth details, chart style preference, and lunar month system.
- `Metadata`: Logic-to-Resource bridge (localized strings, traditional guidance).
- `DayData`: Primary 24h snapshot (Muhurthams, Tithis, Festivals).
- `Serializers`: K-Serialization for Java Time types.

## 3. Core Algorithms

### Harmony Layout (`TimelineCore.kt`)
1. **Anchor**: Assign items to Lane Ranks (Gowri Left, Horai Right).
2. **Refine**: 8-pass iterative expansion into dead space; items meet at midpoints.
3. **Render**: "Best Segment" (max width/height) anchor for content to prevent clipping.

### Event Anchoring (`TamilCalendarUtils.kt`)
- **Ritual Windows**: Pradosha (Sunset -90m), Nishita (Midnight window).
- **Udaya Vyapini**: Festivals anchored to Sunrise state to prevent calendar duplication.

## 4. Symbol Index
| Symbol | Responsibility |
| :--- | :--- |
| `DayDataProvider` | SSOT for unified Panchangam calculation (App, Widget, Worker). |
| `BirthChartCalculator` | Ephemeris math core for generating Rasi (D1) and Navamsa (D9) positions. |
| `DasaCalculator` | Vimsottari Dasa timeline generator with birth nakshatra balance and Bhukti breakdown. |
| `TimelineCore` | 24h grid with dynamic lane scaling and harmony gap-filling. |
| `CacheManager` | Versioned atomic storage for timeline performance. |
| `Metadata` | Mapping between calculation results and localized traditional text. |
| `LagnaCalculator` | High-precision Meeus math core for zodiac ascendants (Lagna). |
| `WidgetUpdateWorker` | Transition-triggered background updates and cache refills. |
| `Custom Timeline` | Persistent user-defined column arrangements in `SettingsRepository`. |
