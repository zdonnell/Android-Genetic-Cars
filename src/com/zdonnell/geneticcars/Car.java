package com.zdonnell.geneticcars;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
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

    private Mesh bodyMesh;


	public float maxDistance = 0.0f;

	public long timeLastMoved;

    /**
     * Creates a car from the provided definition.
     */
    public Car(CarDefinition carDefinition, Body chassis, Body[] wheels) {
		this.carDefinition = carDefinition;
		this.chassis = chassis;
		this.wheels = wheels;

		timeLastMoved = System.currentTimeMillis();

        bodyMesh = new Mesh(true, 3, 3,
                   new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                   new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));

        bodyMesh.setVertices(new float[] { -0.5f, -0.5f, 0, Color.toFloatBits(255, 0, 0, 255),
                                        0.5f, -0.5f, 0, Color.toFloatBits(0, 255, 0, 255),
                                        0, 0.5f, 0, Color.toFloatBits(0, 0, 255, 255) });

        bodyMesh.setIndices(new short[] { 0, 1, 2 });
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

    public void render() {
        bodyMesh.render(GL10.GL_TRIANGLES);
    }
}