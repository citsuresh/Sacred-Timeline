# Roadmap

Future development phases for the Sacred Timeline project.

## Phase 1: Precision & Context (Complete)
- **Localized Timings**: Integration with GPS services to calculate sunrise/sunset based on exact coordinates.
- **Multi-day Navigation**: Seamless swipeable timeline with preloading.
- **Interactive Header**: Robust, non-clipping UI for date and location management.

## Phase 2: Personalization (Complete)
- **Multi-Language Support**: Full English and Tamil support with in-app switching and persistence.
- **Advanced Settings**: Comprehensive user preferences for zoom, time format, and column management.

## Phase 3: Adaptive Ecosystem
- **Adaptive Layouts**: Optimized UI for tablets and foldables using `ListDetailPaneScaffold`.
- **Extended Platform Support**: Exploring Wear OS tiles for quick timing checks.

## Phase 4: Precision Refinement & Traditional Depth
- **High-Precision Ephemeris**: Upgrade `LunarCalendarUtils` to use a more accurate astronomical model (e.g., increased perturbation terms or a native wrapper for an ephemeris library) to fix Nakshatra/Tithi discrepancies.
- **Traditional Calculations**: Implement **Yogam** and **Chandrashtamam** logic based on the refined lunar engine.
- **Enhanced Festive Detection**: Automate detection of specific traditional events like "Aadi Kiruthigai" (Nakshatra-based festivals) and "Pradosham".
- **Dynamic Timing Labels**: Add "Till [Time]" labels for Tithis and Nakshatras to match traditional calendar layouts.
- **Midnight Auto-Transition**: Add a "Midnight Watcher" to automatically scroll the timeline to the new day at 12:00 AM (Nice to have).
