package com.zdonnell.geneticcars;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/**
 * Main Android Activity used to run the simulation
 *
 * @author Zach
 */
public class MainActivity extends AndroidApplication {

	/**
	 * Basic configuration for the Simulation.
	 */
	AndroidApplicationConfiguration cfg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = false;
		cfg.useCompass = false;

		final Simulation sim = new Simulation();
		final View simulationView = initializeForView(sim, cfg);

		setContentView(simulationView);
		sim.setParentView(simulationView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.reset:
				// On "reset" just load in a new simulation
				final Simulation sim = new Simulation();
				final View simulationView = initializeForView(sim, cfg);
				setContentView(simulationView);
				sim.setParentView(simulationView);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}