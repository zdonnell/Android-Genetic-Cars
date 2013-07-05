package com.zdonnell.geneticcars;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * Created by zach on 7/3/13.
 */
public class CarFactory {

	public static final int MOTOR_SPEED = 20;

	/**
	 * Given a provided CarDefinition, this function builds a fully
	 * assembled Car, and places it in the physics world.
	 *
	 * @param definition
	 * @param world
	 * @return a Car reference containing the physics body and the used
	 * car definition
	 */
	public static Car buildCar(CarDefinition definition, World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(0.0f, 2f);

		Body body = world.createBody(bodyDef);

		Vector2[] vertexes = definition.getBodySegments();
		for (int i = 0; i < vertexes.length; i++) {
			attachChassisPiece(body, vertexes[i], vertexes[(i + 1) % vertexes.length]);
		}

		Body[] wheelBodies = new Body[2];
		wheelBodies[0] = buildWheel(world, definition.getWheels()[0]);
		wheelBodies[1] = buildWheel(world, definition.getWheels()[1]);

		attachWheels(world, body, vertexes, wheelBodies, definition.getWheels());

		return new Car(definition, body, wheelBodies);
	}

	/**
	 * Builds a chassis piece for a car.  Due to how box2d works, we are
	 * basically creating a series of triangles for the car.  Each chassis piece
	 * created here is one triangle on the car body.
	 *
	 * @param body the body to build the chassis pieces onto
	 * @param v1
	 * @param v2
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
		fixDef.restitution = 0.2f;
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
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		Body wheelBody = world.createBody(bodyDef);

		FixtureDef fixDef = new FixtureDef();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius((float) wheel.getRadius());
		fixDef.shape = circleShape;
		fixDef.density = (float) wheel.getDensity();
		fixDef.friction = 1;
		fixDef.restitution = 0.2f;
		fixDef.filter.groupIndex = -1;

		wheelBody.createFixture(fixDef);

		return wheelBody;
	}

	/**
	 * Attaches the provided wheels to the specified chassis using
	 * motorized joints.
	 *
	 * @param world the world in which the assembly takes place
	 * @param chassis the chassis of the car to mount the wheels to
	 * @param vertexes the array of vertexes used to assemble the car
	 * @param wheelBodies the box2d wheel body objects to mount to the car
	 * @param wheelDefs the definitions used to create the wheel bodies
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
