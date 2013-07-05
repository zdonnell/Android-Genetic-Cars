package com.zdonnell.geneticcars;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
		cfg.numSamples = 4;

        final Simulation sim = new Simulation();
        final View simulationView = initializeForView(sim, cfg);

        setContentView(simulationView);
        simulationView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                float aspect = (float) v.getWidth() / (float) v.getHeight();
                sim.setAspect(aspect);
            }
        });
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Restart");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case 0:
				// do whatever
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}