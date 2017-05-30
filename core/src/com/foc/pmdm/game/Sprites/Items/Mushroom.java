package com.foc.pmdm.game.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Screens.GameScreen;
import com.foc.pmdm.game.Sprites.Mario;

/**
 * Created by entreri on 30/04/17.
 */

public class Mushroom extends Item {

    /**
     * Constructor encargado de inicializar los atributos, fijar la posicion, velocidad y fisica.
     * @param screen referencia a la pantalla donde mostrar la seta.
     * @param x valor del eje X para la posicion.
     * @param y valor del eje Y para la posicion.
     */
    public Mushroom(GameScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"),0,0,16,16);//Seccion seta, X e Y 0,0 y medida 16x16 pix.
        setBounds(getX(),getY(), 16/LibGDXGame.PPM,16/LibGDXGame.PPM);
        vel = new Vector2(0.7f,0);
    }

    /**
     * Método encargado de gestionar la física y velocidad de la seta a mostrar, los objetos o enemigos con
     * los que puede colisionar.
     */
    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(),getY());
        //Asignamos posicion y tipo de objeto dinamico.
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);
        //Creamos el objeto.
        //Capa con forma que envuelve al objeto para las colisiones etc.
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / LibGDXGame.PPM);
        //Asignamos categoria...
        fixtureDef.filter.categoryBits = LibGDXGame.ITEM_BIT;
        //Puede colisionar con...
        fixtureDef.filter.maskBits = LibGDXGame.MARIO_BIT | LibGDXGame.OBJECT_BIT | LibGDXGame.GROUND_BIT|
                                     LibGDXGame.COIN_BIT | LibGDXGame.BRICK_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);
    }

    /**
     * Metodo encargado de asignar al sprite que se beneficiara de obtener el objeto (Mario).
     * @param mario instancia del personaje controlado por el jugador.
     */
    @Override
    public void use(Mario mario) {
        destroy();
        mario.grow();//Cuando usamos la seta... lo hacemos crecer.
    }

    /**
     * Método encargado en cada iteración de mostrar o destruir el objeto en pantalla.
     * @param deltaTime tiempo de actualizacion.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
        //Centramos el sprite con el cuerpo b2d.
        vel.y = body.getLinearVelocity().y;//Mantenemos la velocidad...
        body.setLinearVelocity(vel);//Asignamos la velocidad...
    }
}
