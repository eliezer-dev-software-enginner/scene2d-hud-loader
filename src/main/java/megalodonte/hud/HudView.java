package megalodonte.hud;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A HUD built by {@link HudLoader} — owns the {@link Skin} it loaded, so the
 * caller must {@link #dispose()} it. Widgets given a nickname in the builder
 * (double-click a placed widget there) are indexed by it here, so normal
 * game code can look one up and use it like any other Scene2D actor —
 * {@code hud.get("jogar", TextButton.class).addListener(new ClickListener() {...})}.
 */
public class HudView {
    public final Group root;
    public final Skin skin;
    public final int canvasWidth;
    public final int canvasHeight;
    private final ObjectMap<String, Actor> actorsByNickname;

    public HudView(Group root, Skin skin, int canvasWidth, int canvasHeight, ObjectMap<String, Actor> actorsByNickname) {
        this.root = root;
        this.skin = skin;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.actorsByNickname = actorsByNickname;
    }

    /** The actor placed under {@code nickname} in the builder, or {@code null} if none was given that nickname. */
    public Actor get(String nickname) {
        return actorsByNickname.get(nickname);
    }

    /** Same as {@link #get(String)}, cast to {@code type} for you. */
    public <T extends Actor> T get(String nickname, Class<T> type) {
        return type.cast(actorsByNickname.get(nickname));
    }

    public void dispose() {
        skin.dispose();
    }
}
