package com.colt.supermario.sprites.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenPlay;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/17/16.
 */

//TODO: Add alternate standing_shell state. Or animation. Something like WAKING_SHELL. :)
//TODO: Die as upsidedown shell.

public class Koopa extends Enemy {

    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public State stateCurrent;
    public State statePrevious;

    private float stateTime;
    private float rotationDegreesDead;
    private boolean destroy;
    private AssetManager manager;
    private TextureRegion animationShell;
    private Animation animationWalk;
    private Array<TextureRegion> frames;

    //Constructor.
    public Koopa(ScreenPlay screen, float x, float y, AssetManager manager) {
        super(screen, x, y);
        this.manager = manager;

        stateCurrent = statePrevious = State.WALKING;
        stateTime = 0;
        rotationDegreesDead = 0;

        //Animations.
        frames = new Array<TextureRegion>();
        //Walk animation.
        for (int i = 0; i <= 1; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("koopa"), i * 16, 0, 16, 32));
        animationWalk = new Animation(0.2f, frames);
        frames.clear();
        //Shell animation.
        animationShell = new TextureRegion(screen.getAtlas().findRegion("koopa"), 4 * 16, 0, 16, 32);

        setBounds(getX(), getY(), 16 / Boot.PPM, 32 / Boot.PPM);
    }

    @Override
    public void update(float deltaTime) {
        setRegion(getFrame(deltaTime));
        velocity.y = body.getLinearVelocity().y;

        if (stateCurrent == State.STANDING_SHELL && stateTime > 5) {
            stateCurrent = State.WALKING;
            velocity.x = 0.5f;
        }

        setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (9 / Boot.PPM));

        if (stateCurrent == State.DEAD) {
            setOriginCenter();
            rotationDegreesDead += 2;
            rotate(rotationDegreesDead); //Rotate sprite.
            if (stateTime > 5 && !destroyed) {
                world.destroyBody(body);
                destroyed = true;
            }
        }
        else
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
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.MARIO_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT | Boot.OBJECT_BIT | Boot.ENEMY_BIT | Boot.WEAPON_BIT;
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
        fixtureDef.restitution = 0.5f; //Bounciness.
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        if (stateCurrent != State.STANDING_SHELL) {
            stateCurrent = State.STANDING_SHELL;
            velocity.x = 0;
        }
        else
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Koopa) {
            if (((Koopa) enemy).stateCurrent == State.MOVING_SHELL && stateCurrent != State.MOVING_SHELL)
                die();
            else if (stateCurrent == State.MOVING_SHELL && ((Koopa) enemy).stateCurrent == State.WALKING)
                return;
            else
                reverseVelocity(true, false);
        }
        else if (stateCurrent != State.MOVING_SHELL) {
            reverseVelocity(true, false);
        }
    }

    public void kick(int speed) {
        manager.get("audio/kick.wav", Sound.class).play();
        velocity.x = speed;
        stateCurrent = State.MOVING_SHELL;
    }

    public TextureRegion getFrame(float deltaTime) {
        TextureRegion region;

        switch (stateCurrent) {
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = animationShell;
                break;
            case WALKING:
            default:
                region = animationWalk.getKeyFrame(stateTime, true);
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

    public State getStateCurrent() {
        return stateCurrent;
    }

    @Override
    public void die() {
        stateCurrent = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = Boot.NOTHING_BIT;
        for (Fixture fixture : body.getFixtureList())
            fixture.setFilterData(filter);
        body.applyLinearImpulse(new Vector2(0, 2f), body.getWorldCenter(), true);
    }

}