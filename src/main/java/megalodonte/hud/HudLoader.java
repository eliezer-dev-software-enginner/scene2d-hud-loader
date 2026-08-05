package megalodonte.hud;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Builds a real Scene2D {@link HudView} from the JSON a scene2d-buider
 * export/save writes. Widgets are placed with {@link Actor#setPosition}, not
 * laid out in a real {@code Table} — the builder's Canva is a
 * free-positioning surface (like this HUD), not a grid, so a plain
 * {@link Group} with absolute positions is the faithful match, not a Table
 * with cells.
 */
public final class HudLoader {

    private HudLoader() {
    }

    public static HudView load(FileHandle layoutFile) {
        Json json = new Json();
        json.setIgnoreUnknownFields(true); // forward-compatible with fields the builder might add later without a format bump

        HudLayout layout = json.fromJson(HudLayout.class, layoutFile);

        FileHandle skinFile = layoutFile.parent().child(layout.skinPath);
        Skin skin = new Skin(skinFile);

        Group root = new Group();
        root.setSize(layout.canvasWidth, layout.canvasHeight);

        ObjectMap<String, Actor> actorsByNickname = new ObjectMap<>();
        for (PlacedWidgetData data : layout.widgets) {
            Actor actor = buildActor(skin, data, layout.canvasHeight);
            root.addActor(actor);
            if (data.nickname != null && !data.nickname.isEmpty()) {
                actorsByNickname.put(data.nickname, actor);
            }
        }

        return new HudView(root, skin, layout.canvasWidth, layout.canvasHeight, actorsByNickname);
    }

    private static Actor buildActor(Skin skin, PlacedWidgetData data, int canvasHeight) {
        Actor actor = switch (data.type) {
            case "button" -> new Button(skin, data.styleName);
            case "textButton" -> new TextButton(data.text, skin, data.styleName);
            case "label" -> new Label(data.text, skin, data.styleName);
            case "image" -> new Image(skin, data.regionName);
            default -> throw new GdxRuntimeException(
                    "Unknown widget type \"" + data.type + "\" (widget " + data.id + ")");
        };

        Layout prefSized = (Layout) actor;
        actor.setSize(prefSized.getPrefWidth(), prefSized.getPrefHeight());

        // The builder's Y grows downward from the top (Canva/JavaFX convention);
        // Scene2D's grows upward from the bottom - flip it.
        actor.setPosition(data.x, canvasHeight - data.y - actor.getHeight());

        return actor;
    }
}
