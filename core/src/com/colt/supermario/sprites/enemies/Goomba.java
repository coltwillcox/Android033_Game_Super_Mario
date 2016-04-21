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
 * Created by colt on 4/14/16.
 */

//TODO: Add side fixtures for colliding with ground (for movement reversing).

public class Goomba extends Enemy {

    private float stateTime;
    private boolean destroy;
    private boolean squished;
    private AssetManager manager;
    private TextureRegion animationDeath;
    private Animation animationWalk;
    private Array<TextureRegion> frames;

    //Constructor.
    public Goomba(ScreenPlay screen, float x, float y, AssetManager manager) {
        super(screen, x, y);
        this.manager = manager;
        
        stateTime = 0;
        destroy = false;
        squished = false;

        //Animations.
        frames = new Array<TextureRegion>();
        //Walk animation.
        for (int i = 0; i <= 1; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        animationWalk = new Animation(0.4f, frames);
        frames.clear();
        //Death.
        animationDeath = new TextureRegion(screen.getAtlas().findRegion("goomba"), 2 * 16, 0, 16, 16); // 2 *, beacuse it is the third Goomba image.

        setBounds(getX(), getY(), 16 / Boot.PPM, 16 / Boot.PPM);
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        velocity.y = body.getLinearVelocity().y;

        if (destroy && !squished) {
            world.destroyBody(body);
            squished = true;
            setRegion(animationDeath);
            stateTime = 0;
        }
        else if (!squished) {
            setRegion(animationWalk.getKeyFrame(stateTime, true));
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (1 / Boot.PPM));
            body.setLinearVelocity(velocity);
        }

        if (stateTime > 1 && squished)
            destroyed = true;
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
        fixtureDef.restitution = 0.5f; //Half of bounciness.
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        manager.get("audio/stomp.wav", Sound.class).play();
        destroy = true;
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Koopa && ((Koopa) enemy).stateCurrent == Koopa.State.MOVING_SHELL)
            destroy = true;
        else
            reverseVelocity(true, false);
    }

    @Override
    public void die() {
        destroy = true;
        Filter filter = new Filter();
        filter.maskBits = Boot.NOTHING_BIT;
        for (Fixture fixture : body.getFixtureList())
            fixture.setFilterData(filter);
        body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
    }

}