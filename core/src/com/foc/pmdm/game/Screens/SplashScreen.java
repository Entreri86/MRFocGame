package com.foc.pmdm.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.foc.pmdm.game.LibGDXGame;


/**
 * Created by entreri on 1/06/17.
 */

public class SplashScreen implements Screen {
    private Texture logo;
    private LibGDXGame game;

    public SplashScreen (LibGDXGame game){
        this.game = game;
    }
    @Override
    public void show() {
        logo = new Texture(Gdx.files.internal("logo22.png"));
    }

    @Override
    public void render(float delta) {
        handleInput();
        GL20 gl = Gdx.graphics.getGL20();
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(logo, 200, 0, 800,600);
        game.batch.end();

    }

    private void handleInput()
    {
        if(Gdx.input.justTouched())
        {
            game.setScreen(new GameScreen(game));
            dispose();
        }
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
