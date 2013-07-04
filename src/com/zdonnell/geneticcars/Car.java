package com.zdonnell.geneticcars;

import com.badlogic.gdx.physics.box2d.Body;

/**
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
     * Creates a car from the provided definition.
     */
    public Car(CarDefinition carDefinition, Body chassis, Body[] wheels) {
		this.carDefinition = carDefinition;
		this.chassis = chassis;
		this.wheels = wheels;
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
}