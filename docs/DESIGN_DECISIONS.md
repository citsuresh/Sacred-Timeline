# Design Decisions

This document records the key architectural and UI/UX decisions for Sacred Timeline.

## Decision 1: Navigation 3
**Status**: Implemented
**Description**: Use **Jetpack Navigation 3** for state-driven routing.
**Reasoning**: It provides a declarative, reactive approach to navigation that aligns perfectly with Compose. By managing the backstack as state, we ensure a single source of truth for the app's navigation lifecycle.

## Decision 2: 24-Hour Vertical Timeline
**Status**: Implemented
**Description**: A 24-hour vertical timeline with a fixed **"NOW" indicator** at the screen center.
**Reasoning**: The 24-hour coverage ensures no timing slot (Gowri or Hora) is missed. Centering the "NOW" indicator provides immediate focus on the current time and upcoming events, reducing the need for manual scanning.

## Decision 3: Jetpack Glance for Widgets
**Status**: Implemented
**Description**: Use **Jetpack Glance** for the home-screen widget.
**Reasoning**: Glance allows us to build app widgets using a Compose-like syntax, ensuring UI consistency between the main app and the widget while leveraging the efficiency of remote views.

## Decision 4: Double-Border UI (Sticker Look)
**Status**: Implemented
**Description**: All timing cards use an outer white border and inner black border.
**Reasoning**: This "sticker" look ensures that timing blocks are clearly defined regardless of whether the app/device is in light or dark mode, or what the home screen wallpaper looks like for the widget.

## Decision 5: Transition-Synchronized Updates
**Status**: Implemented
**Description**: Widget updates are triggered at the exact moment a time slot ends.
**Reasoning**: Conventional periodic updates (e.g., every 15m) can leave the widget showing stale data for up to 14 minutes. Scheduling updates based on timing `endTime` ensures real-time accuracy for Panchangam events.
