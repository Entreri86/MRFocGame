package com.foc.pmdm.game.Sprites.TileObjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Screens.GameScreen;

/**
 * Created by entreri on 25/04/17.
 */

public abstract class InteractiveTileObject {

    //Variables para el mundo y mapa.
    protected World world;
    protected TiledMap tiledMap;
    protected MapObject object;
    //Variables para las formas y texturas.
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    //Variables que referencian al juego y pantalla.
    protected LibGDXGame game;
    protected GameScreen screen;
    /**
     * Constructor encargado de inicializar los atributos, fijar las posiciones de los objetos a crear y la
     * fisica de los mismos.
     * @param screen referencia a la pantalla donde mostrar los objetos.
     * @param object forma con la que se cubrira el objeto (para la fisica).
     */
    public InteractiveTileObject (GameScreen screen, MapObject object){
        this.screen = screen;
        this.world = screen.getWorld();
        this.tiledMap = screen.getTiledMap();
        this.object = object;
        this.bounds = ((RectangleMapObject)object).getRectangle();
        game = new LibGDXGame();
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        bodyDef.type = BodyDef.BodyType.StaticBody;//Cuerpo estatico para este tipo de objeto
        /**
         * Fijamos las posiciones del eje X e eje Y extrayendo del rectangulo la posicion y ajustandola al centro,
         * escalandola por Pixeles Por Metro.
         */
        bodyDef.position.set((bounds.getX() + bounds.getWidth()/2)/ LibGDXGame.PPM, (bounds.getY() + bounds.getHeight()/2)/ LibGDXGame.PPM);
        //Iniciamos el body (monedas en este caso) con la configuracion del BodyDef.
        body = world.createBody(bodyDef);
        //Configuramos la forma en modo cuadrado ajustandolo al centro y escalando la imagen.
        shape.setAsBox((bounds.getWidth()/2)/ LibGDXGame.PPM,(bounds.getHeight()/2)/ LibGDXGame.PPM);
        //Asignamos al objeto la forma.
        fixtureDef.shape = shape;
        //Finalmente creamos la forma.
        fixture = body.createFixture(fixtureDef);

    }

    /**
     * Metodo encargado de gestionar los golpes de Mario con la cabeza en los objetos.
     */
    public abstract void onHeadHit ();



    /**
     * Metodo encargado de asignar el filtro a usar por el objeto (categoria de bits para colisiones etc).
     * @param filterBit valor del filtro a usar.
     */
    public void setCategoryFilter (short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    /**
     * Metodo encargado de devolver una celda del mapa level1.tmx.
     * @return celda del mapa.
     */
    public TiledMapTileLayer.Cell getCell (){
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(1);
        //Recogemos el Layout del mapa (level1.tmx) en la posicion 1 que es donde se ubica el Layout grafico.
        return layer.getCell((int) (body.getPosition().x * LibGDXGame.PPM/16),(int)(body.getPosition().y * LibGDXGame.PPM/16));
        /**
         * Para recoger los valores X e Y hay que recogerlos del cuerpo y escalarlos en positivo porque previamente
         * los hemos escalado haciendolos mas pequeños y lo dividimos por la medida del objeto (16 pix).
         */
    }
}
