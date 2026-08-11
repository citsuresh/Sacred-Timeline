# Project State

**Phase**: Alpha/Stable (Phase 4 Complete, Phase 9 In-Progress)

## 1. Recently Implemented
- **Traditional Event Anchoring**: Resolved lunar duplication bug. Pradosham (Sunset anchor), Shivaratri (Nishita anchor), and Udaya Vyapini festivals (Sunrise anchor) are now timing-aware to prevent double-counting.
- **Precision Engine**: Upgraded astronomical core with date-specific DST/Timezone offset calculation (±1 min accuracy).
- **Harmony Layout Engine**: 8-pass virtual refinement for the "Equally Distributed" view. Boxes mathematically converge to eliminate dead space while maintaining clean rectangles. Includes "Best Segment" logic for text/icon anchoring.
- **UI & Customization**: 
    - Full persistence for "Custom Timeline" (hidden until first use).
    - Calendar-style Tamil Date Anchor in header.
    - Two-way sync between Nav Drawer and Settings; added switch confirmation dialogs.
    - **Widget Synchronization**: Implemented high-precision transition scheduling and instant refresh logic.
- **Architecture**:
    - **Unified Data Pipeline**: Introduced `DayDataProvider` to eliminate logic drift between App and Widget.
- **Performance**: Smart Refresh logic prevents full cache clears for non-mathematical setting changes. default preload increased to 30 days.

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3.
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache).

## 3. Next Focus
- **Adaptive Layouts**: List-Detail patterns for Tablets/Foldables.
- **Wear OS**: Complications and Tiles support.
- **Traditional Assets**: Sourcing high-fidelity spiritual SVGs (Nandi, Shiva, Ganesha).
