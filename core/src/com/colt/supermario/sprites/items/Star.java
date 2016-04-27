package com.colt.supermario.sprites.items;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.hud.HUD;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/26/16.
 */

//TODO: Add Mario reaction.

public class Star extends Item {

    private boolean jumped;
    private float stateTime;
    private Array<TextureRegion> frames;
    private Animation animationStar;

    //Constructor.
    public Star(ScreenAbstract screen, float x, float y, AssetManager manager) {
        super(screen, x, y, manager);

        jumped = false;
        stateTime = 0;

        //Animations.
        frames = new Array<TextureRegion>();
        //Fire animation.
        for (int i = 0; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("star"), i * 16, 0, 16, 16));
        animationStar = new Animation(0.1f, frames);
        frames.clear();

        setBounds(getX(), getY(), 16 / Boot.PPM, 16 / Boot.PPM);
        setRegion(animationStar.getKeyFrame(stateTime, true));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        stateTime += deltaTime;

        if (stateTime < 1)
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() * 1.5f) + (1 / Boot.PPM) + (getHeight() * stateTime));
        else if (stateTime > 1 && !jumped) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - (getHeight() / 2) + (1 / Boot.PPM));
            body.applyLinearImpulse(new Vector2(0.5f, 1), body.getWorldCenter(), true);
            jumped = true;
        }
        else
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - (getHeight() / 2) + (1 / Boot.PPM));

        setRegion(animationStar.getKeyFrame(stateTime, true));
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
        fixtureDef.friction = 0;
        fixtureDef.restitution = 1;
        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    @Override
    public void use(Mario mario) {
        manager.get("audio/powerup.wav", Sound.class).play();
        HUD.addScore(1000);
        HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "1000");
        mario.superMario();
        destroy();
    }

}