package com.colt.supermario.sprites.items;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.hud.HUD;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/21/16.
 */

public class Flower extends Item {

    private float stateTime;
    private Array<TextureRegion> frames;
    private Animation animationFlower;

    //Constructor.
    public Flower(ScreenAbstract screen, float x, float y, AssetManager manager) {
        super(screen, x, y, manager);

        stateTime = 0;

        //Animations.
        frames = new Array<TextureRegion>();
        //Fire animation.
        for (int i = 0; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("flower"), i * 16, 0, 16, 16));
        animationFlower = new Animation(0.1f, frames);
        frames.clear();

        setBounds(getX(), getY(), 16 / Boot.PPM, 16 / Boot.PPM);
        setRegion(animationFlower.getKeyFrame(stateTime, true));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        stateTime += deltaTime;

        if (stateTime < 1)
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() * 1.5f) + (1 / Boot.PPM) + (getHeight() * stateTime));
        else
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - (getHeight() / 2) + (1 / Boot.PPM));
        setRegion(animationFlower.getKeyFrame(stateTime, true));
    }

    @Override
    public void defineItem() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.ITEM_BIT;
        fixtureDef.filter.maskBits = Boot.MARIO_BIT | Boot.OBJECT_BIT | Boot.GROUND_BIT | Boot.COINBLOCK_BIT | Boot.BRICK_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    @Override
    public void use(Mario mario) {
        manager.get("audio/powerup.wav", Sound.class).play();
        HUD.addScore(1000);
        HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "1000");
        mario.setFireballsArmed(true);
        destroy();
    }

}