package com.foc.pmdm.game.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Screens.GameScreen;
import com.foc.pmdm.game.Sprites.Mario;

/**
 * Created by entreri on 28/04/17.
 */

public class Goomba extends Enemy {

    //Variables necesarias para el enemigo a crear.
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    private Sound goombaHit;

    /**
     * Constructor encargado de llamar al constructor padre (Enemy) y de fijar los valores por defecto de
     * los enemigos a mostrar.
     * @param screen pantalla donde se mostrara el enemigo.
     * @param x valor del eje X.
     * @param y valor del eje Y.
     */
    public Goomba(GameScreen screen, float x, float y) {
        super(screen, x, y);
        goombaHit = screen.getManager().get(screen.getMARIO_STOMP());
        frames = new Array<TextureRegion>();
        for (int i= 0;i<2; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"),i*16,0,16,16));
        }
        walkAnimation = new Animation(0.4f,frames);
        stateTime = 0;
        setBounds(getX(),getY(), 16/LibGDXGame.PPM,16/LibGDXGame.PPM);
        setToDestroy = false;
        destroyed = false;

    }

    /**
     * Metodo encargado en cada iteracion de gestionar los datos del enemigo y si ha de ser destruido o no.
     * @param deltaTime valor de tiempo de actualizacion.
     */
    public void update ( float deltaTime){
        stateTime += deltaTime;
        if (setToDestroy && !destroyed){//Si hay que destruir el objeto por una colision y no esta hecho.
            world.destroyBody(b2body);//Destruimos a Goomba.
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"),32,0,16,16));
            //En la seccion goomba es el tercer sprite, 32 pix (16 por sprite)
            // del eje X, eje Y 0 por la altura del sprite, y medida 16x16 pix.
            stateTime = 0;//Reiniciamos el contador para desdibujar en draw() la textura.
        } else if (!destroyed){//Si no ha sido destruido y no debe de ser destruido...
            b2body.setLinearVelocity(vel);//Asignamos la velocidad heredada de Enemy.
            setPosition(b2body.getPosition().x - getWidth() /2, b2body.getPosition().y - getHeight() /2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime,true));
            //Mostramos en pantalla Goomba con su animacion.
        }

    }

    /**
     * Metodo encargado de gestionar la fisica y velocidad del Goomba a mostrar, los objetos o enemigos con
     * los que puede colisionar.
     */
    @Override
    protected void defineEnemy() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(),getY());//Temporal la posicion.
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        //Capa con forma que envuelve al goomba para las colisiones etc.
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / LibGDXGame.PPM);
        fixtureDef.filter.categoryBits = LibGDXGame.ENEMY_BIT;//Asignamos la categoria de goomba.
        //Asignamos con que puede colisionar.
        fixtureDef.filter.maskBits = LibGDXGame.GROUND_BIT | LibGDXGame.COIN_BIT |
                                     LibGDXGame.BRICK_BIT | LibGDXGame.ENEMY_BIT | LibGDXGame.OBJECT_BIT
                                     |LibGDXGame.MARIO_BIT;

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        //Creamos la cabeza para que Mario impacte con ella al saltar.
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];//4 vertices nada mas.
        vertices [0] = new Vector2(-5,8).scl(1/LibGDXGame.PPM);//eje x-5 parte trasera(izquierda) del Sprite 8 eje Y altura escalado.
        vertices [1] = new Vector2(5,8).scl(1/LibGDXGame.PPM);//eje x5 parte delantera (derecha) del Sprite 8 eje Y altura escalado.
        vertices [2] = new Vector2(-3,3).scl(1/LibGDXGame.PPM);//Segunda parte de la cabeza igual que arriba.
        vertices [3] = new Vector2(3,3).scl(1/LibGDXGame.PPM);
        head.set(vertices);//Asignamos los vertices
        fixtureDef.shape = head;
        fixtureDef.restitution = 0.5f;//Cuando Mario golpee subira esos pixeles de rebote al pisar la cabeza de Goomba.
        fixtureDef.filter.categoryBits = LibGDXGame.ENEMY_HEAD_HIT;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    /**
     * Metodo encargado de dibujar o no al Goomba en pantalla llamando al metodo draw padre.
     * @param batch textura a dibujar.
     */
    public void draw (Batch batch){
        if (!destroyed || stateTime < 1){//Solo se dibuja el Goomba si no ha sido destruido o stateTime es menor a 1.
            super.draw(batch);
        }
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle)enemy).currentState == Turtle.State.MOVING_SHELL){
            setToDestroy = true;//Si la tortuga esta en modo caparazon despues de recibir una patada de mario, matamos al enemigo.
        } else {
            reverseVel(true,false);//sino cambiamos la direccion del goomba
        }
    }

    /**
     * Metodo llamado desde WordlContactListener y world.Step de UserInterface (UI).
     */
    @Override
    public void hitOnHead(Mario mario) {
        setToDestroy = true;
        goombaHit.play();
    }
}
