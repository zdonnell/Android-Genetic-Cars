package com.zdonnell.geneticcars;

/**
 * Similar to {@link CarDefinition} this class is primarily a blueprint
 * for the {@link CarFactory} to build box2d physics objects for the wheels
 * from.
 */
public class Wheel {

	/**
	 * The largest a wheel can be
	 */
	public final static double WHEEL_MAX_RADIUS = 0.5;

	/**
	 *  The smallest a wheel can be
	 */
	public final static double WHEEL_MIN_RADIUS = 0.2;

	/**
	 * The maximum density (in box2d) for a wheel
	 */
	public final static int WHEEL_MAX_DENSITY = 100;

	/**
	 * The minimum density (in box2d) for a wheel
	 */
	public final static int WHEEL_MIN_DENSITY = 40;

	/**
	 * How dense the wheel is (more dense means more weight for the same radius)
	 *
	 * @see {@link #WHEEL_MAX_DENSITY}
	 * @see {@link #WHEEL_MIN_DENSITY}
	 */
	private double density;

	/**
	 * Radius of the wheel
	 *
	 * @see {@link #WHEEL_MAX_RADIUS}
	 * @see {@link #WHEEL_MIN_RADIUS}
	 */
	private double radius;

	/**
	 * The mount point for the wheel on the car chassis, as a vertex number
	 */
	private int vertex;

	/**
	 * Generates a random wheel
	 */
	public Wheel() {
		density = Math.random() * WHEEL_MAX_DENSITY + WHEEL_MIN_DENSITY;
		radius = Math.random() * WHEEL_MAX_RADIUS + WHEEL_MIN_RADIUS;
		vertex = (int) Math.floor(Math.random() * 8) % 8;
	}

	/**
	 * Generates a wheel with the specified attributes
	 *
	 * @param density
	 * @param radius
	 * @param vertex
	 */
	public Wheel(double density, double radius, int vertex) {
		this.density = density;
		this.radius = radius;
		this.vertex = vertex;
	}

	/**
	 * This method is available so during reproduction, the parent car
	 * can have it's wheel's mutate
	 *
	 * @see {@link Car#mutate(float)}
	 */
	public void mutate(float mutateFactor) {
		if (Math.random() < mutateFactor)
			density = Math.random() * WHEEL_MAX_DENSITY + WHEEL_MIN_DENSITY;
		if (Math.random() < mutateFactor)
			radius = Math.random() * WHEEL_MAX_RADIUS + WHEEL_MIN_RADIUS;
		if (Math.random() < mutateFactor)
			vertex = (int) Math.floor(Math.random() * 8) % 8;
	}

	/**
	 * @return the wheel's density attribute.
	 */
	public double getDensity() {
		return density;
	}

	/**
	 * @return the wheel's radius attribute.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @return the wheel's mount vertex
	 */
	public int getVertex() {
		return vertex;
	}
}