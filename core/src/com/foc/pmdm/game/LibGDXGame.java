package com.foc.pmdm.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.foc.pmdm.game.Screens.GameScreen;
import com.foc.pmdm.game.Screens.SplashScreen;

/**
 * Created by entreri on 16/04/17.
 */
public class LibGDXGame extends Game {
	public SpriteBatch batch;//Textura del juego.
	public static final int V_WIDHT = 400;
    public static final int V_HEIGHT = 208;
	public static final float PPM = 100; //Pixels por metro.
	//Variables para las colisiones de Box2d.
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROY_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_HIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	/**
	 * Metodo encargado de crear la pantalla del juego y empezar a dibujar.
	 */
	@Override
	public void create () {
		batch = new SpriteBatch();

		setScreen(new SplashScreen(this));//Le pasamos el mismo juego en si de pantalla.
	}

	/**
	 * Metodo encargado de renderizar el juego llamando al constructor padre y actualizar la musica del juego.
	 */
	@Override
	public void render () {
		super.render();//Delegamos el renderizado al constructor padre de juego (Game).

	}

	/**
	 * Metodo encargado de liberar recursos cuando ya no son necesarios.
	 */
	@Override
	public void dispose () {
		super.dispose();
        batch.dispose();
	}




}
