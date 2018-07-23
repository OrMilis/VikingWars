package Utils;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by ormil on 18/02/2018.
 */

public class Constants {

    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 480;

    public static final int PPM = 32;

    public static final Vector2 WORLD_GRAVITY = new Vector2(0, -9.81f);
    public static final Vector2 MAX_STRANGTH = new Vector2 (0,10);

    public static final float GROUND_X = 0;
    public static final float GROUND_Y = 0;
    public static final float GROUND_WIDTH = 500f;
    public static final float GROUND_HEIGHT = 2f;
    public static final float GROUND_DENSITY = 0f;

    public static final float PLAYER_X = 2f;
    public static final float PLAYER_Y = GROUND_Y + GROUND_HEIGHT;
    public static final float PLAYER_WIDTH = 0.75f;
    public static final float PLAYER_HEIGHT = 1.5f;
    public static final float PLAYER_DENSITY = 4f;

    public static final Vector2 CHARACTER_WALK_LINEAR_IMPULSE = new Vector2(5f,0);

    public static final float THROWABLE_SIZE = 0.4f;
    public static final float THROWABLE_DENSITY = 2.5f;
    public static final float THROWABLE_FRICTION = 0.2f;
    public static final float THROWABLE_RESTITUTION = 0.25f;

    public static final short BOUNDARY            = 0x0001;
    public static final short FRIENDLY_CHARACTER  = 0x0002;
    public static final short FRIENDLY_THROWABLE  = 0x0004;
    public static final short ENEMY_CHARACTER     = 0x0008;
    public static final short ENEMY_THROWABLE     = 0x0010;

    public static final short ALL = BOUNDARY | FRIENDLY_CHARACTER | FRIENDLY_THROWABLE | ENEMY_CHARACTER | ENEMY_THROWABLE;

}
