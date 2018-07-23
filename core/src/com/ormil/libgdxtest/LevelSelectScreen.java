package com.ormil.libgdxtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by ormil on 17/03/2018.
 */

public class LevelSelectScreen implements Screen {

    private Skin skin;
    private Stage stage;

    private Table table;

    private VikingGame parent;

    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();

    public LevelSelectScreen(Skin skin, BitmapFont font, VikingGame parent){
        this.skin = skin;
        this.parent = parent;
        table = new Table();
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        for(int i = 0; i < 4; i++) {
            if(i == 2)
                table.row();
            final int level = i + 1;
            TextButton newGameButton = new TextButton("Level " + (i+1), skin); // Use the initialized skin
            if(!(i > 1))
                newGameButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        parent.setScreen(new GameScreen(skin, level, parent));
                    }
                });
            table.add(newGameButton).pad(10f);
        }

        table.setPosition(width / 2 - table.getWidth() / 2, height / 2 - table.getHeight() / 2);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
            parent.setScreen(new MenuScreen(parent));
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
