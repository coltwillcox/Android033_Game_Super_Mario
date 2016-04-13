package com.colt.supermario.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.colt.supermario.Boot;

/**
 * Created by colt on 4/13/16.
 */

public class Controller {

    public Viewport viewport;
    public Stage stage;
    public boolean upPressed;
    public boolean downPressed;
    public boolean leftPressed;
    public boolean rightPressed;
    public Image buttonUp;
    public Image buttonDown;
    public Image buttonLeft;
    public Image buttonRight;
    public OrthographicCamera camera;
    public Table table;

    //Constructor.
    public Controller(SpriteBatch spriteBatch) {
        camera = new OrthographicCamera();
        viewport = new FitViewport(Boot.V_WIDTH, Boot.V_HEIGHT, camera);
        stage = new Stage(viewport, spriteBatch);
        Gdx.input.setInputProcessor(stage);

        //Buttons with images.
        buttonUp = new Image(new Texture("graphics/buttonup.png"));
        buttonUp.setSize(25, 25);
        buttonUp.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });
        buttonDown = new Image(new Texture("graphics/buttondown.png"));
        buttonDown.setSize(25, 25);
        buttonDown.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = false;
            }
        });
        buttonLeft = new Image(new Texture("graphics/buttonleft.png"));
        buttonLeft.setSize(25, 25);
        buttonLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });
        buttonRight = new Image(new Texture("graphics/buttonright.png"));
        buttonRight.setSize(25, 25);
        buttonRight.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        //Table with buttons.
        table = new Table();
        table.left().bottom(); //Align to the left bottom.
        table.add();
        table.add(buttonUp).size(buttonUp.getWidth(), buttonUp.getHeight());
        table.add();
        table.row().pad(5, 5, 5, 5);
        table.add(buttonLeft).size(buttonLeft.getWidth(), buttonLeft.getHeight());
        table.add();
        table.add(buttonRight).size(buttonRight.getWidth(), buttonRight.getHeight());
        table.row().padBottom(5);
        table.add();
        table.add(buttonDown).size(buttonDown.getWidth(), buttonDown.getHeight());
        table.add();

        stage.addActor(table);
    }

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    //Getters.
    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

}