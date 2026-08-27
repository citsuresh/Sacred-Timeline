# Project State

**Phase**: Alpha/Stable (Phase 4 Complete, Phase 9 In-Progress)

## 1. Recently Implemented
- **Traditional Event Anchoring**: Resolved lunar duplication bug. Pradosham (Sunset anchor), Shivaratri (Nishita anchor), and Udaya Vyapini festivals (Sunrise anchor) are now timing-aware to prevent double-counting.
- **Precision Engine**: Upgraded astronomical core with date-specific DST/Timezone offset calculation (±1 min accuracy).
- **Harmony Layout Engine**: 8-pass virtual refinement for the "Equally Distributed" view. Boxes mathematically converge to eliminate dead space while maintaining clean rectangles. Includes "Best Segment" logic for text/icon anchoring.
- **Yogam & Thara Balam**: Full integration of Amritadhi Yogams and Personal Daily Strength. Features separate, independent columns in the Universal and Solo views, dedicated visual boxes in the Glance Widget, and a new "Primary Birth Star" setting for SSOT personal calculations.
- **UI & Customization**: 
    - Dedicated "My Birth Star" setting in General Settings (SSOT for Thara Balam).
    - Full persistence for "Custom Timeline" (hidden until first use).
    - Calendar-style Tamil Date Anchor in header.
    - Fixed `MoonPhaseIcon` rendering logic for Valarpirai (Waxing) phases using robust path-based rendering.
    - Two-way sync between Nav Drawer and Settings; added switch confirmation dialogs.
    - **Widget Synchronization**: Fixed transition scheduling (Start/End boundaries), instant manual refresh, and debounced app-to-widget sync.
    - **UI Integrity**: Resolved parameter-shift bug in `TimelinePager` that affected header and marquee visibility.
- **Architecture**:
    - **Unified Data Pipeline**: Introduced `DayDataProvider` to eliminate logic drift between App and Widget; wired all settings (including Lunar Month System) through the new pipeline.
- **Performance**: Smart Refresh logic prevents full cache clears for non-mathematical setting changes. default preload increased to 30 days. Fix: Cache now correctly persists across app launches (v13).
- **Chandrashtamam**: Full stack implementation complete. High-precision 8th-sign transit detection wired to user birth stars. Features a 36-item selection UI (handling boundary stars), vertical "Muted Red" sticker-look cards in the timeline, and high-visibility header warnings (marquee + expanded). Details panel enhanced to show star-specific warnings. Parity maintained across App, Widget, and Worker with automated cache syncing (v12).

## 2. Technical Stack
- **UI**: Kotlin, Compose (M3), Jetpack Glance (Widgets), Navigation 3.
- **Logic**: ELP-2000 / Meeus / IAU 1982 (Sidereal), Lahiri Ayanamsha.
- **Storage**: DataStore (Settings), Disk JSON (Atomic Cache).

## 3. Next Focus
- **Adaptive Layouts**: List-Detail patterns for Tablets/Foldables.
- **Wear OS**: Complications and Tiles support.
- **Traditional Assets**: Sourcing high-fidelity spiritual SVGs (Nandi, Shiva, Ganesha).
