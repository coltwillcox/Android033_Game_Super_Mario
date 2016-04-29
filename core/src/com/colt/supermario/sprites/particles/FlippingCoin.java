package com.colt.supermario.sprites.particles;

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

/**
 * Created by colt on 4/21/16.
 */

//TODO: Create this little fella.

public class FlippingCoin extends Particle {

    private Array<TextureRegion> frames;
    private Animation animationFlipping;

    public FlippingCoin(ScreenAbstract screen, float x, float y, AssetManager manager) {
        super(screen, x, y, manager);

        manager.get("audio/coin.wav", Sound.class).play();
        HUD.addScore(200);

        //Animations.
        frames = new Array<TextureRegion>();
        //Flipping coin animation.
        for (int i = 0; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("coin_flipping"), i * 16, 0, 16, 16));
        for (int i = 2; i >= 1; i--)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("coin_flipping"), i * 16, 0, 16, 16)); //Add reversed frames, for smooth animation.
        animationFlipping = new Animation(0.1f, frames);
        frames.clear();

        setBounds(getX(), getY(), 16 / Boot.PPM, 16 / Boot.PPM);
        setRegion(animationFlipping.getKeyFrame(stateTime, true));
        body.applyLinearImpulse(new Vector2(0, 2), body.getWorldCenter(), true);
    }

    @Override
    public void defineParticle() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Boot.DESTROYED_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        stateTime += deltaTime;

        setRegion(animationFlipping.getKeyFrame(stateTime, true));
        setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2));

        if (stateTime > 0.3f)
            destroy = true;
    }

}