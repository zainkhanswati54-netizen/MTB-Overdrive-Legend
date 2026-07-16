# Mountain Bike Xtreme — Kotlin + LibGDX + Box2D Clone (Work in Progress)

Yeh **Mountain Bike Xtreme** jaisi game ka clone hai, ab proper game-engine stack pe:
**Kotlin + LibGDX + Box2D**. Yeh stack is genre (physics-based bike/ragdoll games)
ke liye industry-standard hai — Box2D wheel joints, gravity, terrain collision
sab natively milte hain.

## Abhi tak kya bana hai
1. **Continue Screen** — splash / tap-to-continue, exit + settings icons
2. **Map / Environment Selection Screen** — swipeable carousel (Mountains, Forest, Hills, Desert), drag se scroll hoti hai
3. **Box2D physics scaffold** (`BikePhysicsWorld.kt`) — chassis + 2 wheels + revolute joints, gravity world. Abhi kisi screen se connected nahi hai, agla step (gameplay) seedha isi pe banega.

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

### PC pe test karne ke liye (sabse tez):
```
./gradlew desktop:run
```
(Windows pe `gradlew.bat desktop:run`)

### Android pe:
1. Android Studio mein `MBX` folder open karein.
2. Gradle sync hone dein.
3. `android` run configuration select karke device/emulator pe **Run ▶** karein.

> Agar `gradlew`/`gradlew.bat` missing error aaye, Android Studio khud "Recreate
> Gradle Wrapper" offer karega — accept kar dein.

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
