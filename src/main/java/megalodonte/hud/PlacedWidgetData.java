package megalodonte.hud;

/**
 * One entry of {@link HudLayout#widgets}. {@code type} is one of
 * {@code "image"}, {@code "button"}, {@code "textButton"}, {@code "label"};
 * {@code styleName}/{@code regionName}/{@code text} are only set for the
 * types that use them, mirroring scene2d-buider's {@code WidgetSpec}
 * variants — the ones that don't apply are simply absent from the JSON, so
 * they stay {@code null} here. {@code nickname}, when set, is how
 * {@link HudView#get} looks the built actor back up.
 */
public class PlacedWidgetData {
    public String id;
    public String type;
    public String styleName;
    public String regionName;
    public String text;
    public float x;
    public float y;
    public String nickname;
}
