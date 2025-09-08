# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Essential Launcher is a minimal Android launcher (<30KB APK) providing basic functionality with an app drawer, home screen widgets, and a 4-app dock. This is a decompiled version of the v2.2 APK from clemensbartz's Essential Launcher, updated to work with modern Android Studio and Gradle 7.5.

## Build Commands

**Build the APK:**
```bash
./gradlew assembleRelease
```

**Clean build:**
```bash
./gradlew clean
```

**Debug build:**
```bash
./gradlew assembleDebug
```

**Install debug build to connected device:**
```bash
./gradlew installDebug
```

Note: The project uses Gradle 7.5 and requires JDK 17. Android Gradle Plugin version is 7.4.2.

## Architecture

The launcher follows an MVC-style pattern with clear separation between controllers, models, and UI components:

### Core Components

**Main Activity:**
- `Launcher.java` - Main activity that orchestrates all controllers and handles lifecycle

**Controllers:**
- `DockController` - Manages the 4-app dock, pinning/unpinning apps, and dock persistence
- `DrawerController` - Handles app drawer display, filtering, and hiding apps
- `ViewController` - Manages view transitions and UI state
- `WidgetController` - Handles home screen widget placement and management

**Data Layer:**
- `SharedPreferencesDAO` - Singleton for persistent storage of user preferences, pinned apps, and hidden apps
- `ApplicationModel` - Simple data class representing an app (package name, class name, label, hidden state)

**Async Tasks:**
- `LoadDockTask` - Loads dock apps from preferences
- `LoadDrawerListAdapterTask` - Loads all installed apps for the drawer
- `FilterDrawerListAdapterTask` - Filters apps based on search queries
- `LoadSharedPreferencesDAOTask` - Initializes the preferences DAO
- `CreateWidgetAsyncTask` - Handles widget creation
- `ShowWidgetListAsPopupMenuTask` - Shows available widgets in popup menu

### Key Patterns

**Persistence:**
- Apps are pinned to dock using keys like `pin_0|packageName|className`
- Hidden apps stored with keys like `hide_packageName|className`
- Settings use boolean flags in SharedPreferences

**Memory Management:**
- Controllers use `WeakReference` to prevent memory leaks
- AsyncTasks properly handle activity references

**UI Structure:**
- Uses `ViewFlipper` to switch between home screen and app drawer
- `ListView` with custom adapters for app lists
- Context menus for app actions (uninstall, hide, pin)

## Development Guidelines

**Build Requirements:**
- minSdkVersion: 21 (Android 5.0)
- targetSdkVersion: 29
- compileSdkVersion: 33
- Java 8 compatibility
- APK must remain under 30KB
- Minification is disabled (minifyEnabled false)

**Code Style:**
- This is decompiled code, so formatting may be unusual
- Package structure: `de.clemensbartz.android.launcher.*`
- Use existing patterns when adding new functionality
- Follow the controller pattern for new features

**Key Constraints:**
- Size optimization is critical - every KB matters
- No external dependencies beyond Android framework
- Simple data structures to minimize overhead
- Async operations for any potentially slow tasks