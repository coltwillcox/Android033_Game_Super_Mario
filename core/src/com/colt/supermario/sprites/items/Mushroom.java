package com.colt.supermario.sprites.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.colt.supermario.Boot;
import com.colt.supermario.hud.HUD;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/16/16.
 */

public class Mushroom extends Item {

    //Constructor.
    public Mushroom(ScreenAbstract screen, float x, float y) {
        super(screen, x, y);

        velocity = new Vector2(0.5f, 0);

        setBounds(getX(), getY(), 16 / Boot.PPM, 16 / Boot.PPM);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (1 / Boot.PPM));
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
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
        HUD.addScore(1000);
        HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "1000");
        destroy();
        mario.grow();
    }

}