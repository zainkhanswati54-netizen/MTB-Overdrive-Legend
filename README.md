# Mountain Bike Xtreme — Kotlin Clone (Work in Progress)

Yeh project **Mountain Bike Xtreme** jaisi game ka clone hai, Kotlin + Jetpack Compose mein.
Abhi tak sirf 2 screens ban chuki hain jaisa aap ne request kiya tha:

1. **Continue Screen** (`ContinueScreen.kt`) — splash/tap-to-continue screen, exit + settings icons ke sath
2. **Map / Environment Selection Screen** (`MapSelectionScreen.kt`) — swipeable cards: Mountains, Forest, Hills, Desert

## Project structure
```
app/src/main/java/com/example/mountainbikextreme/
├── MainActivity.kt                  -> navigation graph (Continue -> Map Selection)
├── ui/
│   ├── screens/
│   │   ├── ContinueScreen.kt
│   │   └── MapSelectionScreen.kt
│   ├── components/
│   │   ├── EnvironmentBackground.kt -> procedural parallax silhouette scenes (no image assets needed)
│   │   └── BikeRiderIcon.kt         -> simple bike/rider silhouette icon
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
```

## Kaise chalayein (How to run)
1. Android Studio (Koala ya newer) mein `MountainBikeXtreme` folder ko **File > Open** se open karein.
2. Gradle sync hone dein (agar wrapper jar missing error aaye, Android Studio khud offer karega "Recreate Gradle Wrapper" — accept kar dein, ya `File > Sync Project with Gradle Files`).
3. Emulator ya real device pe **Run ▶** karein.
4. App "Tap to continue" screen se shuru hogi, tap karne pe seedha **Environment Selection** screen khulegi.

## Design notes
- Backgrounds real images nahi hain — pure Canvas code se procedurally draw kiye gaye hain (parallax hills + pine trees / cactus silhouettes), taake abhi assets ki zaroorat na pade. Baad mein aap chahen to actual PNG/SVG art se replace kar sakte hain.
- Colors aapke screenshots ke teal/forest palette se match karne ki koshish ki gayi hai.
- Screen orientation **landscape** rakhi gayi hai, jaisa original game mein hai.

## Next steps (jab aap ready hon)
- Main menu screen (Best Distance / Total Distance / Tap to start / Shop)
- Actual gameplay: physics-based bike + rider ragdoll, tilt controls, terrain generation
- Shop screen
- Pause/crash overlay

Bata dijiye ke agla step kya banana hai — main menu ya seedha gameplay physics?
