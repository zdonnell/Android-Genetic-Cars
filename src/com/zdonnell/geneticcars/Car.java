package com.zdonnell.geneticcars;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * This class serves as the assembled representation of a {@link CarDefinition}
 * It primarily exists to keep track of the link between the physics bodies in the simulation
 * and their CarDefinition. It also keeps track of some attributes unique to an individual car, such
 * as the max distance traveled.
 *
 * @author Zach
 */
public class Car {

	/**
	 * The raw attributes that this car is based on
	 */
	private final CarDefinition carDefinition;

	/**
	 * The box2d physics body representing the car chassis
	 */
	private final Body chassis;

	/**
	 * The box2d physics bodies representing the car wheels
	 */
	private final Body[] wheels;

	/**
	 * The max distance in the box2d world that this car reached
	 */
	public float maxDistance = 0.0f;

	/**
	 * The last time (in ms) that the car successfully moved forward
	 */
	public long timeLastMoved;

	/**
	 * This value is true if the car is an elite clone from the
	 * previous generation
	 */
	public boolean isElite = false;

	/**
	 * Creates a car from the provided definition.
	 */
	public Car(CarDefinition carDefinition, Body chassis, Body[] wheels, boolean isElite) {
		this.carDefinition = carDefinition;
		this.chassis = chassis;
		this.wheels = wheels;
		this.isElite = isElite;

		timeLastMoved = System.currentTimeMillis();
	}

	/**
	 *
	 * @return the definition that was used to build this car
	 */
	public CarDefinition getCarDefinition() {
		return carDefinition;
	}

	/**
	 * @return the box2d physics body representing the chassis
	 */
	public Body getChassis() {
		return chassis;
	}

	/**
	 * @return a 2d Array of the box2d physics bodies representing
	 * the wheels
	 */
	public Body[] getWheels() {
		return wheels;
	}

	/**
	 * Removes this car (chassis and wheels) from the provided
	 * box2d world
	 *
	 * @param world the world to remove this car from.
	 */
	public void removeFromWorld(World world) {
		world.destroyBody(chassis);

		for (Body wheel : wheels)
			world.destroyBody(wheel);
	}
}