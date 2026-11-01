# Resolve

A private, offline streak-tracking app for building a self-improvement habit —
redesigned in the **Editorial Luxe** visual system. Everything is stored on the
device: no accounts, no ads, no network calls.

## Download

**[Download the latest APK](dist/Resolve-2.1.apk?raw=true)** (v2.1 · Editorial Luxe)

On your phone: enable "Install unknown apps" for your browser/file manager, then
open the downloaded APK to install. Installs as an update over v1.x and keeps
your data.

> The release APK is signed with the Android **debug** key — fine for
> sideloading, not for the Play Store. To publish, generate your own upload key.

## Design — Editorial Luxe

Deep warm charcoal foundations, restrained bronze and amber accents, occasional
ivory focal cards, **Playfair Display** for display/editorial type and **Inter**
for UI/body (both bundled locally, so the look works fully offline). Two designed
themes: **Editorial Luxe** (dark) and **Ivory Bronze** (light).

## Features

- **Live streak counter** — day count + running HH:MM:SS clock + clean-since date.
- **Evolving rank medal** — an original flame emblem through 13 ranks
  (Spark → Ember → … → Legend) across a bronze→steel→gold→jade→amethyst→platinum
  material progression.
- **Ranks gallery** with locked/unlocked states and a **badge detail sheet**
  (meaning, requirement, status, earned date, next rank).
- **Achievement celebration** when a new rank is reached.
- **Focus Mode** — full-screen, distraction-free session: guided breathing,
  session timer, affirmations, and an "urge resisted" counter.
- **Journal** — mood check-in + note, history with delete-confirm, empty state,
  and a 7-day activity strip on More.
- **Your Why**, **daily reminder notifications**, **home-screen widget**,
  **theme switch**, and a clearly separated **reset** with a confirmation sheet.

## How it's built

A native Android WebView shell (`MainActivity`) loads a single self-contained
`app/src/main/assets/index.html`. State is kept in `localStorage` (schema key
`resolve.v1`); a JS↔native bridge syncs the streak to `SharedPreferences` for
the widget, and native code handles reminders (`Native`/`ReminderReceiver`/
`BootReceiver`) and the widget (`StreakWidget`). Fonts are bundled under
`app/src/main/assets/fonts/`.

## Build

Requires the Android SDK (platform 34, build-tools 34.0.0) and a JDK 17+.

```bash
echo "sdk.dir=/path/to/Android/sdk" > local.properties
./gradlew assembleRelease
```

The APK lands in `app/build/outputs/apk/`.
