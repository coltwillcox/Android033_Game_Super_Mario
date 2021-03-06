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
 * Created by colt on 4/17/16.
 */

public class Koopa extends Enemy {

    public enum State {WALKING, SHELL_STANDING, SHELL_MOVING, DEAD}
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public State stateCurrent;
    public State statePrevious;

    private float stateTime;
    private AssetManager manager;
    private TextureRegion animationShell;
    private Animation animationWalk;
    private Animation animationShellWaking;
    private Array<TextureRegion> frames;

    //Constructor.
    public Koopa(ScreenAbstract screen, float x, float y, AssetManager manager) {
        super(screen, x, y);
        this.manager = manager;

        stateCurrent = statePrevious = State.WALKING;
        stateTime = 0;

        //Animations.
        frames = new Array<TextureRegion>();
        //Walk animation.
        for (int i = 0; i <= 1; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("koopa"), i * 16, 0, 16, 32));
        animationWalk = new Animation(0.2f, frames);
        frames.clear();
        //Shell animation.
        animationShell = new TextureRegion(screen.getAtlas().findRegion("koopa"), 4 * 16, 0, 16, 32);
        //Waking shell animation.
        for (int i = 4; i <= 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("koopa"), i * 16, 0, 16, 32));
        animationShellWaking = new Animation(0.2f, frames);
        frames.clear();

        setBounds(getX(), getY(), 16 / Boot.PPM, 32 / Boot.PPM);
    }

    @Override
    public void update(float deltaTime) {
        setRegion(getFrame(deltaTime));
        velocity.y = body.getLinearVelocity().y;

        if (stateCurrent == State.SHELL_STANDING && stateTime > 5) {
            stateCurrent = State.WALKING;
            velocity.x = 0.5f;
        }

        setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (9 / Boot.PPM));

        if (stateCurrent == State.DEAD) {
            if (stateTime > 1 && !destroyed) {
                world.destroyBody(body);
                destroyed = true;
            }
        }
        else
            body.setLinearVelocity(velocity);
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
        fixtureDef.restitution = 0.5f; //Bounciness.
        body.createFixture(fixtureDef).setUserData(this);
    }

    public void kick(int speed) {
        manager.get("audio/kick.wav", Sound.class).play();
        velocity.x = speed;
        stateCurrent = State.SHELL_MOVING;
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Koopa) {
            if (((Koopa) enemy).stateCurrent == State.SHELL_MOVING && stateCurrent != State.SHELL_MOVING) {
                HUD.addScore(100);
                HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "100");
                die();
            }
            else if (stateCurrent == State.SHELL_MOVING && ((Koopa) enemy).stateCurrent == State.WALKING)
                return;
            else
                reverseVelocity(true, false);
        }
        else if (stateCurrent != State.SHELL_MOVING) {
            reverseVelocity(true, false);
        }
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (stateCurrent != State.SHELL_STANDING) {
            HUD.addScore(100);
            HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "100");
            stateCurrent = State.SHELL_STANDING;
            velocity.x = 0;
        }
        else
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
    }

    @Override
    public void onWeaponHit() {
        HUD.addScore(200);
        HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "200");
        die();
    }

    @Override
    public void onStarHit() {
        HUD.addScore(200);
        HUD.addScoreOverhead((body.getPosition().x - (screen.getCamera().position.x - screen.getCamera().viewportWidth / 2)) * Boot.PPM, body.getPosition().y * Boot.PPM, "200");
        die();
    }

    public void die() {
        stateCurrent = State.DEAD;
        animationShell.flip(false, true);
        Filter filter = new Filter();
        filter.maskBits = Boot.NOTHING_BIT;
        for (Fixture fixture : body.getFixtureList())
            fixture.setFilterData(filter);
        body.applyLinearImpulse(new Vector2(0, 2f), body.getWorldCenter(), true);
    }

    public TextureRegion getFrame(float deltaTime) {
        TextureRegion region;

        switch (stateCurrent) {
            case SHELL_STANDING:
                if (stateTime > 3)
                    region = animationShellWaking.getKeyFrame(stateTime, true);
                else
                    region = animationShell;
                break;
            case SHELL_MOVING:
                region = animationShell;
                break;
            case DEAD:
                //This region is first fliped in die() method.
                region = animationShell;
                break;
            case WALKING:
            default:
                region = animationWalk.getKeyFrame(stateTime, true);
                break;
        }

        if (body.getLinearVelocity().x > 0 && !region.isFlipX())
            region.flip(true, false);
        if (body.getLinearVelocity().x < 0 && region.isFlipX())
            region.flip(true, false);

        stateTime = stateCurrent == statePrevious ? stateTime + deltaTime : 0;
        statePrevious = stateCurrent;
        return region;
    }

    public State getStateCurrent() {
        return stateCurrent;
    }

}