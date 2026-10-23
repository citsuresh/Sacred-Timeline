# Roadmap

Future development phases for the Sacred Timeline project.

## Phase 1: Precision & Context (Complete)
- **Localized Timings**: GPS-based sunrise/sunset calculation.
- **Multi-day Navigation**: Seamless swipeable timeline with preloading.
- **Interactive Header**: Robust, non-clipping UI for date and location management.

## Phase 2: Personalization (Complete)
- **Multi-Language Support**: Full English and Tamil support with in-app switching and persistence.
- **Advanced Settings**: Comprehensive user preferences for zoom, time format, and column management.

## Phase 3: Adaptive Ecosystem (In-Progress)
- **Adaptive Layouts**: Optimized UI for tablets and foldables using `ListDetailPaneScaffold`.
    - **Phone**: Sequential navigation (List -> Click -> Detail).
    - **Tablet**: Side-by-side view (Timeline + Detailed Significance).
- **Wear OS Support**: Complications and Tiles for quick timing checks.

## Phase 4: Traditional Depth & Personal Astrology (Complete)
- **Maitra Muhurtham Engine**: High-precision Lagna-based calculation for debt repayment windows.
- **Yogam, Tara Bala & Chandrashtamam**: Personal warning and strength systems tied to birth star.
- **Multi-Profile Ecosystem**: Active vs. Primary profile separation, avatar picker, and South/North chart renderers.

## Phase 5: Search & Accessibility Suite
- **Global Search**: Search for festivals, holidays, Maitra/Subha Muhurthams by year or date range.
- **Accessibility Suite**: Font scaling for Tamil script, high-contrast modes, and screen reader semantic descriptions.

## Phase 6: Performance & Hybrid Caching
- **Per-Day File Caching**: Granular O(1) disk caching for ephemeris data to optimize memory and date-jump performance.
- **Background Task Optimization**: Battery-friendly WorkManager scheduling with WakeLocks for reminder and widget reliability.

## Phase 7: UI/UX Refinement & Sticky Labels
- **Sticky Timeline Labels**: Floating text/icons within long timing cards as the user scrolls.
- **Bilingual Polish**: Perfect alignment for Tamil + English mixed-mode labels across sheets and cards.

## Phase 8: Traditional Iconography (Complete)
- **Icon Sourcing**: High-fidelity custom PNGs for Pradosham (Nandi), Shivaratri (Shiva), and Chaturthi (Ganesha).

## Phase 9: Configuration Presets & Custom Timelines (Complete)
- **Custom Timeline Persistence**: DataStore sandbox to remember user's manual column visibility and order.

## Phase 10: Widget Polish & Glance Theming
- **Dynamic Theming**: Adapting home screen glance widgets to Material You dynamic colors.
- **Glance Layout Variants**: Additional compact widget formats while preserving core glanceability ("Right Now" status + manual refresh).

## Phase 11: Auspiciousness Scoring Engine & Gauge (1–100%)
- **Weighted Multi-Factor Scoring**: Real-time evaluation combining active Horas, Nalla Neram, Gowri Neram, Rahu Kalam, Maitra/Brahma Muhurthams, and personal birth star / Chandrashtamam status into a 1–100% favorability score.
- **Visual Gauge UI**: Circular arc or linear progress indicator on the dashboard header / detail sheet providing an at-a-glance percentage rating of the present moment.
