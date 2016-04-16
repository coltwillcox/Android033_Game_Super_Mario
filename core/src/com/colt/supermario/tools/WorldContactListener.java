package com.colt.supermario.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.colt.supermario.Boot;
import com.colt.supermario.sprites.enemies.Enemy;
import com.colt.supermario.sprites.InteractiveTileObject;

/**
 * Created by colt on 4/13/16.
 */

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;
            if (object.getUserData() instanceof InteractiveTileObject) {
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }

        switch (cDef) {
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