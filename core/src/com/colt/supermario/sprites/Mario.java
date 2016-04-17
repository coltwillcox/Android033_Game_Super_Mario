package com.colt.supermario.sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenPlay;

/**
 * Created by colt on 4/13/16.
 */

// TODO: Program crashing when Mario have feet.
// TODO: Moving and jumping sensitivity.
// TODO: Sticking to celling while jumping.

public class Mario extends Sprite {

    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD }
    public State stateCurrent;
    public State statePrevious;

    public ScreenPlay screen;
    public World world;
    public Body body;

    private AssetManager manager;

    private float stateTime;
    private boolean runningRight;
    private boolean marioBig;
    private boolean runAnimationGrow;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioDead;
    private TextureRegion animationStand;
    private TextureRegion animationStandBig;
    private TextureRegion animationDead;
    private Animation animationRun;
    private Animation animationRunBig;
    private Animation animationJump;
    private Animation animationJumpBig;
    private Animation animationGrow;
    private Array<TextureRegion> frames;

    //Constructor.
    public Mario(ScreenPlay screen, AssetManager manager) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.manager = manager;

        stateCurrent = State.STANDING;
        statePrevious = State.STANDING;
        stateTime = 0;
        runningRight = true;

        //Animations.
        frames = new Array<TextureRegion>();
        //Standing and dead. Not really animations.
        animationStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        animationStandBig = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);
        animationDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 6 * 16, 0, 16, 16);
        //Run animations.
        for (int i = 1; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        animationRun = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 1; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        animationRunBig = new Animation(0.1f, frames);
        frames.clear();
        //Jump animations.
        for (int i = 4; i <= 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        animationJump = new Animation(0.2f, frames);
        frames.clear();
        for (int i = 4; i <= 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        animationJumpBig = new Animation(0.2f, frames);
        frames.clear();
        //Grow animation.
        for (int i = 0; i <= 1; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 15 * 16, 0, 16, 32)); //15 * 16, because it's 16th big_mario image.
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        }
        animationGrow = new Animation(0.2f, frames);
        frames.clear();

        defineMario();
        setBounds(0, 0, 16 / Boot.PPM, 16 / Boot.PPM);
        setRegion(animationStand);
    }

    public void update(float deltaTime) {
        if (marioBig)
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) - (7 / Boot.PPM)); //Sets the position where the sprite will be drawn.
        else
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (1 / Boot.PPM)); //+ (1 / Boot.PPM) because radius is just 6.
        setRegion(getFrame(deltaTime));

        //Must define and redefine Mario with boolean and update, because body can't be destroyed in world.step cycle.
        if (timeToDefineBigMario)
            defineBigMario();
        if (timeToRedefineMario)
            redefineMario();
    }

    public void defineMario() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32 / Boot.PPM, 64 / Boot.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.4f; //Stop Mario from iceskating! ;)
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.MARIO_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COIN_BIT | Boot.ENEMY_BIT | Boot.ENEMY_HEAD_BIT | Boot.ITEM_BIT; //Mario (fixture) will collide only with these BITS.
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        //Create Mario's head and make it a sensor for smashing objects.
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Boot.PPM, 7 / Boot.PPM), new Vector2(2 / Boot.PPM, 7 / Boot.PPM));
        fixtureDef.filter.categoryBits = Boot.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        //Create Mario's feet. Problem with this fixture.
        //EdgeShape feet = new EdgeShape();
        //feet.set(new Vector2(-2 / Boot.PPM, -7 / Boot.PPM), new Vector2(2 / Boot.PPM, -7 / Boot.PPM));
        //fixtureDef.shape = feet;
        //fixtureDef.isSensor = false;
        //body.createFixture(fixtureDef);
    }

    public void defineBigMario() {
        Vector2 currentPosition = body.getPosition();
        world.destroyBody(body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition.add(0, 8 / Boot.PPM));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.4f; //Stop Mario from iceskating! ;)
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.MARIO_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COIN_BIT | Boot.ENEMY_BIT | Boot.ENEMY_HEAD_BIT | Boot.ITEM_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        //Lower circle in Mario's body.
        shape.setPosition(new Vector2(0, -16 / Boot.PPM));
        body.createFixture(fixtureDef).setUserData(this);

        //Create Mario's head and make it a sensor for smashing objects.
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Boot.PPM, 7 / Boot.PPM), new Vector2(2 / Boot.PPM, 7 / Boot.PPM)); // 2, 7, compared to body(Def) position, 1st upper circle.
        fixtureDef.filter.categoryBits = Boot.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        timeToDefineBigMario = false;
    }

    public void redefineMario() {
        Vector2 currentPosition = body.getPosition();
        world.destroyBody(body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.4f;
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.MARIO_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COIN_BIT | Boot.ENEMY_BIT | Boot.ENEMY_HEAD_BIT | Boot.ITEM_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Boot.PPM, 7 / Boot.PPM), new Vector2(2 / Boot.PPM, 7 / Boot.PPM));
        fixtureDef.filter.categoryBits = Boot.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        timeToRedefineMario = false;
    }

    public void grow() {
        manager.get("audio/powerup.wav", Sound.class).play();
        runAnimationGrow = true;
        marioBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
    }

    //Mario shrinks or dies.
    public void hit() {
        if (marioBig) {
            manager.get("audio/powerdown.wav", Sound.class).play();
            marioBig = false;
            timeToRedefineMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() / 2); //Mawio was big, so he needs to be cut down in half. \m/
        } else {
            manager.get("audio/music.ogg", Music.class).stop();
            manager.get("audio/death.wav", Sound.class).play();
            marioDead = true;
            Filter filter = new Filter();
            filter.maskBits = Boot.NOTHING_BIT;
            for (Fixture fixture : body.getFixtureList())
                fixture.setFilterData(filter); //Every fixture in Mario's body will collide with nothing (NOTHING_BIT).
            body.setLinearVelocity(0, 0);
            body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
        }
    }

    //Method to return TextureRegion (or frame) to be drawn on screen.
    public TextureRegion getFrame(float deltaTime) {
        stateCurrent = getState();
        TextureRegion region;
        switch (stateCurrent) {
            case GROWING:
                region = animationGrow.getKeyFrame(stateTime);
                if (animationGrow.isAnimationFinished(stateTime))
                    runAnimationGrow = false;
                break;
            case JUMPING:
                region = marioBig ? animationJumpBig.getKeyFrame(stateTime) : animationJump.getKeyFrame(stateTime);
                break;
            case RUNNING:
                region = marioBig ? animationRunBig.getKeyFrame(stateTime, true) : animationRun.getKeyFrame(stateTime, true); //true - loop animation.
                break;
            case DEAD:
                region = animationDead;
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioBig ? animationStandBig : animationStand;
                break;
        }
        if ((body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false); //Flip on X axis only.
            runningRight = false;
        } else if ((body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTime = stateCurrent == statePrevious ? stateTime + deltaTime : 0;
        statePrevious = stateCurrent;
        return region;
    }

    public State getState() {
        if (marioDead)
            return State.DEAD;
        else if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && statePrevious == State.JUMPING))
            return State.JUMPING;
        else if (body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else if (runAnimationGrow)
            return State.GROWING;
        else
            return State.STANDING;
    }

    public boolean isBig() {
        return marioBig;
    }

}