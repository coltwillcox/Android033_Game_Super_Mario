package com.colt.supermario.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.colt.supermario.Boot;

/**
 * Created by colt on 4/22/16.
 */

public class ScreenMenu extends ScreenAbstract {

    public ScreenMenu(Boot game, AssetManager manager) {
        super(game, manager);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        //Screen changing must go through render() method.
        if (Gdx.input.justTouched()) {
            dispose();
            game.setScreen(new ScreenLevel11((Boot) game, manager));
        }
    }

    @Override
    public String mapName() {
        return "graphic/menu.tmx";
    }

    @Override
    public void handleInput(float deltaTime) {
        //Fuck you, Jose, we do not handle input in main menu! :D
    }

}