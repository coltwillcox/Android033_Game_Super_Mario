package com.colt.supermario.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
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

    public enum State { FALLING, JUMPING, STANDING, RUNNING };
    public State stateCurrent;
    public State statePrevious;
    public World world;
    public Body body;
    private float stateTime;
    private boolean runningRight;
    private TextureRegion animationStand;
    private Animation animationRun;
    private Animation animationJump;
    private Array<TextureRegion> frames;

    //Constructor.
    public Mario(ScreenPlay screen) {
        this.world = screen.getWorld();

        stateCurrent = State.STANDING;
        statePrevious = State.STANDING;
        stateTime = 0;
        runningRight = true;

        //Animations.
        frames = new Array<TextureRegion>();
        //Mario standing. Not really animation.
        animationStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        //Run animation.
        for (int i = 1; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        animationRun = new Animation(0.1f, frames);
        frames.clear();
        //Jump animation.
        for (int i = 4; i <= 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        animationJump = new Animation(0.1f, frames);
        frames.clear();

        defineMario();
        setBounds(0, 0, 16 / Boot.PPM, 16 / Boot.PPM);
        setRegion(animationStand);
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
        body.createFixture(fixtureDef);

        //Create Mario's head and make it a sensor for smashing objects.
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Boot.PPM, 7 / Boot.PPM), new Vector2(2 / Boot.PPM, 7 / Boot.PPM));
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("head");

        //Create Mario's feet. Problem with this fixture.
        //EdgeShape feet = new EdgeShape();
        //feet.set(new Vector2(-2 / Boot.PPM, -7 / Boot.PPM), new Vector2(2 / Boot.PPM, -7 / Boot.PPM));
        //fixtureDef.shape = feet;
        //fixtureDef.isSensor = false;
        //body.createFixture(fixtureDef);
    }

    public void update(float deltaTime) {
        setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2)); //Minus (1 / Boot.PPM) because of feet fixture.
        setRegion(getFrame(deltaTime));
    }

    public TextureRegion getFrame(float deltaTime) {
        stateCurrent = getState();
        TextureRegion region;
        switch (stateCurrent) {
            case JUMPING:
                region = animationJump.getKeyFrame(stateTime);
                break;
            case RUNNING:
                region = animationRun.getKeyFrame(stateTime, true); //true - loop animation.
                break;
            case FALLING:
            case STANDING:
            default:
                region = animationStand;
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
        if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && statePrevious == State.JUMPING))
            return State.JUMPING;
        else if (body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

}