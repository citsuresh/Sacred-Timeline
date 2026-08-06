# Design Decisions

Historical record of architectural and UI/UX choices.

## UI & Layout
- **24-Hour Vertical Timeline**: Ensures all slots (Gowri/Hora) are visible; "NOW" indicator centered for immediate focus.
- **High-Fidelity Spiritual Icons**: Switched from simplified code-drawn SVGs to user-provided PNGs for Nandi, Shiva, and Ganesha to ensure 100% traditional accuracy.
- **Double-Border (Sticker Look)**: All timing cards and custom icons (like Vel) use white outer and black inner borders (or vice versa based on mode) to remain legible on any wallpaper or theme.
- **Marquee Header**: 
    - **Events Row (Line 2)**: Uses auto-scrolling (Marquee) to show all Tithis, Nakshatras, and Festivals occurring in the day, with explicit Start/End times.
    - **Timings Row (Line 3)**: Sunrise/Sunset are fixed at edges; Muhurthams (Brahma/Abhijit) marquee in the center.
- **Header Row Terminology**:
    - **Calendar Row**: Line 1 (Tamil Date, Paksha).
    - **Events Row**: Line 2 (Marquee: Festivals, Tithi, Nakshatra).
    - **Timings Row**: Line 3 (Sun times, Brahma Muhurtham).
- **Interactive Scrolling Headers**: Replaced `Modifier.basicMarquee` with a custom `AutoScrollingRow` using `ScrollState`. Traditional marquees in Compose only animate drawing, leaving touch targets behind. The custom implementation ensures that significance details can be accessed by clicking any item while it is moving.

## Architecture
- **Compositional Theme Switching**: Theme management (Light/Dark/System) is handled purely within the Compose root to avoid Activity recreation loops associated with `AppCompatDelegate` in a hybrid environment.
- **Interval-Based Lunar Calculation**: Switched from noon-representative values to a full 24h interval search. The engine now identifies all Tithi/Nakshatra transitions within a day to ensure 100% accuracy in the marquee list.
- **Navigation 3**: Reactive, state-driven routing managed in `MainActivity`.
- **Dispatcher Hoisting**: Manually providing `NavigationEventDispatcherOwner` at the root to fix Nav3/Compose integration crashes.
- **Transition-Synchronized Updates**: Widget refreshes are timed to `endTime` of slots, not periodic intervals, ensuring 100% accuracy.
- **AppCompat Localization**: Using `AppCompatDelegate.setApplicationLocales` for system-integrated, restart-free language switching (Android 13+).
