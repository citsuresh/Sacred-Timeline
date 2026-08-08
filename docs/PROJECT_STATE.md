# Project State

**Phase**: Alpha/Stable (Phase 4 Complete)

## 1. Recently Implemented
- **Maitra Muhurtham (Debt Repayment)**: Implemented a high-precision detection engine for Maitra windows:
    - **Vara-Based Potency**: Intelligently handles day-boundary transitions (Sunrise-to-Sunrise Vara) to show 5-star potency even after midnight.
    - **Dual-Tone Gold Styling**: High-potency windows (Tuesdays) use rich gold, while standard windows use a paler gold (`#FFE082`).
    - **Fully Localized**: English and Tamil support for all labels and potency descriptions.
- **Shared Astronomical Core**: Consolidated Lahiri Ayanamsha logic into `LunarCalendarUtils` to ensure perfect alignment between Lagna (zodiac rise) and Nakshatra (moon position).
- **Comprehensive Widget Support**: Integrated Maitra windows into the Home Screen Widget with star indicators and potency labels.
- **Universal View Refinement**: Added a dedicated Maitra column and included Maitra windows in the "Nalla Neram" (NERAM) specific view mode.
- **Three-Style Layout System**: Introduced a toggle for users to choose their preferred horizontal distribution:
    - **Fixed 3-Track**: Predictable pillars for Gowri/Horai.
    - **Equal Rectangular**: Symmetrical widths for all overlapping items.
    - **Orthogonal Stepped**: Dynamic width adjustment (90-degree steps).

## 2. Technical Stack
- **UI**: Kotlin, Jetpack Compose (Material 3), Jetpack Glance (Widgets).
- **Navigation**: Navigation 3.
- **Persistence**: DataStore (Settings), Disk JSON (Cache).
- **Minimum SDK**: 26 (Android 8.0).
- **Target SDK**: 34 (Android 14).

## 3. Next Focus
- **Adaptive Layouts**: Optimization for Tablets/Foldables using List-Detail patterns.
- **Wear OS Support**: Complications and Tiles for quick timing checks.
