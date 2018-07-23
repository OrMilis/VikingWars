package com.ormil.libgdxtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Random;

import ActorUserData.CharacterUserData;
import ActorUserData.UserDataType;
import Actors.Character;
import Actors.GameActor;
import Actors.Ground;
import Actors.Throwable;
import Listeners.StageContactListener;
import Utils.Constants;
import Utils.WorldUtils;

import static Utils.Constants.PPM;

/**
 * Created by ormil on 18/02/2018.
 */

public class GameStage extends Stage implements InputProcessor, GestureDetector.GestureListener {

    private static final int VIEWPORT_WIDTH = 5;
    private static final int VIEWPORT_HEIGHT = 5;

    private final float POWER_MULTIPLY = 5f;
    private final float MAX_VELOCITY = 50f;
    private final int NUM_OF_DOTS = 20;
    private final float TIME_INTERVAL = 0.05f;
    private final float ZOOM_MODIFIER = 0.0001f;

    private int ppm = PPM;

    private boolean toShoot = false;
    private boolean enemyTurn = false;
    private boolean toZoom = false;
    private boolean toPan = false;
    private boolean isPanning = false;
    private boolean disablePan = false;

    private float timeToZoom = 1f;
    private float cameraZoomDuration = 1f;
    private float cameraZoomTarget = 1.5f;

    private float timeToPan = 1f;
    private float cameraPanDuration = 1f;
    private float cameraPanTarget;

    public ArrayMap<Integer, Character> friendlyCharacters = new ArrayMap<Integer, Character>();
    public ArrayMap<Integer, Character> enemyCharacters = new ArrayMap<Integer, Character>();
    private Character activeCharacter;
    private GameActor cameraFollowObj;

    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();

    private TiledMap map;
    private OrthogonalTiledMapRenderer tmr;

    private World world;
    private Ground ground;

    private final float TIME_STEP = 1 / 60f;
    private float accumulator = 0f;

    protected OrthographicCamera camera;

    private Vector3 touchPoint;

    boolean isDrag = false;

    public SpriteBatch batch;

    private Texture trajectoryTexture;

    private Sprite friendlyCharacterTexture;
    private Sprite enemyCharacterTexture;
    private Sprite friendlyWeaponSprite;
    private Sprite enemyWeaponSprite;

    private Vector2 previousShot;
    private Character previousCharacter;

    private Vector2 maxXYBoundries = new Vector2();
    private Vector2 minXYBoundries = new Vector2(100, 100);

    private GestureDetector gestureDetector;
    private InputMultiplexer inputMultiplexer;


    public GameStage(int level) {
        batch = new SpriteBatch();

        trajectoryTexture = new Texture("trajectoryTexture.png");
        friendlyCharacterTexture = new Sprite(new Texture("Viking.png"));
        enemyCharacterTexture = new Sprite(new Texture("Goblin.png"));
        friendlyWeaponSprite = new Sprite(new Texture("Axe.png"));
        enemyWeaponSprite = new Sprite(new Texture("Knife.png"));

        friendlyCharacters = new ArrayMap<Integer, Character>();
        enemyCharacters = new ArrayMap<Integer, Character>();

        CharacterUserData.resetIDGeneratorValues();

        map = new TmxMapLoader().load("Maps/Map" + level +".tmx");
        tmr = new OrthogonalTiledMapRenderer(map, 1f/PPM);

        setUpWorld();
        setupCamera();
        setMapBoundries();

        inputMultiplexer = new InputMultiplexer();
        gestureDetector = new GestureDetector(this);

        inputMultiplexer.addProcessor(gestureDetector);
        inputMultiplexer.addProcessor(this);

        Gdx.input.setInputProcessor(inputMultiplexer);
        Gdx.input.setCatchBackKey(true);
        world.setContactListener(new StageContactListener(friendlyCharacters, enemyCharacters));

        //Gdx.input.setInputProcessor(this);
        touchPoint = new Vector3();

        batch.setProjectionMatrix(camera.combined);
    }

    private void setUpWorld() {
        world = WorldUtils.createWorld();
        setUpGround();
        setUpCharacters(true);
        activeCharacter = friendlyCharacters.get(0);
        cameraPanTarget = activeCharacter.getBody().getPosition().x;
        setUpCharacters(false);
    }

    private void setUpGround() {
        ground = new Ground(WorldUtils.createWorldMap(world, map.getLayers().get("Map").getObjects()).get(0));
        addActor(ground);
    }

    private void setUpCharacters(boolean isFriendly) {
        String layerName = isFriendly ? "FriendlyCharacterPositions" : "EnemyCharacterPositions";
        ArrayList<Vector2> positions = WorldUtils.pivotsPositions(map.getLayers().get(layerName).getObjects());
        int index = 0;
        for(Vector2 pos : positions) {
            Character character;
            if(isFriendly) {
                character = new Character(WorldUtils.createFriendlyCharacter(world, new Vector2(pos.x, pos.y + (Constants.PLAYER_HEIGHT +2f/PPM)/ 2)),
                        ground.getBody(), friendlyCharacterTexture, batch);
                friendlyCharacters.put(index++, character);
                addActor(character);
            }
            else{
                character = new Character(WorldUtils.createEnemyCharacter(world, new Vector2(pos.x, pos.y + (Constants.PLAYER_HEIGHT +2f/PPM) / 2)),
                        ground.getBody(),enemyCharacterTexture, batch);
                enemyCharacters.put(index++, character);
            }
            addActor(character);
        }
    }

    private void setupCamera() {
        camera = new OrthographicCamera(width / (PPM*2), height / (PPM*2));
        Gdx.app.log("Camera", camera.zoom + "");
        cameraFollowObj = activeCharacter;
        updateCameraPosition();
        camera.update();
    }

    private void setMapBoundries(){
        ArrayList<Vector2> position = WorldUtils.pivotsPositions(map.getLayers().get("Boundries").getObjects());

        for (Vector2 pos : position){
            if(pos.x > maxXYBoundries.x)
                maxXYBoundries.x = pos.x;
            if(pos.y > maxXYBoundries.y)
                maxXYBoundries.y = pos.y;
            if(pos.x < minXYBoundries.x)
                minXYBoundries.x = pos.x;
            if(pos.y < minXYBoundries.y)
                minXYBoundries.y = pos.y;
            Gdx.app.log("Boundry: ", pos.toString());
        }
    }

    private void updateCameraPosition(){
        Vector2 cameraObjPos = cameraFollowObj.getBody().getPosition();

        Vector2 cameraPos = new Vector2(camera.position.x, camera.position.y);

        if(cameraObjPos.x > maxXYBoundries.x)
            cameraObjPos.x = maxXYBoundries.x;
        if(cameraObjPos.y > maxXYBoundries.y)
            cameraObjPos.y = maxXYBoundries.y;
        if(cameraObjPos.x < minXYBoundries.x)
            cameraObjPos.x = minXYBoundries.x;
        if(cameraObjPos.y < minXYBoundries.y)
            cameraObjPos.y = minXYBoundries.y;

        if(!isPanning && !toPan) {
            camera.position.set(cameraObjPos, 0f);
            camera.update();
        }

        batch.setProjectionMatrix(camera.combined);
    }

    private void updateCameraViewport(float delta){
        camera.zoom += delta * ZOOM_MODIFIER;

        if(camera.zoom < 0.8f)
            camera.zoom = 0.8f;
        else if(camera.zoom > 2)
            camera.zoom = 2f;
        Gdx.app.log("Camera", camera.zoom + "");
        camera.update();
    }

    private void cameraZoomInterpolation(float deltaTime){
        if (timeToZoom >= 0) {
            timeToZoom -= deltaTime;
            float progress;
            if(timeToZoom < 0){
                progress = 1;
                toZoom = false;
                timeToZoom = 1f;
            }
            else
                progress = 1f - timeToZoom / cameraZoomDuration;
            camera.zoom = Interpolation.linear.apply(camera.zoom, cameraZoomTarget, progress);
        }
    }

    private void cameraPanInterpolation(float deltaTime){
        if(!disablePan) {
            if (timeToPan >= 0) {
                timeToPan -= deltaTime;
                float progress;
                if (timeToPan < 0) {
                    progress = 1;
                    toPan = false;
                } else
                    progress = 1f - timeToPan / cameraPanDuration;
                Vector3 temp = camera.position;
                temp.x = Interpolation.linear.apply(temp.x, cameraPanTarget, progress);
                camera.position.set(temp);
                camera.update();
            }
        }
    }

    private void AsyncTurnChange(final Throwable throwable){

        cameraFollowObj = throwable;

        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                GameActor temp = cameraFollowObj;
                //if(!friendlyCharacters.containsKey(((CharacterUserData)activeCharacter.getBody().getUserData()).getCharacterID()))
                activeCharacter = friendlyCharacters.get(friendlyCharacters.keys().toArray().random());
                if(activeCharacter == null)
                {
                    activeCharacter = enemyCharacters.get(enemyCharacters.keys().toArray().random());
                }
                cameraFollowObj = activeCharacter;
                world.destroyBody(temp.getBody());
                temp.remove();
                toZoom = true;
                cameraZoomTarget = 0.8f;
                timeToZoom = 1f;
                if(enemyTurn && enemyCharacters.size > 0) {
                    /*Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {*/

                                disablePan = true;

                                Array<Integer> friendlyKeys = friendlyCharacters.keys().toArray();
                                Array<Integer> enemyKeys = enemyCharacters.keys().toArray();

                                int randomTarget = friendlyKeys.random();
                                int randomShooter = enemyKeys.random();

                                Vector2 friendlyPos = friendlyCharacters.get(randomTarget).getBody().getPosition();
                                Vector2 enemyPos = enemyCharacters.get(randomShooter).getBody().getPosition();

                                cameraFollowObj = activeCharacter =  enemyCharacters.get(randomShooter);


                                //Thread.sleep(2000);
                                enemyPlay(friendlyPos, enemyPos);
                           /* } catch (Exception e){Gdx.app.log("Thread", e.toString());}
                        }
                    });
                    t.start();*/
                }
            }
        };

        Timer.schedule(task, 5f);
    }

    private void enemyPlay(Vector2 friendlyPos, Vector2 enemyPos){

        Random random = new Random();

        float radius = 1f;

        float distanceY = friendlyPos.y - enemyPos.y;
        float time = (random.nextFloat() * (3f - 1f) + 1f);
        float accelTimeY = -9.81f * time * time;
        float speedY = ((2 * distanceY) - (accelTimeY)) / (2*time);


        float targetPos = (random.nextFloat() * (2 * radius) + (friendlyPos.x - radius));
        float distanceX = targetPos - enemyPos.x;
        float speedx = (2 * distanceX) / (2*time);

        Vector2 throwableSpeed = new Vector2(speedx, speedY);

        Throwable throwable = new Throwable(WorldUtils.createEnemyThrowable(world, enemyPos), enemyWeaponSprite, batch);
        addActor(throwable);

        throwable.getBody().setLinearVelocity(throwableSpeed);
        throwable.getBody().applyAngularImpulse(1f, true);

        toShoot = false;
        enemyTurn = false;
        cameraZoomTarget = 1.5f;
        toZoom = true;

        AsyncTurnChange(throwable);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Fixed timestep
        accumulator += delta;

        while (accumulator >= delta) {
            world.step(TIME_STEP, 8, 3);
            accumulator -= TIME_STEP;
        }

        //TODO: Implement interpolation

        updateCameraPosition();
        if(toZoom){
            cameraZoomInterpolation(delta);
        }
        if(toPan){
            cameraPanInterpolation(delta);
        }
    }

    @Override
    public void draw() {

        tmr.setView(camera);
        tmr.render();

        super.draw();

        batch.begin();
        boolean isMyCharacter = activeCharacter.getUserData().getUserDataType() == UserDataType.FRIENDLY_CHARACTER;
        if((!toZoom || isPanning)&& isMyCharacter && !enemyTurn && previousShot != null && previousCharacter != null) {
            printDots(false, 0.3f);
        }

        if(isDrag && disablePan && isMyCharacter && toShoot){
            printDots(true, 1f);
        }

        batch.end();


    }

    private void printDots(boolean isLive, float alpha){
        float t = 0;
        batch.setColor(batch.getColor().r,batch.getColor().g, batch.getColor().b, alpha);
        for (int i = 0; i < NUM_OF_DOTS; i++) {
            float x = getX(t, isLive);
            float y = getY(t, isLive);
            batch.draw(trajectoryTexture, x, y, 5f / PPM, 5f / PPM);

            t += TIME_INTERVAL;
        }
        batch.setColor(batch.getColor().r,batch.getColor().g, batch.getColor().b, 1f);
    }

    private QueryCallback queryCallback = new QueryCallback() {

        @Override
        public boolean reportFixture(Fixture fixture){
            if(!fixture.testPoint(touchPoint.x, touchPoint.y))
                return true;

            if(fixture.getBody().getUserData() instanceof CharacterUserData) {
                CharacterUserData tempData = (CharacterUserData) fixture.getBody().getUserData();

                if(tempData.getUserDataType() == UserDataType.FRIENDLY_CHARACTER){
                    if(activeCharacter.equals(friendlyCharacters.get(tempData.getCharacterID()))) {
                        toShoot = true;
                        disablePan = true;
                    }
                }

                return false;
            }
            return true;
        }

    };

    private float getX(float t, boolean isLive){
        Vector2 startPos;
        if(isLive)
            startPos = activeCharacter.getBody().getPosition();
        else
            startPos = previousCharacter.getBody().getPosition();
        Vector2 temp = calculateVelocity(isLive);

        return ((temp.x * t) + startPos.x);
    }

    private float getY(float t, boolean isLive){
        Vector2 startPos;
        if(isLive)
            startPos = activeCharacter.getBody().getPosition();
        else
            startPos = previousCharacter.getBody().getPosition();

        Vector2 temp = calculateVelocity(isLive);

        return startPos.y + (temp.y * t) - (9.81f * t * t * 0.5f);
    }

    private Vector2 calculateVelocity(boolean isLive){
        if(!isLive)
            return previousShot;

        Vector2 characterPos = activeCharacter.getBody().getPosition();

        float power = (new Vector2(characterPos.x - touchPoint.x, characterPos.y - touchPoint.y)).len() * POWER_MULTIPLY;
        float angle = (new Vector2(characterPos.x - touchPoint.x, characterPos.y - touchPoint.y)).angle();

        power = Math.min(power, MAX_VELOCITY);

        float xVelocity = power * MathUtils.cosDeg(angle);
        float yVelocity = power * MathUtils.sinDeg(angle);

        return new Vector2(xVelocity,yVelocity);
    }

    private void translateScreenToWorldCoordinates(float x, float y) {
        camera.unproject(touchPoint.set(x, y, 0));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        translateScreenToWorldCoordinates(screenX, screenY);
        if(!toZoom)
            world.QueryAABB(queryCallback, touchPoint.x, touchPoint.y, touchPoint.x, touchPoint.y);

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        isDrag = true;
        translateScreenToWorldCoordinates(screenX, screenY);

        return true;
    }

    @Override
    public void dispose() {
        Gdx.app.log("STAGE", "Disposed!");
        map.dispose();
        batch.dispose();
        trajectoryTexture.dispose();
        super.dispose();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isDrag = false;

        if(toShoot) {
            Vector2 characterPos = activeCharacter.getBody().getPosition();

            Vector2 velocity = calculateVelocity(true);

            Gdx.app.log("Up", "power: " + velocity.len());

            if(velocity.len() > 2f) {
                Throwable throwable = new Throwable(WorldUtils.createFriendlyThrowable(world, characterPos), friendlyWeaponSprite, batch);
                addActor(throwable);

                throwable.getBody().setLinearVelocity(velocity);
                throwable.getBody().applyAngularImpulse(-1.5f, true);

                previousShot = velocity;
                previousCharacter = activeCharacter;

                toShoot = false;
                enemyTurn = true;

                cameraZoomTarget = 1.5f;
                toZoom = true;

                disablePan = true;
                toPan = false;
                isPanning = false;

                AsyncTurnChange(throwable);
                return true;
            }
        }

        if(activeCharacter.getUserData().getUserDataType() == UserDataType.FRIENDLY_CHARACTER) {
            toShoot = false;
            disablePan = false;
        }
        return true;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

        if(!disablePan) {
            Vector3 temp = camera.position;
            temp.x -= deltaX / PPM;

            if (temp.x < minXYBoundries.x)
                temp.x = minXYBoundries.x;

            camera.position.set(temp);

            camera.update();

            toPan = false;
            isPanning = true;
            toShoot = false;

            cameraZoomTarget = 1.5f;

            if(camera.zoom <= cameraZoomTarget)
                toZoom = true;
        }

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {

        cameraPanTarget = activeCharacter.getBody().getPosition().x;

        if(isPanning)
            toPan = true;

        isPanning = false;
        toZoom = false;
        timeToZoom = 1f;
        timeToPan = 1f;

        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        updateCameraViewport(initialDistance - distance);
        toZoom = false;
        toShoot = false;
        toPan = false;
        disablePan = true;
        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
