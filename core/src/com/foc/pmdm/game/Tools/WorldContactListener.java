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

        if (fixtureA.getUserData() == "head" || fixtureB.getUserData() == "head"){//Insertar en Switch!!
            //Asi conocemos que la colision del Sprite de Mario es con la cabeza.
            Fixture head = fixtureA.getUserData() == "head" ? fixtureA : fixtureB;
            //Obtenemos la cabeza testando cual de los objetos es la cabeza.
            Fixture objeto = head == fixtureA ? fixtureB : fixtureA;
            //Asignamos el otro objeto en colision (no es la cabeza) como el objeto en colision en si.

            if (objeto.getUserData() instanceof InteractiveTileObject){
                ((InteractiveTileObject)objeto.getUserData()).onHeadHit();
                //Sabemos que hay una colision entre objetos con la cabeza del Sprite y ejecutamos el metodo.
            }

        }//Segun la colision dada...
        switch (colisionDefi){
            case LibGDXGame.ENEMY_HEAD_HIT | LibGDXGame.MARIO_BIT://Si mario colisiona con la cabeza de un Enemigo...
                if (fixtureA.getFilterData().categoryBits == LibGDXGame.ENEMY_HEAD_HIT){
                    ((Enemy) fixtureA.getUserData()).hitOnHead();
                } else {
                    ((Enemy)fixtureB.getUserData()).hitOnHead();
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
                Gdx.app.log("Mario","Fiambre!");
                break;

            case LibGDXGame.ENEMY_BIT | LibGDXGame.ENEMY_BIT://Si dos enemigos chocan...cambiamos la direccion de mov.
                ((Enemy) fixtureA.getUserData()).reverseVel(true,false);//Cambiamos de direccion eje X.
                ((Enemy) fixtureB.getUserData()).reverseVel(true,false);//Cambiamos de direccion eje X.
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
