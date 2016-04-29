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
//TODO: Mario shrink animation looks stupid. :p

public class Mario extends Sprite {

    //States.
    public enum State {FALLING, JUMPING, STANDING, RUNNING, BRAKING, GROWING, SHRINKING, CROUCHING, CLIMBING, DEAD}
    public State stateCurrent;
    public State statePrevious;

    //Screen, world, body.
    public ScreenAbstract screen;
    public World world;
    public Body body;

    //Asset manager.
    private AssetManager manager;

    //Some shit.
    private float stateTime;
    private float polePosition;
    private float animationFiringTimer; //Firing animation.
    private float invincibleTimer;
    private boolean runningRight;
    private boolean crouch;
    private boolean brake;
    private boolean climb;
    private boolean marioBig;
    private boolean growingUp;
    private boolean shrinkingDown;
    private boolean firing;
    private boolean invincible;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioDead;
    private boolean isLevelCompleted;

    //Frames to keep TextureRegions (to make Animations).
    private Array<TextureRegion> frames;

    //Normal animations.
    private TextureRegion animationStandSmall;
    private TextureRegion animationStandBig;
    private TextureRegion animationStandFire;
    private TextureRegion animationStandFiring;
    private TextureRegion animationBrakeSmall;
    private TextureRegion animationBrakeBig;
    private TextureRegion animationBrakeFire;
    private TextureRegion animationJumpSmall;
    private TextureRegion animationJumpBig;
    private TextureRegion animationJumpFire;
    private TextureRegion animationJumpFiring;
    private TextureRegion animationCrouchBig;
    private TextureRegion animationCrouchFire;
    private TextureRegion animationDead;
    private Animation animationClimbSmall;
    private Animation animationClimbBig;
    private Animation animationClimbFire;
    private Animation animationRunSmall;
    private Animation animationRunBig;
    private Animation animationRunFire;
    private Animation animationRunFiring;
    private Animation animationGrow;
    private Animation animationShrink;

    //Invincible animations.
    private Animation animationStandSmallInvincible;
    private Animation animationStandBigInvincible;
    private Animation animationStandFiringInvincible;
    private Animation animationBrakeSmallInvincible;
    private Animation animationBrakeBigInvincible;
    private Animation animationJumpSmallInvincible;
    private Animation animationJumpBigInvincible;
    private Animation animationJumpFiringInvincible;
    private Animation animationClimbSmallInvincible;
    private Animation animationClimbBigInvincible;
    private Animation animationRunSmallInvincible;
    private Animation animationRunBigInvincible;
    private Animation animationRunFiringInvincible;
    private Animation animationCrouchBigInvincible;
    private Animation animationGrowInvincible;

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
        invincibleTimer = 0;
        runningRight = true;
        crouch = false;
        brake = false;
        climb = false;
        growingUp = false;
        shrinkingDown = false;
        firing = false;
        invincible = false;
        isLevelCompleted = false;

        //Normal animations.
        frames = new Array<TextureRegion>();
        //Stand, brake, crouch and dead. Not really animations.
        animationStandSmall = new TextureRegion(screen.getAtlas().findRegion("mario_small"), 0, 0, 16, 16);
        animationStandBig = new TextureRegion(screen.getAtlas().findRegion("mario_big"), 0, 0, 16, 32);
        animationStandFire = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 0, 0, 16, 32);
        animationStandFiring = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 16 * 16, 0, 16, 32);
        animationBrakeSmall = new TextureRegion(screen.getAtlas().findRegion("mario_small"), 4 * 16, 0, 16, 16);
        animationBrakeBig = new TextureRegion(screen.getAtlas().findRegion("mario_big"), 4 * 16, 0, 16, 32);
        animationBrakeFire = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 4 * 16, 0, 16, 32);
        animationCrouchBig = new TextureRegion(screen.getAtlas().findRegion("mario_big"), 6 * 16, 0, 16, 32);
        animationCrouchFire = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 6 * 16, 0, 16, 32);
        animationDead = new TextureRegion(screen.getAtlas().findRegion("mario_small"), 6 * 16, 0, 16, 16);
        //Climb animation.
        for (int i = 7; i <= 8; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small"), i * 16, 0, 16, 16));
        animationClimbSmall = new Animation(0.1f, frames);
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
        animationRunSmall = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 1; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big"), i * 16, 0, 16, 32));
        animationRunBig = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 1; i <= 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_fire"), i * 16, 0, 16, 32));
        animationRunFire = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 16; i <= 18; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_fire"), i * 16, 0, 16, 32));
        animationRunFiring = new Animation(0.1f, frames);
        frames.clear();
        //Jump animations.
        animationJumpSmall = new TextureRegion(screen.getAtlas().findRegion("mario_small"), 5 * 16, 0, 16, 16); //Only one frame needed here.
        animationJumpBig = new TextureRegion(screen.getAtlas().findRegion("mario_big"), 5 * 16, 0, 16, 32);
        animationJumpFire = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 5 * 16, 0, 16, 32);
        animationJumpFiring = new TextureRegion(screen.getAtlas().findRegion("mario_fire"), 20 * 16, 0, 16, 32);
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

        //Invincible animations.
        //Stand small and big animations.
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible1"), 0, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible2"), 0, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible3"), 0, 0, 16, 16));
        animationStandSmallInvincible = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 0, 0, 16, 32));
        animationStandBigInvincible = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 16 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 16 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 16 * 16, 0, 16, 32));
        animationStandFiringInvincible = new Animation(0.1f, frames);
        frames.clear();
        //Run animations. I couldn't think of proper loop for this.
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible1"), 1 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible2"), 2 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible3"), 3 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible2"), 1 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible3"), 2 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible1"), 3 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible3"), 1 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible1"), 2 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible2"), 3 * 16, 0, 16, 16));
        animationRunSmallInvincible = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 1 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 2 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 3 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 1 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 2 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 3 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 1 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 2 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 3 * 16, 0, 16, 32));
        animationRunBigInvincible = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 16 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 17 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 18 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 16 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 17 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 18 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 16 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 17 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 18 * 16, 0, 16, 32));
        animationRunFiringInvincible = new Animation(0.1f, frames);
        frames.clear();
        //Brake animations.
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible1"), 4 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible2"), 4 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible3"), 4 * 16, 0, 16, 16));
        animationBrakeSmallInvincible = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 4 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 4 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 4 * 16, 0, 16, 32));
        animationBrakeBigInvincible = new Animation(0.1f, frames);
        frames.clear();
        //Jump animations.
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible1"), 5 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible2"), 5 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible3"), 5 * 16, 0, 16, 16));
        animationJumpSmallInvincible = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 5 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 5 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 5 * 16, 0, 16, 32));
        animationJumpBigInvincible = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 20 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 20 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 20 * 16, 0, 16, 32));
        animationJumpFiringInvincible = new Animation(0.1f, frames);
        frames.clear();
        //Climb animations.
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible1"), 7 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible2"), 8 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible3"), 7 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible1"), 8 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible2"), 7 * 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_small_invincible3"), 8 * 16, 0, 16, 16));
        animationClimbSmallInvincible = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 7 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 8 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 7 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 8 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 7 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 8 * 16, 0, 16, 32));
        animationClimbBigInvincible = new Animation(0.1f, frames);
        frames.clear();
        //Crouch animation.
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 6 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 6 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 6 * 16, 0, 16, 32));
        animationCrouchBigInvincible = new Animation(0.1f, frames);
        frames.clear();
        //Grow animation.
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 15 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 15 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible1"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible2"), 15 * 16, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("mario_big_invincible3"), 0, 0, 16, 32));
        animationGrowInvincible = new Animation(0.1f, frames);
        frames.clear();

        //Fireballs.
        fireballsArmed = false;
        fireballsToSpawn = new LinkedBlockingQueue<FireballDefinition>(1);
        fireballs = new Array<Fireball>();

        defineMario();
        setBounds(0, 0, 16 / Boot.PPM, 16 / Boot.PPM);
        setRegion(animationStandSmall);
    }

    public void update(float deltaTime) {
        handleFireballs();

        if (invincible) {
            invincibleTimer -= deltaTime;
            if (invincibleTimer <= 0) {
                invincible = false;
            }
            else if (invincibleTimer <= 2 && !manager.get("audio/music.ogg", Music.class).isPlaying()) {
                manager.get("audio/invincible.ogg", Music.class).stop();
                manager.get("audio/music.ogg", Music.class).play();
            }
        }

        animationFiringTimer += deltaTime;
        if (animationFiringTimer > 0.3f)
            firing = false;

        if (isLevelCompleted)
            handleLevelCompleted();

        if (marioBig)
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (9 / Boot.PPM)); //Sets the position where the sprite will be drawn.
        else
            setPosition(body.getPosition().x - (getWidth() / 2), body.getPosition().y - (getHeight() / 2) + (1 / Boot.PPM)); //+ (1 / Boot.PPM) because radius is just 6.

        setRegion(getFrame(deltaTime));

        //Must define and redefine Mario with boolean and update, because body can't be destroyed in world.step cycle.
        if (timeToDefineBigMario)
            defineBigMario();
        if (timeToRedefineMario)
            redefineSmallMario();

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
        bodyDef.position.set(currentPosition);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);
        body.setLinearVelocity(currentVelocity);

        //Lower circle in Mario's body.
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.4f;
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Boot.PPM);
        fixtureDef.filter.categoryBits = Boot.MARIO_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT | Boot.ENEMY_BIT | Boot.ENEMY_HEAD_BIT | Boot.ITEM_BIT | Boot.FLAGPOLE_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        //Upper circle in Mario's body.
        shape.setPosition(new Vector2(0, 16 / Boot.PPM));
        body.createFixture(fixtureDef).setUserData(this);

        //Create Mario's head and make it a sensor for smashing objects.
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Boot.PPM, 23 / Boot.PPM), new Vector2(2 / Boot.PPM, 23 / Boot.PPM)); // 2, 23, compared to body(Def) position, 1st lower circle.
        fixtureDef.filter.categoryBits = Boot.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        //Feet.
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-2 / Boot.PPM, -7 / Boot.PPM), new Vector2(2 / Boot.PPM, -7 / Boot.PPM));
        fixtureDef.shape = feet;
        fixtureDef.filter.categoryBits = Boot.MARIO_FEET_BIT;
        fixtureDef.filter.maskBits = Boot.GROUND_BIT | Boot.OBJECT_BIT | Boot.BRICK_BIT | Boot.COINBLOCK_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        timeToDefineBigMario = false;
    }

    public void redefineSmallMario() {
        Vector2 currentPosition = body.getPosition();
        Vector2 currentVelocity = body.getLinearVelocity();
        if (crouch)
            currentVelocity.x *= 0.75f;

        world.destroyBody(body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition);
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
        //Make Mario big only if he is small.
        if (!marioBig) {
            growingUp = true;
            marioBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        }
    }

    public void shrink() {
        manager.get("audio/powerdown.wav", Sound.class).play();
        shrinkingDown = true;
        fireballsArmed = false;
        marioBig = false;
        timeToRedefineMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() / 2); //Mawio was big, so he needs to be cut down in half. \m/
    }

    public void crouch() {
        if (!crouch) {
            crouch = marioBig;
            if (crouch)
                redefineSmallMario();
        }
    }

    public void standUp() {
        if (crouch) {
            defineBigMario();
            crouch = false;
        }
    }

    public void jump() {
        if (stateCurrent != State.JUMPING && stateCurrent != State.CROUCHING) {
            if (!marioBig)
                manager.get("audio/jumpsmall.wav", Sound.class).play();
            else
                manager.get("audio/jumpbig.wav", Sound.class).play();
            body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
            stateCurrent = State.JUMPING;
        }
    }

    public void die() {
        Boot.musicStop();
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
        fireballsToSpawn.add(new FireballDefinition(body.getPosition().x, body.getPosition().y + (16 / Boot.PPM), body.getLinearVelocity().x, runningRight));
    }

    public void handleFireballs() {
        if (fireballsToSpawn.size() > 0) {
            FireballDefinition fireballDefinition = fireballsToSpawn.poll();
            fireballs.add(new Fireball(screen, fireballDefinition.x, fireballDefinition.y, fireballDefinition.velocity, fireballDefinition.fireRight));
        }
    }

    //Mario hit by enemy ar dangerous object.
    public void hit(Object object) {
        if (object instanceof Enemy && invincible)
            ((Enemy) object).die();
        else if (object instanceof Koopa && ((Koopa) object).getStateCurrent() == Koopa.State.SHELL_STANDING)
            ((Koopa) object).kick(this.getX() <= ((Koopa) object).getX() ? Koopa.KICK_RIGHT_SPEED : Koopa.KICK_LEFT_SPEED);
        else {
            if (marioBig && object instanceof Enemy)
                shrink();
            else if (!shrinkingDown)
                die();
        }
    }

    //Mario goes invincible by a star.
    public void superMario() {
        manager.get("audio/music.ogg", Music.class).stop();
        manager.get("audio/invincible.ogg", Music.class).play();
        invincible = true;
        invincibleTimer = 10;
    }

    //Method to return TextureRegion (or frame) to be drawn on screen.
    public TextureRegion getFrame(float deltaTime) {
        stateCurrent = getState();
        TextureRegion region;

        switch (stateCurrent) {
            case GROWING:
                if (!invincible) {
                    region = animationGrow.getKeyFrame(stateTime);
                    if (animationGrow.isAnimationFinished(stateTime))
                        growingUp = false;
                }
                else {
                    region = animationGrowInvincible.getKeyFrame(stateTime);
                    if (animationGrowInvincible.isAnimationFinished(stateTime))
                        growingUp = false;
                }
                break;
            case SHRINKING:
                region = animationShrink.getKeyFrame(stateTime);
                if (animationShrink.isAnimationFinished(stateTime))
                    shrinkingDown = false;
                break;
            case JUMPING:
                if (!invincible) {
                    if (!fireballsArmed)
                        region = marioBig ? animationJumpBig : animationJumpSmall;
                    else
                        region = firing ? animationJumpFiring : animationJumpFire;
                }
                else {
                    if (!fireballsArmed)
                        region = marioBig ? animationJumpBigInvincible.getKeyFrame(stateTime, true) : animationJumpSmallInvincible.getKeyFrame(stateTime, true);
                    else
                        region = firing ? animationJumpFiringInvincible.getKeyFrame(stateTime, true) : animationJumpBigInvincible.getKeyFrame(stateTime, true);
                }
                break;
            case RUNNING:
                if (!invincible) {
                    if (!fireballsArmed)
                        region = marioBig ? animationRunBig.getKeyFrame(stateTime, true) : animationRunSmall.getKeyFrame(stateTime, true); //true - loop animation.
                    else
                        region = firing ? animationRunFiring.getKeyFrame(stateTime, true) : animationRunFire.getKeyFrame(stateTime, true);
                }
                else {
                    if (!fireballsArmed)
                        region = marioBig ? animationRunBigInvincible.getKeyFrame(stateTime, true) : animationRunSmallInvincible.getKeyFrame(stateTime, true);
                    else
                        region = firing ? animationRunFiringInvincible.getKeyFrame(stateTime, true) : animationRunBigInvincible.getKeyFrame(stateTime, true);
                }
                break;
            case BRAKING:
                if (!invincible) {
                    if (!fireballsArmed)
                        region = marioBig ? animationBrakeBig : animationBrakeSmall;
                    else
                        region = animationBrakeFire;
                }
                else
                    region = marioBig ? animationBrakeBigInvincible.getKeyFrame(stateTime, true) : animationBrakeSmallInvincible.getKeyFrame(stateTime, true);
                break;
            case CROUCHING:
                if (!invincible)
                    region = fireballsArmed ? animationCrouchFire : animationCrouchBig;
                else
                    region = animationCrouchBigInvincible.getKeyFrame(stateTime, true);
                break;
            case CLIMBING:
                if (!invincible) {
                    if (!fireballsArmed)
                        region = marioBig ? animationClimbBig.getKeyFrame(stateTime, true) : animationClimbSmall.getKeyFrame(stateTime, true);
                    else
                        region = animationClimbFire.getKeyFrame(stateTime, true);
                }
                else
                    region = marioBig ? animationClimbBigInvincible.getKeyFrame(stateTime, true) : animationClimbSmallInvincible.getKeyFrame(stateTime, true);
                break;
            case DEAD:
                region = animationDead;
                break;
            case FALLING:
            case STANDING:
            default:
                if (!invincible) {
                    if (!fireballsArmed)
                        region = marioBig ? animationStandBig : animationStandSmall;
                    else
                        region = firing ? animationStandFiring : animationStandFire;
                }
                else {
                    if (!fireballsArmed)
                        region = marioBig ? animationStandBigInvincible.getKeyFrame(stateTime, true) : animationStandSmallInvincible.getKeyFrame(stateTime, true);
                    else
                        region = firing ? animationStandFiringInvincible.getKeyFrame(stateTime, true) : animationStandBigInvincible.getKeyFrame(stateTime, true);
                }
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
        else if (brake && stateCurrent != State.JUMPING) {
            brake = false;
            return State.BRAKING;
        }
        else if (crouch)
            return State.CROUCHING;
        else if (climb)
            return State.CLIMBING;
        else if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && statePrevious == State.JUMPING))
            return State.JUMPING;
        else if (body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else if (growingUp)
            return State.GROWING;
        else if (shrinkingDown)
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

}