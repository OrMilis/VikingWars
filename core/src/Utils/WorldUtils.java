package Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

import ActorUserData.CharacterUserData;
import ActorUserData.GroundUserData;
import ActorUserData.ThrowableUserData;
import ActorUserData.UserDataType;

import static Utils.Constants.PPM;

/**
 * Created by ormil on 18/02/2018.
 */

public class WorldUtils {

    public static World createWorld(){
        return new World(Constants.WORLD_GRAVITY, true);
    }

    public static Body createGround(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(Constants.GROUND_X, Constants.GROUND_Y));
        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.GROUND_WIDTH / 2, Constants.GROUND_HEIGHT / 2);
        body.createFixture(shape, Constants.GROUND_DENSITY);
        body.setUserData(new GroundUserData());
        shape.dispose();
        return body;
    }

    private static Body createCharacter(World world, Vector2 position, UserDataType characterType, short categoryBits, short maskBits) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.PLAYER_WIDTH / 2, Constants.PLAYER_HEIGHT / 2);
        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = Constants.PLAYER_DENSITY;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;

        body.createFixture(fixtureDef);
        body.resetMassData();
        body.setUserData(new CharacterUserData(characterType));
        shape.dispose();
        return body;
    }

    public static Body createFriendlyCharacter(World world, Vector2 position){
        Body body = WorldUtils.createCharacter(world, position, UserDataType.FRIENDLY_CHARACTER, Constants.FRIENDLY_CHARACTER, (short)(Constants.BOUNDARY | Constants.ENEMY_THROWABLE));
        return body;
    }

    public static Body createEnemyCharacter(World world, Vector2 position){
        Body body = WorldUtils.createCharacter(world, position, UserDataType.ENEMY_CHARACTER, Constants.ENEMY_CHARACTER, (short)(Constants.BOUNDARY | Constants.FRIENDLY_THROWABLE | Constants.ENEMY_CHARACTER));
        return body;
    }

    private static Body createThrowable(World world, Vector2 position, short categoryBits, short maskBits) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        CircleShape shape = new CircleShape();
        shape.setRadius(Constants.THROWABLE_SIZE);

        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = Constants.THROWABLE_DENSITY;
        fixtureDef.friction = Constants.THROWABLE_FRICTION;
        fixtureDef.restitution = Constants.THROWABLE_RESTITUTION;
        //fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;

        //body.createFixture(fixtureDef);

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("Throwable.json"));

        if(categoryBits == Constants.FRIENDLY_THROWABLE)
            loader.attachFixture(body, "Axe", fixtureDef, 1);
        else
            loader.attachFixture(body, "Knife", fixtureDef, 1);

        body.setAngularDamping(1f);

        body.resetMassData();
        body.setUserData(new ThrowableUserData());
        shape.dispose();
        return body;
    }

    public static Body createFriendlyThrowable(World world, Vector2 position){
        Body body = WorldUtils.createThrowable(world, position, Constants.FRIENDLY_THROWABLE, (short)(Constants.BOUNDARY | Constants.ENEMY_CHARACTER));
        return body;
    }

    public static Body createEnemyThrowable(World world, Vector2 position){
        Body body = WorldUtils.createThrowable(world, position, Constants.ENEMY_THROWABLE, (short)(Constants.BOUNDARY | Constants.FRIENDLY_CHARACTER));
        return body;
    }

    public static ArrayList<Body> createWorldMap(World world, MapObjects objects){

        ArrayList<Body> bodyList = new ArrayList<Body>();

        for (MapObject object : objects) {
            Shape shape;

            if(object instanceof PolylineMapObject)
                shape = createPolyline((PolylineMapObject) object);
            else
                continue;

            Body body;
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            body = world.createBody(bodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 1f;
            fixtureDef.friction = 1f;
            fixtureDef.shape = shape;
            fixtureDef.filter.maskBits = Constants.ALL;
            fixtureDef.filter.categoryBits = Constants.BOUNDARY;

            body.createFixture(fixtureDef);
            body.setUserData(new GroundUserData());
            bodyList.add(body);
        }

        return bodyList;
    }

    private static ChainShape createPolyline(PolylineMapObject polyline){
        float[] vertices = polyline.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for(int i = 0; i < worldVertices.length; i++){
            worldVertices[i] = new Vector2(vertices[i*2] / PPM, vertices[i*2 + 1] / PPM );
        }
        ChainShape cs = new ChainShape();
        cs.createChain(worldVertices);
        return cs;
    }

    public static ArrayList<Vector2> pivotsPositions(MapObjects objects){
        ArrayList<Vector2> positions = new ArrayList<Vector2>();

        for (MapObject object : objects) {
            float[] pivots;

            if (object instanceof PolylineMapObject) {
                pivots = ((PolylineMapObject) object).getPolyline().getTransformedVertices();
                positions.add(new Vector2(pivots[0] / PPM, pivots[1] / PPM));
            }
            else
                continue;


        }
        return positions;
    }

}
