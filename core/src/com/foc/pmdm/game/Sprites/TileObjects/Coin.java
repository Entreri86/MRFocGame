package com.foc.pmdm.game.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Scenes.UI;
import com.foc.pmdm.game.Screens.GameScreen;
import com.foc.pmdm.game.Sprites.Items.ItemDefinition;
import com.foc.pmdm.game.Sprites.Items.Mushroom;

/**
 * Created by entreri on 25/04/17.
 */

public class Coin extends InteractiveTileObject {

    private AssetManager assetManager;
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN_ID = 28;
    //Posicion con ID en tileset_gutter num 27, aqui empieza la cuenta desde 1 y no desde 0 por eso se suma uno mas.

    /**
     * Constructor de la clase encargado de inicializar los atributos, fisica, preparar la textura del objeto
     * a mostrar.
     * @param rBounds forma con la que se cubrira el objeto (para la fisica).
     */
    public Coin(GameScreen screen, Rectangle rBounds) {
        super(screen, rBounds);
        tileSet = tiledMap.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);//Asignamos datos personalizados para el objeto.
        prepareManager ();//Musica
        setCategoryFilter(LibGDXGame.COIN_BIT);//Asignamos categoria de bits.
    }

    /**
     * Metodo encargado de llevar la fisica del bloque en cuanto colisiona mario con el mediante su cabeza,
     * se marca y se bloquea su nuevo uso.
     */
    @Override
    public void onHeadHit() {
        Gdx.app.log("Moneda","Colision");
        if (getCell().getTile().getId() == BLANK_COIN_ID){
            assetManager.get(game.getMARIO_BUMP(),Sound.class).play();//Cuando golpee el bloque reproducimos sonido.
        } else {
            assetManager.get(game.getMARIO_COIN(),Sound.class).play();//Cuando golpee el bloque con monedas reproducimos sonido.
            screen.spawnItem(new ItemDefinition(new Vector2(body.getPosition().x,
                    body.getPosition().y + 16 /LibGDXGame.PPM), Mushroom.class));
            //Cuando golpee suelta la seta 16 pixeles escalados en el eje Y mas arriba del bloque.
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN_ID));//Asignamos bloque vacio.
        UI.addScore(200);//Añadimos puntuacion.
    }

    /**
     * Método encargado de iniciar el AssetManager para reproducir el sonido, temporal en la tarea global se
     * pasara por referencias.
     */
    public void prepareManager (){
        assetManager = new AssetManager();
        assetManager.load(game.getMARIO_BUMP(),Sound.class);
        assetManager.load(game.getMARIO_COIN(),Sound.class);
        assetManager.finishLoading();
    }
}
