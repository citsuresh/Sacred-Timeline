# Sacred Timeline

**Panchangam & Spiritual Timings Visualizer**

Sacred Timeline is a professional, high-precision Android application designed to help users visualize Panchangam timings, micro-planetary hours, and traditional astrological insights with absolute astronomical accuracy.

---

## Key Features

- **24-Hour Vertical Timeline**: A comprehensive view of the entire day's spiritual, solar, and planetary timings with a real-time "NOW" indicator.
- **Color-Coded Auspiciousness**: Instantly identify favorable (Gold/Green), neutral, and unfavorable (Red) periods through an intuitive dual-tone color system.
- **Sub-Horai (Micro-Planetary Hours)**: Ephemeris-derived proportional sub-divisions of Horas across all 7 weekdays complete with planetary compatibility and interactive 350.dp detail sheets.
- **Yogam Detection**: Automatic calculation of Amrita, Siddha, Marana, and Vadha Yogas based on Weekday + Nakshatra combinations.
- **Thara Balam (Daily Strength)**: Personal daily strength calculation relative to the user's birth star (Janma, Sampat, Vipat, Kshema, etc.).
- **Chandrashtamam Warning System**: Monthly 8th moon-sign personal warning alert relative to birth rasi.
- **Maitra Muhurtham**: High-precision Lagna-based calculations for debt repayment and auspicious new beginnings.
- **Advanced Event Reminders & WorkManager**: Full-stack reminder system supporting relative offsets and recurring events, powered by Jetpack WorkManager with WakeLocks and 12 dedicated 1-to-1 notification channels.
- **Bilingual Tamil & English Parity**: Complete in-app language switching and full translation parity across English (`values/`) and Tamil (`values-ta/`).
- **Home-Screen Widget**: Access critical daily timings and panchangam at a glance using Jetpack Glance widgets.

---

## Astronomical Precision & Methodology

### Drik (Thirukanitha) vs. Vakya
This app calculates all planetary, lunar, and solar positions using the **Drik (Thirukanitha) Siddhanta** (ELP-2000 Meeus model and Lahiri Ayanamsha). This is the modern scientific standard used by professional astronomers and high-precision Panchangams.

> [!NOTE]
> **Why our timings may differ from other Tamil calendar apps:**
> Traditional "Pambu" calendars use the ancient **Vakya Siddhanta**, which has not been updated for Earth's slow precession and drifts by ~2 hours. Sacred Timeline provides astronomical truth synchronized with the actual sky.

### Sunrise & Sunset Precision
Calculations are performed locally using a **Local High-Precision Solar Engine** (Meeus/NOAA algorithm):
- **Sub-Minute Accuracy**: Computed based on your exact GPS coordinates.
- **Scientific vs. Traditional Definitions**: Toggle between "Scientific (Top Edge)" refraction-adjusted sunrise and "Traditional (Center of Disk)" in Settings.

### Regional Conventions
- **Amanta (South India)**: Lunar month ends on the New Moon (Default).
- **Purnimanta (North India)**: Lunar month ends on the Full Moon, toggleable in Settings.

---

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation**: Navigation 3
- **Background Processing**: Jetpack WorkManager (`ReminderWorker`, `WidgetUpdateWorker`)
- **App Widgets**: Jetpack Glance
- **Storage**: DataStore (Settings) & Atomic JSON Disk Caching

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/sacred-timeline.git
   ```
2. Open the project in Android Studio (Ladybug or newer).
3. Sync Project with Gradle Files.
4. Run the app on an emulator or a physical device.

---

## License

[License Placeholder]
