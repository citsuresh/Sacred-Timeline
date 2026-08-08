# Project State

**Phase**: Alpha/Stable (Phase 3 Complete)

## 1. Recently Implemented
- **Three-Style Layout System**: Introduced a toggle for users to choose their preferred horizontal distribution in the Universal Timeline:
    - **Fixed 3-Track**: Predictable pillars for Gowri/Horai.
    - **Equal Rectangular**: Symmetrical widths for all overlapping items.
    - **Orthogonal Stepped**: Dynamic width adjustment (90-degree steps) to eliminate empty gaps and maximize space.
- **Settings Reorganization**: Structured the long settings list into 7 logical groups (General, Conventions, Layout, Indicators, Location, Widget, Advanced) to improve discoverability.
- **Improved Marquee Stability**: Fixed a state-reset bug where marquee labels would appear fragmented after swiping dates.
- **Enhanced Text Legibility**: Implemented 2-line text wrapping and dynamic font scaling in Timing Cards.
- **Maitra Muhurtham Research**: Finalized the calculation rules (Tuesday + Ashwini/Anuradha + Mesha/Vrishchika Lagna) and prepared the technical implementation plan. See [MAITRA_MUHURTHAM_PLAN.md](plans/MAITRA_MUHURTHAM_PLAN.md).

## 2. Technical Stack
- **UI**: Kotlin, Jetpack Compose (Material 3), Jetpack Glance (Widgets).
- **Navigation**: Navigation 3.
- **Persistence**: DataStore (Settings), Disk JSON (Cache).
- **Minimum SDK**: 26 (Android 8.0).
- **Target SDK**: 34 (Android 14).

## 3. Next Focus
- **Adaptive Layouts**: Optimization for Tablets/Foldables using List-Detail patterns.
- **Wear OS Support**: Complications and Tiles for quick timing checks.
