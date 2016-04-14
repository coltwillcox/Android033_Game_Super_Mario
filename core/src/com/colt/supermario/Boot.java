package com.colt.supermario;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.colt.supermario.screens.ScreenPlay;

public class Boot extends Game {

    public static final int V_WIDTH = 370;
    public static final int V_HEIGHT = 208;
	public static final float PPM = 100; //Pixels per meter.
    public static final short DEFAULT_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
        setScreen(new ScreenPlay(this));
	}

	@Override
	public void render () {
		super.render();
	}

}