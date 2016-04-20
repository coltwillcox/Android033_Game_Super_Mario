package com.colt.supermario.sprites.tiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenPlay;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/13/16.
 */

//TODO: Add mushrom, coin... checkers.

public class CoinBlock extends MapTileObject {

    private boolean hit;
    private boolean moved;
    private float stateTime;
    private AssetManager manager;
    private Vector2 positionOriginal;
    private Vector2 positionMovable;
    private Vector2 positionTarget;
    private Animation animationQuestion;
    private Array<TextureRegion> frames;
    private TextureRegion animationBumped;

    public CoinBlock(ScreenPlay screen, float x, float y, TiledMapTileMapObject mapObject, AssetManager manager) {
        super(screen, x, y, mapObject);
        this.manager = manager;

        stateTime = 0;
        hit = false;

        //Animations.
        frames = new Array<TextureRegion>();
        //Question mark animation.
        for (int i = 25; i <= 27; i++)
            frames.add(screen.getMap().getTileSets().getTileSet(0).getTile(i).getTextureRegion());
        animationQuestion = new Animation(0.2f, frames);
        frames.clear();
        //Bumped animation. Not really animation.
        animationBumped = screen.getMap().getTileSets().getTileSet(0).getTile(28).getTextureRegion();

        positionOriginal = new Vector2(x, y);
        positionMovable = new Vector2(x, y + 0.05f);
        positionTarget = positionOriginal;
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        if (hit)
            setRegion(animationBumped);
        else
            setRegion(animationQuestion.getKeyFrame(stateTime, true));

        float x = body.getPosition().x;
        float y = body.getPosition().y;
        Vector2 dist = new Vector2(x, y).sub(positionTarget);
        if (dist.len2() > 0.0001f)
            body.setTransform(new Vector2(x, y).lerp(positionTarget, 0.6f), 0);
        else {
            body.setTransform(positionTarget, 0);
            positionTarget = positionOriginal;
        }

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    @Override
    protected void defineBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / Boot.PPM / 2, 16 / Boot.PPM / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Boot.COINBLOCK_BIT;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (!hit) {
            manager.get("audio/powerupspawn.wav", Sound.class).play();
            positionTarget = positionMovable;
            hit = true;
        }
        else
            manager.get("audio/bump.wav", Sound.class).play();
    }

}