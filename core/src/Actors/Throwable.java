package Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.ormil.libgdxtest.GameStage;

import ActorUserData.ThrowableUserData;
import ActorUserData.UserData;
import Utils.Constants;

/**
 * Created by ormil on 20/02/2018.
 */

public class Throwable extends GameActor {

    private Sprite weaponSprite;
    private SpriteBatch spriteBatch;

    public Throwable(Body body, Sprite weaponSprite, SpriteBatch spriteBatch) {
        super(body);
        this.weaponSprite = weaponSprite;
        this.spriteBatch = spriteBatch;
        weaponSprite.setSize(1, 1);
        weaponSprite.setOrigin(weaponSprite.getWidth() / 2, weaponSprite.getHeight() / 2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        weaponSprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        spriteBatch.begin();
        weaponSprite.setPosition(body.getPosition().x - 0.5f, body.getPosition().y - 0.5f);
        weaponSprite.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public ThrowableUserData getUserData() {
        return (ThrowableUserData) userData;
    }
}
