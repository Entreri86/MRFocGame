package com.foc.pmdm.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Screens.GameScreen;
import com.foc.pmdm.game.Sprites.TileObjects.Brick;
import com.foc.pmdm.game.Sprites.TileObjects.Coin;
import com.foc.pmdm.game.Sprites.Enemies.Goomba;

/**
 * Created by entreri on 25/04/17.
 */

public class B2dWorldCreator {
    //Array de enemigos.
    private Array<Goomba> goombas;

    /**
     * Constructor encargado de extraer del mapa level1.tmx los objetos, darles forma para la reaccion
     * fisica con el Sprite de Mario.
     */
    public B2dWorldCreator (GameScreen screen){
        World world = screen.getWorld();//TODO: Extraer ASSETMANAGER DE Screen y pasar objeto Music individualmente Tglobal.
        TiledMap tiledMap = screen.getTiledMap();
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;
        /**
         * Necesitamos un Body y un Fixture por cada tipo de objeto creado en el mapa de Tiled
         * (bloques (posicion 5), tuberias (posicion 3), monedas (posicion 4), suelo (pos 2 )
         * Goombas posicion 6 de la lista) )
         */
        //Suelo del mapa.
        for (MapObject object : tiledMap.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;//Cuerpo estatico para este tipo de objeto
            /**
             * Fijamos las posiciones del eje X e eje Y extrayendo del rectangulo la posicion y ajustandola al centro,
             * escalandola por Pixeles Por Metro.
             */
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth()/2) / LibGDXGame.PPM, (rectangle.getY() + rectangle.getHeight()/2) / LibGDXGame.PPM);
            //Iniciamos el body (suelo en este caso) con la configuracion del BodyDef.
            body = world.createBody(bodyDef);
            //Configuramos la forma en modo cuadrado ajustandolo al centro y escalando la imagen.
            shape.setAsBox((rectangle.getWidth()/2)/ LibGDXGame.PPM,(rectangle.getHeight()/2)/ LibGDXGame.PPM);
            //Asignamos al objeto la forma.
            fixtureDef.shape = shape;
            //Finalmente creamos la forma.
            body.createFixture(fixtureDef);
        }
        //Tuberias del mapa
        for (MapObject object : tiledMap.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){

            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;//Cuerpo estatico para este tipo de objeto
            /**
             * Fijamos las posiciones del eje X e eje Y extrayendo del rectangulo la posicion y ajustandola al centro,
             * escalandola por Pixeles Por Metro.
             */
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth()/2)/ LibGDXGame.PPM, (rectangle.getY() + rectangle.getHeight()/2)/ LibGDXGame.PPM);
            //Iniciamos el body (tuberia en este caso) con la configuracion del BodyDef.
            body = world.createBody(bodyDef);
            //Configuramos la forma en modo cuadrado ajustandolo al centro y escalando la imagen.
            shape.setAsBox((rectangle.getWidth()/2)/ LibGDXGame.PPM,(rectangle.getHeight()/2)/ LibGDXGame.PPM);
            //Asignamos al objeto la forma.
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = LibGDXGame.OBJECT_BIT;//Asignamos tipo de colision.
            //Finalmente creamos la forma.
            body.createFixture(fixtureDef);
        }
        //Bloques rompibles
        for (MapObject object : tiledMap.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){


            new Brick(screen,object);
        }
        //Monedas
        for (MapObject object : tiledMap.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){

            new Coin(screen,object);
        }
        //Goombas
        goombas = new Array<Goomba>();
        for (MapObject object : tiledMap.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rectangle.getX()/ LibGDXGame.PPM, rectangle.getY()/ LibGDXGame.PPM));
        }
    }

    /**
     * Metodo get que retorna el array de enemigos Goomba.
     * @return array de enemigos.
     */
    public Array<Goomba> getGoombas() {
        return goombas;
    }
}
