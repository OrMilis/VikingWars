package Actors;

import com.badlogic.gdx.physics.box2d.Body;
import ActorUserData.GroundUserData;

/**
 * Created by ormil on 18/02/2018.
 */

public class Ground extends GameActor {

    public Ground(Body body){
        super(body);
    }

    @Override
    public GroundUserData getUserData() {
        return (GroundUserData) userData;
    }
}
