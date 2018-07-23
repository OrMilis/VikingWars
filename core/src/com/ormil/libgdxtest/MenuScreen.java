package com.ormil.libgdxtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by ormil on 10/03/2018.
 */

public class MenuScreen implements Screen {
    private Skin skin;
    private Stage stage;
    private BitmapFont font;

    private VikingGame parent;

    private Camera camera;

    private Texture texture = new Texture(Gdx.files.internal("ImageAssets/Header.png"));
    private Image image = new Image(texture);

    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();

    public MenuScreen(VikingGame game){
        parent = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(width, height);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        image.setPosition(0, height - (image.getHeight() + height/32));
        image.setWidth(width);

        createBasicSkin();
        TextButton newGameButton = new TextButton("Start", skin); // Use the initialized skin
        newGameButton.setPosition(width/2 - newGameButton.getWidth() / 2, height/2 - newGameButton.getHeight() / 2);
        newGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                parent.setScreen(new LevelSelectScreen(skin, font, parent));
            }
        });
        stage.addActor(newGameButton);
        stage.addActor(image);
    }

    private void createBasicSkin(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Cornerstone.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        skin = new Skin();
        skin.add("default", font);

        Pixmap pixmap = new Pixmap(width/4,height/10, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("background", new Texture(pixmap));

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("background", Color.GRAY);
        textButtonStyle.down = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
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
        texture.dispose();
        //skin.dispose();
        stage.dispose();
    }
}
