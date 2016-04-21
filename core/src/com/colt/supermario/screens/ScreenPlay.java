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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.colt.supermario.Boot;
import com.colt.supermario.scenes.HUD;
import com.colt.supermario.sprites.enemies.Enemy;
import com.colt.supermario.sprites.Mario;
import com.colt.supermario.sprites.items.Flower;
import com.colt.supermario.sprites.items.Item;
import com.colt.supermario.sprites.items.ItemDefinition;
import com.colt.supermario.sprites.items.Mushroom;
import com.colt.supermario.sprites.particles.Debris;
import com.colt.supermario.sprites.particles.Particle;
import com.colt.supermario.sprites.particles.ParticleDefinition;
import com.colt.supermario.sprites.tiles.MapTileObject;
import com.colt.supermario.tools.Controller;
import com.colt.supermario.tools.WorldContactListener;
import com.colt.supermario.tools.WorldCreator;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by colt on 4/12/16.
 */

//TODO: More levels.
//TODO: Add scores over heads.
//TODO: Brick smashing.
//TODO: Bricks bumping a little.

public class ScreenPlay implements Screen {

    private Boot game;

    //Asset manager.
    private AssetManager manager;

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

    //Borders.
    private float cameraBorderLeft;
    private float cameraBorderRight;

    //Box2D variables.
    private World world;
    private WorldContactListener worldContactListener;
    private Box2DDebugRenderer b2ddr;
    private WorldCreator worldCreator;

    //Player and enemies.
    private Mario mario;
    private float speed;

    //Fire timers.
    private float fireTimer;
    private float fireInterval;

    //Items.
    private LinkedBlockingQueue<ItemDefinition> itemsToSpawn;
    private Array<Item> items;

    //Particles (coins, bricks fragments...).
    private LinkedBlockingQueue<ParticleDefinition> particlesToSpawn;
    private Array<Particle> particles;

    //Joypad-like controller.
    private Controller controller;

    //Audio.
    private Music music;

    //Constructor.
    public ScreenPlay(Boot game, AssetManager manager) {
        this.game = game;
        this.manager = manager;

        //Textures.
        atlas = new TextureAtlas("graphic/sprites.atlas");

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

        //Set borders. Subtract one tile from each side. TMX file must have 1 tile offset (eg, pipes) on sides.
        cameraBorderLeft = (Boot.V_WIDTH / Boot.PPM / 2) + (((TiledMapTileLayer) map.getLayers().get(0)).getTileWidth() / Boot.PPM); //Viewport half + tile size.
        cameraBorderRight = (((TiledMapTileLayer) map.getLayers().get(0)).getWidth() * ((TiledMapTileLayer) map.getLayers().get(0)).getTileWidth() / Boot.PPM) - (Boot.V_WIDTH / Boot.PPM / 2) - (((TiledMapTileLayer) map.getLayers().get(0)).getTileWidth() / Boot.PPM); //Map width (in tiles) * tiles size - viewport width - tile size.

        //Set camera.
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0); //Center camera in the middle of the viewport.

        //Create world.
        world = new World(new Vector2(0, -10), true); //Create world and give it x, y gravity vector.
        worldContactListener = new WorldContactListener();
        world.setContactListener(worldContactListener);
        b2ddr = new Box2DDebugRenderer(); //Debug lines in Box2D world.
        worldCreator = new WorldCreator(this, manager);

        //Player and enemies.
        mario = new Mario(this, manager);
        speed = 1;

        //Fire timers.
        fireTimer = 0;
        fireInterval = 0.2f;

        //Items.
        itemsToSpawn = new LinkedBlockingQueue<ItemDefinition>();
        items = new Array<Item>();

        //Particles.
        particlesToSpawn = new LinkedBlockingQueue<ParticleDefinition>();
        particles = new Array<Particle>();

        //Controller.
        controller = new Controller(game.batch);

        //Audio.
        music = manager.get("audio/music.ogg", Music.class);
        music.setLooping(true);
        //music.play();
    }

    public void update(float deltaTime) {
        fireTimer += deltaTime;

        handleInput(deltaTime); //Handle user input first.
        handleItems();
        handleParticles();

        world.step(1 / 60f, 6, 2); //Takes 1 step in the physics simulation (60 times per second).
        mario.update(deltaTime);

        for (MapTileObject tileObject : worldCreator.getTileObjects()) {
            if (!tileObject.isDestroyed())
                tileObject.update(deltaTime);
            else
                WorldCreator.removeTileObject(tileObject);
        }

        for (Enemy enemy : worldCreator.getEnemies()) {
            if (!enemy.isDestroyed()) {
                enemy.update(deltaTime);
                if (enemy.getX() < mario.getX() + (224 / Boot.PPM)) //224 = 14 * 16 (Bricks from Mario * BrickSize).
                    enemy.body.setActive(true); //Set enemy active only if player is close (at < upper value).
            }
            else
                WorldCreator.removeEnemy(enemy);
        }

        for (Item item : items) {
            if (!item.isDestroyed())
                item.update(deltaTime);
            else
                items.removeValue(item, true);
        }

        for (Particle particle : particles) {
            if (!particle.isDestroyed())
                particle.update(deltaTime);
            else
                particles.removeValue(particle, true);

        }

        hud.update(deltaTime);

        //Attach camera to Mario, only when not dead. Set camera boundaries.
        if (mario.stateCurrent != Mario.State.DEAD)
            camera.position.x = MathUtils.clamp(mario.body.getPosition().x, cameraBorderLeft, cameraBorderRight);

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
        //b2ddr.render(world, camera.combined); //Render Box2DDebugLines.

        //Draw player and enemies.
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        mario.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        for (MapTileObject mapTileObject : worldCreator.getTileObjects())
            mapTileObject.draw(game.batch);
        for (Enemy enemy : worldCreator.getEnemies())
            enemy.draw(game.batch);
        for (Particle particle : particles)
            particle.draw(game.batch);
        game.batch.end();

        //Draw HUD.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        //Draw controller.
        controller.draw();

        //Check if game is over, so draw Game Over screen.
        if (gameOver()) {
            game.setScreen(new ScreenGameOver(game, manager));
            dispose();
        }
    }

    @Override
    public void show() {

    }

    public void handleInput(float deltaTime) {
        //Control Mario only if he is not dead.
        if (mario.stateCurrent != Mario.State.DEAD) {
            if ((controller.isUpPressed() || controller.isbPressed()) && worldContactListener.jumpability())
                mario.jump();
            if (controller.isRightPressed() && mario.body.getLinearVelocity().x <= speed)
                mario.body.applyLinearImpulse(new Vector2(0.2f, 0), mario.body.getWorldCenter(), true);
            if (controller.isLeftPressed() && mario.body.getLinearVelocity().x >= -speed)
                mario.body.applyLinearImpulse(new Vector2(-0.2f, 0), mario.body.getWorldCenter(), true);
            //Fire fireballs.
            if (controller.isaPressed() && fireTimer >= fireInterval && mario.getAmmo() < 2) {
                mario.spawnFireball();
                fireTimer = 0;
            }
            //Run faster.
            if (controller.isaPressed())
                speed = 2;
            else
                speed = 1;
        }
    }

    public void spawnItem(ItemDefinition itemDefinition) {
        itemsToSpawn.add(itemDefinition);
    }

    public void handleItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDefinition itemDefinition = itemsToSpawn.poll();
            if (itemDefinition.type == Mushroom.class)
                items.add(new Mushroom(this, itemDefinition.position.x, itemDefinition.position.y));
            else if (itemDefinition.type == Flower.class)
                items.add(new Flower(this, itemDefinition.position.x, itemDefinition.position.y));
        }
    }

    public boolean gameOver() {
        if (mario.stateCurrent == Mario.State.DEAD && mario.getStateTime() > 3)
            return true;
        else
            return false;
    }

    public void spawnParticle(ParticleDefinition particleDefinition) {
        particlesToSpawn.add(particleDefinition);
    }

    public void handleParticles() {
        if (!particlesToSpawn.isEmpty()) {
            ParticleDefinition particleDefinition = particlesToSpawn.poll();
            if (particleDefinition.type == Debris.class)
                particles.add(new Debris(this, particleDefinition.position.x, particleDefinition.position.y));
        }
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
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        b2ddr.dispose();
        hud.dispose();
    }

    //Getters.
    public TextureAtlas getAtlas() {
        return atlas;
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

}