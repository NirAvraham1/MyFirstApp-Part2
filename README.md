# 🚗 Endless Avoidance Game – Android Project

### 🎓 Course: Mobile Application Development  
### 👨‍🏫 Instructor: Tom Cohen 
### 👤 Student: Nir Avraham  
### 📅 Semester: Spring 2025  

## 🕹️ Overview

This is a Kotlin-based Android game where the player must avoid falling obstacles, collect coins, and survive as long as possible. The game includes multiple control modes, location-aware high scores, and uses modern Android components such as RecyclerView, SharedPreferences, and Google Maps.

## 🚀 Features

- 🎮 **5-column endless runner gameplay**
- 👆 **Button and tilt (sensor) controls**
- 🪙 **Coin collection and scoring system**
- 💓 **Lives system with vibration and sound feedback**
- 🌐 **Location tracking with user permission**
- 🗺️ **Google Maps integration to show where high scores were achieved**
- 💾 **Top 10 high scores saved using SharedPreferences + JSON**
- 📊 **Score formula:**  
  `score = meters passed + 10 × coins collected`
- 🧭 **Default location fallback** if GPS is unavailable: Afeka College

## 📦 Technologies

| Technology | Purpose |
|------------|---------|
| Kotlin | Main language |
| Android Studio | Development |
| SharedPreferences + Gson | Persistent high score storage |
| Google Maps SDK | Show high score location |
| SensorManager | Tilt-based control |
| RecyclerView | Display high score table |
| MediaPlayer | Play sound effects |
| Material Design | UI elements (FABs, buttons, dialogs) |


## 🗂 Project Structure

├── MainActivity.kt                // Core gameplay logic

├── MenuActivity.kt               // Game mode selector

├── HighScoresActivity.kt         // RecyclerView + embedded map

├── MapActivity.kt                // Full-screen map view

├── GameLogic.kt                  // Matrix-based game logic

├── HighScoreManager.kt           // Manages score list (with JSON persistence)

├── SharedPreferencesManagerV3.kt// Singleton for data storage (as taught in class)

├── SignalManager.kt              // Vibration and toast utilities

├── SensorMovementController.kt  // Manages accelerometer control

├── res/layout/…                  // All XML UIs

└── res/drawable/…                // All icons and assets


## ⚙️ Setup Instructions

1. Clone the repository into Android Studio.
2. Add your Google Maps API key to `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE" />

  and to the google_maps_api.xml
3. Run the app on a device or emulator.
4. Allow location permission when prompted.

Functional Highlights
✔️ Location permission request and use
✔️ Sensor input (real devices only)
✔️ Material Design FABs and buttons
✔️ Persistent data using JSON in SharedPreferences
✔️ Google Maps marker based on high score location
✔️ RecyclerView-based score table
✔️ Score breakdown: meters + coins
✔️ Full string externalization (strings.xml)

📚 Credits
Developed as part of a university assignment.
Assets and icons from Android Studio and vector drawables.




