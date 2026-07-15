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

## GitHub se APK build karna (Android Studio ke bina)

Maine `android` module, Gradle setup, aur ek GitHub Actions workflow
(`.github/workflows/android-build.yml`) add kar diya hai jo apne aap
debug APK bana dega — tumhe apne computer pe kuch install karne ki
zaroorat nahi.

1. Is poore `mtbgame` folder ko ek naye GitHub repository mein push karo:
   ```
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin <tumhari-repo-ka-URL>
   git push -u origin main
   ```
2. GitHub par apni repo kholo → **Actions** tab pe jao.
3. "Build Android APK" workflow apne aap chalegi (push hote hi), ya
   tum "Run workflow" button se manually bhi trigger kar sakte ho.
4. Build complete hone ke baad, us workflow run ke andar niche
   **Artifacts** section mein `mtbgame-debug-apk` milega — usse
   download karke apne phone pe install kar sakte ho (Unknown
   Sources allow karna padega).

Note: abhi koi custom app icon nahi hai (default Android icon use
hoga) — agar chaho to baad mein `android/src/main/res/mipmap-*`
folders mein apna icon daal sakte ho.

## Aage kya banana hai (next steps)

1. Real touch buttons (throttle/brake/lean) UI overlay — Android ke liye zaroori
2. Actual art/sprites (parallax forest background, bike sprite) — abhi
   ShapeRenderer se placeholder shapes hain
3. Environment selection (Forest / Mountains / Hills / Desert) — background themes
4. Shop system + coins/currency
5. Sound effects
6. Android module (AndroidManifest, activity) taaki phone pe install ho sake

Bolo agla step kaunsa karna hai — main isi structure ko aage badhata rahunga.
