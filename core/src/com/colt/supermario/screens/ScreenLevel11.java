package com.colt.supermario.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.colt.supermario.Boot;

/**
 * Created by colt on 4/12/16.
 */

//TODO: More levels.

public class ScreenLevel11 extends ScreenAbstract{

    //Audio.
    protected Music music;

    public ScreenLevel11(Boot game, AssetManager manager) {
        super(game, manager);

        //Audio.
        music = manager.get("audio/music.ogg", Music.class);
        music.setLooping(true);
        music.play();
    }

    @Override
    public String mapName() {
        return "graphic/level11.tmx";
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        //Draw controller.
        controller.draw();
    }


}