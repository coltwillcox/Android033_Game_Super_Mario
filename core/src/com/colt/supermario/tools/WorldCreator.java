package com.colt.supermario.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.tiles.Brick;
import com.colt.supermario.sprites.tiles.CoinBlock;
import com.colt.supermario.sprites.enemies.Enemy;
import com.colt.supermario.sprites.enemies.Goomba;
import com.colt.supermario.sprites.enemies.Koopa;
import com.colt.supermario.sprites.tiles.Flagpole;
import com.colt.supermario.sprites.tiles.MapTileObject;
import com.colt.supermario.sprites.tiles.Pipe;

/**
 * Created by colt on 4/13/16.
 */

public class WorldCreator {

    private AssetManager manager;
    private World world;
    private TiledMap map;
    private MapLayer mapLayer;

    //Enemies.
    private static Array<Enemy> enemies;
    private static Array<MapTileObject> tileObjects;

    //Flag and door.
    private Vector2 flagPosition;
    private Vector2 doorPosition;

    public WorldCreator(ScreenAbstract screen, AssetManager manager) {
        this.manager = manager;
        this.world = screen.getWorld();
        this.map = screen.getMap();

        //Create ground. Ground is created as rectangles in tmx map, not like objects (later on).
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;
        //Create ground bodies and fixtures.
        mapLayer = map.getLayers().get("ground");
        if (mapLayer != null) {
            for (MapObject object : mapLayer.getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / Boot.PPM, (rect.getY() + rect.getHeight() / 2) / Boot.PPM); //Get the centre of rect for positioning.

                body = world.createBody(bodyDef);

                shape.setAsBox((rect.getWidth() / 2) / Boot.PPM, (rect.getHeight() / 2) / Boot.PPM);
                fixtureDef.shape = shape;
                body.createFixture(fixtureDef);
            }
        }

        //Create objects.
        tileObjects = new Array<MapTileObject>();
        //Bricks.
        mapLayer = map.getLayers().get("bricks");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();
                tileObjects.add(new Brick(screen, (x + 8) / Boot.PPM, (y + 8) / Boot.PPM, (TiledMapTileMapObject) mapObject, manager));
            }
        }
        //Coinblocks.
        mapLayer = map.getLayers().get("coinblocks");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();
                tileObjects.add(new CoinBlock(screen, (x + 8) / Boot.PPM, (y + 8) / Boot.PPM, (TiledMapTileMapObject) mapObject, manager));
            }
        }
        //Pipes.
        mapLayer = map.getLayers().get("pipes");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();
                tileObjects.add(new Pipe(screen, (x + 8) / Boot.PPM, (y + 8) / Boot.PPM, (TiledMapTileMapObject) mapObject, manager));
            }
        }
        //Flagpole.
        mapLayer = map.getLayers().get("flagpole");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();
                tileObjects.add(new Flagpole(screen, (x + 8) / Boot.PPM, (y + 8) / Boot.PPM, (TiledMapTileMapObject) mapObject, manager));
            }
        }
        //Flag and door. Get only position.
        mapLayer = map.getLayers().get("flag");
        flagPosition = new Vector2(0, 0); //Default values, if flag or door does not exist.
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = (((TiledMapTileMapObject) mapObject).getX() - 9) / Boot.PPM;
                float y = ((TiledMapTileMapObject) mapObject).getY() / Boot.PPM;
                flagPosition = new Vector2(x, y);
            }
        }
        mapLayer = map.getLayers().get("door");
        doorPosition = new Vector2(0, 0);
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = (((TiledMapTileMapObject) mapObject).getX() + 8) / Boot.PPM;
                float y = ((TiledMapTileMapObject) mapObject).getY() / Boot.PPM;
                doorPosition = new Vector2(x, y);
            }
        }

        //Create enemies.
        enemies = new Array<Enemy>();
        //Goombas.
        mapLayer = map.getLayers().get("goombas");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();
                enemies.add(new Goomba(screen, (x + 8) / Boot.PPM, (y + 8) / Boot.PPM, manager));
            }
        }
        //Turtles.
        mapLayer = map.getLayers().get("koopas");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();
                enemies.add(new Koopa(screen, (x + 8) / Boot.PPM, (y + 8) / Boot.PPM, manager));
            }
        }
    }

    //Remove tile (eg. brick) when destroyed.
    public static void removeTileObject(MapTileObject tileObject) {
        tileObjects.removeValue(tileObject, true);
    }

    //Remove enemy (eg. Koopa) from array when it's killed.
    public static void removeEnemy(Enemy enemy) {
        enemies.removeValue(enemy, true);
    }

    //Getters.
    public Array<MapTileObject> getTileObjects() {
        return tileObjects;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Vector2 getFlagPosition() {
        return flagPosition;
    }

    public Vector2 getDoorPosition() {
        return doorPosition;
    }

}