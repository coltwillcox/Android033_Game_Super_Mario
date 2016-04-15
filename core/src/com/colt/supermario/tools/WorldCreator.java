package com.colt.supermario.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenPlay;
import com.colt.supermario.sprites.Brick;
import com.colt.supermario.sprites.Coin;
import com.colt.supermario.sprites.EnemyGoomba;

/**
 * Created by colt on 4/13/16.
 */

public class WorldCreator {

    private AssetManager manager;
    private World world;
    private TiledMap map;
    private Array<EnemyGoomba> goombas;

    public WorldCreator(ScreenPlay screen, AssetManager manager) {
        this.manager = manager;
        this.world = screen.getWorld();
        this.map = screen.getMap();

        //Create body and fixture variables.
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        //Create ground bodies/fixtures.
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / Boot.PPM, (rect.getY() + rect.getHeight() / 2) / Boot.PPM); //Get the centre of rect for positioning.

            body = world.createBody(bodyDef);

            shape.setAsBox((rect.getWidth() / 2) / Boot.PPM, (rect.getHeight() / 2) / Boot.PPM);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }

        //Create pipes bodies/fixtures.
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / Boot.PPM, (rect.getY() + rect.getHeight() / 2) / Boot.PPM);

            body = world.createBody(bodyDef);

            shape.setAsBox((rect.getWidth() / 2) / Boot.PPM, (rect.getHeight() / 2) / Boot.PPM);
            fixtureDef.filter.categoryBits = Boot.OBJECT_BIT;
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }

        //Create coins bodies/fixtures.
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Coin(screen, rect, manager);
        }

        //Create bricks bodies/fixtures.
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Brick(screen, rect, manager);
        }

        //Create goombas.
        goombas = new Array<EnemyGoomba>();
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new EnemyGoomba(screen, rect.getX() / Boot.PPM, rect.getY() / Boot.PPM));
        }
    }

    //Getter.
    public Array<EnemyGoomba> getGoombas() {
        return goombas;
    }

}