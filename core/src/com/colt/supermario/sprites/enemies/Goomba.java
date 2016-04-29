package com.colt.supermario.sprites.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.hud.HUD;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/14/16.
 */

//TODO: Add side fixtures for colliding with ground (for movement reversing). Or maybe not. Add objects on map if needed?

public class Goomba extends Enemy {

    private float stateTime;
    private boolean destroy;
    private boolean squished;
    private boolean stared; //Hit by stared Mario or weapon.
    private AssetManager manager;
    private TextureRegion animationStomped;
    private TextureRegion animationDeath;
    private Animation animationWalk;
    private Array<TextureRegion> frames;

    //Constructor.
    public Goomba(ScreenAbstract screen, float x, float y, AssetManager manager) {
        super(screen, x, y);
        this.manager = manager;
        
        stateTime = 0;
        destroy = false;
        squished = false;
        stared = false;

        //Animations.
        frames = new Array<TextureRegion>();
        //Walk animation.
        for (int i = 0; i <= 1; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        animationWalk = new Animation(0.4f, frames);
        frames.clear();
        //Stomped and death.
        animationStomped = new TextureRegion(screen.getAtlas().findRegion("goomba"), 2 * 16, 0, 16, 16); // 2 *, beacuse it is the third Goomba image.
        animationDeath = new TextureRegion(screen.getAtlas().findRegion("goomba"), 0, 0, 16, 16);
        animationDeath.flip(false, true);

        setBounds(getX(), getY(), 16 / Boot.PPM, 16 / Boot.PPM);
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        velocity.y = body.getLinearVelocity().y;

        if (squished && !destroy) {
            Filter filter = new Filter();
            filter.categoryBits = Boot.DESTROYED_BIT;
            for (Fixture fixture : body.getFixtureList())
                fixture.setFilterData(filter);
            destroy = true;
            stateTime = 0;
            setRegion(animationStomped);
        }
        else if (stared && !destroy) {
            Filter filter = new Filter();
            filter.maskBits = Boot.NOTHING_BIT;
            for (Fixture fixture : body.getFixtureList())
                fixture.setFilterData(filter);
            destroy = true;
            stateTime = 0;
            setRegion(animationDeath);
            body.applyLinearImpulse(new Vector2(0, 2f), body.getWorldCenter(), true);
        }
        else if (!destroy) {
            setRegion(animationWalk.getKeyFrame(stateTime, true));
            body.setLinearVelocity(velocity);
        }

        setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (1 / Boot.PPM));

        if (stateTime > 1 && destroy) {
            world.destroyBody(body);
            destroyed = true;
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.ENEMY_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.MARIO_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT | Boot.OBJECT_BIT | Boot.ENEMY_BIT | Boot.WEAPON_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        //Create head.
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 9).scl(1 / Boot.PPM);
        vertice[1] = new Vector2(5, 9).scl(1 / Boot.PPM);
        vertice[2] = new Vector2(-2, 6).scl(1 / Boot.PPM);
        vertice[3] = new Vector2(2, 6).scl(1 / Boot.PPM);
        head.set(vertice);
        fixtureDef.filter.categoryBits = Boot.ENEMY_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.restitution = 0.5f; //Half of bounciness.
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Koopa && ((Koopa) enemy).stateCurrent == Koopa.State.SHELL_MOVING) {
            HUD.addScore(100);
            HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "100");
            squished = true;
        }
        else
            reverseVelocity(true, false);
    }

    @Override
    public void onHeadHit(Mario mario) {
        manager.get("audio/stomp.wav", Sound.class).play();
        HUD.addScore(100);
        HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "100");
        squished = true;
    }

    @Override
    public void onWeaponHit() {
        HUD.addScore(100);
        HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "100");
        stared = true;
    }

    @Override
    public void onStarHit() {
        HUD.addScore(100);
        HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "100");
        stared = true;
    }

}