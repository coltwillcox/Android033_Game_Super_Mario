package com.colt.supermario.sprites.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenPlay;

/**
 * Created by colt on 4/17/16.
 */

//TODO: Add alternate shell state. Or animation.

public class Turtle extends Enemy {

    public enum State {WALKING, SHELL}
    public State stateCurrent;
    public State statePrevious;

    private float stateTime;
    private boolean destroy;
    private boolean destroyed;
    private AssetManager manager;
    private TextureRegion animationShell;
    private Animation animationWalk;
    private Array<TextureRegion> frames;

    //Constructor.
    public Turtle(ScreenPlay screen, float x, float y, AssetManager manager) {
        super(screen, x, y);
        this.manager = manager;

        stateCurrent = statePrevious = State.WALKING;

        //Animations.
        frames = new Array<TextureRegion>();
        //Walk animation.
        for (int i = 0; i <= 1; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), i * 16, 0, 16, 24));
        animationWalk = new Animation(0.2f, frames);
        //Shell animation.
        animationShell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 4 * 16, 0, 16, 24);

        setBounds(getX(), getY(), 16 / Boot.PPM, 24 / Boot.PPM);
    }

    @Override
    public void update(float deltaTime) {
        setRegion(getFrame(deltaTime));
        if (stateCurrent == State.SHELL && stateTime > 5) {
            stateCurrent = State.WALKING;
            velocity.x = 0.5f;
        }
        setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (5 / Boot.PPM));
        body.setLinearVelocity(velocity);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.ENEMY_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.MARIO_BIT | Boot.BRICK_BIT | Boot.COIN_BIT | Boot.OBJECT_BIT | Boot.ENEMY_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        //Create head.
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-4, 8).scl(1 / Boot.PPM);
        vertice[1] = new Vector2(4, 8).scl(1 / Boot.PPM);
        vertice[2] = new Vector2(-2, 6).scl(1 / Boot.PPM);
        vertice[3] = new Vector2(2, 6).scl(1 / Boot.PPM);
        head.set(vertice);
        fixtureDef.filter.categoryBits = Boot.ENEMY_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.restitution = 0.5f; //Half of bounciness.
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead() {
        if (stateCurrent == State.WALKING) {
            stateCurrent = State.SHELL;
            velocity.x = 0;
        } else {

        }
    }

    public TextureRegion getFrame(float deltaTime) {
        TextureRegion region;

        switch (stateCurrent) {
            case SHELL:
                region = animationShell;
                break;
            case WALKING:
            default:
                region = animationWalk.getKeyFrame(stateTime);
                break;
        }

        if (body.getLinearVelocity().x > 0 && !region.isFlipX())
            region.flip(true, false);
        if (body.getLinearVelocity().x < 0 && region.isFlipX())
            region.flip(true, false);

        stateTime = stateCurrent == statePrevious ? stateTime + deltaTime : 0;
        statePrevious = stateCurrent;
        return region;
    }

}