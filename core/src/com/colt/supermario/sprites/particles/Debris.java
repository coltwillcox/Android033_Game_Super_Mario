package com.colt.supermario.sprites.particles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenAbstract;

/**
 * Created by colt on 4/21/16.
 */

public class Debris extends Particle {

    private Array<TextureRegion> frames;
    private Animation animationDebris;
    private Vector2 force;

    //Constructor.
    public Debris(ScreenAbstract screen, float x, float y) {
        super(screen, x, y);

        //Animations.
        frames = new Array<TextureRegion>();
        //Debris animation.
        frames.add(new TextureRegion(screen.getAtlas().findRegion("debris"), 0, 0, 8, 8));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("debris"), 8, 0, 8, 8));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("debris"), 8, 8, 8, 8));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("debris"), 0, 8, 8, 8));
        animationDebris = new Animation(0.1f, frames);
        frames.clear();

        setRegion(animationDebris.getKeyFrame(stateTime, true));
        force = new Vector2(MathUtils.random() * 1.6f - 0.8f, MathUtils.random() * 1.2f + 2.4f);
        body.applyLinearImpulse(force, body.getWorldCenter(), true);
    }

    @Override
    public void defineParticle() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(4 / Boot.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Boot.DESTROYED_BIT;
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.25f;
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        stateTime += deltaTime;

        setRegion(animationDebris.getKeyFrame(stateTime, true));
        setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2));

        if (stateTime > 1)
            destroy = true;
    }

}