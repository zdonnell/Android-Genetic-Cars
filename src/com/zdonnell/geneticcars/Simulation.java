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

/**
 * This is the main class maintaining the state of the simulation.  It primarily keeps track of
 * global simulation details, such as the number of currently existing cars, and what generation
 * we are on.
 *
 * @author Zach
 */
public class Simulation implements ApplicationListener {

	/**
	 * The number of cars to be spawned at the beginning of each generation
	 */
	private static final int GENERATION_SIZE = 10;

	/**
	 * The libgdx camera to render the cars/terrain to
	 */
	OrthographicCamera camera;

	/**
	 * Reference to the custom rendering class that handles drawing of the simulation
	 * elements
	 */
	private Renderer renderer;

	/**
	 * The box2d World.  This is the "world" that the actual physics simulation is done in.
	 */
	protected World world;

	/**
	 * The font class used to draw text to the screen
	 */
	private BitmapFont font;

	/**
	 * SpriteBatch used with {@link #font} to render text
	 */
	private SpriteBatch spriteBatch;

	/**
	 * The list of all terrain tiles (box2d physics bodies)
	 */
	private List<Body> terrainTiles = new ArrayList<Body>();

	/**
	 * The list of cars still alive in the current generation
	 */
	private List<Car> activeCars = new ArrayList<Car>(GENERATION_SIZE);

	/**
	 * All the cars from the current generation DEAD OR ALIVE
	 */
	private List<Car> genCars = new ArrayList<Car>(GENERATION_SIZE);

	/**
	 * A reference to the view containing this simulation.  This is currently
	 * needed to determine the size of the area (in pixels) used to
	 * display the simulation
	 */
	private View view;

	/**
	 * The max distance any car has traveled
	 */
	private double maxDistance = 0;

	/**
	 * The generation that {@link #maxDistance} was set during
	 */
	private int maxDistanceGeneration = 0;

	/**
	 * The height of the screen in pixels, this is only needed
	 * so we can render the gui text at different points on the screen.
	 */
	private int screenHeight;

	/**
	 * The current generation number
	 */
	private int generation = 0;

	/**
	 * Set this so the simulation can figure out it's size in pixels when it
	 * tries in {@link #create()}  This is important, we need it to figure out how
	 * to scale the camera frame, so it's 1:1 with the view frame.
	 *
	 * TODO: Figure out if there is a better way to do this.
	 *
	 * @param simulationView the view that contains the simulation
	 */
	public void setParentView(View simulationView) {
		this.view = simulationView;
	}

	@Override
	public void create() {
		// Get the containing views size
		// The measured width/height should be set by the time this method is called
		int screenWidth = view.getMeasuredWidth();
		screenHeight = view.getMeasuredHeight();
		view = null;

		// create the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 12, 12 * (float) screenHeight / (float) screenWidth);
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

		// Draw car list
		spriteBatch.begin();
		for (Car car : activeCars) {
			String xPos = String.format("%4.2f", car.getChassis().getPosition().x);
			font.draw(spriteBatch, "Car# " + genCars.indexOf(car) + ": " + xPos + (car.isElite ? "*" : ""), 10, screenHeight - 10 - 27 * activeCars.indexOf(car));
		}

		// Draw the current gen / max distance info
		font.draw(spriteBatch, "Generation: " + generation, 10, 64);
		if (generation != 0)
			font.draw(spriteBatch, "Max Distance: " + String.format("%4.2f", maxDistance) + "(gen " + maxDistanceGeneration + ")", 10, 37);
		spriteBatch.end();
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
				}
			}
		}
	}

	/**
	 * This populates the simulation with a new generation.  The top car from the last
	 * generation will be placed in this generation automatically as an "elite" (blue car).<br><br>
	 *
	 * The remainder of the generation ({@link #GENERATION_SIZE} - 1) will be populated by
	 * "mating" the rest of the last generation, the parents will be chosen with an emphasis on
	 * how well they performed.
	 */
	private void nextGeneration() {
		// Sort the cars by how far they made it
		Collections.sort(genCars, new CarDistanceSort());

		// Update the top score if necessary
		if (genCars.get(0).maxDistance > maxDistance) {
			maxDistance = genCars.get(0).maxDistance;
			maxDistanceGeneration = generation;
		}

		// Make a clone of the top car
		Car elite = CarFactory.buildClone(genCars.get(0), world);
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
		genCars.clear();
		genCars.addAll(activeCars);

		generation++;
	}

	/**
	 * Gets a Car from the previous generation, with a preference for
	 * "high performing" cars.
	 *
	 * @return a Car from the previous generation
	 */
	private Car getParent() {
		double r = Math.random();
		if (r == 0)
			return genCars.get(0);
		return genCars.get((int) (-Math.log(r) * GENERATION_SIZE) % GENERATION_SIZE);
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