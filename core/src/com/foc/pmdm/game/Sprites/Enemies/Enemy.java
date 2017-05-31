package com.foc.pmdm.game.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.foc.pmdm.game.Screens.GameScreen;
import com.foc.pmdm.game.Sprites.Mario;

/**
 * Created by entreri on 28/04/17.
 */

public abstract class Enemy extends Sprite {

    //Variables necesarias para los enemigos.
    protected World world;
    protected GameScreen screen;
    public Body b2body;
    protected Vector2 vel;

    /**
     * Constructor encargado de inicializar los datos y fijar la posicion, forma y velocidad de los enemigos.
     * @param screen Pantalla donde debe de ser mostrado.
     * @param x valor del eje X.
     * @param y valor del eje Y.
     */
    public Enemy (GameScreen screen,float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x,y);//Fijamos posicion.
        defineEnemy();//Definimos al enemigo.
        vel = new Vector2(1,0);//Velocidad de los enemigos.
        b2body.setActive(false);//Ponemos enemigos inactivos (para que no se caigan por el precipicio antes de verlos.
    }

    /**
     * Metodo encargado de gestionar la fisica y velocidad del enemigo a mostrar.
     */
    protected abstract void defineEnemy ();

    /**
     * Metodo encargado de  gestionar los golpes en la cabeza (muerte) del enemigo.
     */
    public abstract void hitOnHead(Mario mario);

    /**
     * Metodo encargado en cada iteracion de actualizar los datos del enemigo.
     * @param deltaTime valor de tiempo de actualizacion.
     */
    public abstract void update (float deltaTime);

    /**
     * Método encargado de cambiar de dirección (velocidad positiva/negativa) a los enemigos.
     * @param x eje X
     * @param y eje Y
     */
    public void reverseVel (boolean x, boolean y){
        if (x){
            vel.x = -vel.x;
        } else if (y){
            vel.y = -vel.y;
        }
    }

    public abstract void onEnemyHit(Enemy enemy);
}
