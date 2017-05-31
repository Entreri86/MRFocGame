package com.foc.pmdm.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Sprites.Enemies.Enemy;
import com.foc.pmdm.game.Sprites.Items.Item;
import com.foc.pmdm.game.Sprites.Mario;
import com.foc.pmdm.game.Sprites.TileObjects.InteractiveTileObject;

/**
 * Created by entreri on 28/04/17.
 */

public class WorldContactListener implements ContactListener {


    /**
     * Metodo encargado de realizar la conexion entre las colisiones de los objetos.
     * @param contact contacto entre dos objetos.
     */
    @Override
    public void beginContact(Contact contact) {
        Gdx.app.log("Inicio de contacto","");
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int colisionDefi = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
        //Recogemos el valor de la colision cuando se estrellan dos objetos.
        //Segun la colision dada...
        switch (colisionDefi){
            case LibGDXGame.MARIO_HEAD_BIT | LibGDXGame.BRICK_BIT:
            case LibGDXGame.MARIO_HEAD_BIT | LibGDXGame.COIN_BIT:
                if (fixtureA.getFilterData().categoryBits == LibGDXGame.MARIO_HEAD_BIT){
                    ((InteractiveTileObject)fixtureB.getUserData()).onHeadHit((Mario)fixtureA.getUserData());
                }else {
                    ((InteractiveTileObject)fixtureA.getUserData()).onHeadHit((Mario)fixtureB.getUserData());
                }
                break;
            case LibGDXGame.ENEMY_HEAD_HIT | LibGDXGame.MARIO_BIT://Si mario colisiona con la cabeza de un Enemigo...
                if (fixtureA.getFilterData().categoryBits == LibGDXGame.ENEMY_HEAD_HIT){
                    ((Enemy) fixtureA.getUserData()).hitOnHead((Mario)fixtureB.getUserData());
                } else {
                    ((Enemy)fixtureB.getUserData()).hitOnHead((Mario)fixtureA.getUserData());
                }
                break;
            case LibGDXGame.ENEMY_BIT | LibGDXGame.OBJECT_BIT://Si el enemigo colisiona con un objeto...
                if (fixtureA.getFilterData().categoryBits == LibGDXGame.ENEMY_BIT){
                    ((Enemy) fixtureA.getUserData()).reverseVel(true,false);//Cambiamos de direccion eje X.
                } else {
                    ((Enemy)fixtureB.getUserData()).reverseVel(true,false);
                }
                break;
            case LibGDXGame.MARIO_BIT | LibGDXGame.ENEMY_BIT://Si mario toca a un enemigo (y no en la cabeza) muere.
                if (fixtureA.getFilterData().categoryBits == LibGDXGame.MARIO_BIT){
                    ((Mario)fixtureA.getUserData()).hit((Enemy) fixtureB.getUserData());
                }else {
                    ((Mario)fixtureB.getUserData()).hit((Enemy) fixtureA.getUserData());
                }
                break;
            case LibGDXGame.ENEMY_BIT | LibGDXGame.ENEMY_BIT://Si dos enemigos chocan...cambiamos la direccion de mov.
                ((Enemy) fixtureA.getUserData()).onEnemyHit((Enemy) fixtureB.getUserData());//Cambiamos de direccion eje X.
                ((Enemy) fixtureB.getUserData()).onEnemyHit((Enemy) fixtureA.getUserData());//Cambiamos de direccion eje X.
                break;
            case LibGDXGame.ITEM_BIT | LibGDXGame.OBJECT_BIT://Si la seta colisiona con un objeto...
                if (fixtureA.getFilterData().categoryBits == LibGDXGame.ITEM_BIT){
                    ((Item) fixtureA.getUserData()).reverseVel(true,false);//Cambiamos de direccion eje X.
                } else {
                    ((Item)fixtureB.getUserData()).reverseVel(true,false);
                }
                break;
            case LibGDXGame.ITEM_BIT | LibGDXGame.MARIO_BIT://Si la seta colisiona con mario...
                if (fixtureA.getFilterData().categoryBits == LibGDXGame.ITEM_BIT){//Si fixtureA es la seta...
                    //usamos la seta con mario
                    ((Item) fixtureA.getUserData()).use((Mario)fixtureB.getUserData());
                } else {
                    //Si fixtureB es la seta... usamos en Mario FixA la seta.
                    ((Item)fixtureB.getUserData()).use((Mario)fixtureA.getUserData());
                }
                break;

        }
    }
    /**
     * Metodo encargado de mostrar en consola el final del contacto.
     * @param contact contacto finalizado.
     */
    @Override
    public void endContact(Contact contact) {
        Gdx.app.log("Fin de contacto","");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
