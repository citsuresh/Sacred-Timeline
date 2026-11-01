---
name: vedic-astrology-engine
description: Provides architectural guidelines, high-precision astronomical algorithms (Meeus ELP-2000, Lahiri Ayanamsha, Sidereal Ascendant / Lagna, Vimsottari Dasa & Bhukti timeline calculation with birth nakshatra balance slicing), and Jetpack Compose canvas/grid UI patterns for rendering traditional South Indian Grid and North Indian Diamond astrological charts in Android apps.
---

# Vedic Astrology Engine & Chart Rendering Skill

This skill provides architectural guidance and implementation patterns for building Vedic Astrology (Jyotish) engines and traditional chart renderers in Android apps using Kotlin and Jetpack Compose.

## Core Capabilities Covered
1. **High-Precision Ephemeris & Lagna (Ascendant) Calculation**:
   - Meeus algorithms for GMST/LST, Lahiri Ayanamsha, and precise local horizon intersecting zodiac signs.
2. **Vimsottari Dasa & Bhukti Timeline Engine**:
   - Birth nakshatra fractional balance calculation, 120-year lifespan boundary enforcement, zero-gap Bhukti slicing, and dual-lane timeline visual tracking.
3. **Traditional Chart Canvas & Grid Rendering**:
   - South Indian 4x4 fixed rasi grid and North Indian diamond chart renderers with Lagna (`Lag`) and Janma Rasi (`Rasi`) highlighting.
