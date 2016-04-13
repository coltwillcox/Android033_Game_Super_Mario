package com.colt.supermario.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.colt.supermario.Boot;

/**
 * Created by colt on 4/13/16.
 */

public class Mario extends Sprite {

    public World world;
    public Body body;

    //Constructor.
    public Mario(World world) {
        this.world = world;
        defineMario();
    }

    public void defineMario() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32 / Boot.PPM, 64 / Boot.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / Boot.PPM);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }

}