package com.colt.supermario.sprites.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
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
 * Created by colt on 4/14/16.
 */

// TODO: Add side fixtures for colliding with ground (for movement reversing).

public class Goomba extends Enemy {

    private float stateTime;
    private boolean destroy;
    private boolean destroyed;
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
        destroyed = false;

        //Walk animation.
        frames = new Array<TextureRegion>();
        for (int i = 0; i <= 1; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        animationWalk = new Animation(0.4f, frames);
        //Death.
        animationDeath = new TextureRegion(screen.getAtlas().findRegion("goomba"), 2 * 16, 0, 16, 16); // 2 *, beacuse it is the third Goomba image.

        setBounds(getX(), getY(), 16 / Boot.PPM, 16 / Boot.PPM);
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;

        if (destroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
            setRegion(animationDeath);
            stateTime = 0;
        } else if (!destroyed) {
            setRegion(animationWalk.getKeyFrame(stateTime, true));
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2));
            body.setLinearVelocity(velocity);
        }
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
    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1)
            super.draw(batch);
    }

    @Override
    public void hitOnHead() {
        manager.get("audio/stomp.wav", Sound.class).play();
        destroy = true;
    }



}