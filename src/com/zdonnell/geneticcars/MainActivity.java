package com.zdonnell.geneticcars;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

        setContentView(initializeForView(new Simulation(), cfg));
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