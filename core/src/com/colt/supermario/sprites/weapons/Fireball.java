package com.colt.supermario.sprites.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenPlay;

/**
 * Created by colt on 4/18/16.
 */

public class Fireball extends Sprite {

    public ScreenPlay screen;
    public World world;
    private Body body;
    private float stateTime;
    private float velocity;
    private boolean exploded;
    private boolean destroy;
    private boolean destroyed;
    private boolean fireRight;
    private Array<TextureRegion> frames;
    private Animation animationFire;
    private TextureRegion animationExplosion;

    //Constructor.
    public Fireball(ScreenPlay screen, float x, float y, float velocity, boolean fireRight) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.velocity = velocity;
        this.fireRight = fireRight;

        stateTime = 0;
        exploded = false;
        destroy = false;
        destroyed = false;

        //Animations.
        frames = new Array<TextureRegion>();
        //Fire animation.
        for (int i = 0; i <= 1; i++)
            for (int j = 0; j <= 1; j++)
                frames.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i * 8, j * 8, 8, 8));
        animationFire = new Animation(0.1f, frames);
        frames.clear();
        //Explosion animation.
        animationExplosion = new TextureRegion(screen.getAtlas().findRegion("fireball"), 20, 4, 8, 8);

        setPosition(x, y);
        defineFireball();
        setBounds(getX(), getY(), 8 / Boot.PPM, 8 / Boot.PPM);
        setRegion(animationFire.getKeyFrame(stateTime, true));
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;

        if ((destroy || stateTime > 1) && !exploded) {
            world.destroyBody(body);
            destroy = true;
            exploded = true;
            setRegion(animationExplosion);
            stateTime = 0;
        }
        if (!exploded) {
            setRegion(animationFire.getKeyFrame(stateTime, true));
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2));
        }
        if (stateTime > 0.1f && exploded)
            destroyed = true;
    }

    public void defineFireball() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(fireRight ? getX() + (8 / Boot.PPM) : getX() - (8 / Boot.PPM), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        if(!world.isLocked())
            body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(4 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.WEAPON_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT | Boot.OBJECT_BIT | Boot.ENEMY_BIT;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0.75f;
        body.createFixture(fixtureDef).setUserData(this);
        body.applyLinearImpulse(new Vector2(fireRight ? 2 + velocity : -2 + velocity, 1.25f), body.getWorldCenter(), true);
        //body.setLinearVelocity(new Vector2(fireRight ? 2 : -2, 1.25f));

        shape.dispose();
    }

    //Getters and setters.
    public void setDestroy() {
        destroy = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

}