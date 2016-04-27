package com.colt.supermario.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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
import com.colt.supermario.hud.HUD;

/**
 * Created by colt on 4/17/16.
 */

//TODO: Add labels.

public class ScreenGameOver implements Screen {

    //Asset manager.
    private AssetManager manager;

    //Stage, camera...
    private Stage stage;
    private Camera camera; //Game Over screen have its own camera and viewport, same as HUD.
    private Viewport viewport;
    private Game game;

    //State timer.
    private float stateTime;

    //Font.
    private BitmapFont font;

    //Table.
    private Table table;
    private Label labelGameOver;
    private Label labelPlayAgain;

    //Constructor.
    public ScreenGameOver(Game game, AssetManager manager) {
        this.game = game;
        this.manager = manager;

        manager.get("audio/gameover.wav", Music.class).play();

        //Stage, camera...
        camera = new OrthographicCamera();
        viewport = new FitViewport(Boot.V_WIDTH, Boot.V_HEIGHT, camera);
        stage = new Stage(viewport, ((Boot) game).batch);

        stateTime = 0;

        //Font. Uses HUD static font.
        font = HUD.getFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font.getData().setScale(0.3f);

        //Labels.
        labelGameOver = new Label("GAME OVER", new Label.LabelStyle(font, Color.WHITE));
        labelPlayAgain = new Label("PLAY AGAIN?", new Label.LabelStyle(font, Color.WHITE));

        //Table.
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.add(labelGameOver).expandX();
        table.row();
        table.add(labelPlayAgain).expandX().padTop(20);

        stage.addActor(table);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    @Override
    public void render(float delta) {
        update(delta);

        if (Gdx.input.justTouched()) {
            dispose();
            game.setScreen(new ScreenLevel11((Boot) game, manager));
        }
        else if (stateTime > 5) {
            dispose();
            game.setScreen(new ScreenMenu((Boot) game, manager));
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
        Boot.musicStop();
        stage.dispose();
    }

}