package com.colt.supermario.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.colt.supermario.Boot;
import com.colt.supermario.sprites.Mario;
import com.colt.supermario.sprites.enemies.Enemy;
import com.colt.supermario.sprites.InteractiveTileObject;
import com.colt.supermario.sprites.items.Item;

/**
 * Created by colt on 4/13/16.
 */

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case Boot.MARIO_HEAD_BIT | Boot.BRICK_BIT: //Mario smash bricks with his head! \m/
            case Boot.MARIO_HEAD_BIT | Boot.COIN_BIT:
                if (fixA.getFilterData().categoryBits == Boot.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case Boot.MARIO_BIT | Boot.ENEMY_HEAD_BIT: //If those two collide. Mario jumping on enemy's head.
                if (fixA.getFilterData().categoryBits == Boot.ENEMY_HEAD_BIT)
                    ((Enemy) fixA.getUserData()).hitOnHead();
                else
                    ((Enemy) fixB.getUserData()).hitOnHead();
                break;
            case Boot.ENEMY_BIT | Boot.OBJECT_BIT: //Enemy will reverse their movement when they hit an object.
                if (fixA.getFilterData().categoryBits == Boot.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Boot.ENEMY_BIT | Boot.ENEMY_BIT: //Enemy will reverse their movement when they hit each other.
                ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
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
            case Boot.MARIO_BIT | Boot.ENEMY_BIT: //Mario dies.
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}