package com.colt.supermario.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.colt.supermario.Boot;
import com.colt.supermario.scenes.HUD;

/**
 * Created by colt on 4/12/16.
 */

public class ScreenPlay implements Screen {

    private OrthographicCamera camera;
    private Viewport viewport;
    private Boot game;
    private HUD hud;

    //Constructor.
    public ScreenPlay(Boot game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(Boot.V_WIDTH, Boot.V_HEIGHT, camera); //Viewports cam be Fit, Screen, Stretch...
        hud = new HUD(game.batch);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}