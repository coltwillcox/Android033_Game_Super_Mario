package com.colt.supermario.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.colt.supermario.Boot;
import com.colt.supermario.scenes.HUD;
import com.colt.supermario.sprites.Mario;
import com.colt.supermario.tools.Controller;

/**
 * Created by colt on 4/12/16.
 */

public class ScreenPlay implements Screen {

    private Boot game;
    private HUD hud;
    private Mario mario;
    private OrthographicCamera camera;
    private Viewport viewport;

    //Tiled map variables.
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    //Box2D variables.
    private World world;
    private Box2DDebugRenderer b2ddr;

    //Joypad-like controller.
    private Controller controller;

    //Constructor.
    public ScreenPlay(Boot game) {
        this.game = game;
        camera = new OrthographicCamera(); //Camera to follow Mario.
        viewport = new FitViewport(Boot.V_WIDTH / Boot.PPM, Boot.V_HEIGHT / Boot.PPM, camera); //Viewports cam be Fit, Screen, Stretch...
        hud = new HUD(game.batch); //HUD for scores, timers, infos...

        //Parameters for tiled texture rendering.
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;

        //Load map and setup map renderer.
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("graphics/level11.tmx", params);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / Boot.PPM);

        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0); //Center camera in the middle of the viewport.
        world = new World(new Vector2(0, -10), true); //Create world and give it x, y gravity vector.
        b2ddr = new Box2DDebugRenderer();

        mario = new Mario(world); //Player.
        controller = new Controller(game.batch);

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //Create ground bodies/fixtures.
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Boot.PPM, (rect.getY() + rect.getHeight() / 2) / Boot.PPM); //Get the centre of rect for positioning.

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Boot.PPM, (rect.getHeight() / 2) / Boot.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //Create pipes bodies/fixtures.
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Boot.PPM, (rect.getY() + rect.getHeight() / 2) / Boot.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Boot.PPM, (rect.getHeight() / 2) / Boot.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //Create coins bodies/fixtures.
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Boot.PPM, (rect.getY() + rect.getHeight() / 2) / Boot.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Boot.PPM, (rect.getHeight() / 2) / Boot.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //Create bricks bodies/fixtures.
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Boot.PPM, (rect.getY() + rect.getHeight() / 2) / Boot.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Boot.PPM, (rect.getHeight() / 2) / Boot.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
    }

    @Override
    public void show() {

    }

    public void handleInput(float deltaTime) {
        if (controller.isUpPressed() && mario.body.getLinearVelocity().y == 0)
            mario.body.applyLinearImpulse(new Vector2(0, 4), mario.body.getWorldCenter(), true); //true - will this impulse wake object.
        if (controller.isRightPressed() && mario.body.getLinearVelocity().x <= 2)
            mario.body.applyLinearImpulse(new Vector2(0.1f, 0), mario.body.getWorldCenter(), true);
        if (controller.isLeftPressed() && mario.body.getLinearVelocity().x >= -2)
            mario.body.applyLinearImpulse(new Vector2(-0.1f, 0), mario.body.getWorldCenter(), true);
    }

    public void update(float deltaTime) {
        handleInput(deltaTime); //Handle user input first.
        world.step(1/60f, 6, 2);
        camera.position.x = mario.body.getPosition().x;
        camera.update(); //Update camera with correct coordinates after changes.
        mapRenderer.setView(camera); //Set renderer to draw only what camera can see in game world.
    }

    @Override
    public void render(float delta) {
        update(delta);

        //Clear the screen with given color.
        Gdx.gl.glClearColor(0.39f, 0.68f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render(); //Render game map.
        b2ddr.render(world, camera.combined); //Render B0x2DDebugLines.

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        //if (Gdx.app.getType() == Application.ApplicationType.Android) //Use to check and show controller only on android devices.
        controller.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        controller.resize(width, height);
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