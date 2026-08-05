# scene2d-hud-loader

A small, standalone Java library that turns the JSON [`scene2d-buider`](../scene2d-buider)
exports/saves into a real libGDX Scene2D `Skin` + `Group` + `Actor`s. General-purpose —
any libGDX game can depend on it, not just [`libgdx-example-game`](../libgdx-example-game)
(the sample project in this suite that actually uses it).

Part of the [`scene2d-suite`](..) trio — see [`../README.md`](../README.md) for how the
three projects fit together.

## Usage

```java
HudView hud = HudLoader.load(Gdx.files.internal("ui/hud-demo.json"));
Stage stage = new Stage(new FitViewport(hud.canvasWidth, hud.canvasHeight));
stage.addActor(hud.root);
Gdx.input.setInputProcessor(stage); // without this the Stage never receives clicks
```

Widgets given a nickname in the builder (double-click a placed widget there, or use
its "Properties" panel) can be looked up and wired up with normal Scene2D code:

```java
TextButton jogarButton = hud.get("jogar", TextButton.class);
if (jogarButton != null) {
    jogarButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            // real game logic goes here
        }
    });
}
```

(the nickname itself is whatever you set in the builder — `libgdx-example-game`'s own
`GameScreen` happens to use `"jogar"` for its play button; see [`libgdx-example-game/README.md`](../libgdx-example-game/README.md).)

`HudView` owns the `Skin` it loaded, so call `hud.dispose()` when you're done with it
(e.g. in the owning `Screen`/`Game`'s own `dispose()`).

## What it does

`HudLoader.load(FileHandle)`:

1. Reads the layout JSON via libGDX's own reflection-based `Json` (`setIgnoreUnknownFields(true)`,
   so it stays forward-compatible with fields a newer builder export might add without a format bump).
2. Resolves `skinPath` relative to the layout file's own folder and loads it as a real `Skin`
   (same "`.atlas` next to the `.json`" convention the builder itself uses).
3. Builds one Scene2D actor per widget entry, matching the type 1:1 —
   `new Button(skin, styleName)` / `new TextButton(text, skin, styleName)` /
   `new Label(text, skin, styleName)` / `new Image(skin, regionName)` — sized via each
   actor's own `getPrefWidth()/getPrefHeight()` (the same drawables/font the builder used
   to preview it), then positioned with `actor.setPosition(...)`.
4. Adds every actor to a plain `Group` (not a `Table`) — the builder's Canva is a
   free-positioning surface, not a grid, so a `Group` with absolute positions is the
   faithful match, not a `Table` fighting the model with cells.
5. Flips the Y axis while positioning: the builder's coordinates have their origin at the
   top-left with Y growing downward (Canva/JavaFX convention); Scene2D's origin is
   bottom-left with Y growing upward — `stageY = canvasHeight - y - actorHeight`.
6. Indexes actors by nickname (when one was set in the builder) into an `ObjectMap`, exposed
   via `HudView.get(String)` / `HudView.get(String, Class<T>)`.

## API surface

- **`HudLoader.load(FileHandle layoutFile)`** — the one entry point, returns a `HudView`.
- **`HudView`** — `root` (`Group`), `skin` (`Skin`), `canvasWidth`/`canvasHeight` (`int`,
  the layout's target viewport size), `get(nickname)` / `get(nickname, Class<T>)`, `dispose()`.
- **`HudLayout`** / **`PlacedWidgetData`** — plain public-field POJOs mirroring the JSON
  schema `my_app.project.UiLayout` / `PlacedWidgetDto` write in scene2d-buider. Not usually
  something you touch directly — `HudLoader.load` is the intended entry point — but they're
  public in case you need to read the raw layout data yourself.

## Installing

### Via JitPack (recommended for consumers outside this monorepo)

Published from [github.com/eliezer-dev-software-enginner/scene2d-hud-loader](https://github.com/eliezer-dev-software-enginner/scene2d-hud-loader).
JitPack builds straight from a tag/commit/branch — no manual publish step, no `mavenLocal()`:

```kotlin
// build.gradle.kts, in the module that needs it
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.eliezer-dev-software-enginner:scene2d-hud-loader:v1.0.0-beta")
}
```

`v1.0.0-beta` is the current tagged release. A specific commit hash or `main-SNAPSHOT`
(always builds the latest commit on `main`) also work in place of the tag. `jitpack.yml`
in this repo pins the build to JDK 17, matching the toolchain this project targets.

### Via `mavenLocal()` (for working on this monorepo directly)

```kotlin
// build.gradle.kts, in the module that needs it
repositories {
    mavenLocal()
}

dependencies {
    implementation("megalodonte:scene2d-hud-loader:1.0.0-beta")
}
```

Publish it locally first (and re-run this after every change — consumers don't build it
from source, they resolve the already-published jar):

```bash
./gradlew publishToMavenLocal
```

## Requirements

- Java 17
- libGDX `1.14.2` (pulled in transitively as `api`, since `HudView`'s fields are real
  `com.badlogic.gdx.*` types — no LWJGL/graphics backend needed here, this library only
  parses JSON and builds a Scene2D actor tree, it never touches the GPU itself)
