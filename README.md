# Mountain Bike Xtreme — Kotlin + LibGDX + Box2D Clone (Work in Progress)

Yeh **Mountain Bike Xtreme** jaisi game ka clone hai, ab proper game-engine stack pe:
**Kotlin + LibGDX + Box2D**. Yeh stack is genre (physics-based bike/ragdoll games)
ke liye industry-standard hai — Box2D wheel joints, gravity, terrain collision
sab natively milte hain.

## Abhi tak kya bana hai (FULL GAME LOOP)
1. **Continue Screen** — splash / tap-to-continue
2. **Main Menu** — Best Distance / Total Distance (persisted on-device via LibGDX Preferences), Tap to Start, Shop, Environment Settings gear
3. **Map / Environment Selection** — swipeable carousel (Mountains, Forest, Hills, Desert)
4. **Shop** — cosmetic bike-color preview (placeholder, easy to extend into real purchases)
5. **Gameplay** — the real game:
   - Procedurally generated infinite rolling terrain (deterministic per biome seed), streamed in/out as you ride
   - Real Box2D bike: chassis + 2 motorised wheels + torso + head, with lean/pedal/brake controls
   - Camera follows the bike smoothly
   - Coins scattered along the terrain, collectible
   - Crash detection (bike flips past ~78°) — shows a "Crashed!" overlay with distance, tap to restart
   - Pause button — pause/resume, or exit to Main Menu (commits your run's distance to Best/Total first)
   - HUD: timer, distance, coins

## Controls (gameplay screen)
- **Bottom-right big circle** — pedal / accelerate
- **Bottom-right "no entry" circle** — brake
- **Bottom-left two circles** — lean forward / lean backward (balance, wheelies, jumps)
- **Top-left gear icon** — pause

## Module structure
```
MBX/
├── core/       -> saari game logic (screens, rendering, physics) — platform-independent
│   └── src/main/kotlin/com/example/mbx/
│       ├── MountainBikeGame.kt      -> Game class, entry point
│       ├── Assets.kt                -> shared batch/font/shaperenderer
│       ├── screens/
│       │   ├── ContinueScreen.kt
│       │   └── MapSelectionScreen.kt
│       ├── render/
│       │   ├── Biome.kt             -> environment enum + color palettes
│       │   ├── ParallaxRenderer.kt  -> procedural parallax scenes (no image assets)
│       │   └── IconRenderer.kt      -> bike/gear/arrow icons, drawn with ShapeRenderer
│       └── physics/
│           └── BikePhysicsWorld.kt  -> Box2D scaffold for next step
├── android/    -> Android launcher (thin wrapper)
└── desktop/    -> LWJGL3 launcher — run on PC for fast iteration, no emulator needed
```

## Kaise chalayein

### PC pe test karne ke liye (sabse tez, agar chahen):
Iss project mein `gradlew` wrapper script shamil nahi hai (uska binary jar
generate karne ke liye internet chahiye hota hai). Do options hain:

**Option A — khud wrapper generate kar lein** (agar aapke pass Gradle already installed hai):
```
gradle wrapper --gradle-version 8.7
./gradlew desktop:run
```

**Option B — seedha system Gradle se run karein** (Gradle installed hona chahiye — `sdk install gradle` via SDKMAN, ya apna package manager):
```
gradle desktop:run
```

Agar aap sirf Android APK chahte hain (GitHub Actions se), toh yeh step
zaroori nahi — seedha neeche wale section pe jayein.

### Android pe — GitHub Actions se (Android Studio ki zaroorat nahi)

Iss repo mein `.github/workflows/android-build.yml` already add ki hui hai. Yeh
workflow GitHub ke servers pe APK build karti hai aur usse ek downloadable
**artifact** ke tor pe upload kar deti hai.

**Steps:**
1. Iss poore `MBX` folder ko apne GitHub repo mein push karein:
   ```
   cd MBX
   git init
   git add .
   git commit -m "Mountain Bike Xtreme - continue + map selection screens"
   git branch -M main
   git remote add origin https://github.com/<aap-ka-username>/<repo-name>.git
   git push -u origin main
   ```
2. GitHub pe apne repo ke **Actions** tab pe jayein — push hote hi workflow khud
   chal jayegi (ya "Run workflow" button se manually bhi chala sakte hain,
   kyunke `workflow_dispatch` enabled hai).
3. Build complete hone ke baad (~3-5 min), workflow run ke page ke neeche
   **Artifacts** section mein `mountain-bike-xtreme-debug-apk` milega —
   usse download kar lein (ek `.zip` milega jisme APK hoga).
4. Zip se APK nikal ke apne phone pe transfer karein (USB, Google Drive,
   WhatsApp, jo bhi tareeqa aasan ho).
5. Phone pe **Settings > Install unknown apps** se us app (jis se APK open
   kar rahe hain, e.g. Files) ko permission dein, phir APK tap karke install
   kar lein.

> Note: GitHub Artifacts download karne ke liye GitHub account se logged-in
> hona zaroori hai (private ya public dono repos ke liye) — yeh sirf browser
> se PC pe download hota hai, phone browser se seedha nahi (GitHub login flow
> ki wajah se). Isiliye PC pe download karke phir phone pe transfer karna
> sabse aasan raasta hai.

### Android Studio se (agar kabhi zaroorat pade):
1. `MBX` folder open karein, Gradle sync hone dein, `android` run config select
   karke Run karein.


## Design notes
- Har screen ka background **procedurally drawn** hai (ShapeRenderer se — sky gradient +
  layered hills + pine trees/cactus), koi image asset nahi use hui. Baad mein
  chahen to real PNG/texture-atlas art se replace kar sakte hain.
- Screens `com.badlogic.gdx.Screen` interface implement karte hain, `FitViewport`
  (1200x675 virtual size) use karte hain taake sab device sizes pe sahi letterbox ho.
- Touch input polling-based hai (`Gdx.input.isTouched()`, `deltaX/deltaY`) — koi
  Scene2D UI/Skin asset ki zaroorat nahi.

## Next steps
- Main menu screen (Best Distance / Total Distance / Tap to start / Shop)
- Gameplay: `BikePhysicsWorld` ko ek nayi `GameplayScreen` se connect karna —
  tilt/lean controls, terrain generation (chain of `EdgeShape` segments), camera follow, crash detection
- Rider ragdoll (multiple Box2D bodies + revolute joints for limbs)
- Shop screen, pause/crash overlay

Bata dijiye agla step kya banana hai — **main menu**, ya seedha **gameplay physics**
(bike controls + terrain) pe chalein?
