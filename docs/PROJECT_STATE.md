# Project State

**Phase**: Alpha/Stable (Phase 3 Complete)

## 1. Recently Implemented
- **Header Significance Details**: Added a details pane (BottomSheet) for every item in the header (Tithi, Nakshatra, Events, Muhurthams) with traditional guidance and precise start/end times.
- **Interactive Marquees**: Replaced `basicMarquee` with a custom `AutoScrollingRow` to ensure all scrolling header items remain 100% clickable during animation.
- **Timeline Timing Toggles**: New settings to show/hide Sunrise, Sunset, and Brahma Muhurtham.
- **Brahma Muhurtham Display**: Explicit window calculation and UI integration in the dashboard.
- **Header Standardization**: Established three-row layout terminology: Calendar Row, Events Row, and Timings Row.
- **Traditional Tamil Calendar**: Full Lunar Engine for Tithi, Nakshatra, and Paksha calculations.
- **Religious Events**: Integrated 2024-2026 TN Public Holidays and Subha Muhurthams.
- **Spiritual UI**: High-fidelity PNG icons for Shiva, Ganesha, and Nandi; refined custom Vel icon with dynamic visibility borders.
- **App Theme Switcher**: Persistent System/Light/Dark mode selection integrated into Compose theme.
- **Precision Timing Ranges**: Tithis and Nakshatras now display full "Start" and "End" times with multiday context (Yesterday/Tomorrow).
- **Muhurtham Marquee**: Added central marquee for Brahma and Abhijit Muhurthams with fixed Sunrise/Sunset positions.
- **Header Synchronization**: All Tithi/Nakshatra elements occurring in a 24h window are now listed in the marquee to prevent missed transitions.
- **Timeline Display Customization**: Detailed toggles for individual Tithis and Nakshatras.
- **Enhanced Localized Labels**: Added specific labels for `Starts`, `Ends Tomorrow`, and `Started Yesterday` in English and Tamil.

## 2. Technical Stack
- **UI**: Kotlin, Jetpack Compose (Material 3), Jetpack Glance (Widgets).
- **Navigation**: Navigation 3.
- **Persistence**: DataStore (Settings), Disk JSON (Cache).
- **Minimum SDK**: 26 (Android 8.0).
- **Target SDK**: 34 (Android 14).

## 3. Next Focus
- **Adaptive Layouts**: Optimization for Tablets/Foldables using List-Detail patterns.
- **Wear OS Support**: Complications and Tiles for quick timing checks.
