package com.colt.supermario.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.colt.supermario.Boot;
import com.colt.supermario.sprites.Mario;
import com.colt.supermario.sprites.enemies.Enemy;
import com.colt.supermario.sprites.items.Item;
import com.colt.supermario.sprites.tiles.MapTileObject;
import com.colt.supermario.sprites.weapons.Fireball;

/**
 * Created by colt on 4/13/16.
 */

public class WorldContactListener implements ContactListener {

    private int feetOnGround;

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int beginDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (beginDef) {
            case Boot.MARIO_HEAD_BIT | Boot.COINBLOCK_BIT:
            case Boot.MARIO_HEAD_BIT | Boot.BRICK_BIT: //Mario smash bricks with his head! \m/
                if (fixA.getFilterData().categoryBits == Boot.MARIO_HEAD_BIT)
                    ((MapTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((MapTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case Boot.MARIO_BIT | Boot.ENEMY_HEAD_BIT: //If those two collide. Mario jumping on enemy's head.
                if (fixA.getFilterData().categoryBits == Boot.ENEMY_HEAD_BIT)
                    ((Enemy) fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else
                    ((Enemy) fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            case Boot.ENEMY_BIT | Boot.OBJECT_BIT: //Enemy will reverse their movement when they hit an object.
                if (fixA.getFilterData().categoryBits == Boot.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Boot.ENEMY_BIT | Boot.ENEMY_BIT: //Enemy will reverse their movement when they hit each other.
                ((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                ((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
                break;
            case Boot.ITEM_BIT | Boot.OBJECT_BIT: //Item will reverse their movement when they hit an object.
                if (fixA.getFilterData().categoryBits == Boot.ITEM_BIT)
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Boot.ITEM_BIT | Boot.MARIO_BIT: //Mario will use an item when they collide.
                if (fixA.getFilterData().categoryBits == Boot.ITEM_BIT)
                    ((Item) fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item) fixB.getUserData()).use((Mario) fixA.getUserData());
                break;
            case Boot.WEAPON_BIT | Boot.OBJECT_BIT: //Fireball hits an object (eg. pipe).
                if (fixA.getFilterData().categoryBits == Boot.WEAPON_BIT)
                    ((Fireball) fixA.getUserData()).setDestroy();
                else
                    ((Fireball) fixB.getUserData()).setDestroy();
                break;
            case Boot.WEAPON_BIT | Boot.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == Boot.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).die();
                else
                    ((Enemy) fixB.getUserData()).die();
                break;
            case Boot.MARIO_FEET_BIT | Boot.GROUND_BIT: //Check if Mario is on the ground, so he can jump.
            case Boot.MARIO_FEET_BIT | Boot.BRICK_BIT:
            case Boot.MARIO_FEET_BIT | Boot.COINBLOCK_BIT:
            case Boot.MARIO_FEET_BIT | Boot.OBJECT_BIT:
                feetOnGround++;
                break;
            case Boot.MARIO_BIT | Boot.ENEMY_BIT: //Mario dies.
                if (fixA.getFilterData().categoryBits == Boot.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit((Enemy) fixB.getUserData());
                else
                    ((Mario) fixB.getUserData()).hit((Enemy) fixA.getUserData());
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int beginDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (beginDef) {
            case Boot.MARIO_FEET_BIT | Boot.GROUND_BIT: //Check if Mario is not on the ground, so he can't jump.
            case Boot.MARIO_FEET_BIT | Boot.BRICK_BIT:
            case Boot.MARIO_FEET_BIT | Boot.COINBLOCK_BIT:
            case Boot.MARIO_FEET_BIT | Boot.OBJECT_BIT:
                feetOnGround--;
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    //Check if Mario is on the ground with one feet at least. Useful when walking over two tiles.
    public boolean jumpability() {
        return feetOnGround > 0;
    }

}