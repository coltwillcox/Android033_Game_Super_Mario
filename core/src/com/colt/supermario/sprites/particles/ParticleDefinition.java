package com.colt.supermario.sprites.particles;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by colt on 4/21/16.
 */

public class ParticleDefinition {

    public Vector2 position;
    public Class<?> type;

    //Constructor.
    public ParticleDefinition(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }

}