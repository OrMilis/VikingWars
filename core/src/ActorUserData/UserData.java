package ActorUserData;

/**
 * Created by ormil on 19/02/2018.
 */

public abstract class UserData {

    protected UserDataType userDataType;

    public UserData(){

    }

    public UserDataType getUserDataType(){
        return userDataType;
    }

}
