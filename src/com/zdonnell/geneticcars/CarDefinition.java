package com.zdonnell.geneticcars;

import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Zach
 */
public class CarDefinition {

	private final static int  ATTRIBUTE_COUNT = 14;

	private final static float CHASSIS_MAX_AXIS = 1.1f;
    private final static float CHASSIS_MIN_AXIS = 0.1f;

    /**
     * Represents the raw values for the chassis bounds.
     */
    private Vector2[] bodySegments;

    /**
     * Represents the raw values for the wheel definitions.
     */
    private Wheel[] wheels;

    /**
     * Creates a car with random attributes.
     */
    public CarDefinition() {
        generateRandomAttributes();
    }

    /**
     * Creates a new car based on the provided raw data
     *
     * @param bodySegments
     * @param wheels
     */
    public CarDefinition(Vector2[] bodySegments, Wheel[] wheels) {
        this.bodySegments = bodySegments;
        this.wheels = wheels;
    }

    /**
     * Generates random attributes for the car's chassis
     * and it's wheels.
     */
    private void generateRandomAttributes() {
        // Create 2 random wheels
        wheels = new Wheel[2];
        for (int i = 0; i < 2; i++) {
            wheels[i] = new Wheel();
        }
        // Create 8 random body segments
		bodySegments = new Vector2[8];
		bodySegments[0] = new Vector2((float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS, 0f);
		bodySegments[1] = new Vector2((float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS, (float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS);
		bodySegments[2] = new Vector2(0f, (float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS);
		bodySegments[3] = new Vector2((float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS, (float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS);
		bodySegments[4] = new Vector2((float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS, 0f);
		bodySegments[5] = new Vector2((float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS, (float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS);
		bodySegments[6] = new Vector2(0f, (float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS);
		bodySegments[7] = new Vector2((float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS, (float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS);
    }

    /**
     * Returns the raw values for the chassis bounds.
     */
    public Vector2[] getBodySegments() {
        return bodySegments;
    }

	/**
	 * Returns the raw values for the wheels
	 */
	public Wheel[] getWheels() {
		return wheels;
	}

	/**
	 * Mimics genetic chromosome crossover found in
	 * sexual reproduction.<br><br>
	 *
	 * This method picks a point in the figurative list of
	 * attributes to alternate which parent attributes it uses for the
	 * child.<br><br>
	 *
	 * For example, if the crossover point is 8 all attributes before 8 will
	 * be pulled from parent one, the remainder will be pulled from parent two.<br><br>
	 *
	 * For our case the attributes look like this
	 * <ol>
	 *     <li>Body Segment 1 (Vector2)</li>
	 *     <li>Body Segment 2 (Vector2)</li>
	 *     <li>Body Segment 3 (Vector2)</li>
	 *     <li>Body Segment 4 (Vector2)</li>
	 *     <li>Body Segment 5 (Vector2)</li>
	 *     <li>Body Segment 6 (Vector2)</li>
	 *     <li>Body Segment 7 (Vector2)</li>
	 *     <li>Body Segment 8 (Vector2)</li>
	 *     <li>Wheel 1 Density</li>
	 *     <li>Wheel 1 Radius</li>
	 *     <li>Wheel 1 Vertex</li>
	 *     <li>Wheel 2 Density</li>
	 *     <li>Wheel 2 Radius</li>
	 *     <li>Wheel 2 Vertex</li>
	 * </ol>
	 *
	 * @param p1 the first parent used in reproduction
	 * @param p2 the second parent used in reproduction
	 * @return the new born child
	 */
	public static CarDefinition geneticCrossover(CarDefinition p1, CarDefinition p2) {
		int split = (int) (Math.random() * ATTRIBUTE_COUNT);

		// Select body segments from parents
		Vector2[] childBodySegs = new Vector2[8];
		int segNum = 0;
		for (;segNum < 8; segNum++) {
			childBodySegs[segNum] = new Vector2();
			childBodySegs[segNum].set(p1.getBodySegments()[segNum].cpy());
		}

		// Select wheel attributes from parents
		Wheel[] wheels = new Wheel[2];
		for (int i = 0; i < wheels.length; i++) {
			double w1Den = (segNum + (i * 3 + 1) < split) ? p1.wheels[i].getDensity() : p2.wheels[i].getDensity();
			double w1Rad = (segNum + (i * 3 + 2) < split) ? p1.wheels[i].getRadius() : p2.wheels[i].getRadius();
			int w1Vert = (segNum + (i * 3 + 3) < split) ? p1.wheels[i].getVertex() : p2.wheels[i].getVertex();
			wheels[i] = new Wheel(w1Den, w1Rad, w1Vert);
		}

		return new CarDefinition(childBodySegs, wheels);
	}

	/**
	 * This applies mutations to each attribute (for the sake of this
	 * program, a mutation is a new random value for that attribute) with a probability
	 * of that provided.<br><br>
	 *
	 * For example, providing a mutation factor of 0.05 (5%) would give each
	 * attribute a 5% chance of being randomized.
	 *
	 * @param mutateFactor the mutation factor
	 */
	public void mutate(float mutateFactor) {
		// check for body/chassis mutations
		if (Math.random() < mutateFactor)
			bodySegments[0] = new Vector2((float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS, 0f);
		if (Math.random() < mutateFactor)
			bodySegments[1] = new Vector2((float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS, (float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS);
		if (Math.random() < mutateFactor)
			bodySegments[2] = new Vector2(0f, (float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS);
		if (Math.random() < mutateFactor)
			bodySegments[3] = new Vector2((float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS, (float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS);
		if (Math.random() < mutateFactor)
			bodySegments[4] = new Vector2((float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS, 0f);
		if (Math.random() < mutateFactor)
			bodySegments[5] = new Vector2((float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS, (float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS);
		if (Math.random() < mutateFactor)
			bodySegments[6] = new Vector2(0f, (float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS);
		if (Math.random() < mutateFactor)
			bodySegments[7] = new Vector2((float) Math.random() * CHASSIS_MAX_AXIS + CHASSIS_MIN_AXIS, (float) -Math.random() * CHASSIS_MAX_AXIS - CHASSIS_MIN_AXIS);

		// check for wheel mutations
		for (int i = 0; i < 2; i++) {
			wheels[i].mutate(mutateFactor);
		}
	}
}