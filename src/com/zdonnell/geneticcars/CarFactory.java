package com.zdonnell.geneticcars;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import java.util.Arrays;

/**
 * This class houses the car building methods.  This primarily involves creating the box2d physics
 * items from the specified {@link CarDefinition}.  There is no need to instantiate this class
 * as all methods are static.
 *
 * @author Zach
 */
public class CarFactory {

	/**
	 * How fast the wheels will rotate
	 */
	public static final int MOTOR_SPEED = 20;

	/**
	 * Given a provided CarDefinition, this function builds a fully
	 * assembled Car, and places it in the physics world.
	 *
	 * @param definition the Definition to build the car from
	 * @param world      the world to build the car in
	 * @return a Car reference containing the physics body and the used
	 *         car definition
	 */
	public static Car buildCar(CarDefinition definition, World world, boolean isElite) {
		// Create the box2d physics body for the chassis
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(0.0f, 0.5f);

		Body body = world.createBody(bodyDef);

		// Build the car chassis from the eight body segment vertices
		// Eight individual pieces are needed because box2d does not support
		// concave shapes, thus 8 convex triangles are used to build the chassis.
		Vector2[] vertices = definition.getBodySegments();
		for (int i = 0; i < vertices.length; i++) {
			attachChassisPiece(body, vertices[i], vertices[(i + 1) % vertices.length]);
		}

		// build the wheels
		Body[] wheelBodies = new Body[2];
		wheelBodies[0] = buildWheel(world, definition.getWheels()[0]);
		wheelBodies[1] = buildWheel(world, definition.getWheels()[1]);

		attachWheels(world, body, vertices, wheelBodies, definition.getWheels());

		return new Car(definition, body, wheelBodies, isElite);
	}

	/**
	 * Using the definition of the provided car, an exact clone will be made
	 *
	 * @param elite the car to clone
	 * @param world the world to build the car in
	 * @return the fully assembled car
	 */
	public static Car buildClone(Car elite, World world) {
		Vector2[] bodySegCopy = Arrays.copyOfRange(elite.getCarDefinition().getBodySegments(), 0, 8);
		Wheel[] wheelsCopy = Arrays.copyOfRange(elite.getCarDefinition().getWheels(), 0, 2);

		return buildCar(new CarDefinition(bodySegCopy, wheelsCopy), world, true);
	}

	/**
	 * Makes a baby car by combining the "dna" of the two parent cars and then performing a
	 * chance based mutation.
	 *
	 * @param parent1 the first Parent
	 * @param parent2 the Second Parent
	 * @param world   the world to bring this baby into
	 * @return The built car.
	 */
	public static Car buildBabyCar(Car parent1, Car parent2, World world) {
		CarDefinition babyDefinition = CarDefinition.geneticCrossover(parent1.getCarDefinition(), parent2.getCarDefinition());
		babyDefinition.mutate(0.05f);

		return buildCar(babyDefinition, world, false);
	}

	/**
	 * Builds a chassis piece for a car.  Due to how box2d works, we are
	 * basically creating a series of triangles for the car.  Each chassis piece
	 * created here is one triangle on the car body.  The two vertices passed, along
	 * with the center point (0, 0) of the chassis will create the triangle.
	 *
	 * @param body the body to build the chassis pieces onto
	 * @param v1 the first vertex to use on the triangle chassis piece
	 * @param v2 the second vertex to use on the triangle chassis piece
	 */
	private static void attachChassisPiece(Body body, Vector2 v1, Vector2 v2) {
		Vector2[] pieceVertexes = new Vector2[3];
		pieceVertexes[0] = v1;
		pieceVertexes[1] = v2;
		pieceVertexes[2] = new Vector2(0f, 0f);

		FixtureDef fixDef = new FixtureDef();
		PolygonShape pieceShape = new PolygonShape();
		pieceShape.set(pieceVertexes);
		fixDef.shape = pieceShape;
		fixDef.density = 80;
		fixDef.friction = 10;
		fixDef.restitution = 0f;
		fixDef.filter.groupIndex = -1;

		body.createFixture(fixDef);
	}

	/**
	 * Creates the wheel body in the provided world.
	 *
	 * @param world the world that the wheel is to be created in
	 * @param wheel the definition to create the wheel from
	 * @return The created box2d body object representing the wheel
	 */
	private static Body buildWheel(World world, Wheel wheel) {
		// Build the box2d physics body for the wheel
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		Body wheelBody = world.createBody(bodyDef);

		// Create the circle fixture representing the wheel shape
		FixtureDef fixDef = new FixtureDef();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius((float) wheel.getRadius());
		fixDef.shape = circleShape;
		fixDef.density = (float) wheel.getDensity();
		fixDef.friction = 1;
		fixDef.restitution = 0f;
		fixDef.filter.groupIndex = -1;

		wheelBody.createFixture(fixDef);

		return wheelBody;
	}

	/**
	 * Attaches the provided wheels to the specified chassis using
	 * motorized joints.
	 *
	 * @param world       the world in which the assembly takes place
	 * @param chassis     the chassis of the car to mount the wheels to
	 * @param vertexes    the array of vertexes used to assemble the car
	 * @param wheelBodies the box2d wheel body objects to mount to the car
	 * @param wheelDefs   the definitions used to create the wheel bodies
	 */
	private static void attachWheels(World world, Body chassis, Vector2[] vertexes, Body[] wheelBodies, Wheel[] wheelDefs) {
		// Calculate the total mass of the chassis and the wheels
		double totalMass = chassis.getMass();
		for (Body wheelBody : wheelBodies)
			totalMass += wheelBody.getMass();

		// For each wheel provided create a motorized joint linking the wheel and the chassis
		for (int i = 0; i < wheelBodies.length; i++) {
			RevoluteJointDef jointDef = new RevoluteJointDef();
			Vector2 vertex = vertexes[wheelDefs[i].getVertex()];
			jointDef.localAnchorA.set(vertex.x, vertex.y);
			jointDef.localAnchorB.set(0, 0);

			jointDef.maxMotorTorque = (float) (totalMass * -world.getGravity().y / wheelDefs[i].getRadius());
			jointDef.motorSpeed = -MOTOR_SPEED;
			jointDef.enableMotor = true;
			jointDef.bodyA = chassis;
			jointDef.bodyB = wheelBodies[i];

			world.createJoint(jointDef);
		}
	}
}
