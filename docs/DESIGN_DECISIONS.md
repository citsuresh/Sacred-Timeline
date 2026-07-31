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
