package com.colt.supermario.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.colt.supermario.Boot;

/**
 * Created by colt on 4/17/16.
 */

//TODO: Add music.
//TODO: Add labels.
//TODO: Make Game Over screen disappears after some time if no user input, and return to Main Menu screen.

public class ScreenGameOver implements Screen {

    //Asset manager.
    private AssetManager manager;

    private Stage stage;
    private Camera camera; //Game Over screen have its own camera and viewport, same as HUD.
    private Viewport viewport;
    private Game game;
    private BitmapFont font;
    private Table table;
    private Label labelGameOver;
    private Label labelPlayAgain;

    //Constructor.
    public ScreenGameOver(Game game, AssetManager manager) {
        this.game = game;
        this.manager = manager;
        camera = new OrthographicCamera();
        viewport = new FitViewport(Boot.V_WIDTH, Boot.V_HEIGHT, camera);
        stage = new Stage(viewport, ((Boot) game).batch);

        font = new BitmapFont(Gdx.files.internal("graphic/fontsupermario.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font.getData().setScale(0.3f);

        labelGameOver = new Label("GAME OVER", new Label.LabelStyle(font, Color.WHITE));
        labelPlayAgain = new Label("PLAY AGAIN?", new Label.LabelStyle(font, Color.WHITE));

        table = new Table();
        table.center();
        table.setFillParent(true);
        table.add(labelGameOver).expandX();
        table.row();
        table.add(labelPlayAgain).expandX().padTop(20);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            game.setScreen(new ScreenPlay((Boot) game, manager));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

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
        stage.dispose();
    }

}