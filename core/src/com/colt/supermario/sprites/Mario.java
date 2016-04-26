package com.colt.supermario.sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.colt.supermario.Boot;
import com.colt.supermario.hud.HUD;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.enemies.Enemy;
import com.colt.supermario.sprites.enemies.Koopa;
import com.colt.supermario.sprites.weapons.Fireball;
import com.colt.supermario.sprites.weapons.FireballDefinition;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by colt on 4/13/16.
 */

//TODO: Program crashing when Mario have feet (sensor = false).
//TODO: Mario animations (crouch, invincibility).

public class Mario extends Sprite {

    //States.
    public enum State {FALLING, JUMPING, STANDING, RUNNING, BRAKING, GROWING, SHRINKING, CLIMBING, DEAD}
    public State stateCurrent;
    public State statePrevious;

    //Screen, world, body.
    public ScreenAbstract screen;
    public World world;
    public Body body;

    //Asset manager.
    private AssetManager manager;

    private float stateTime;
    private float polePosition;
    private float animationFiringTimer; //Firing animation.
    private float animationBrakeTimer; //Braking animation.
    private boolean climb;
    private boolean runningRight;
    private boolean brake;
    private boolean marioBig;
    private boolean growUp;
    private boolean shrinkDown;
    private boolean firing;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioDead;
    private boolean isLevelCompleted;

    //Animations.
    private TextureRegion animationStand;
    private TextureRegion animationStandBig;
    private TextureRegion animationStandFire;
    private TextureRegion animationStandFiring;
    private TextureRegion animationBrake;
    private TextureRegion animationBrakeBig;
    private TextureRegion animationBrakeFire;
    private TextureRegion animationJumpFiring;
    private TextureRegion animationDead;
    private Animation animationClimb;
    private Animation animationClimbBig;
    private Animation animationClimbFire;
    private Animation animationRun;
    private Animation animationRunBig;
    private Animation animationRunFire;
    private Animation animationRunFiring;
    private Animation animationJump;
    private Animation animationJumpBig;
    private Animation animationJumpFire;
    private Animation animationGrow;
    private Animation animationShrink;
    private Array<TextureRegion> frames;

    //Fireballs.
    private boolean fireballsArmed;
    private LinkedBlockingQueue<FireballDefinition> fireballsToSpawn;
    private Array<Fireball> fireballs;

    //Constructor.
    public Mario(ScreenAbstract screen, AssetManager manager) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.manager = manager;

        stateCurrent = statePrevious = State.STANDING;
        stateTime = 0;
        polePosition = 0;
        animationFiringTimer = 0;
        animationBrakeTimer = 0;
        runningRight = true;
        brake = false;
        climb = false;
        isLevelCompleted = false;
        growUp = false;
        shrinkDown = false;
        firing = false;

        //Animations.
        frames = new Array<TextureRegion>();
        //Standing, brake and dead. Not really animations.
        animationStand = new TextureRegion(screen.getAtlas().findRegion("mario_small"), 0, 0, 16, 16);
        animationStandBig = new TextureRegion(screen.getAtlas().findRegion("mario_big"), 0, 0, 16, 32);
        animationStandFire = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 0, 0, 16, 32);
        animationStandFiring = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 16 * 16, 0, 16, 32);
        animationBrake = new TextureRegion(screen.getAtlas().findRegion("mario_small"), 4 * 16, 0, 16, 16);
        animationBrakeBig = new TextureRegion(screen.getAtlas().findRegion("mario_big"), 4 * 16, 0, 16, 32);
        animationBrakeFire = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 4 * 16, 0, 16, 32);
        animationDead = new TextureRegion(screen.getAtlas().findRegion("mario_small"), 6 * 16, 0, 16, 16);
        //Climb animation.
        for (int i = 7; i <= 8; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small"), i * 16, 0, 16, 16));
        animationClimb = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 7; i <= 8; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big"), i * 16, 0, 16, 32));
        animationClimbBig = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 7; i <= 8; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_fire"), i * 16, 0, 16, 32));
        animationClimbFire = new Animation(0.1f, frames);
        frames.clear();
        //Run animations.
        for (int i = 1; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small"), i * 16, 0, 16, 16));
        animationRun = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 1; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big"), i * 16, 0, 16, 32));
        animationRunBig = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 1; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_fire"), i * 16, 0, 16, 32));
        animationRunFire = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 16; i <= 18; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_fire"), i * 16, 0, 16, 32));
        }
        animationRunFiring = new Animation(0.1f, frames);
        frames.clear();
        //Jump animations.
        for (int i = 4; i <= 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small"), i * 16, 0, 16, 16));
        animationJump = new Animation(0.2f, frames);
        frames.clear();
        for (int i = 4; i <= 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big"), i * 16, 0, 16, 32));
        animationJumpBig = new Animation(0.2f, frames);
        frames.clear();
        for (int i = 4; i <= 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_fire"), i * 16, 0, 16, 32));
        animationJumpFire = new Animation(0.2f, frames);
        frames.clear();
        animationJumpFiring = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 20 * 16, 0, 16, 32); //Only one frame needed here.
        //Grow and shrink animations.
        for (int i = 0; i <= 1; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big"), 15 * 16, 0, 16, 32)); //15 * 16, because it's 16th big_mario image.
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big"), 0, 0, 16, 32));
        }
        animationGrow = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 0; i <= 1; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big"), 0, 0, 16, 32));
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big"), 15 * 16, 0, 16, 32));
        }
        animationShrink = new Animation(0.1f, frames);
        frames.clear();

        //Fireballs.
        fireballsArmed = false;
        fireballsToSpawn = new LinkedBlockingQueue<FireballDefinition>(1);
        fireballs = new Array<Fireball>();

        defineMario();
        setBounds(0, 0, 16 / Boot.PPM, 16 / Boot.PPM);
        setRegion(animationStand);
    }

    public void update(float deltaTime) {
        handleFireballs();

        animationFiringTimer += deltaTime;
        if (animationFiringTimer > 0.3f)
            firing = false;
        animationBrakeTimer += deltaTime;
        if (animationBrakeTimer > 0.3f)
            brake = false;

        if (isLevelCompleted)
            handleLevelCompleted();

        if (marioBig)
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) - (7 / Boot.PPM)); //Sets the position where the sprite will be drawn.
        else
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (1 / Boot.PPM)); //+ (1 / Boot.PPM) because radius is just 6.

        setRegion(getFrame(deltaTime));

        //Must define and redefine Mario with boolean and update, because body can't be destroyed in world.step cycle.
        if (timeToDefineBigMario)
            defineBigMario();
        if (timeToRedefineMario)
            redefineMario();

        for (Fireball fireball : fireballs) {
            if (fireball.isDestroyed()) {
                fireballs.removeValue(fireball, true);
            }
            else
                fireball.update(deltaTime);
        }
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        for (Fireball fireball : fireballs)
            fireball.draw(batch);
    }

    public void defineMario() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(186 / Boot.PPM, 40 / Boot.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.4f; //Stop Mario from iceskating! ;)
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.MARIO_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT | Boot.ENEMY_BIT | Boot.ENEMY_HEAD_BIT | Boot.ITEM_BIT | Boot.FLAGPOLE_BIT; //Mario (fixture) will collide only with these BITS.
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        //Create Mario's head and make it a sensor for smashing objects.
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Boot.PPM, 7 / Boot.PPM), new Vector2(2 / Boot.PPM, 7 / Boot.PPM));
        fixtureDef.filter.categoryBits = Boot.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        //Create Mario's feet. Problem with this fixture if sensor = false.
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-2 / Boot.PPM, -7 / Boot.PPM), new Vector2(2 / Boot.PPM, -7 / Boot.PPM));
        fixtureDef.shape = feet;
        fixtureDef.filter.categoryBits = Boot.MARIO_FEET_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
    }

    public void defineBigMario() {
        Vector2 currentPosition = body.getPosition();
        Vector2 currentVelocity = body.getLinearVelocity();

        world.destroyBody(body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition.add(0, 8 / Boot.PPM));
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);
        body.setLinearVelocity(currentVelocity);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.4f;
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.MARIO_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT | Boot.ENEMY_BIT | Boot.ENEMY_HEAD_BIT | Boot.ITEM_BIT | Boot.FLAGPOLE_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        //Lower circle in Mario's body.
        shape.setPosition(new Vector2(0, -16 / Boot.PPM));
        body.createFixture(fixtureDef).setUserData(this);

        //Create Mario's head and make it a sensor for smashing objects.
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Boot.PPM, 7 / Boot.PPM), new Vector2(2 / Boot.PPM, 7 / Boot.PPM)); // 2, 7, compared to body(Def) position, 1st upper circle.
        fixtureDef.filter.categoryBits = Boot.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-2 / Boot.PPM, -23 / Boot.PPM), new Vector2(2 / Boot.PPM, -23 / Boot.PPM));
        fixtureDef.shape = feet;
        fixtureDef.filter.categoryBits = Boot.MARIO_FEET_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        timeToDefineBigMario = false;
    }

    public void redefineMario() {
        Vector2 currentPosition = body.getPosition();
        world.destroyBody(body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.4f;
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.MARIO_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT | Boot.ENEMY_BIT | Boot.ENEMY_HEAD_BIT | Boot.ITEM_BIT | Boot.FLAGPOLE_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Boot.PPM, 7 / Boot.PPM), new Vector2(2 / Boot.PPM, 7 / Boot.PPM));
        fixtureDef.filter.categoryBits = Boot.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-2 / Boot.PPM, -7 / Boot.PPM), new Vector2(2 / Boot.PPM, -7 / Boot.PPM));
        fixtureDef.shape = feet;
        fixtureDef.filter.categoryBits = Boot.MARIO_FEET_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        timeToRedefineMario = false;
    }

    public void grow() {
        //Make Mario big only if he is small, or add points if he is already big.
        if (!marioBig) {
            manager.get("audio/powerup.wav", Sound.class).play();
            growUp = true;
            marioBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        } else {
            manager.get("audio/powerup.wav", Sound.class).play();
        }
    }

    public void shrink() {
        manager.get("audio/powerdown.wav", Sound.class).play();
        shrinkDown = true;
        fireballsArmed = false;
        marioBig = false;
        timeToRedefineMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() / 2); //Mawio was big, so he needs to be cut down in half. \m/
    }

    public void die() {
        manager.get("audio/music.ogg", Music.class).stop();
        manager.get("audio/death.wav", Sound.class).play();
        if (marioBig)
            setBounds(getX(), getY(), getWidth(), getHeight() / 2);
        marioDead = true;
        HUD.setPaused(true);
        screen.setControllerOn(false);
        Filter filter = new Filter();
        filter.maskBits = Boot.NOTHING_BIT;
        for (Fixture fixture : body.getFixtureList())
            fixture.setFilterData(filter); //Every fixture in Mario's body will collide with nothing (NOTHING_BIT).
        body.setLinearVelocity(0, 0);
        body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
    }

    //Mario fires.
    public void spawnFireball() {
        manager.get("audio/fireball.wav", Sound.class).play();
        firing = true;
        fireballsToSpawn.add(new FireballDefinition(body.getPosition().x, body.getPosition().y, body.getLinearVelocity().x, runningRight));
    }

    public void handleFireballs() {
        if (fireballsToSpawn.size() > 0) {
            FireballDefinition fireballDefinition = fireballsToSpawn.poll();
            fireballs.add(new Fireball(screen, fireballDefinition.x, fireballDefinition.y, fireballDefinition.velocity, fireballDefinition.fireRight));
        }
    }

    public void jump() {
        if (stateCurrent != State.JUMPING) {
            if (!marioBig)
                manager.get("audio/jumpsmall.wav", Sound.class).play();
            else
                manager.get("audio/jumpbig.wav", Sound.class).play();
            body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
            stateCurrent = State.JUMPING;
        }
    }

    //Mario hit by enemy ar dangerous object.
    public void hit(Object object) {
        if (object instanceof Koopa && ((Koopa) object).getStateCurrent() == Koopa.State.SHELL_STANDING)
            ((Koopa) object).kick(this.getX() <= ((Koopa) object).getX() ? Koopa.KICK_RIGHT_SPEED : Koopa.KICK_LEFT_SPEED);
        else {
            if (marioBig && object instanceof Enemy)
                shrink();
            else if (!shrinkDown)
                die();
        }
    }

    //Method to return TextureRegion (or frame) to be drawn on screen.
    public TextureRegion getFrame(float deltaTime) {
        stateCurrent = getState();
        TextureRegion region;

        switch (stateCurrent) {
            case GROWING:
                region = animationGrow.getKeyFrame(stateTime);
                if (animationGrow.isAnimationFinished(stateTime))
                    growUp = false;
                break;
            case SHRINKING:
                region = animationShrink.getKeyFrame(stateTime);
                if (animationShrink.isAnimationFinished(stateTime))
                    shrinkDown = false;
                break;
            case JUMPING:
                if (!fireballsArmed)
                    region = marioBig ? animationJumpBig.getKeyFrame(stateTime) : animationJump.getKeyFrame(stateTime);
                else
                    region = firing ? animationJumpFiring : animationJumpFire.getKeyFrame(stateTime);
                break;
            case RUNNING:
                if (!fireballsArmed)
                    region = marioBig ? animationRunBig.getKeyFrame(stateTime, true) : animationRun.getKeyFrame(stateTime, true); //true - loop animation.
                else
                    region = firing ? animationRunFiring.getKeyFrame(stateTime, true) : animationRunFire.getKeyFrame(stateTime, true);
                break;
            case BRAKING:
                if (!fireballsArmed)
                    region = marioBig ? animationBrakeBig : animationBrake;
                else
                    region = animationBrakeFire;
                break;
            case CLIMBING:
                if (!fireballsArmed)
                    region = marioBig ? animationClimbBig.getKeyFrame(stateTime, true) : animationClimb.getKeyFrame(stateTime, true);
                else
                    region = animationClimbFire.getKeyFrame(stateTime, true);
                break;
            case DEAD:
                region = animationDead;
                break;
            case FALLING:
            case STANDING:
            default:
                if (!fireballsArmed)
                    region = marioBig ? animationStandBig : animationStand;
                else
                    region = firing ? animationStandFiring : animationStandFire;
                break;
        }

        if ((body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false); //Flip on X axis only.
            runningRight = false;
        } else if ((body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTime = stateCurrent == statePrevious ? stateTime + deltaTime : 0;
        statePrevious = stateCurrent;
        return region;
    }

    public State getState() {
        if (marioDead)
            return State.DEAD;
        else if (brake && stateCurrent != State.JUMPING)
            return State.BRAKING;
        else if (climb)
            return State.CLIMBING;
        else if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && statePrevious == State.JUMPING))
            return State.JUMPING;
        else if (body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else if (growUp)
            return State.GROWING;
        else if (shrinkDown)
            return State.SHRINKING;
        else
            return State.STANDING;
    }

    public void levelCompleted(float x) {
        if (isLevelCompleted)
            return;

        isLevelCompleted = true;
        climb = true;
        polePosition = x;
    }

    public void handleLevelCompleted() {
        if (climb) {
            runningRight = true;
            body.setTransform(polePosition, body.getPosition().y, 0);
            body.setLinearVelocity(new Vector2(0, -0.33f));
            //Wait for the flag to slide down (actor to finish action).
            if (screen.getFlag().getActions().size == 0) {
                manager.get("audio/stageclear.wav", Music.class).play();
                climb = false;
            }
        }
        else if (body.getPosition().x < screen.getWorldCreator().getDoorPosition().x)
            body.applyLinearImpulse(new Vector2(body.getMass() * (1 - body.getLinearVelocity().x), 0f), body.getWorldCenter(), true);
        else if (body.getPosition().x > screen.getWorldCreator().getDoorPosition().x)
            body.setTransform(screen.getWorldCreator().getDoorPosition().x, body.getPosition().y, 0);
    }

    //Getters and setters.
    public float getStateTime() {
        return stateTime;
    }

    public boolean isBig() {
        return marioBig;
    }

    public boolean isDead() {
        return marioDead;
    }

    public State getStateCurrent() {
        return stateCurrent;
    }

    public int getAmmo() {
        return fireballs.size;
    }

    public boolean isFireballsArmed() {
        return fireballsArmed;
    }

    public void setFireballsArmed(boolean fireballsArmed) {
        this.fireballsArmed = fireballsArmed;
    }

    public void setBrake(boolean brake) {
        this.brake = brake;
    }

    public void setAnimationFiringTimer(float animationFiringTimer) {
        this.animationFiringTimer = animationFiringTimer;
    }

    public void setAnimationBrakeTimer(float animationBrakeTimer) {
        this.animationBrakeTimer = animationBrakeTimer;
    }

}