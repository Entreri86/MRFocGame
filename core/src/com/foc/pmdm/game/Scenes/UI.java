package com.foc.pmdm.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Sprites.Mario;

/**
 * Created by entreri on 25/03/17.
 */

public class UI implements Disposable {
    //Variables del nivel.
    public Stage stage;
    private Viewport viewPort;
    private Integer worldTimer;
    private static Integer uScore;
    private float timeCount;
    //Variables de los marcadores
    private Label cDownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label mrLabel;
    //botones personalizados.
    private Button buttonIzq;
    private Button buttonDer;
    private Button buttonJum;

    /**
     * Constructor de clase encargado de mostrar en pantalla los elementos.
     * @param spriteBatch Sprites que debe de mostrar la pantalla.
     */
    public UI (SpriteBatch spriteBatch){
        worldTimer = 300;
        timeCount = 0;
        uScore = 0;
        //Iniciamos las variables
        viewPort = new FitViewport(LibGDXGame.V_WIDHT,LibGDXGame.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewPort,spriteBatch);
        Table table = new Table();//Tabla para organizar los Labels.
        table.top();
        table.setFillParent(true);//Hacemos que recrezca con la pantalla.
        /**
         * Formatamos la cadena de texto a mostrar, creamos el estilo de letra de las etiquetas color blanco.
         */
        cDownLabel = new Label (String.format("%03d",worldTimer),new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label (String.format("%06d",uScore),new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label ("TIME",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label ("1-1",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label ("WORLD",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        mrLabel = new Label ("MARIO",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        //Primera fila de la tabla en horizontal, 10 pixeles.
        table.add(mrLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).padTop(10);
        //Nueva fila para los elementos restantes.
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(cDownLabel).expandX();

        //Añadimos las tablas al Stage.
        stage.addActor(table);
        //stage.addActor(gamePadTable);
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Metodo encargado de añadir "Actores" (en este caso de momento los touchListener) al stage.
     * @param actor Actor a añadir.
     */
    public void addActor (Actor actor){
        stage.addActor(actor);
    }

    /**
     * Metodo encargado de actualizar los datos en pantalla.
     * @param deltaTime tiempo a actualizar.
     */
    public void update ( float deltaTime){
        timeCount += deltaTime;
        if (timeCount >= 1){
            worldTimer --;
            cDownLabel.setText(String.format("%03d",worldTimer));
            timeCount = 0;
        }
    }

    /**
     * Metodo encargado de actualizar la puntuacion a mostrar en pantalla.
     * @param value valor a añadir y mostrar.
     */
    public static void addScore (int value){
        uScore += value;
        scoreLabel.setText(String.format("%06d",uScore));
    }
    /**
     * Metodo encargado de limpiar los objetos que ya no se usaran y liberar asi la memoria.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
