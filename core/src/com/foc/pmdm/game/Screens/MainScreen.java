package com.foc.pmdm.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.foc.pmdm.game.LibGDXGame;
import com.foc.pmdm.game.Sprites.Mario;

/**
 * Created by entreri on 1/06/17.
 */

public class MainScreen implements Screen {

    private Texture mainScreen;
    private LibGDXGame game;
    private Viewport viewport;
    private Stage stage;
    private Table mainTable;
    private Button start;
    private Button exit;

    public MainScreen (LibGDXGame game){
        this.game = game;
        viewport = new FitViewport(LibGDXGame.V_WIDHT, LibGDXGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, (game).batch);
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void show() {
        mainScreen = new Texture(Gdx.files.internal("main.png"));
        prepareButtons();
    }

    public void prepareButtons(){
        Skin mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.bottom();//Centrado en la parte inferior
        start = new TextButton("Jugar",mySkin,"small");
        start.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //NADA no es necesario en este uso
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
                dispose();
                return true;
            }
        });
        exit = new TextButton("Salir",mySkin,"small");
        exit.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //NADA no es necesario en este uso
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });
        mainTable.add(start).height(40).width(70);
        mainTable.row().pad(5, 0, 5, 0);
        mainTable.add(exit).height(40).width(70);
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        GL20 gl = Gdx.graphics.getGL20();
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(mainScreen, 0, 0, stage.getWidth(),stage.getHeight());
        game.batch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
