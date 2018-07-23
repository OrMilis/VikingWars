package Listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ArrayMap;

import ActorUserData.*;
import Actors.Character;

/**
 * Created by ormil on 09/03/2018.
 */

public class StageContactListener implements ContactListener {

    private ArrayMap<Integer, Character> friendlyCharacters = new ArrayMap<Integer, Character>();
    private ArrayMap<Integer, Character> enemyCharacters = new ArrayMap<Integer, Character>();

    public StageContactListener(ArrayMap<Integer, Character> friendlyCharacters, ArrayMap<Integer, Character> enemyCharacters){
        this.friendlyCharacters = friendlyCharacters;
        this.enemyCharacters = enemyCharacters;
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        //region Throwable X Character
        if(bodyA.getUserData() instanceof ThrowableUserData
                && bodyB.getUserData() instanceof CharacterUserData){

        }

        else if(bodyA.getUserData() instanceof CharacterUserData
                && bodyB.getUserData() instanceof ThrowableUserData){

            CharacterUserData characterUserData = (CharacterUserData) bodyA.getUserData();
            ThrowableUserData throwableUserData = (ThrowableUserData) bodyB.getUserData();

            if(characterUserData.getUserDataType() == UserDataType.ENEMY_CHARACTER) {
                enemyCharacters.get(characterUserData.getCharacterID()).hit((int)bodyB.getLinearVelocity().len());
            }
            else if(characterUserData.getUserDataType() == UserDataType.FRIENDLY_CHARACTER) {
                friendlyCharacters.get(characterUserData.getCharacterID()).hit((int)bodyB.getLinearVelocity().len());
            }
        }
        //endregion

        //region Character X Ground
        else if(bodyA.getUserData() instanceof CharacterUserData
                && bodyB.getUserData() instanceof GroundUserData){

            CharacterUserData characterUserData = (CharacterUserData) bodyA.getUserData();
            GroundUserData groundUserData = (GroundUserData) bodyB.getUserData();

            characterUserData.numOfGroundContacts++;
            Gdx.app.log("Contact", "ON");
        }

        else if(bodyA.getUserData() instanceof GroundUserData
                && bodyB.getUserData() instanceof CharacterUserData){

            GroundUserData groundUserData = (GroundUserData) bodyA.getUserData();
            CharacterUserData characterUserData = (CharacterUserData) bodyB.getUserData();

            characterUserData.numOfGroundContacts++;

            Gdx.app.log("Contact", "ON");
        }
        //endregion

    }

    @Override
    public void endContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        //region Character X Ground
        if(bodyA.getUserData() instanceof CharacterUserData
                && bodyB.getUserData() instanceof GroundUserData){
            Gdx.app.log("Contact", "OFF");
        }

        else if(bodyA.getUserData() instanceof GroundUserData
                && bodyB.getUserData() instanceof CharacterUserData){
            GroundUserData groundUserData = (GroundUserData) bodyA.getUserData();
            CharacterUserData characterUserData = (CharacterUserData) bodyB.getUserData();

            characterUserData.numOfGroundContacts--;

            if(characterUserData.getUserDataType() == UserDataType.ENEMY_CHARACTER
                    && characterUserData.numOfGroundContacts == 0) {
                enemyCharacters.get(characterUserData.getCharacterID()).hit(100);
            }
            else if(characterUserData.getUserDataType() == UserDataType.FRIENDLY_CHARACTER
                    && characterUserData.numOfGroundContacts == 0) {
                friendlyCharacters.get(characterUserData.getCharacterID()).hit(100);
            }
        }
        //endregion

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
