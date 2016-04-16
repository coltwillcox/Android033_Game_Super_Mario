package com.colt.supermario.sprites.items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by colt on 4/16/16.
 */

public class ItemDefinition {

    public Vector2 position;
    public Class<?> type;

    //Constructor.
    public ItemDefinition(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }
    
}