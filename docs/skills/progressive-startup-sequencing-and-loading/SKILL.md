---
name: progressive-startup-sequencing-and-loading
description: Provides architectural patterns for sequencing asynchronous cold starts in Android Jetpack Compose and ViewModels, preventing race conditions and double-refreshes when collecting DataStore flows, and implementing granular progressive loading states with localized status messages.
---

# Progressive Startup Sequencing & Loading Feedback

## Overview
When initializing an app with multiple dependent configuration flows (e.g., location settings, birth profiles, and preloaded date ranges), independent coroutine collectors can trigger simultaneous `preloadData()` calls on cold start, causing double-refreshes and UI jank. This skill outlines the pattern for strict sequential startup and progressive loading feedback.

## Key Principles
1. **Strict Sequential Initialization**: Load critical configuration (such as location settings and disk cache) first in a unified startup block before triggering data calculation (`preloadData`).
2. **Isolate Subsequent Updates**: Use `.drop(1)` on background observers (like location or settings change listeners) so they only react to active user changes, rather than firing redundantly on app open.
3. **Progressive Loading States**: Enhance `TimelineUiState.Loading` to carry a descriptive message (e.g., `"Loading location…"`, `"Calculating Panchangam & Ephemeris…"`), displaying both a circular progress indicator and status text during heavy computations or uncached date jumps.
