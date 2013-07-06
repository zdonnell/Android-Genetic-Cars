package com.zdonnell.geneticcars;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 *
 * @author Zach
 */
public class Car extends Image {

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

	public float maxDistance = 0.0f;

	public long timeLastMoved;

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

	public CarDefinition getCarDefinition() {
		return carDefinition;
	}

	public Body getChassis() {
		return chassis;
	}

	public Body[] getWheels() {
		return wheels;
	}

	public void removeFromWorld(World world) {
		world.destroyBody(chassis);

		for (Body wheel : wheels)
			world.destroyBody(wheel);
	}
}