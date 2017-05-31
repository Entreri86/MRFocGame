package com.foc.pmdm.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Scenes.UI;
import com.foc.pmdm.game.Sprites.Enemies.Enemy;
import com.foc.pmdm.game.Sprites.Items.Item;
import com.foc.pmdm.game.Sprites.Items.ItemDefinition;
import com.foc.pmdm.game.Sprites.Items.Mushroom;
import com.foc.pmdm.game.Sprites.Mario;
import com.foc.pmdm.game.Tools.B2dWorldCreator;
import com.foc.pmdm.game.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by entreri on 25/03/17.
 */

public class GameScreen implements Screen {

    private LibGDXGame lGame;//Referencia al juego para asignar las pantallas.
    //Variables para la vista del juego.
    private OrthographicCamera oCamGame;//Camara del juego.
    private Viewport gamePort;
    private UI userInterface;
    //Variable para el tileset de sprites.
    private TextureAtlas atlas;
    //Variables para gestionar  el Mapa personalizado de Tiled.
    private TmxMapLoader tmxMapLoader;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    //Variables de box2d
    private World world;
    private Box2DDebugRenderer b2dr;//Representacion fisica de los cuerpos del juego como bloques.
    private B2dWorldCreator worldCreator;
    //Variable del personaje.
    private Mario player;
    //Variable para la musica.
    private Music music;
    //Variables para Items.
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDefinition> itemsToSpawn;
    private Button buttonIzq;
    private Button buttonDer;
    private Button buttonJum;
    private Table gamePadTable;
    //Variables para la Musica y efectos.
    private final java.lang.String MARIO_MUSIC = "audio/music/mario_music.ogg";
    private final java.lang.String MARIO_BUMP = "audio/sounds/bump.wav";
    private final java.lang.String MARIO_BREAK = "audio/sounds/breakblock.wav";
    private final java.lang.String MARIO_COIN = "audio/sounds/coin.wav";
    private final java.lang.String MARIO_MUSHROOM_PW = "audio/sounds/powerup.wav";
    private final java.lang.String MARIO_MUSHROOM = "audio/sounds/powerup_spawn.wav";
    private final java.lang.String MARIO_STOMP = "audio/sounds/stomp.wav";
    private final java.lang.String MARIO_PW_DWN = "audio/sounds/powerdown.wav";
    private final java.lang.String MARIO_DIE = "audio/sounds/mariodie.wav";
    private AssetManager manager;

    /**
     * Constructor encargado de inicializar todos los atributos necesarios para mostrar en pantalla los
     * objetos relacionados con el juego.
     * @param game instancia del juego en la cual hay que cargar los objetos.
     */
    public GameScreen (LibGDXGame game) {
        prepareMusic();//Preparamos musica.
        atlas = new TextureAtlas("Mario_and_Enemies.pack");//Paquete con los tileset de los Enemigos y Mario.
        this.lGame =game;

        //Creamos la camara utilizada para seguir al personaje alrededor del mapa.
        oCamGame = new OrthographicCamera();
        /**
         * Creamos el FitViewPort para que mantenga la relacion de aspecto a pesar de que las medidas
         * de la pantalla donde se visualice cambien dividiendolo por los pixeles por metro,
         * rellenando el espacio sobrante con dos barras negras.
         */
        gamePort = new FitViewport(LibGDXGame.V_WIDHT / LibGDXGame.PPM,LibGDXGame.V_HEIGHT / LibGDXGame.PPM,oCamGame);
        //Le pasamos el Sprite por parametro,donde iran los marcadores de puntuaciones y nivel.
        userInterface = new UI(lGame.batch);
        prepareButtons();//Preparamos los botones
        userInterface.addActor(gamePadTable);//los añadimos al stage que hara de inputListener.
        //Cargamos el mapa y preparamos el renderizado.
        tmxMapLoader = new TmxMapLoader();
        tiledMap = tmxMapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / LibGDXGame.PPM);//Escalamos el mapa.
        //Fijamos inicialmente la camara centrada al inicio del mapa.
        oCamGame.position.set(gamePort.getWorldWidth()/2,gamePort.getWorldHeight()/2,0);
        world = new World(new Vector2(0,-10),true); //De momento sin gravedad (parametros de vector).
        b2dr = new Box2DDebugRenderer();
        worldCreator = new B2dWorldCreator(this);//Rellenamos el mapa con el contenido remarcado de tiled (bloques, monedas...).
        player = new Mario (this);//Pasamos la pantalla por parametro.
        //Listener de colisiones y contactos.
        world.setContactListener(new WorldContactListener());
        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDefinition>();
    }

    /**
     * Metodo encargado de añadir a la lista de objetos a mostrar en pantalla los objetos que deben de
     * ser visualizados.
     * @param itemDefinition Definicion del objeto a mostrar.
     */
    public void spawnItem ( ItemDefinition itemDefinition){
        itemsToSpawn.add(itemDefinition);
    }

    /**
     * Metodo encargado de gestionar los objetos que deben de ser visualizados en pantalla.
     */
    public void handleSpawnItems(){
        if (!itemsToSpawn.isEmpty()){//Si aun quedan objetos por soltar.
            ItemDefinition itemDefinition = itemsToSpawn.poll();
            //Recogemos objeto de la lista, si es una seta...
            if (itemDefinition.type == Mushroom.class){
                items.add(new Mushroom(this,itemDefinition.position.x,itemDefinition.position.y));
                //Creamos una seta y la colocamos en su posicion.
            }
        }

    }

    /**
     * Metodo encargado de preparar el gestor de musica con la musica del juego.
     */
    public void prepareMusic (){
        manager = new AssetManager();
        manager.load(MARIO_MUSIC,Music.class);
        manager.load(MARIO_BREAK,Sound.class);//Brick.java
        manager.load(MARIO_BUMP,Sound.class);//Coin.java
        manager.load(MARIO_COIN,Sound.class);//Coin.java
        manager.load(MARIO_MUSHROOM,Sound.class);//Seta al recogerla
        manager.load(MARIO_MUSHROOM_PW,Sound.class);//Creciendo a mario.
        manager.load(MARIO_PW_DWN,Sound.class);//transformandose en pequeño
        manager.load(MARIO_STOMP,Sound.class);//pisando enemigos
        manager.load(MARIO_DIE,Sound.class);
        manager.finishLoading();
        music = manager.get(MARIO_MUSIC,Music.class);
        music.setLooping(true);
        music.play();
    }

    /**
     * Metodo encargado de gestionar los movimientos que realiza el usuario mediante uso
     * de un teclado.
     */
    public void handleInput (float deltaTime){//Float deltaTime no usado de momento.
        if (player.currentState != Mario.States.DEAD){
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                //Si pulsa arriba...
                //Aplicamos un impulso hacia arriba en el eje Y de 4 para el salto.
                player.b2body.applyLinearImpulse(new Vector2(0 , 4f), player.b2body.getWorldCenter(),true);
            }
            if ((Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))&& (player.b2body.getLinearVelocity().x <=2)){
                /**
                 * Si pulsa derecha y la velocidad es menor a 2 se mueve 0,1f el Sprite de Mario en el eje X.
                 */
                player.b2body.applyLinearImpulse(new Vector2(0.8f,0),player.b2body.getWorldCenter(),true);
            }
            if ((Gdx.input.isKeyJustPressed(Input.Keys.LEFT))&& (player.b2body.getLinearVelocity().x >= -2)){
                /**
                 * Si pulsa izquierda y la velocidad es mayor a -2 (al desplazar a Mario hacia la izquierda se resta en el
                 * eje X en vez de sumar) se mueve -0,1f el Sprite de Mario en el eje X.
                 */
                player.b2body.applyLinearImpulse(new Vector2(-0.8f,0),player.b2body.getWorldCenter(),true);
            }
        }
    }

    /**
     * Metodo encargado de parar la musica.
     */
    public void stopMusic(){
        music.stop();
    }
    /**
     * Metodo encargado de preparar y gestionar los botones y sus toques en la pantalla.
     */
    public void prepareButtons(){
        //Creamos el skin de los botones recogiendo el fichero json
        Skin mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        //Creamos nueva tabla para los botones del "gamepad rustico"
        gamePadTable = new Table();
        gamePadTable.bottom();//Centrado en la parte inferior
        gamePadTable.setFillParent(true);//Expandido como el padre.
        buttonDer  = new TextButton("Der.",mySkin,"small");
        buttonDer.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //NADA no es necesario en este uso
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (player.currentState != Mario.States.DEAD){
                    if (buttonDer.isPressed()==true){
                        player.b2body.applyLinearImpulse(new Vector2(0.8f,0),player.b2body.getWorldCenter(),true);
                    }
                }
                return true;
            }
        });
        buttonIzq  = new TextButton("Izq.",mySkin,"small");
        buttonIzq.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //NADA no es necesario en este uso
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (player.currentState != Mario.States.DEAD){
                    if (buttonIzq.isPressed()==true){
                        player.b2body.applyLinearImpulse(new Vector2(-0.8f,0),player.b2body.getWorldCenter(),true);
                    }
                }
                return true;
            }
        });
        buttonJum  = new TextButton("Saltar",mySkin,"small");
        buttonJum.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //NADA no es necesario en este uso
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (player.currentState != Mario.States.DEAD){
                    if (buttonJum.isPressed()==true){
                        player.b2body.applyLinearImpulse(new Vector2(0 , 4f), player.b2body.getWorldCenter(),true);
                    }
                }
                return true;
            }
        });
        //Iniciamos los botones..
        gamePadTable.add(buttonIzq).height(35).width(50).align(Align.left).padBottom(5);
        gamePadTable.add(buttonDer).height(35).width(50).expandX().padBottom(5).align(Align.left);
        gamePadTable.add(buttonJum).height(35).width(50).expandX().padBottom(5).align(Align.right);
        //Añadimos los botones con las posiciones y tamaños personalizados.
    }
    /**
     * Metodo encargado en cada iteracion de actualizar los datos, enemigos y objetos mostrados en pantalla.
     * @param deltaTime valor para el calculo del tiempo de actualizacion.
     */
    public void update (float deltaTime){
        //Primero capturamos el input del usuario.
        handleInput(deltaTime);
        //Controlamos los objetos a soltar.
        handleSpawnItems();
        //De timestep fijamos 60 veces por segundo, velocidad de iteracion 6 y iteraciones de posicion 2.
        world.step(1/60f,6,2);//Bucle del juego principal.
        player.update(deltaTime);//Actualizamos el personaje (sprite).
        //Actualizamos los goombas iniciandolos por cercania.
        for (Enemy enemy: worldCreator.getEnemies()){
            enemy.update(deltaTime);
            if (enemy.getX() < player.getX() + 224/LibGDXGame.PPM){
                enemy.b2body.setActive(true);//Activamos los Goomba por proximidad con el jugador.
            }
        }
        //Actualizamos los objetos a mostrar.
        for (Item item : items){
            item.update(deltaTime);
        }
        userInterface.update(deltaTime);//
        if (player.currentState != Mario.States.DEAD){
            //Solo realizamos el seguimiento de la camara en el eje X (lateral) y no en los saltos.
            oCamGame.position.x = player.b2body.getPosition().x;
        }
        oCamGame.update(); //Actualizamos la camara en cada ciclo de renderizacion.
        lGame.batch.setProjectionMatrix(oCamGame.combined);
        renderer.setView(oCamGame);//Solo renderiza lo que la camara actualmente ve.
    }

    /**
     * Metodo encargado de renderizar los objetos a mostrar en pantalla asi como el fondo negro.
     * @param delta valor para el calculo del tiempo de actualizacion.
     */
    @Override
    public void render(float delta) {
        update(delta);//Lo primero que debemos hacer es actualizar la pantalla.
        //Colocamos un fondo negro.
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Renderizamos el mapa del juego.
        renderer.render();
        //Renderizado de los objetos de Box2d (bloques etc.)
        b2dr.render(world,oCamGame.combined);//Renderizamos con la proyeccion Matrix combinada.
        //Fijamos en el Sprite lo que la camara actualmente esta viendo.
        lGame.batch.setProjectionMatrix(oCamGame.combined);
        lGame.batch.begin();
        player.draw(lGame.batch);//Dibujamos a mario, los goombas y tortugas.
        for (Enemy enemy: worldCreator.getEnemies()){
            enemy.draw(lGame.batch);
        }
        //Dibujamos los objetos especiales...
        for (Item item : items){
            item.draw(lGame.batch);
        }
        lGame.batch.end();
        userInterface.stage.draw();
        if (gameOver()){
            lGame.setScreen(new GameOverScreen(lGame));
            dispose();
        }
    }

    /**
     * Metodo encargado de comprobar si la partida esta por terminar.
     * @return true en caso de tener que mostrar la pantalla de GameOver, false en caso contrario.
     */
    public boolean gameOver(){
        if (player.currentState == Mario.States.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }
    /**
     * Metodo encargado de adaptar las medidas a la pantalla.
     * @param width ancho.
     * @param height alto.
     */
    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);//Actualizamos las medidas para adaptarla a la pantalla.
    }

    /**
     *
     */
    @Override
    public void pause() {

    }

    /**
     *
     */
    @Override
    public void resume() {

    }

    /**
     *
     */
    @Override
    public void hide() {

    }
    /**
     *
     */
    @Override
    public void show() {

    }
    /**
     * Metodo encargado de liberar los recursos que ya no son necesarios para su uso.
     */
    @Override
    public void dispose() {
        tiledMap.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        userInterface.dispose();
    }

    public AssetManager getManager() {
        return manager;
    }
    public TiledMap getTiledMap (){
        return tiledMap;
    }
    public World getWorld (){
        return world;
    }
    public TextureAtlas getAtlas (){
        return atlas;
    }
    public java.lang.String getMARIO_MUSIC() {
        return MARIO_MUSIC;
    }
    public java.lang.String getMARIO_BUMP() {
        return MARIO_BUMP;
    }
    public java.lang.String getMARIO_BREAK() {
        return MARIO_BREAK;
    }
    public java.lang.String getMARIO_COIN() {
        return MARIO_COIN;
    }
    public String getMARIO_MUSHROOM() {
        return MARIO_MUSHROOM;
    }
    public String getMARIO_MUSHROOM_PW() {
        return MARIO_MUSHROOM_PW;
    }
    public String getMARIO_PW_DWN() {
        return MARIO_PW_DWN;
    }
    public String getMARIO_STOMP() {
        return MARIO_STOMP;
    }
    public String getMARIO_DIE() {
        return MARIO_DIE;
    }
}
