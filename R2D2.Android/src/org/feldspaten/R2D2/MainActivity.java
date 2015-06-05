package org.feldspaten.R2D2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	/** Sensor manager instance */
	private SensorManager mSensorManager;
	/** List of all device sensors, once queried */
	private final List<Sensor> deviceSensors = new ArrayList<>();

	/* GUI elements */
	private TextView tvStatus = null;
	private ListView lvSensors = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		this.tvStatus = (TextView) findViewById(R.id.tvSensors);
		this.lvSensors = (ListView) findViewById(R.id.lvSensors);

		runBackground(new Runnable() {

			@Override
			public void run() {
				MainActivity.this.querySensors();
			}
		});

		this.lvSensors
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						try {

							final Sensor sensor = deviceSensors.get(position);

							// Start activity for this sensor
							final Intent intent = new Intent(MainActivity.this,
									SensorActivity.class);
							intent.putExtra("sensor", sensor.getName());
							startActivity(intent);

						} catch (IndexOutOfBoundsException e) {
							return;
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setStatus(final int resID) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tvStatus.setText(getString(resID));
			}
		});
	}

	/**
	 * Set status message
	 * 
	 * @param msg
	 *            to be set
	 */
	public void setStatus(final CharSequence msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tvStatus.setText(msg);
			}
		});
	}

	/**
	 * Runs a task as background thread
	 * 
	 * @param runnable
	 *            to be run
	 * @return the thread that has been started
	 */
	protected final Thread runBackground(final Runnable runnable) {
		final Thread thread = new Thread(runnable);
		thread.start();
		return thread;
	}

	/**
	 * Query the sensors and list them
	 */
	protected void querySensors() {
		setStatus(R.string.sensors_loading);
		deviceSensors.clear();
		deviceSensors.addAll(mSensorManager.getSensorList(Sensor.TYPE_ALL));
		setStatus(deviceSensors.size() + " " + getString(R.string.sensors)
				+ ":");

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		for (final Sensor sensor : deviceSensors) {
			adapter.add(sensor.getName());
		}
		lvSensors.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

}
