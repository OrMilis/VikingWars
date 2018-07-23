package Actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.ormil.libgdxtest.GameStage;

import ActorUserData.CharacterUserData;
import ActorUserData.UserDataType;

import static Utils.Constants.PPM;

/**
 * Created by ormil on 18/02/2018.
 */

public class Character extends GameActor {

    private int health = 100;
    private Vector2 initPos;

    private Sprite characterSprite;
    private SpriteBatch spriteBatch;

    World world;
    Body ground;

    private boolean isHit = false;
    private float timeInvic = 0;
    private final float timeStep = 0.20f;

    private MouseJointDef mouseJointDef;
    private MouseJoint mouseJoint;

    public Character(Body body, Body ground, Sprite characterSprite, SpriteBatch spriteBatch) {
        super(body);
        body.setFixedRotation(true);
        this.initPos = body.getPosition();
        this.ground = ground;

        this.spriteBatch = spriteBatch;
        this.characterSprite = characterSprite;


        characterSprite.setSize(1, 2);
        characterSprite.setOrigin(characterSprite.getWidth() / 2, characterSprite.getHeight() / 2);

        world = body.getWorld();

        mouseJointDef = new MouseJointDef();
        mouseJointDef.bodyA = mouseJointDef.bodyB = this.body;
        mouseJointDef.target.set(initPos);
        mouseJointDef.maxForce = 30;
        mouseJoint = (MouseJoint) world.createJoint(mouseJointDef);
    }

    public void hit(int damage){
        if(!isHit){
            isHit = true;
            health -= damage;
            if(health <= 0) {
                world.destroyJoint(mouseJoint);
                mouseJoint = null;
                Filter filter = new Filter();
                filter.categoryBits = 0;
                filter.maskBits = 0;
                body.setFixedRotation(false);
                body.getFixtureList().first().setFilterData(filter);

                if (userData.getUserDataType() == UserDataType.ENEMY_CHARACTER)
                    ((GameStage) getStage()).enemyCharacters.removeKey(((CharacterUserData) userData).getCharacterID());
                else if (userData.getUserDataType() == UserDataType.FRIENDLY_CHARACTER)
                    ((GameStage) getStage()).friendlyCharacters.removeKey(((CharacterUserData) userData).getCharacterID());
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timeInvic += delta;
        if(timeInvic >= timeStep) {
            isHit = false;
            timeInvic = 0;
        }

        if(body.getPosition().y < -50){
            remove();
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        spriteBatch.begin();
        characterSprite.setPosition(body.getPosition().x - 1f/2 , body.getPosition().y - 1f + 8f/PPM);
        characterSprite.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public CharacterUserData getUserData() {
        return (CharacterUserData) userData;
    }

}
