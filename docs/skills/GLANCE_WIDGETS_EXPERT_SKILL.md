# Expert Skill: Advanced Jetpack Glance App Widgets Blueprint

This skill guide captures production-tested architectural patterns for building advanced Android App Widgets using **Jetpack Glance**, supporting multi-widget architectures, runtime canvas bitmap rendering, responsive scaling, and instant settings synchronization.

## 1. Multi-Widget Architecture (Multiple Independent Widgets)
To allow users to select multiple distinct widgets from the Android desktop widget picker:
- Create separate `GlanceAppWidget` classes (e.g. `ScoreGaugeWidget`, `PowerMeterWidget`, `PanchangamWidget`).
- Create a corresponding `GlanceAppWidgetReceiver` for each widget class.
- Register every receiver in `AndroidManifest.xml` with distinct labels:
  ```xml
  <receiver
      android:name=".widget.ScoreGaugeWidgetReceiver"
      android:exported="true"
      android:label="Score Gauge">
      <intent-filter>
          <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
      </intent-filter>
      <meta-data
          android:name="android.appwidget.provider"
          android:resource="@xml/score_gauge_widget_info" />
  </receiver>
  ```

## 2. Rich Widget Picker Previews (`android:previewLayout`)
To display gorgeous visual preview thumbnails in the Android widget picker instead of generic app icon placeholders:
- Create static preview layout XMLs in `res/layout/` (e.g., `widget_preview_score_gauge.xml`).
- Reference them in your appwidget provider XMLs in `res/xml/`:
  ```xml
  <appwidget-provider ...
      android:previewLayout="@layout/widget_preview_score_gauge" />
  ```

## 3. Runtime Canvas-to-Bitmap Rendering (`SpeedometerBitmapUtils`)
Since Jetpack Glance does not support native runtime `Canvas` or custom path drawing in composables:
- Implement a utility class that draws custom graphics (arcs, needles, badges) onto an Android `Bitmap` using native `Canvas` and `Paint`.
- Display the generated bitmap inside Glance using an `Image` component:
  ```kotlin
  val bitmap = SpeedometerBitmapUtils.createSpeedometerBitmap(score)
  Image(
      provider = ImageProvider(bitmap),
      contentDescription = null,
      modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
      contentScale = ContentScale.Fit
  )
  ```

## 4. Responsive Sizing (`SizeMode.Responsive`)
To ensure widgets scale gracefully when resized across 1x1, 2x2, and 3x3 grid cells:
- Define size tiers in your `GlanceAppWidget`:
  ```kotlin
  override val sizeMode: SizeMode = SizeMode.Responsive(
      setOf(
          DpSize(60.dp, 60.dp),
          DpSize(120.dp, 120.dp),
          DpSize(200.dp, 200.dp),
          DpSize(300.dp, 300.dp)
      )
  )
  ```
- Use `LocalSize.current` to adapt typography and layout constraints in real-time.

## 5. Instant Settings Synchronization
To ensure widgets update immediately when settings (like background opacity or columns) change:
- Invoke `.updateAll(context)` synchronously on your Glance widget classes inside your `SettingsViewModel`:
  ```kotlin
  viewModelScope.launch {
      repository.setWidgetBackgroundOpacity(opacity)
      ScoreGaugeWidget().updateAll(getApplication())
      WidgetUpdateWorker.triggerImmediateUpdate(getApplication())
  }
  ```
