package com.colt.supermario.sprites.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenAbstract;

/**
 * Created by colt on 4/23/16.
 */

public class Flag extends Actor {

    private Sprite spriteFlag;

    //Constructor.
    public Flag(ScreenAbstract screen, float x, float y) {
        spriteFlag = new Sprite(new TextureRegion(screen.getAtlas().findRegion("flag"), 0, 0, 16, 16));
        spriteFlag.setBounds(x, y, 16 / Boot.PPM, 16 / Boot.PPM);
        setBounds(spriteFlag.getX(), spriteFlag.getY(), spriteFlag.getWidth(), spriteFlag.getHeight());
    }

    @Override
    protected void positionChanged() {
        spriteFlag.setPosition(getX(), getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        spriteFlag.draw(batch);
    }

}