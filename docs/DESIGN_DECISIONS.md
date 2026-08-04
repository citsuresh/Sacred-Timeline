# Design Decisions

Historical record of architectural and UI/UX choices.

## UI & Layout
- **24-Hour Vertical Timeline**: Ensures all slots (Gowri/Hora) are visible; "NOW" indicator centered for immediate focus.
- **High-Fidelity Spiritual Icons**: Switched from simplified code-drawn SVGs to user-provided PNGs for Nandi, Shiva, and Ganesha to ensure 100% traditional accuracy.
- **Double-Border (Sticker Look)**: All timing cards and custom icons (like Vel) use white outer and black inner borders (or vice versa based on mode) to remain legible on any wallpaper or theme.
- **Marquee Header**: Line 2 of the header uses auto-scrolling (Marquee) to prevent clipping when multiple festivals or holidays occur on the same day.

## Architecture
- **Compositional Theme Switching**: Theme management (Light/Dark/System) is handled purely within the Compose root to avoid Activity recreation loops associated with `AppCompatDelegate` in a hybrid environment.
- **Noon-Based Lunar Calculation**: Tithi and Nakshatra are calculated at 12:00 PM for each day to provide the most "representative" value for the day's dashboard view.
- **Navigation 3**: Reactive, state-driven routing managed in `MainActivity`.
- **Dispatcher Hoisting**: Manually providing `NavigationEventDispatcherOwner` at the root to fix Nav3/Compose integration crashes.
- **Transition-Synchronized Updates**: Widget refreshes are timed to `endTime` of slots, not periodic intervals, ensuring 100% accuracy.
- **AppCompat Localization**: Using `AppCompatDelegate.setApplicationLocales` for system-integrated, restart-free language switching (Android 13+).
