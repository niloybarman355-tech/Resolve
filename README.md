# Resolve

A private, offline streak-tracking app for building a self-improvement habit.
Everything is stored on the device — no accounts, no ads, no network calls.

## Download

**[Download the latest APK](dist/Resolve-1.3.apk?raw=true)** (v1.3)

On your phone: enable "Install unknown apps" for your browser or file manager,
then open the downloaded APK to install.

> The release APK is signed with the Android **debug** key. That's fine for
> sideloading, but not for the Play Store. To publish, generate your own upload
> key and configure a real `signingConfig`.

## Features

- **Live streak counter** — day count plus a running HH:MM:SS clock since your last reset.
- **Evolving rank badge** — an original emblem that levels up through 13 ranks
  (Spark → Ember → … → Legend) as milestones are reached.
- **Progress ring** to the next rank, and a full **Badges** gallery.
- **Focus Mode** — a full-screen, distraction-free session with guided breathing,
  a session timer, rotating affirmations, and an "urge resisted" counter.
- **Daily check-in journal** with mood, and a 7-day insight strip.
- **Your why** — a personal motivation line shown on Home.
- **Accent themes** and light/dark mode.

## How it's built

A lightweight native Android shell (`WebView`) that loads a single self-contained
HTML/CSS/JS app from `app/src/main/assets/index.html`. State is kept in the
WebView's `localStorage`. No third-party runtime dependencies beyond AndroidX
`appcompat` and `webkit`.

## Build from source

Requires the Android SDK (platform 34, build-tools 34.0.0) and a JDK 17+.

```bash
echo "sdk.dir=/path/to/Android/sdk" > local.properties

./gradlew assembleDebug      # debug build
./gradlew assembleRelease    # release build (debug-signed, for sideloading)
```

The APK lands in `app/build/outputs/apk/`.
