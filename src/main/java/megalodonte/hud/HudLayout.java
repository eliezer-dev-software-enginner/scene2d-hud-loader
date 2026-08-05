package megalodonte.hud;

import java.util.List;

/**
 * Mirrors the JSON schema written by scene2d-buider's export/save
 * ({@code my_app.project.UiLayout} in the scene2d-buider project). Public
 * fields, no getters/setters — that's what libGDX's reflection-based
 * {@link com.badlogic.gdx.utils.Json} expects.
 */
public class HudLayout {
    public int formatVersion;
    public String skinPath;
    public int canvasWidth;
    public int canvasHeight;
    public List<PlacedWidgetData> widgets;
}
