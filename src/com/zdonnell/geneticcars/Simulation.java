package com.zdonnell.geneticcars;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class Simulation implements ApplicationListener {

    SpriteBatch batch;
    OrthographicCamera camera;

	BitmapFont font;

	/** the renderer **/
	protected Box2DDebugRenderer renderer;

	/** our box2D world **/
	protected World world;

	private ArrayList<Body> terrainTiles = new ArrayList<Body>();

	Car mainCar;

    @Override
    public void create() {
        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
		camera.position.set(0, 0, 0);
		camera.zoom = 0.02f;

		// create the debug renderer
		renderer = new Box2DDebugRenderer(true, true, false, false);
		batch = new SpriteBatch();

		//font = new BitmapFont();

		// create the world
		world = new World(new Vector2(0, -9.8f), true);
		TerrainGenerator.generate(world);

		mainCar = CarFactory.buildCar(new CarDefinition(), world);
    }

    @Override
    public void render() {
		world.step(Gdx.app.getGraphics().getDeltaTime(), 10, 10);

		// clear the screen and setup the projection matrix
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		camera.position.set(mainCar.getChassis().getPosition().x, mainCar.getChassis().getPosition().y, 0);

		// render the world using the debug renderer
		renderer.render(world, camera.combined);
    }

    @Override
    public void dispose() {
        batch.dispose();
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
}