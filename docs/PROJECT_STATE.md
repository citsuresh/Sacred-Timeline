# Project State

**Phase**: Alpha/Stable (Phase 4 Complete, Phase 9 In-Progress)

## 1. Recently Implemented
- **Traditional Event Anchoring**: Fixed a critical bug where lunar events (Pradosham, Shivaratri, etc.) appeared on consecutive days. Implemented a ritual-window anchor system:
    - **Pradosham**: Tied to Sunset (Trayodashi during the 90-min Pradosha window).
    - **Shivaratri**: Tied to Nishita Kala (Krishna Chaturdashi during the midnight window).
    - **Udaya Vyapini**: Naga Chaturthi and Aadi festivals now anchor to the Tithi/Nakshatra at Sunrise, ensuring single-day occurrence.
- **Astronomical Engine Refinement**: Upgraded the high-precision engine to calculate local DST/Timezone offsets dynamically based on the target date, ensuring ±1 minute accuracy across all years.
- **Iterative "Harmony" Layout Engine**: Upgraded the **Equally Distributed** layout with an 8-pass virtual refinement algorithm. Boxes now mathematically converge to meet at the midpoint of any gap, filling dead space co-operatively while maintaining perfectly clean rectangles.
- **Custom Timeline & Persistence**: 
    - Introduced **"Custom Timeline"** as a first-class citizen in the side menu.
    - Implemented a **"Hidden until Born"** strategy: the menu shortcut only appears once the user makes a manual change in Settings.
    - Custom layouts are now **fully persisted**; switching to a "Standard View" (Universal, Horai, etc.) saves the custom configuration for one-click restoration.
- **Side Menu Synchronization**: 
    - Full two-way sync between the navigation drawer and Settings toggles. 
    - Added **Switch Confirmation Dialogs** to protect user-created custom layouts.
- **"Best Segment" Drawing Logic**: Fixed a critical rendering bug where stepped boxes appeared blank. The engine now intelligently scans all horizontal slices of a box and selects the **widest and tallest** segment to anchor text/icons.
- **Calendar-Style Tamil Date Anchor**: Replaced dead space in the timeline header with a high-density "Calendar Icon" widget showing the Tamil Month and Day, providing a traditional anchor for the vertical grid.
- **Localization Polish**: Integrated the full 60-year Tamil cycle, localized AM/PM labels (**மு.ப / பி.ப**), and standardized Muhurtham names across the dashboard and marquee.
- **Performance Optimization**: 
    - Implemented **"Smart Refresh"** logic to prevent full cache clears when changing non-math settings (like preload range).
    - Increased default preload range to **30 days** for seamless swiping.

## 2. Technical Stack
- **UI**: Kotlin, Jetpack Compose (Material 3), Jetpack Glance (Widgets).
- **Navigation**: Navigation 3.
- **Layout Logic**: Transitive Clustering + 8-Pass Iterative Harmony Refiner.
- **Persistence**: DataStore (Settings + Custom Layout Slot), Disk JSON (Cache).

## 3. Next Focus
- **Adaptive Layouts**: Optimization for Tablets/Foldables using List-Detail patterns.
- **Wear OS Support**: Complications and Tiles for quick timing checks.
- **Traditional Iconography**: Sourcing SVGs for Nandi, Shiva, and Ganesha icons.
