# Design Decisions

Historical record of architectural and UI/UX choices.

## UI & Layout
- **24-Hour Vertical Timeline**: Ensures all slots (Gowri/Hora) are visible; "NOW" indicator centered for immediate focus.
- **Double-Border (Sticker Look)**: All timing cards use white outer and black inner borders to remain legible on any wallpaper or theme.
- **Tamil Space Optimization**: Shortened AM/PM markers ("மு.ப"/"பி.ப") and stacked them vertically to keep the time column narrow.

## Architecture
- **Navigation 3**: Reactive, state-driven routing managed in `MainActivity`.
- **Dispatcher Hoisting**: Manually providing `NavigationEventDispatcherOwner` at the root to fix Nav3/Compose integration crashes.
- **Transition-Synchronized Updates**: Widget refreshes are timed to `endTime` of slots, not periodic intervals, ensuring 100% accuracy.
- **AppCompat Localization**: Using `AppCompatDelegate.setApplicationLocales` for system-integrated, restart-free language switching (Android 13+).
