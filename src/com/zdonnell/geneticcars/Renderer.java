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
 * Created by zach on 7/4/13.
 */
public class Renderer {

	private ShapeRenderer shapeRenderer;

	public Renderer(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
	}

	public void setProjectionMatrix(Matrix4 matrix) {
		shapeRenderer.setProjectionMatrix(matrix);
	}

	public void renderCars(List<Car> cars) {
		for (Car car : cars) {
			float lifeLeft = (float) (System.currentTimeMillis() - car.timeLastMoved) / 5000f;
			float alpha = lifeLeft / 1f;
			// draw wheels
			for (Body wheel : car.getWheels()) {
				Vector2 pos = wheel.getPosition();

				float wheelDensityRange = Wheel.WHEEL_MAX_DENSITY - Wheel.WHEEL_MIN_DENSITY;
				float densityRatio = (wheel.getMass() - Wheel.WHEEL_MIN_DENSITY) / wheelDensityRange;
				densityRatio = 1f - densityRatio;

				shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);
				shapeRenderer.setColor(densityRatio, densityRatio, densityRatio, 1f - alpha);
				shapeRenderer.filledCircle(pos.x, pos.y, wheel.getFixtureList().get(0).getShape().getRadius(), 30);
				shapeRenderer.end();

				shapeRenderer.begin(ShapeRenderer.ShapeType.Circle);
				shapeRenderer.setColor(0, 0, 0, 1f - alpha);
				shapeRenderer.circle(pos.x, pos.y, wheel.getFixtureList().get(0).getShape().getRadius(), 30);
				shapeRenderer.end();
			}


			// draw chassis
            Vector2 v1 = new Vector2();
            Vector2 v2 = new Vector2();
            Vector2 v3 = new Vector2();

            for (Fixture chassisPart : car.getChassis().getFixtureList()) {
				PolygonShape chassisPieceShape = (PolygonShape) chassisPart.getShape();

                chassisPieceShape.getVertex(0, v1);
				chassisPieceShape.getVertex(1, v2);
				chassisPieceShape.getVertex(2, v3);
				car.getChassis().getTransform().mul(v1);
				car.getChassis().getTransform().mul(v2);
				car.getChassis().getTransform().mul(v3);

				shapeRenderer.begin(ShapeRenderer.ShapeType.FilledTriangle);
				shapeRenderer.setColor(car.isElite? 0.6f : 0.9f, 0.6f, car.isElite? 0.9f : 0.6f, 1f - alpha);
				shapeRenderer.filledTriangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
				shapeRenderer.end();

				shapeRenderer.begin(ShapeRenderer.ShapeType.Triangle);
				shapeRenderer.setColor(car.isElite? 0.1f : 0.9f, 0.1f, car.isElite? 0.9f : 0.1f, 1f - alpha);
				shapeRenderer.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
				shapeRenderer.end();
			}
        }
	}

    /**
     * Uses the shape renderer to draw all tiles in the List
     *
     * @param tiles
     */
	public void renderTiles(List<Body> tiles) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.DARK_GRAY);

        // Setup some reusable Vector2s
        Vector2[] verts = new Vector2[4];
        for (int i = 0; i < verts.length; i++)
            verts[i] = new Vector2();
        Vector2 lv = new Vector2(), f = new Vector2();

        for (Body tile : tiles) {
			PolygonShape tileShape = (PolygonShape) tile.getFixtureList().get(0).getShape();

			for (int i = 0; i < verts.length; i++) {
				tileShape.getVertex(i, verts[i]);
				tile.getTransform().mul(verts[i]);
			}

			lv.set(verts[0]);
			f.set(verts[0]);
			for (int i = 1; i < verts.length; i++) {
				Vector2 v = verts[i];

				shapeRenderer.line(lv.x, lv.y, v.x, v.y);
				lv.set(v);
			}
			shapeRenderer.line(f.x, f.y, lv.x, lv.y);
		}
		shapeRenderer.end();
	}
}
