# Project State

**Phase**: Alpha/Stable (Phase 2 Complete)

## 1. Recently Implemented
- **Multi-Language Support**: Persistent English/Tamil switching via `AppCompatDelegate`.
- **UI Optimization**: Shortened/stacked Tamil AM/PM markers, dynamic wrapping for sun indicators.
- **Stability Fixes**: Resolved Navigation 3 dispatcher crashes and Activity recreation loops.
- **Advanced Settings**: Per-view zoom levels, 24h format toggle, and column reordering.
- **Widget Reliability**: Transition-synchronized refreshes and shared JSON cache.

## 2. Technical Stack
- **UI**: Kotlin, Jetpack Compose (Material 3), Jetpack Glance (Widgets).
- **Navigation**: Navigation 3.
- **Persistence**: DataStore (Settings), Disk JSON (Cache).
- **Minimum SDK**: 26 (Android 8.0).
- **Target SDK**: 34 (Android 14).

## 3. Next Focus
- **Tamil Month/Year**: Integration of the traditional Tamil calendar system.
- **User Thresholds**: Customizable auspiciousness levels.
