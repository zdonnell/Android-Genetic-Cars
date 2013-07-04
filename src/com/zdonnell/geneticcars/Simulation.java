package com.zdonnell.geneticcars;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Simulation implements ApplicationListener {
    Texture dropImage;
    Texture bucketImage;
    SpriteBatch batch;
    OrthographicCamera camera;

	BitmapFont font;

	/** the renderer **/
	protected Box2DDebugRenderer renderer;

	/** our box2D world **/
	protected World world;

    @Override
    public void create() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
		camera.position.set(0, 0, 0);
		camera.zoom = 0.01f;

		// create the debug renderer
		renderer = new Box2DDebugRenderer(true, true, false, false);
		batch = new SpriteBatch();

		//font = new BitmapFont();

		// create the world
		world = new World(new Vector2(0, -9.8f), true);

		CarFactory.buildCar(new CarDefinition(), world);
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(0, 0);
		Body ground = world.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();

		PolygonShape groundFixShape = new PolygonShape();

		Vector2[] groundVertexes = new Vector2[4];
		groundVertexes[0] = new Vector2(-10, -1);
		groundVertexes[1] = new Vector2(-10, -1.1f);
		groundVertexes[2] = new Vector2(10, -1.1f);
		groundVertexes[3] = new Vector2(10, -1);
		groundFixShape.set(groundVertexes);
		groundFixture.shape = groundFixShape;
		groundFixture.friction = 0.5f;
		ground.createFixture(groundFixture);
    }

    @Override
    public void render() {
		world.step(Gdx.app.getGraphics().getDeltaTime(), 3, 3);

		// clear the screen and setup the projection matrix
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();

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