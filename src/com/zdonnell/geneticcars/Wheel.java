package com.zdonnell.geneticcars;

public class Wheel {
	private final static int WHEEL_MAX_MASS = 100;
	private final static int WHEEL_MAX_RADIUS = 5;

	private final double mass;
	private final double radius;

	public Wheel() {
		mass = Math.random() * WHEEL_MAX_MASS;
		radius = Math.random() * WHEEL_MAX_RADIUS;
	}

	public double getMass() {
		return mass;
	}

	public double getRadius() {
		return radius;
	}
}
