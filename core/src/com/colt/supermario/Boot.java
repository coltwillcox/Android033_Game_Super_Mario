package com.colt.supermario;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.colt.supermario.screens.ScreenPlay;

public class Boot extends Game {

    public static final int V_WIDTH = 370;
    public static final int V_HEIGHT = 208;
	public static final float PPM = 100; //Pixels per meter.
    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
    public static final short MARIO_HEAD_BIT = 512;
    public AssetManager manager;
    public SpriteBatch batch;

	@Override
	public void create () {
        manager = new AssetManager(); //Must pass it to every class that needs it.
        manager.load("audio/music.ogg", Music.class);
        manager.load("audio/breakblock.wav", Sound.class);
        manager.load("audio/bump.wav", Sound.class);
        manager.load("audio/coin.wav", Sound.class);
        manager.load("audio/death.wav", Sound.class);
        manager.load("audio/powerdown.wav", Sound.class);
        manager.load("audio/powerup.wav", Sound.class);
        manager.load("audio/powerupspawn.wav", Sound.class);
        manager.load("audio/stomp.wav", Sound.class);
        manager.finishLoading(); //Synchronized loading.

		batch = new SpriteBatch();
        setScreen(new ScreenPlay(this, manager));
	}

	@Override
	public void render () {
		super.render();
	}

    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
        batch.dispose();
    }

}