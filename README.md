# Mountain Bike Prototype (Kotlin + LibGDX + Box2D)

Ye ek physics-based downhill mountain bike game ka **core prototype** hai — terrain
procedurally generate hota hai, bike Box2D physics (do wheels + WheelJoint suspension)
se chalti hai.

## Kaise chalayein (Desktop pe test karne ke liye)

1. **Android Studio** ya **IntelliJ IDEA** mein is folder ko open karo
   (File → Open → `mtbgame` folder select karo). IDE khud Gradle wrapper download
   kar lega (internet chahiye — mere sandbox mein network nahi tha isliye maine
   wrapper generate nahi kiya).
2. Agar wrapper missing bole to terminal mein: `gradle wrapper` chala do
   (system-installed Gradle se), phir `./gradlew :desktop:run`.
3. IDE se seedha `DesktopLauncher.kt` ke `main()` par right-click → Run bhi kar sakte ho.

## Controls (abhi ke liye keyboard, testing ke liye)

- **Right Arrow** → throttle (aage badho)
- **Left Arrow** → brake / reverse
- **Up Arrow** → lean back (wheelie / backflip control)
- **Down Arrow** → lean forward (nose dive control)
- **R** → crash ke baad restart
- **F1** → Box2D debug collision view toggle

## Abhi kya bana hai

- `Terrain.kt` — random-walk based procedural hills, chunk-by-chunk generate
  hote hain jaise bike aage badhti hai
- `Bike.kt` — chassis + 2 wheels, WheelJoint (suspension + rear motor),
  lean/flip ke liye torque control
- `GameScreen.kt` — physics loop, camera follow, ShapeRenderer se placeholder
  rendering, HUD (distance + best distance saved via Preferences)
- `MenuScreen.kt` — basic start screen

## Aage kya banana hai (next steps)

1. Real touch buttons (throttle/brake/lean) UI overlay — Android ke liye zaroori
2. Actual art/sprites (parallax forest background, bike sprite) — abhi
   ShapeRenderer se placeholder shapes hain
3. Environment selection (Forest / Mountains / Hills / Desert) — background themes
4. Shop system + coins/currency
5. Sound effects
6. Android module (AndroidManifest, activity) taaki phone pe install ho sake

Bolo agla step kaunsa karna hai — main isi structure ko aage badhata rahunga.
