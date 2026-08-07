# Project State

**Phase**: Alpha/Stable (Phase 3 Complete)

## 1. Recently Implemented
- **High-Precision Ephemeris Engine**: Replaced simplified math with a professional **ELP-2000 (Meeus)** lunar model and **Lahiri Ayanamsha**, bringing Tithi/Nakshatra timings within ±1 minute of professional standards and fixing the 2-hour Vakya drift.
- **Local Solar Logic**: Implemented an offline mathematical engine (Meeus/NOAA) for Sunrise/Sunset, removing reliance on external web APIs.
- **Regional Convention Support**: Added a toggle for **Amanta (South)** vs **Purnimanta (North)** lunar month systems.
- **Sunrise Definition Toggle**: Users can now choose between **Scientific (Top Edge)** and **Traditional (Center of Disk)** definitions.
- **Dynamic Timing Labels**: Implemented intuitive **"Till [Time]"** labels in the dashboard marquee for active Nakshatras and Tithis.
- **Special Period Flexibility**: Added a setting to switch between **Proportional (Astronomical)** and **Fixed (1.5h)** Rahu/Yama/Kuli timings.
- **Automated Traditional Festival Detection**: Implemented dynamic detection for "Aadi Kiruthigai" and "Aadi Pooram" by intersecting Lunar (Nakshatra) and Solar (Month) calendars.
- **Iterative Festival Engine**: Refactored `TimelineViewModel` to search all Tithi/Nakshatra intervals in a 24h window, ensuring festivals starting later in the day are never missed.
- **Enhanced Traditional Styling**: Updated `SacredTimelineColors` to correctly apply the Gold "Traditional" tint to Pradosham and Sivaratri (previously defaulting to Purple).
- **Rigorous Auditing Protocol**: Established the `AGENTS.md` mandate, requiring independent Regression Auditor sub-agents to verify every code change and "wiring" integrity.
- **Universal Timeline**: A high-density combined view that merges Nalla Neram, Brahma/Abhijit Muhurthams, Gowri Neram, and Horai into a single column.
- **Smart Lane Distribution**: Implemented a sweep-line algorithm for equal distribution. Gowri is anchored Left, Horai is anchored Right, and other items fill the Center with sub-lane resolution for overlaps.
- **Full-Day Event Hierarchy**:
    - **Spiritual/Auspicious (Gold)**: Subha Muhurtham, Festivals, and Traditional windows apply a Gold tint to the entire timeline and a sticky heading.
    - **Secular/Public (Purple)**: Holidays like Independence Day apply a Purple tint and a sticky heading.
    - **Priority Logic**: Gold tint takes precedence over Purple for background styling while showing both labels.
- **Date Picker & Pager Sync**: Fixed a timezone-offset bug in the Date Picker and implemented `snapshotFlow` tracking to ensure the Pager and Selected Date remain perfectly synchronized.
- **Horai Renaming**: Standardized the term "Horai" (Tamil: ஹோரை) across all English and Tamil UI components.
- **Enhanced Box Detail**: Added categorical headings (e.g., NERAM, HORAI) to individual timeline cards for instant identification in combined views.
- **Header Significance Details**: Added a details pane (BottomSheet) for every item in the header (Tithi, Nakshatra, Events, Muhurthams) with traditional guidance and precise start/end times.
- **Interactive Marquees**: Replaced `basicMarquee` with a custom `AutoScrollingRow` to ensure all scrolling header items remain 100% clickable during animation.
- **Timeline Timing Toggles**: New settings to show/hide Sunrise, Sunset, and Brahma Muhurtham.
- **Brahma Muhurtham Display**: Explicit window calculation and UI integration in the dashboard.
- **Header Standardization**: Established three-row layout terminology: Calendar Row, Events Row, and Timings Row.
- **Traditional Tamil Calendar**: Full Lunar Engine for Tithi, Nakshatra, and Paksha calculations.
- **Religious Events**: Integrated 2024-2026 TN Public Holidays and Subha Muhurthams.
- **Spiritual UI**: High-fidelity PNG icons for Shiva, Ganesha, and Nandi; refined custom Vel icon with dynamic visibility borders.
- **App Theme Switcher**: Persistent System/Light/Dark mode selection integrated into Compose theme.
- **Expandable Header**: Implemented vertical swipe gestures (Swipe Down to Expand, Swipe Up to Collapse) on the dashboard header.
- **Enhanced Marquee**: Added manual horizontal scrolling and "Pause on Interaction" logic to the auto-scrolling marquee items.
- **Landscape Accessibility**: Side panel in landscape mode is now scrollable to accommodate header expansion without obscuring the Date Dial.
- **Header Item Refactoring**: Unified Tithi, Nakshatra, and Event UI into reusable components for consistency between compact and expanded views.
- **Precision Timing Ranges**: Tithis and Nakshatras now display full "Start" and "End" times with multiday context (Yesterday/Tomorrow).
- **Muhurtham Marquee**: Added central marquee for Brahma and Abhijit Muhurthams with fixed Sunrise/Sunset positions.
- **Header Synchronization**: All Tithi/Nakshatra elements occurring in a 24h window are now listed in the marquee to prevent missed transitions.
- **Timeline Display Customization**: Detailed toggles for individual Tithis and Nakshatras.
- **Enhanced Localized Labels**: Added specific labels for `Starts`, `Ends Tomorrow`, and `Started Yesterday` in English and Tamil.
- **Precision Astronomical Overhaul**:
    - Replaced low-precision math with **ELP-2000 / Meeus** lunar perturbations.
    - Implemented **Lahiri Ayanamsha** for sidereal accuracy.
    - Results verified to match **Drik Panchang** exactly (±1 minute).
- **Offline Solar Engine**: Removed dependency on `sunrise-sunset.org` for local math.
- **Pan-Indian Flexibility**:
    - Added **Amanta/Purnimanta** toggle (Default: South India/Amanta).
    - Added **Scientific/Traditional** Sunrise toggle.
    - Added **Proportional/Fixed** Special Period style toggle.
- **Neram / Muhurtham View Expansion**: Integrated Rahu Kalam, Yamagandam, and Kuligai into the unified "Neram / Muhurtham" timeline.
- **Now-Line Customization**: Defaulted "NOW" line color to Green.
- **Improved Marquee Intelligence**: Active Tithis/Nakshatras now display as **"Till [Time]"** for easier reading.
- **Tithi Filtering Fix**: Resolved logic bug where hidden Tithis (like Ashtami) were not matching across both Pakshas.

## 2. Technical Stack
- **UI**: Kotlin, Jetpack Compose (Material 3), Jetpack Glance (Widgets).
- **Navigation**: Navigation 3.
- **Persistence**: DataStore (Settings), Disk JSON (Cache).
- **Minimum SDK**: 26 (Android 8.0).
- **Target SDK**: 34 (Android 14).

## 3. Next Focus
- **Adaptive Layouts**: Optimization for Tablets/Foldables using List-Detail patterns.
- **Wear OS Support**: Complications and Tiles for quick timing checks.
