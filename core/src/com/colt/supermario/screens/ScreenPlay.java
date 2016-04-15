package com.colt.supermario.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.colt.supermario.Boot;
import com.colt.supermario.scenes.HUD;
import com.colt.supermario.sprites.Enemy;
import com.colt.supermario.sprites.EnemyGoomba;
import com.colt.supermario.sprites.Mario;
import com.colt.supermario.tools.Controller;
import com.colt.supermario.tools.WorldContactListener;
import com.colt.supermario.tools.WorldCreator;

/**
 * Created by colt on 4/12/16.
 */

public class ScreenPlay implements Screen {

    private Boot game;

    //Textures.
    private TextureAtlas atlas;

    //Camera, Viewport, HUD.
    private OrthographicCamera camera;
    private Viewport viewport;
    private HUD hud;

    //Tiled map variables.
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    //Box2D variables.
    private World world;
    private Box2DDebugRenderer b2ddr;
    private WorldCreator worldCreator;

    //Player and enemies.
    private Mario mario;

    //Joypad-like controller.
    private Controller controller;

    //Asset manager.
    private AssetManager manager;

    //Audio.
    private Music music;

    //Constructor.
    public ScreenPlay(Boot game, AssetManager manager) {
        this.game = game;
        this.manager = manager;

        //Textures.
        atlas = new TextureAtlas("graphic/sprites.pack");

        //Camera to follow Mario, Viewport, HUD.
        camera = new OrthographicCamera();
        viewport = new FitViewport(Boot.V_WIDTH / Boot.PPM, Boot.V_HEIGHT / Boot.PPM, camera);
        hud = new HUD(game.batch);

        //Parameters for tiled texture rendering. Change Nearest to Linear for filtering.
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;

        //Load map and setup map renderer.
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("graphic/level11.tmx", params);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / Boot.PPM);

        //Set camera position and create world.
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0); //Center camera in the middle of the viewport.
        world = new World(new Vector2(0, -10), true); //Create world and give it x, y gravity vector.
        b2ddr = new Box2DDebugRenderer(); //Debug lines in Box2D world.
        worldCreator = new WorldCreator(this, manager);
        world.setContactListener(new WorldContactListener());

        //Player and enemies.
        mario = new Mario(this);

        //Controller.
        controller = new Controller(game.batch);

        //Audio.
        //music = manager.get("audio/music.ogg", Music.class);
        //music.setLooping(true);
        //music.play();
    }

    @Override
    public void show() {

    }

    public void handleInput(float deltaTime) {
        if ((controller.isUpPressed() || controller.isbPressed()) && mario.body.getLinearVelocity().y == 0)
            mario.body.applyLinearImpulse(new Vector2(0, 4), mario.body.getWorldCenter(), true); //true - will this impulse wake object.
        if (controller.isRightPressed() && mario.body.getLinearVelocity().x <= 2)
            mario.body.applyLinearImpulse(new Vector2(0.2f, 0), mario.body.getWorldCenter(), true);
        if (controller.isLeftPressed() && mario.body.getLinearVelocity().x >= -2)
            mario.body.applyLinearImpulse(new Vector2(-0.2f, 0), mario.body.getWorldCenter(), true);
    }

    public void update(float deltaTime) {
        handleInput(deltaTime); //Handle user input first.
        world.step(1 / 60f, 6, 2); //Takes 1 step in the physics simulation (60 times per second).
        mario.update(deltaTime);
        for (Enemy enemy : worldCreator.getGoombas())
            enemy.update(deltaTime);
        hud.update(deltaTime);
        camera.position.x = mario.body.getPosition().x; //Attach camera to Mario.
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
        b2ddr.render(world, camera.combined); //Render Box2DDebugLines.

        //Draw player and enemies.
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        mario.draw(game.batch);
        for (Enemy enemy : worldCreator.getGoombas())
            enemy.draw(game.batch);
        game.batch.end();

        //Draw HUD.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

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

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        b2ddr.dispose();
        hud.dispose();
    }

}