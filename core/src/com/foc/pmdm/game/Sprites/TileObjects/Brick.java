package com.foc.pmdm.game.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Scenes.UI;
import com.foc.pmdm.game.Screens.GameScreen;
import com.foc.pmdm.game.Sprites.Mario;

/**
 * Created by entreri on 25/04/17.
 */

public class Brick extends InteractiveTileObject {
    //Asset para el sonido del objeto, temporal en la tarea global pasar por referencias.
    private AssetManager assetManager;
    private Sound marioBreak;
    private Sound marioBump;
    /**
     * Constructor encargado de inicializar los atributos y preparar la textura del objeto a mostrar
     * mediante llamadas a otros metodos.
     * @param screen referencia a la pantalla donde debe de ser mostrado el objeto.
     * @param object forma con la que se cubrira el objeto (para la fisica).
     */
    public Brick(GameScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);//Asignamos datos personalizados para el objeto.
        assetManager = screen.getManager();
        marioBreak = assetManager.get(screen.getMARIO_BREAK(),Sound.class);
        marioBump = assetManager.get(screen.getMARIO_BUMP(),Sound.class);
        //prepareManager ();//Musica
        setCategoryFilter(LibGDXGame.BRICK_BIT);//Asignamos categoria de bits.
    }

    /**
     * Metodo encargado de llevar la fisica del bloque en cuanto colisiona mario con el mediante su cabeza,
     * se marca y se remueve.
     */
    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()){
            Gdx.app.log("Bloque","Colision");
            setCategoryFilter(LibGDXGame.DESTROY_BIT);//Marcamos el filtro para que no se pueda volver a colisionar con el objeto
            getCell().setTile(null); //Borramos el objeto colisionado.
            UI.addScore(100);//Subimos puntuacion.
            marioBreak.play();//Cuando golpee el bloque reproducimos sonido.
        } else{
            marioBump.play();
        }

    }

    /**
     * Metodo encargado de iniciar el AssetManager para reproducir el sonido, temporal en la tarea global se
     * pasara por referencias.
     */
    public void prepareManager (){
        assetManager = new AssetManager();
        //assetManager.load(game.getMARIO_BREAK(),Sound.class);//Cargamos sonido...
        assetManager.finishLoading();//finalizamos carga...
    }
}
