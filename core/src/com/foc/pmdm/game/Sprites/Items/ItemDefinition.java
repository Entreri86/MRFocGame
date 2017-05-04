package com.foc.pmdm.game.Sprites.Items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by entreri on 30/04/17.
 */

public class ItemDefinition {
    public Vector2 position;
    public Class<?> type;
    //Variables necesarias para el uso del objeto.

    /**
     * Constructor encargado de inicializar los atributos.
     * @param position posicion del objeto.
     * @param type tipo del objeto.
     */
    public ItemDefinition (Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;
    }
}
