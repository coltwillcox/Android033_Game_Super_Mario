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

//TODO: Check fireballs creation. Every 1sec (stateTime > 1?) there is one stupid fireball. Maybe cuz of body destruction?

public class Fireball extends Sprite {

    public ScreenPlay screen;
    public World world;
    public Body body;

    private float stateTime;
    private boolean exploded;
    private boolean destroy;
    private boolean destroyed;
    private boolean fireRight;
    private TextureRegion animationExplosion;
    private Animation animationFire;
    private Array<TextureRegion> frames;

    //Constructor.
    public Fireball(ScreenPlay screen, float x, float y, boolean fireRight) {
        this.screen = screen;
        this.world = screen.getWorld();
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
        setBounds(getX(), getY(), 8 / Boot.PPM, 8 / Boot.PPM);
        setRegion(animationFire.getKeyFrame(stateTime, true));
        defineFireball();
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;

        if ((destroy && !exploded) || (stateTime > 1 && !exploded)) {
            world.destroyBody(body);
            exploded = true;
            setRegion(animationExplosion);
            stateTime = 0;
        }
        else if (!exploded) {
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
        body.setLinearVelocity(new Vector2(fireRight ? 2 : -2, 2));
    }

    //Getters and setters.
    public void setDestroy() {
        destroy = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

}