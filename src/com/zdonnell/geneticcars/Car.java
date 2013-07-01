package com.zdonnell.geneticcars;

public class Car {

	private final static int BODY_SEGMENTS = 8;
	private final static int BODY_SEGMENT_MAXLEN = 10;

	double[] bodySegmentLengths = new double[BODY_SEGMENTS];
	double[] bodySegmentAngles = new double[BODY_SEGMENTS];

	private Wheel[] wheels = new Wheel[2];

	public Car() {
		generateAttrs();
		generateWheels();
	}

	/**
	 * Generates random attributes for the car, up to the max bounds
	 */
	private void generateAttrs() {
		for(int i = 0; i < BODY_SEGMENTS; ++i) {
			bodySegmentLengths[i] = Math.random() * BODY_SEGMENT_MAXLEN;
			bodySegmentAngles[i] = Math.random() * (180d / BODY_SEGMENTS);
		}
	}

	/**
	 * Generates two wheels
	 */
	private void generateWheels() {
		wheels[0] = new Wheel();
		wheels[1] = new Wheel();
	}
}
