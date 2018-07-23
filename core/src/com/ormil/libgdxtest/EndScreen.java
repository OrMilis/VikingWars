package com.ormil.libgdxtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

/**
 * Created by ormil on 17/03/2018.
 */

public class EndScreen implements Screen {

    private Skin skin;
    private Table table;
    private BitmapFont font;
    private Label label;

    private Stage stage;
    private VikingGame parent;

    private int level;
    private boolean isWin;

    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();

    public EndScreen(Skin skin, int level, boolean isWin, VikingGame parent){
        this.skin = skin;
        this.level = level;
        this.isWin = isWin;
        this.parent = parent;

        stage = new Stage();
        table = new Table();

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Cornerstone.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 120;
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.SCARLET;

        skin.add("WinLoseHeader", labelStyle);


    }

    @Override
    public void show() {
        String status;
        ArrayList<TextButton> buttons = new ArrayList<TextButton>();
        if(isWin) {
            status = "YOU WON!";
            TextButton nextLevelBtn = makeTextbutton("Next Level");
            nextLevelBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    parent.setScreen(new GameScreen(skin, ++level, parent));
                }
            });
            TextButton replayBtn = makeTextbutton("Replay");
            replayBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    parent.setScreen(new GameScreen(skin, level, parent));
                }
            });

            TextButton menuBtn = makeTextbutton("Menu");
            menuBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    parent.setScreen(new MenuScreen(parent));
                }
            });

            buttons.add(nextLevelBtn);
            buttons.add(replayBtn);
            buttons.add(menuBtn);

        }
        else {
            status = "YOU LOSE!";
            TextButton replayBtn = makeTextbutton("Replay");
            replayBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    parent.setScreen(new GameScreen(skin, level, parent));
                }
            });

            TextButton menuBtn = makeTextbutton("Menu");
            menuBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    parent.setScreen(new MenuScreen(parent));
                }
            });

            buttons.add(replayBtn);
            buttons.add(menuBtn);
        }
        label = new Label(status, skin, "WinLoseHeader");
        table.add(label);
        table.row().pad(10f);

        for (TextButton button:buttons) {
            table.add(button);
            table.row().pad(10f);
        }

        table.setPosition(width / 2 - table.getWidth() / 2, height / 2 - table.getHeight() / 2);
        stage.addActor(table);
    }

    public TextButton makeTextbutton(final String text){
        TextButton newGameButton = new TextButton(text, skin); // Use the initialized skin
        newGameButton.setPosition(width / 2 - newGameButton.getWidth() / 2, height / 2 - newGameButton.getHeight() / 2);
        return newGameButton;
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
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
