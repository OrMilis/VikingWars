package ActorUserData;

import com.badlogic.gdx.math.Vector2;
import Utils.Constants;

/**
 * Created by ormil on 19/02/2018.
 */

public class CharacterUserData extends UserData {

    private static int friendlyIdGenerator = 0;
    private static int enemyIdGenerator = 0;
    public int numOfGroundContacts = 0;
    private int characterID;

    public CharacterUserData(UserDataType characterType){
        super();
        userDataType = characterType;
        if(characterType == UserDataType.FRIENDLY_CHARACTER)
            characterID = friendlyIdGenerator++;
        else if(characterType == UserDataType.ENEMY_CHARACTER)
            characterID = enemyIdGenerator++;
    }

    public int getCharacterID(){
        return characterID;
    }

    public static void resetIDGeneratorValues(){
        friendlyIdGenerator = 0;
        enemyIdGenerator = 0;
    }

}
