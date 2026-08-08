# Design Decisions

Historical record of architectural and UI/UX choices.

## UI & Layout
- **Universal Timeline Distribution (Track-based Lanes)**: 
    - Implemented a multi-track lane system for the high-density Universal view.
    - **Gowri Neram** is pinned to the Left Track, **Horai** to the Right Track, and all other events to the Center.
    - **Dynamic Track Widths**: If only two tracks are active (e.g., Left and Right), they split the space 50/50. If three are active, they split 33/33/33. This eliminates empty gaps in the timeline.
    - **Sub-Lane Resolution**: Items within the *same* track that overlap (like Rahu and Abhijit in the center) further sub-divide their track's width equally to prevent overlapping boxes.
- **Priority Event Tinting**:
    - **Spiritual/Gold Priority**: When multiple full-day events occur (e.g., a Public Holiday and a Subha Muhurtham), the Gold tint takes precedence as it defines the "traditional quality" of the day.
    - **Global Column Tinting**: Tints are applied to the entire 24h column, including the Time Markers, to ensure the day's quality is visible even when the timeline is crowded with cards.
- **Strict Pager Synchronization**: Switched from `animateScrollToPage` to a `snapshotFlow` observer and UTC-anchored dates to prevent "midway stops" in the timeline when swiping or picking dates.
- **Horai Standardization**: Adopted the term "Horai" (instead of "Hora") to better align with Tamil traditional nomenclature while maintaining English legibility.
- **24-Hour Vertical Timeline**: Ensures all slots (Gowri/Horai) are visible; "NOW" indicator centered for immediate focus.
- **High-Fidelity Spiritual Icons**: Switched from simplified code-drawn SVGs to user-provided PNGs for Nandi, Shiva, and Ganesha to ensure 100% traditional accuracy.
- **Double-Border (Sticker Look)**: All timing cards and custom icons (like Vel) use white outer and black inner borders (or vice versa based on mode) to remain legible on any wallpaper or theme.
- **Marquee Header**: 
    - **Events Row (Line 2)**: Uses auto-scrolling (Marquee) to show all Tithis, Nakshatras, and Festivals occurring in the day, with explicit Start/End times.
    - **Timings Row (Line 3)**: Sunrise/Sunset are fixed at edges; Muhurthams (Brahma/Abhijit) marquee in the center.
- **Header Row Terminology**:
    - **Calendar Row**: Line 1 (Tamil Date, Paksha).
    - **Events Row**: Line 2 (Marquee: Festivals, Tithi, Nakshatra).
    - **Timings Row**: Line 3 (Sun times, Brahma Muhurtham).
- **Expandable Header (Contextual Expansion)**: 
    - The header now supports a two-state layout: a compact auto-scrolling marquee (default) and an expanded wrapping view (`FlowRow`).
    - Expansion is triggered by a Swipe Down gesture, allowing users to see all spiritual significance data at once without waiting for the marquee.
    - Transitions are smoothed using `AnimatedContent` and `animateContentSize`.
- **Interaction-Aware Marquees**: 
    - Replaced `basicMarquee` with a custom `AutoScrollingRow` that now supports manual horizontal scrolling.
    - The auto-scroll animation intelligently pauses when `isScrollInProgress` is true (user touch/drag), ensuring hit targets remain stable for clicking.
- **Landscape Side-Panel Scrolling**: To maintain accessibility of the vertical Date Dial in landscape mode, the side-panel `Column` is now scrollable, preventing the expanded header from pushing other controls off-screen.
- **Compositional Theme Switching**: Theme management (Light/Dark/System) is handled purely within the Compose root to avoid Activity recreation loops associated with `AppCompatDelegate` in a hybrid environment.
- **Interval-Based Lunar Calculation**: Switched from noon-representative values to a full 24h interval search. The engine now identifies all Tithi/Nakshatra transitions within a day to ensure 100% accuracy in the marquee list.
- **High-Precision Ephemeris (Drik Siddhanta)**: Moved from simplified lunar math to a professional **ELP-2000 (Meeus)** model and **Lahiri Ayanamsha**. This ensures timings match professional Panchangams (like DrikPanchang) within ±1 minute, correcting the ~2-hour drift found in Vakya-based calendars.
- **Local Solar Computation**: Decoupled from external Sunrise/Sunset APIs in favor of a local mathematical model. This ensures offline reliability and allows user-selectable definitions (Scientific Top-Edge vs. Traditional Center-of-Disk).
- **Proportional vs. Fixed Slots**: Implemented a hybrid timing system for Rahu/Yama/Kuli. While traditional proportional division (daylight / 8) is the default for astronomical accuracy, a "Fixed 1.5h" toggle is provided for users following wall-calendar conventions.
- **North/South Convention Toggle**: Implemented a global switch for Amanta (South) and Purnimanta (North) lunar month systems to ensure universal applicability across India.
- **Navigation 3**: Reactive, state-driven routing managed in `MainActivity`.
- **Dispatcher Hoisting**: Manually providing `NavigationEventDispatcherOwner` at the root to fix Nav3/Compose integration crashes.
- **Transition-Synchronized Updates**: Widget refreshes are timed to `endTime` of slots, not periodic intervals, ensuring 100% accuracy.
- **AppCompat Localization**: Using `AppCompatDelegate.setApplicationLocales` for system-integrated, restart-free language switching (Android 13+).
- **Vedic Day (Vara) Continuity**: Implemented Vara-based logic for auspicious windows (like Maitra Muhurtham). The "day quality" (potency) is anchored to the sunrise-to-sunrise cycle, not calendar midnight, ensuring traditional accuracy for late-night windows.
- **Auspiciousness Color Gradation**: Introduced sub-levels of color coding within the "GOLD" category (e.g., Bright Gold vs. Pale Gold) to visually signal different potency levels of the same Muhurtham without introducing new conflicting colors.
- **Shared Ephemeris Source**: Pinned all Nirayana (Sidereal) calculations to a single public Lahiri Ayanamsha function to prevent "coordinate drift" between the lunar engine and the zodiac-rise engine.
