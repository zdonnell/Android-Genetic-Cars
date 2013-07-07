package com.zdonnell.geneticcars;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.List;

/**
 * This class handles the rendering of the cars and terrain using a specified ShapeRenderer
 *
 * Created by zach on 7/4/13.
 */
public class Renderer {

	/**
	 * The shape renderer that will be used to actually render the car wheels and
	 * body segments.
	 */
	private ShapeRenderer shapeRenderer;

	/**
	 * Constructor
	 *
	 * @param shapeRenderer an instantiated {@link ShapeRenderer} to use.
	 */
	public Renderer(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
	}

	/**
	 * If the camera is modified we need to let the shapeRenderer know, so the translation
	 * from box2d coordinates to screen coordinates remains correct.
	 *
	 * @param matrix
	 */
	public void setProjectionMatrix(Matrix4 matrix) {
		shapeRenderer.setProjectionMatrix(matrix);
	}

	/**
	 * Draws all the cars in the List provided.
	 *
	 * @param cars {@link List} of cars
	 */
	public void renderCars(List<Car> cars) {
		for (Car car : cars) {

			// Calculate an alpha value that represents how much longer the car has before it is killed.
			float lifeLeft = (float) (System.currentTimeMillis() - car.timeLastMoved) / 5000f;
			float alpha = lifeLeft / 1f;

			// draw wheels
			for (Body wheel : car.getWheels()) {
				Vector2 pos = wheel.getPosition();

				// Figure out the color of the wheel based on it's density
				float wheelDensityRange = Wheel.WHEEL_MAX_DENSITY - Wheel.WHEEL_MIN_DENSITY;
				float densityRatio = (wheel.getMass() - Wheel.WHEEL_MIN_DENSITY) / wheelDensityRange;
				densityRatio = 1f - densityRatio;

				// Draw the solid wheel color
				shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);
				shapeRenderer.setColor(densityRatio, densityRatio, densityRatio, 1f - alpha);
				shapeRenderer.filledCircle(pos.x, pos.y, wheel.getFixtureList().get(0).getShape().getRadius(), 30);
				shapeRenderer.end();

				// Draw the wheel outline
				shapeRenderer.begin(ShapeRenderer.ShapeType.Circle);
				shapeRenderer.setColor(0, 0, 0, 1f - alpha);
				shapeRenderer.circle(pos.x, pos.y, wheel.getFixtureList().get(0).getShape().getRadius(), 30);
				shapeRenderer.end();
			}

			// Draw the chassis pieces (triangles)
			Vector2[] v = new Vector2[3];
			for (Fixture chassisPart : car.getChassis().getFixtureList()) {
				PolygonShape chassisPieceShape = (PolygonShape) chassisPart.getShape();

				// Get the vertices for this chassis piece (triangle)
				for (int i = 0; i < 3; i++) {
					v[i] = new Vector2();
					chassisPieceShape.getVertex(i, v[i]);
					car.getChassis().getTransform().mul(v[i]);
				}

				// Draw the light red solid color
				shapeRenderer.begin(ShapeRenderer.ShapeType.FilledTriangle);
				shapeRenderer.setColor(car.isElite ? 0.6f : 0.9f, 0.6f, car.isElite ? 0.9f : 0.6f, 1f - alpha);
				shapeRenderer.filledTriangle(v[0].x, v[0].y, v[1].x, v[1].y, v[2].x, v[2].y);
				shapeRenderer.end();

				// Draw the darker red triangle outlines
				shapeRenderer.begin(ShapeRenderer.ShapeType.Triangle);
				shapeRenderer.setColor(car.isElite ? 0.1f : 0.9f, 0.1f, car.isElite ? 0.9f : 0.1f, 1f - alpha);
				shapeRenderer.triangle(v[0].x, v[0].y, v[1].x, v[1].y, v[2].x, v[2].y);
				shapeRenderer.end();
			}
		}
	}

	/**
	 * Uses the shape renderer to draw all tiles in the List
	 *
	 * @param tiles the {@link List} of tiles to render
	 */
	public void renderTiles(List<Body> tiles) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.DARK_GRAY);

		// Setup some reusable Vector2s
		Vector2[] verts = new Vector2[4];
		for (int i = 0; i < verts.length; i++)
			verts[i] = new Vector2();
		Vector2 lv = new Vector2();
		Vector2 f = new Vector2();

		for (Body tile : tiles) {
			PolygonShape tileShape = (PolygonShape) tile.getFixtureList().get(0).getShape();

			// Get the vertices for the shape.
			for (int i = 0; i < verts.length; i++) {
				tileShape.getVertex(i, verts[i]);
				tile.getTransform().mul(verts[i]);
			}

			// Set the starting vertices to draw the lines from
			lv.set(verts[0]);
			f.set(verts[0]);

			// For each of the vertices draw a line connecting it to the previous
			for (int i = 1; i < verts.length; i++) {
				Vector2 v = verts[i];
				shapeRenderer.line(lv.x, lv.y, v.x, v.y);
				lv.set(v);
			}
			// Finally draw the last line, connecting back to the first vertex
			shapeRenderer.line(f.x, f.y, lv.x, lv.y);
		}
		shapeRenderer.end();
	}
}
