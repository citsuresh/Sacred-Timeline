# Code Summary & Structural Map

Technical overview of the project architecture and data flow.

## 1. Module & Data Flow
- **:app**: Monolithic module containing all features.
- **Architecture**: MVI-lite (StateFlow driven UI).

```mermaid
graph TD
    PanchangamCalculator["PanchangamCalculator (Logic)"]
    MockPanchangamProvider["MockPanchangamProvider (Data)"]
    TimelineViewModel["TimelineViewModel (State)"]
    TimelineDashboard["TimelineDashboard (UI)"]
    PanchangamWidget["PanchangamWidget (Glance Widget)"]
    WidgetUpdateWorker["WidgetUpdateWorker (WorkManager)"]

    PanchangamCalculator --> MockPanchangamProvider
    MockPanchangamProvider --> TimelineViewModel
    TimelineViewModel --> TimelineDashboard
    MockPanchangamProvider --> PanchangamWidget
    WidgetUpdateWorker --> PanchangamWidget
```

## 2. Layer Responsibilities

### Domain/Logic (`logic/`)
- `PanchangamCalculator`: Core engine for Nalla Neram (sunrise-relative), Gowri Neram (8 slots), and Hora (planetary hours).

### UI (`ui/`)
- `ui/dashboard`: 24h vertical timeline, `TimelinePager` (multi-day swipe), `TimingCard` (sticker-look blocks).
- `ui/settings`: Persistence-linked preferences (Locale, Zoom, Column mgmt).
- **Navigation**: Navigation 3 (backstack-as-state).

### Data (`data/`)
- `SettingsRepository`: Jetpack DataStore for user preferences.
- `CacheManager`: Shared JSON caching for App/Widget offline support.

## 3. Infrastructure
- **Background**: `WorkManager` synchronized with time-slot transitions.
- **Localization**: `AppCompatDelegate` for dynamic English/Tamil switching.

## 4. Symbol Index
| Class | Responsibility |
| :--- | :--- |
| `PanchangamCalculator` | Math for all time slots. |
| `MainActivity` | App lifecycle & Navigation host. |
| `SettingsRepository` | Source of truth for preferences. |
| `PanchangamWidget` | Glance-based Home Screen summary. |
| `Metadata` | UI mapping for localized strings/icons. |
