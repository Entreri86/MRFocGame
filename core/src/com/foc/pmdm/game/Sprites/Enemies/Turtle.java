package com.foc.pmdm.game.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Screens.GameScreen;
import com.foc.pmdm.game.Sprites.Mario;

/**
 * Created by entreri on 30/05/17.
 */

public class Turtle extends Enemy {
    public enum State  {WALKING, STAND_SHELL, MOVING_SHELL, DEAD};
    public State currentState;
    public State previousState;
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    //Variables necesarias para el enemigo a crear.
    private float stateTime;
    private Animation walkAnimation;
    private TextureRegion shell;
    private Array<TextureRegion> frames;
    private float deadRotationDegrees;
    private boolean destroyed;
    /**
     * Constructor encargado de inicializar los datos y fijar la posicion, forma y velocidad de los enemigos.
     *
     * @param screen Pantalla donde debe de ser mostrado.
     * @param x      valor del eje X.
     * @param y      valor del eje Y.
     */
    public Turtle(GameScreen screen, float x, float y) {
        super(screen, x, y);
        String turtle = "turtle";
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion(turtle),0,0,16,24));//16*24 pix
        frames.add(new TextureRegion(screen.getAtlas().findRegion(turtle),16,0,16,24));//Segunda pos.
        shell = new TextureRegion(screen.getAtlas().findRegion(turtle),64,0,16,24);//4 pos.
        walkAnimation = new Animation(0.2f,frames);
        currentState = previousState = State.WALKING;//Asignamos las dos animaciones a andar.
        deadRotationDegrees = 0;
        setBounds(getX(),getY(),16/ LibGDXGame.PPM,24/LibGDXGame.PPM);//Escalamos la tortuga...

    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(),getY());//Temporal la posicion.
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        //Capa con forma que envuelve a la tortuga para las colisiones etc.
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / LibGDXGame.PPM);
        fixtureDef.filter.categoryBits = LibGDXGame.ENEMY_BIT;//Asignamos la categoria de Tortuga.
        //Asignamos con que puede colisionar.
        fixtureDef.filter.maskBits = LibGDXGame.GROUND_BIT | LibGDXGame.COIN_BIT |
                LibGDXGame.BRICK_BIT | LibGDXGame.ENEMY_BIT | LibGDXGame.OBJECT_BIT
                |LibGDXGame.MARIO_BIT;

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        //Creamos la cabeza para que tortuga impacte con ella al saltar.
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];//4 vertices nada mas.
        vertices [0] = new Vector2(-5,8).scl(1/LibGDXGame.PPM);//eje x-5 parte trasera(izquierda) del Sprite 8 eje Y altura escalado.
        vertices [1] = new Vector2(5,8).scl(1/LibGDXGame.PPM);//eje x5 parte delantera (derecha) del Sprite 8 eje Y altura escalado.
        vertices [2] = new Vector2(-3,3).scl(1/LibGDXGame.PPM);//Segunda parte de la cabeza igual que arriba.
        vertices [3] = new Vector2(3,3).scl(1/LibGDXGame.PPM);
        head.set(vertices);//Asignamos los vertices
        fixtureDef.shape = head;
        fixtureDef.restitution = 1.5f;//Cuando Mario golpee subira esos pixeles de rebote al pisar la cabeza de la tortuga.
        fixtureDef.filter.categoryBits = LibGDXGame.ENEMY_HEAD_HIT;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        //Si esta andando y recibe un golpe en la cabeza lo convertimos en caparazon.
        if (currentState != State.STAND_SHELL){
            currentState = State.STAND_SHELL;
            vel.x = 0;//paramos velocidad.
        } else {
            kick(mario.getX()<= this.getX() ? KICK_RIGHT_SPEED:KICK_LEFT_SPEED);
        }
    }

    @Override
    public void update(float deltaTime) {
        setRegion(getFrame(deltaTime));
        if (currentState == State.STAND_SHELL && stateTime>5){//Cinco segundos para volverse a mover
            currentState = State.WALKING;
            vel.x = 1;
        }
        setPosition(b2body.getPosition().x - getWidth() /2, b2body.getPosition().y - 8/LibGDXGame.PPM);
        if (currentState == State.DEAD){
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if (stateTime >5 && !destroyed){
                world.destroyBody(b2body);
                destroyed = true;
            }
        } else {
            b2body.setLinearVelocity(vel);
        }
    }

    public TextureRegion getFrame (float deltaTime){
        TextureRegion region;

        switch (currentState){

            case MOVING_SHELL:
            case STAND_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = (TextureRegion) walkAnimation.getKeyFrame(stateTime, true);
                break;
        }
        //Si la velocidad es mas grande que 0 (hacia la derecha) y el sprite esta posicionado hacia la izquierda...
        if (vel.x >0 && !region.isFlipX()){
            region.flip(true,false);//Giramos el sprite en eje x.
        }
        //Si la velocidad es menor que 0 (hacia la izquierda) y el sprite esta posicionado hacia la derecha...
        if (vel.x <0 && region.isFlipX()){
            region.flip(true,false);//Giramos el sprite en eje x.
        }
        stateTime = currentState == previousState ? stateTime + deltaTime : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle){
            if (((Turtle)enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL){
                killed();
            } else if (currentState == State.MOVING_SHELL && ((Turtle)enemy).currentState == State.WALKING){
                return;
            } else{
                reverseVel(true,false);
            }
        } else if (currentState != State.MOVING_SHELL){//Si el enemigo es un goomba y no se esta en modo caparazon
            reverseVel(true,false);//cambiamos la direccion.
        }
    }

    public State getCurrentState (){
        return currentState;
    }

    public void kick (int speed){
        vel.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public void killed (){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = LibGDXGame.NOTHING_BIT;

        for (Fixture fixture: b2body.getFixtureList()){
            fixture.setFilterData(filter);
        }
        b2body.applyLinearImpulse(new Vector2(0,5f),b2body.getWorldCenter(),true);
    }

    public void draw (Batch batch){
        if (!destroyed){
            super.draw(batch);
        }
    }
}
