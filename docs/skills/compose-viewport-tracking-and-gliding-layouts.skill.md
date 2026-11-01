---
name: compose-viewport-tracking-and-gliding-layouts
description: Provides architectural patterns and implementation recipes for building high-performance custom vertical timelines, 24-hour grid layouts, and layout-phase (Modifier.offset { ... }) viewport-tracking / sticky-gliding UI elements in Jetpack Compose without composition-phase lag or scroll jank.
---

# Jetpack Compose Viewport-Tracking & Gliding Layouts

## 1. Overview
This skill provides production-tested architectural patterns for developing complex, high-density 24-hour vertical timelines and custom layout-phase scroll effects in Jetpack Compose. It covers multi-track lane calculations, bounding box management, and zero-jank scroll-clamped text gliding using layout-phase modifiers.

---

## 2. Core Architectural Patterns

### A. Layout-Phase Scroll Tracking (`Modifier.offset { ... }`)
To achieve 60fps buttery-smooth scrolling without forcing expensive full-card recompositions on every pixel of scroll:
- **Avoid Composition-Phase State Reading**: Do not pass raw scroll offsets as state parameters that trigger recomposition of complex child cards.
- **Use Layout-Phase Modifier Lambdas**:
  ```kotlin
  Modifier.offset {
      val currentScroll = scrollState.value.toFloat()
      val screenY = cardTopPx - currentScroll
      val dy = when {
          screenY >= 0f -> 0f
          else -> (-screenY).coerceIn(0f, (cardHeightPx - textHeightPx).coerceAtLeast(0f))
      }
      IntOffset(0, dy.toInt())
  }
  ```
  This lambda executes purely during the layout/draw phase on every scroll tick, bypassing composition overhead entirely.

### B. Symmetrical Viewport Clamping (`maxOf` / `coerceIn`)
When implementing content that needs to rest in a default position (e.g., vertical centering) when fully visible, but glide/stick to the screen edge when scrolling off-screen:
- **Resting Zone**: `cardScreenY >= 0f` -> default alignment (`centerOffsetY`).
- **Gliding Zone**: `cardScreenY < 0f` -> track viewport top (`-screenY`) clamped between `0f` and `cardHeightPx - textHeightPx`.

---

## 3. Best Practices
1. **Dynamic Text Measurement**: Use `Modifier.onGloballyPositioned` to measure the true rendered height of multi-line titles, star icons, and sub-horas at runtime rather than relying on fixed height estimates.
2. **State-Based Scroll Triggers**: For programmatic jumps (e.g., "Go to Now"), use `StateFlow<Long>` timestamps (`_scrollToNowEvent.value = System.currentTimeMillis()`) observed in `LaunchedEffect` to guarantee reliable UI animation triggers.
