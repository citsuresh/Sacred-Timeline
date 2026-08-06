# Sacred Timeline

**Panchangam Timings Visualizer**

Sacred Timeline is a modern Android application designed to help users visualize Panchangam timings with ease and precision.

## Features
- **24h Timeline**: A comprehensive view of the entire day's spiritual and astronomical timings.
- **Color-coded auspiciousness**: Instantly identify favorable and unfavorable periods through an intuitive color system.
- **Date navigation**: Easily browse through past and future dates to plan ahead.
- **Home-screen widget**: Access critical timings directly from your home screen without opening the app.
- **Astronomical Precision**: Uses high-precision algorithms (ELP-2000) for sub-minute accuracy.

## Astronomical Precision & Methodology

### Drik (Thirukanitha) vs. Vakya
This app calculates all planetary, lunar, and solar positions using the **Drik (Thirukanitha) Siddhanta**. This is the modern scientific standard used by professional astronomers and high-precision Panchangams (like DrikPanchang.com).

> [!NOTE]
> **Why our timings may differ from other Tamil calendar apps:**
> Many traditional Tamil apps and "Pambu" calendars use the **Vakya Siddhanta**, which relies on ancient mnemonic formulas written over 1,500 years ago. Because these formulas have not been updated for the Earth's slow precession, they are currently out of sync with the actual sky by roughly **2 hours**. 
>
> **Sacred Timeline provides the "Scientific Truth"**: If the app says a Nakshatra starts at 8:13 PM, that is the exact moment the Moon physically enters that constellation in the sky.

### Sunrise & Sunset Precision
Unlike many apps that use hardcoded tables or simple approximations, Sacred Timeline uses a **Local High-Precision Solar Engine** (Meeus/NOAA algorithm).
- **Sub-minute accuracy**: Calculations are performed locally based on your exact GPS coordinates.
- **Scientific Definition**: By default, we use the "Top Edge" (Apparent) sunrise, accounting for atmospheric refraction—matching what your eyes actually see.
- **Traditional Support**: Users can toggle to the "Center of Disk" definition in Settings to match specific traditional rituals.

### Regional Conventions
- **Amanta (Default)**: Follows the South Indian (Tamil Nadu, Karnataka, AP) convention where the lunar month ends on the New Moon.
- **Purnimanta**: Supports the North Indian convention where the month ends on the Full Moon, toggleable in Settings.

### Privacy & Reliability
- **100% Offline**: All astronomical math is performed locally on your device. No internet connection is required for calculations.
- **GPS Privacy**: Your location is used only for local coordinate-based math. It is never uploaded to any server.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation**: Navigation 3
- **Background Processing**: WorkManager
- **App Widgets**: Glance

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/sacred-timeline.git
   ```
2. Open the project in Android Studio (Ladybug or newer).
3. Sync Project with Gradle Files.
4. Run the app on an emulator or a physical device.

## License
[License Placeholder]
