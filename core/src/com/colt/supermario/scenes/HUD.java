package com.colt.supermario.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.colt.supermario.Boot;

/**
 * Created by colt on 4/12/16.
 */

public class HUD implements Disposable {

    public Stage stage;
    private static Integer score;
    private static Label labelScore;
    private float timeCount;
    private Integer worldTimer;
    private Camera camera; //HUD have its own camera and viewport.
    private Viewport viewport;
    private BitmapFont font;
    private Table table;
    private Label labelMario;
    private Label labelWorld;
    private Label labelTime;
    private Label labelLevel;
    private Label labelCountdown;

    //Constructor.
    public HUD(SpriteBatch spriteBatch) {
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        camera = new OrthographicCamera();
        viewport = new FitViewport(Boot.V_WIDTH, Boot.V_HEIGHT, camera);
        stage = new Stage(viewport, spriteBatch);

        font = new BitmapFont(Gdx.files.internal("graphic/fontsupermario.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font.getData().setScale(0.3f);

        labelMario = new Label("MARIO", new Label.LabelStyle(font, Color.WHITE));
        labelWorld = new Label("WORLD", new Label.LabelStyle(font, Color.WHITE));
        labelTime = new Label("TIME", new Label.LabelStyle(font, Color.WHITE));
        labelScore = new Label(String.format("%06d", score), new Label.LabelStyle(font, Color.WHITE));
        labelLevel = new Label("1-1", new Label.LabelStyle(font, Color.WHITE));
        labelCountdown = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(font, Color.WHITE));

        table = new Table();
        table.setFillParent(true); //Table is size of Stage.
        table.top(); //Align to the top.
        table.add(labelMario).expandX().padTop(10);
        table.add(labelWorld).expandX().padTop(10);
        table.add(labelTime).expandX().padTop(10);
        table.row();
        table.add(labelScore).expandX();
        table.add(labelLevel).expandX();
        table.add(labelCountdown).expandX();
        
        stage.addActor(table);
    }

    public void update(float deltaTime) {
        timeCount += deltaTime;
        if (timeCount >= 1) {
            worldTimer--;
            labelCountdown.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int scoreToAdd) {
        score += scoreToAdd;
        labelScore.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}