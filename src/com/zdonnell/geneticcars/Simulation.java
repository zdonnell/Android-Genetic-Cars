package com.zdonnell.geneticcars;

import android.view.View;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class Simulation implements ApplicationListener {

	private static final int GENERATION_SIZE = 10;

	public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<>";

	OrthographicCamera camera;

	private Renderer renderer;

	protected World world;

	private List<Body> terrainTiles = new ArrayList<Body>();

	private List<Car> activeCars = new ArrayList<Car>(GENERATION_SIZE);

	private List<Car> genCars = new ArrayList<Car>(GENERATION_SIZE);

	private List<Car> deadCars = new ArrayList<Car>(GENERATION_SIZE);

    private float aspect = 0.5f;

	private View view;

	private int screenWidth, screenHeight;

	private int generation = 0;

	private BitmapFont font;
	private SpriteBatch spriteBatch;

    public void setParentView(View simulationView) {
		this.view = simulationView;
    }

    @Override
    public void create() {
		screenWidth = view.getMeasuredWidth();
		screenHeight = view.getMeasuredHeight();
		view = null;
		aspect = (float) screenHeight / (float) screenWidth;

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 12, 12 * aspect);
		camera.position.set(0, 0, 0);

		font = new BitmapFont(Gdx.files.internal("khmer.fnt"), Gdx.files.internal("khmer.png"), false);
		spriteBatch = new SpriteBatch();

		// create the world
		world = new World(new Vector2(0, -9.8f), true);

		// Create the renderer
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		renderer = new Renderer(shapeRenderer);

		// Generate the terrain
		terrainTiles = TerrainGenerator.generate(world);

		// Create initial car generation
		createGeneration();
    }

    @Override
    public void render() {

		// Step the physics simulation forward
        world.step(Gdx.app.getGraphics().getDeltaTime(), 20, 20);

		// Reset gl frame stuff
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		Gdx.gl.glLineWidth(3);
		camera.update();

		// move the camera to the lead car
		// give it a bit of a linear interpolation to smooth things out
		Car leadCar = determineLeadCar();
		Vector3 position = camera.position;
		position.x += (leadCar.getChassis().getPosition().x - position.x) * 0.2f;
		position.y += (leadCar.getChassis().getPosition().y - position.y) * 0.2f;

		// see if any cars have died since the last cycle through.
		findDeadCars();

		// check for when we run out of cars, so we can create the next generation
		if (activeCars.isEmpty())
			nextGeneration();

		// actually render stuff
		renderer.setProjectionMatrix(camera.combined);
		renderer.renderCars(activeCars);
		renderer.renderTiles(terrainTiles);

		spriteBatch.begin();
		for (Car car : activeCars) {
			String xPos = String.format("%4.2f", car.getChassis().getPosition().x);
			font.draw(spriteBatch, "Car# " + genCars.indexOf(car) + ": " + xPos + (car.isElite ? "*" : ""), 10, screenHeight - 10 - 27 * activeCars.indexOf(car));
		}
		spriteBatch.end();
	}

	/**
	 * Resets the current simulation with a fresh terrain and set of cars.
	 */
	public void reset() {
		// Clear out all the cars we are keeping track of
		for (Car car : activeCars)
			car.removeFromWorld(world);
		activeCars.clear();
		deadCars.clear();
		genCars.clear();

		// Remove the old terrain
		for (Body tile : terrainTiles) {
			world.destroyBody(tile);
		}
		terrainTiles.clear();

		// Generate new terrain
		TerrainGenerator.generate(world);

		// Spawn initial population
		createGeneration();
	}

	/**
	 * Looks through the list of active cars to determine which
	 * are still "alive."
	 */
	private void findDeadCars() {
		ListIterator<Car> iter = activeCars.listIterator();
		// Iterate through each active car and see if it has surpassed it's previous
		// max distance.  If it hasn't in the last 5 seconds, kill it :(
		while (iter.hasNext()) {
			Car car = iter.next();
			if (car.getChassis().getPosition().x > car.maxDistance) {
				car.maxDistance = car.getChassis().getPosition().x;
				car.timeLastMoved = System.currentTimeMillis();
			} else {
				if (System.currentTimeMillis() - car.timeLastMoved > 5000) {
					iter.remove();
					car.removeFromWorld(world);
                    deadCars.add(car);
				}
			}
		}
	}

	/**
	 *
	 */
	private void nextGeneration() {
		genCars.clear();

		// Sort the cars by how far they made it
		Collections.sort(deadCars, new CarDistanceSort());
		
		// Make a clone of the top car
		Car elite = CarFactory.buildClone(deadCars.get(0), world);
		activeCars.add(elite);
		
		// make babies!
		for (int i = 0; i < GENERATION_SIZE - 1; i++) {
			Car p1 = getParent();
			Car p2 = p1;
			while (p2 == p1)
				p2 = getParent();

			Car baby = CarFactory.buildBabyCar(p1, p2, world);
			activeCars.add(baby);
		}
		// Add the new cars to the list of all cars for the generation
		genCars.addAll(activeCars);

		deadCars.clear();
		generation++;
	}

	/**
	 * Gets a Car from the previous generation, with a preference on
	 * "high performing" cars.
	 * 
	 * @return a Car from the previous generation
	 */
	private Car getParent() {
		double r = Math.random();
		if (r == 0)
			return deadCars.get(0);
		return deadCars.get((int) (-Math.log(r) * GENERATION_SIZE) % GENERATION_SIZE);
	}

	/**
	 * Creates an entirely new/random generation.
	 */
	private void createGeneration() {
		for (int i = 0; i < GENERATION_SIZE; i++) {
			Car newCar = CarFactory.buildCar(new CarDefinition(), world, false);
			activeCars.add(newCar);
		}
		genCars.addAll(activeCars);
	}

	/**
	 * Searches through the list of active cars to find the one
	 * currently the furthest along the x axis.
	 *
	 * @return the Car with the greatest max distance.
	 */
	private Car determineLeadCar() {
		Car leadCar = activeCars.get(0);
		for (Car car : activeCars) {
			float leadCarDist = (leadCar == null) ? 0 : leadCar.getChassis().getPosition().x;
			float curCarDist = car.getChassis().getPosition().x;
			if (curCarDist > leadCarDist) {
				leadCar = car;
			}
		}
		return leadCar;
	}

	/**
	 * Sorting class to sort Cars in a List by their max distance traveled.
	 */
	public class CarDistanceSort implements Comparator<Car> {
		@Override
		public int compare(Car car, Car car2) {
			if (car.maxDistance > car2.maxDistance)
				return -1;
			else if (car.maxDistance > car2.maxDistance)
				return 1;
			else return 0;
		}
	}

	@Override
	public void dispose() {
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