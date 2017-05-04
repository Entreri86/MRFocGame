package com.foc.pmdm.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Screens.GameScreen;

/**
 * Created by entreri on 25/04/17.
 */

public class Mario extends Sprite {
    //Variables relacionadas con los estados de Mario.
    public enum  States {FALL, JUMP, STAND, RUN};
    public States currentState;
    public States previousState;
    //Variables relacionadas con el mundo y el cuerpo de mario.
    public World world;
    public Body b2body;
    //Variables para la Textura para el Mario y sus animaciones.
    private TextureRegion marioQuiet;
    private com.badlogic.gdx.graphics.g2d.Animation marioRun;
    private com.badlogic.gdx.graphics.g2d.Animation marioJump;
    //Variable relacionada con la direccion de Mario y el tiempo.
    private boolean runRight;
    private float stateTimer;
    /*
     * Constructor encargado de inicializar los atributos y gestionar la fisica, forma, comportamiento
     * del Sprite de Mario en el juego mediante llamadas a otros metodos.
     */
    public Mario (GameScreen gameScreen){
        super(gameScreen.getAtlas().findRegion("little_mario"));//Recogemos la textura del mario pequeño
        this.world = gameScreen.getWorld();
        /**
         * Iniciamos variables relacionadas con el estado de Mario mediante el Enum States.
         */
        currentState = States.STAND;
        previousState = States.STAND;
        stateTimer = 0;
        runRight = true;
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i<4;i++){
            /**
             * Recogemos el primer Sprite de la carrera de Mario que empieza en la segunda posicion (16 pix eje X)
             * en el archivo little_mario. Por eso la variable i se inicia a 1 para obviar el mario quieto.
             * En el eje Y posicion 0 y la altura de los pix del sprite es 16 de ancho por 16 de alto.
             */
            frames.add(new TextureRegion(getTexture(),i*16,11,16,16));
        }
        marioRun = new com.badlogic.gdx.graphics.g2d.Animation(0.1f,frames);//Asignamos la animacion...
        frames.clear();//Limpiamos el array para reutilizarlo y consumir menos recursos.
        for (int i = 4; i<6;i++){
            /**
             * Recogemos el primer Sprite de la carrera de Mario que empieza en la quinta posicion (16 pix eje X)
             * en el archivo little_mario. Por eso la variable i se inicia a 4 para obviar el mario corriendo y quieto.
             * En el eje Y posicion 0 y la altura de los pix del sprite es 16 de ancho por 16 de alto.
             */
            frames.add(new TextureRegion(getTexture(),i*16,11,16,16));
        }
        marioJump = new com.badlogic.gdx.graphics.g2d.Animation(0.1f,frames);//Asignamos la animacion...
        defMario();
        /**
         * La textura esta en las coordenadas del pack de texturas 0,0 (la primera mario parado) en 16x16 pix.
         * Escalamos la forma y asignamos la region al sprite del Mario.
         */
        marioQuiet = new TextureRegion(getTexture(),1,11,16,16);
        setBounds(0,0,16/LibGDXGame.PPM,16/LibGDXGame.PPM);//Asignamos los bordes con el escalado.
        setRegion(marioQuiet);
    }

    /**
     * Metodo encargado de en cada iteracion actualizar los datos relacionados con el Sprite de Mario.
     * @param deltaTime valor del tiempo.
     */
    public void update (float deltaTime){
        setPosition(b2body.getPosition().x - getWidth() /2, b2body.getPosition().y - getHeight() /2);
        setRegion(getFrame(deltaTime));
    }

    /**
     * Metodo encargado de retornar la textura deseada segun las necesidades y momento del juego.
     * @param deltaTime  valor para realizar calculos con el tiempo.
     * @return region deseada.
     */
    public TextureRegion getFrame (float deltaTime){
        currentState = getState();
        TextureRegion region ;
        //Segun el estado de mario...
        switch (currentState){
            case JUMP:
                region = (TextureRegion) marioJump.getKeyFrame(stateTimer);//Recogemos textura de la animacion...
                break;
            case RUN:
                region = (TextureRegion) marioRun.getKeyFrame(stateTimer,true);
                //Segundo parametro a true para el bucle de la animacion al correr.
                break;
            case FALL://Mas adelante en tarea global se utilizaran los demas estados...
            case STAND:
            default:
                region = marioQuiet;
                break;
        }
        //Comprobaciones del Sprite de mario parado.
        if ((b2body.getLinearVelocity().x <0 || !runRight)&&(!region.isFlipX())){
            //Si mario no esta corriendo hacia la derecha  y mario esta mirando hacia la izquierda
            region.flip(true,false);//Giramos el sprite en el eje X (primer parametro).
            runRight = false;
        } else if ((b2body.getLinearVelocity().x>0 || runRight)&&(region.isFlipX())){
            //Si mario esta corriendo hacia la derecha y esta mirando a la izquierda (en un salto girando el sprite por ejemplo)
            region.flip(true,false);//Giramos el sprite hacia la direccion adecuada.
            runRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;
    }

    /**
     * Metodo encargado de gestionar y retornar el estado del Sprite de Mario actual en el juego.
     * @return estado actual del sprite de Mario.
     */
    public States getState (){
        if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y <0 && previousState == States.JUMP))
            return States.JUMP;//Si el eje Y es mayor a 0 debe de estar saltando,o si el eje Y es menor a 0 y el estado era salto debe de estar cayendo. .
        else if (b2body.getLinearVelocity().y < 0 )
            return States.FALL;//Si el eje y decae debe de estar cayendo.
        else if (b2body.getLinearVelocity().x !=0)
            return States.RUN;//Si el eje x no es 0 esta corriendo.
        else
            return States.STAND;
    }
    /**
     * Metodo encargado de la definicion de la fisica, colisiones e interaciones de mario con el mundo del
     * juego.
     */
    public void defMario (){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / LibGDXGame.PPM,32 / LibGDXGame.PPM);//Temporal la posicion.
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);//Creamos el cuerpo de mario...
        //Capa con forma que envuelve al mario para las colisiones etc.
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / LibGDXGame.PPM);
        fixtureDef.filter.categoryBits = LibGDXGame.MARIO_BIT;//Asignamos la categoria para las colisiones de Mario.
        //Asignamos con que puede colisionar.
        fixtureDef.filter.maskBits = LibGDXGame.GROUND_BIT | LibGDXGame.COIN_BIT | LibGDXGame.BRICK_BIT | LibGDXGame.ENEMY_BIT
                                     |LibGDXGame.OBJECT_BIT| LibGDXGame.ENEMY_HEAD_HIT | LibGDXGame.ITEM_BIT;
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef);//Asignamos la fisica...
        EdgeShape head = new EdgeShape();
        //Dos vectores que rodeen la cabeza, desde el eje X -2 (parte trasera de la cabeza) hasta la parte delantera
        //del sprite de la cabeza X+2 escalado por pixeles por metro.
        head.set(new Vector2(-2/ LibGDXGame.PPM, 6/LibGDXGame.PPM),new Vector2(2/ LibGDXGame.PPM, 6/LibGDXGame.PPM));
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;//Definimos un sensor para conocer si colisiona con algun objeto.
        b2body.createFixture(fixtureDef).setUserData("head");//Asignamos la textura y le damos un nombre.
    }
}
