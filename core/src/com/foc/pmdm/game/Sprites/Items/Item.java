package com.foc.pmdm.game.Sprites.Items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Screens.GameScreen;
import com.foc.pmdm.game.Sprites.Mario;

/**
 * Created by entreri on 30/04/17.
 */

public abstract class Item extends Sprite {
    //Variables comunes para todos los objetos derivados de Item.
    protected GameScreen screen;
    protected World world;
    protected Vector2 vel;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body body;

    /**
     * Constructor de la clase encargado de inicializar los atributos, fijar la fisica, forma y posicion
     * de los objetos a mostrar.
     * @param screen referencia a la pantalla donde mostrar los objetos.
     * @param x valor del eje X.
     * @param y valor del eje Y.
     */
    public Item(GameScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 16 / LibGDXGame.PPM, 16 / LibGDXGame.PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;

    }

    /**
     * Método encargado de gestionar la física, velocidad y forma del objeto a mostrar.
     */
    public abstract void defineItem();

    /**
     * Metodo encargado de asignar al sprite que se beneficiara de obtener el objeto (Mario).
     * @param mario instancia del personaje controlado por el jugador.
     */
    public abstract void use(Mario mario);

    /**
     * Metodo encargado en cada iteracion de mostrar o destruir el objeto en pantalla.
     * @param deltaTime tiempo de actualizacion.
     */
    public void update (float deltaTime){
        if (toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
        }
    }

    /**
     * Metodo encargado de asignar el estado del objeto a destruir.
     */
    public void destroy (){
        toDestroy = true;
    }

    /**
     * Metodo encargado de dibujar el objeto en pantalla en caso de no estar destruido mediante la
     * llamada al metodo padre.
     * @param batch textura del objeto a visualizar.
     */
    public void draw (Batch batch){
        if (!destroyed){
            super.draw(batch);
        }

    }

    /**
     * Método encargado de cambiar de dirección (velocidad positiva/negativa) a los enemigos.
     * @param x true si eje x es el que debe de cambiar de direccion.
     * @param y true si eje y es el que debe de cambiar de direccion.
     */
    public void reverseVel (boolean x, boolean y){
        if (x){
            vel.x = -vel.x;
        } else if (y){
            vel.y = -vel.y;
        }
    }

}


