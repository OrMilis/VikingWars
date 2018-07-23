package com.ormil.libgdxtest;

import com.badlogic.gdx.Game;

/**
 * Created by ormil on 18/02/2018.
 */

public class VikingGame extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
