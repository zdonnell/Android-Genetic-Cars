package com.zdonnell.geneticcars;

/**
 *
 * @author Zach
 */
public class Car {

    /**
     * The genetic representation of the Car's phenotypes.
     */
    private boolean[] dna;

    /**
     * The number of segments in the car's body.
     */
	private final static int BODY_SEGMENTS = 8;

    /**
     * The max length of the car's body segments
     */
	private final static int MAX_BODY_SEGMENT_LEN = 10;

    /**
     * The max number of wheels a car can have
     */
    private final static int MAX_WHEELS = 2;

    /**
     * An array the size of {@link #BODY_SEGMENTS} that stores
     * the length of each for this car.
     */
	private double[] bodySegmentLengths = new double[BODY_SEGMENTS];

    /**
     * An array the size of {@link #BODY_SEGMENTS} that stores
     * the angle of each segment. This number represents the offset from the
     * previous angle, not the total angle.
     */
	private double[] bodySegmentAngles = new double[BODY_SEGMENTS];

    /**
     * The actual number of wheels for this car.
     */
    private int wheelCount;

	private Wheel[] wheels;

    private int[] wheelMountPoints;

    /**
     * Generates a car based on the provided dna, or randomly generated one if
     * a null dna is provide.
     *
     * @param dna the dna to express for this car, can be null
     */
	public Car(boolean[] dna) {
        if (dna == null) {
            dna = randomGenome();
        }
        this.dna = dna;
        expressGenome(dna);
	}

    /**
     * Sets the cars attributes based on the specified dna
     *
     * @param dna
     */
	private void expressGenome(boolean[] dna) {

    }

    private boolean[] randomGenome() {
        return null;
    }

    public boolean[] getDna() {
        return dna;
    }

    public Car mateWith(Car mate) {
        boolean[] childDna = GeneticAlgorithms.reproduce(this.getDna(), mate.getDna());
        return new Car(childDna);
    }
}