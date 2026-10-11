# Roadmap

Future development phases for the Sacred Timeline project.

## Phase 1: Precision & Context (Complete)
- **Localized Timings**: Integration with GPS services to calculate sunrise/sunset based on exact coordinates.
- **Multi-day Navigation**: Seamless swipeable timeline with preloading.
- **Interactive Header**: Robust, non-clipping UI for date and location management.

## Phase 2: Personalization (Complete)
- **Multi-Language Support**: Full English and Tamil support with in-app switching and persistence.
- **Advanced Settings**: Comprehensive user preferences for zoom, time format, and column management.

## Phase 3: Adaptive Ecosystem (In-Progress)
- **Adaptive Layouts**: Optimized UI for tablets and foldables using `ListDetailPaneScaffold`.
    - **Phone**: Sequential navigation (List -> Click -> Detail).
    - **Tablet**: Side-by-side view (Timeline + Detailed Significance) to eliminate back-and-forth navigation.
- **Wear OS Support**: Complications and Tiles for quick timing checks.

## Phase 4: Traditional Depth (Complete)
- **Maitra Muhurtham Engine**: High-precision Lagna-based calculation for debt repayment windows (Aries/Scorpio alignment).
- **Yogam & Chandrashtamam**: 
    - **Chandrashtamam**: Monthly moon-sign warning system relative to birth star.
    - **Amritadhi Yogams**: Detection for universal daily combinations (Amrita, Siddha, Marana Yoga) based on Weekday + Nakshatra.
    - **Tara Bala**: Personal strength calculation relative to birth star (Janma, Sampat, Vipat, etc.). Fully integrated with Detail Panel birth star context.

## Phase 5: Search & Accessibility
- **Global Search**: Search for festivals, holidays, Maitra/Subha Muhurthams by year or date range.
- **Accessibility Suite**: 
    - Font scaling for Tamil script.
    - High-contrast modes.
    - Screen reader support for Panchangam terms.

## Phase 6: Performance & Reliability
- **Background Optimization**: Battery-friendly background tasks for widget refreshes.
- **Data Accuracy Audit**: Continuous cross-check of Hora/Neram logic with traditional references.
- **Backup & Restore**: Export/Import user settings and column configurations.

## Phase 7: UI/UX Refinement
- **Layout Tuning**: Portrait vs. Landscape specific optimizations.
- **Sticky Timeline Labels (Floating Text)**: Implement "Sticky" labels for long timing cards (Yogam, Thara Balam, etc.).
    - Text and icons will dynamically float within the visible portion of a box as the user scrolls.
    - Ensures key information is always visible without manual scrolling to the center of a block.
    - Context-aware support for Orthogonal Stepped views (finding the "best" visible segment).
- **Multi-column Polish**: Smooth navigation and density management for the "Universal" view.
- **Bilingual Labels**: Perfect alignment for Tamil + English mixed-mode labels.

## Phase 8: Traditional Iconography
- **Icon Sourcing**: 
    - Verify and integrate high-quality SVGs for Pradosham (Nandi), Shivaratri (Shiva), and Chaturthi (Ganesha).
    - Establish a consistent "Sticker Style" icon set for Subha Muhurthams, special Tithis, and Nakshatras.

## Phase 9: Configuration Presets & Customization (In-Progress)
- **Custom Timeline Persistence [COMPLETE]**: Sandbox slot in DataStore to remember user's manual column visibility and order.
- **Side Menu Integration [COMPLETE]**: One-click restoration of custom setups with "Hidden until Born" shortcut logic.
- **Advanced Column Picker**: Future dedicated sub-screen for even more granular control.

## Phase 10: Multi-Profile & Personalized Astrology Ecosystem (Roadmap)
- **Top-Bar Profile Switcher**: 
    - Quick profile avatar picker in the top app bar.
    - Support for selecting preset avatars or uploading custom profile pictures (Image Uri / local storage).
    - Instant timeline re-calculation, refresh, and rendering tailored to the active profile's computed Lagna, Rasi, and Birth Star.
    - **Architectural Complexity & Design Notes**: While data computation is lightweight on-device, multi-profile state propagation requires careful Flow scoping (`StateFlow` keyed by `activeProfileId`), DataStore migration to a multi-profile schema (`profiles_v1.json`), and thread-safe caching.
- **Profile-Tied & Shared Reminders**:
    - Dual reminder scoping: **Common/Shared Reminders** (applicable across all profiles, e.g. major public festivals or Ekadashi vrats) vs. **Profile-Specific Reminders** (personalized Nakshatra/Thara Balam/Hora alerts tied strictly to a specific user profile).
- **Search for Next Auspicious Slot**:
    - Activity-based search engine allowing users to select an activity, query forthcoming favorable slots, and create reminders directly from search results.
