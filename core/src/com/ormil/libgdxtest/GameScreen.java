package com.ormil.libgdxtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by ormil on 18/02/2018.
 */

public class GameScreen implements Screen {

    private GameStage stage;
    private Skin skin;
    private int level;

    private VikingGame parent;

    private final float TIME_STEP = 1 / 60f;
    private float accumulator = 0f;

    public GameScreen(Skin skin, int level, VikingGame parent){
        this.level = level;
        this.skin = skin;
        this.parent = parent;
        stage = new GameStage(level);

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        accumulator += delta;

        while (accumulator >= delta) {
            accumulator -= TIME_STEP;
            Gdx.gl.glClearColor(0.6f,0.87f,1f,1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            stage.act(delta);
            stage.draw();

            if(checkIfOver()){
                if(stage.friendlyCharacters.size <=0)
                    parent.setScreen(new EndScreen(skin, level, false, parent));
                else
                    parent.setScreen(new EndScreen(skin, level, true, parent));
                //previousStage = stage;
                /*Timer.Task task = new Timer.Task() {
                    @Override
                    public void run() {
                        previousStage.dispose();
                    }
                };
                Timer.schedule(task, 10);*/
            }

            if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
                parent.setScreen(new MenuScreen(parent));
            }
        }
    }

    private boolean checkIfOver(){
        if(stage.friendlyCharacters.size <= 0 || stage.enemyCharacters.size <= 0)
            return true;
        return false;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
