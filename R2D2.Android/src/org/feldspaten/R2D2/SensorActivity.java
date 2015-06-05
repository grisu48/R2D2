package org.feldspaten.R2D2;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class SensorActivity extends Activity {

	private TextView tvSensorName = null;
	private TextView tvReading = null;
	private TextView tvPower = null;
	private TextView tvResolution = null;
	private TextView tvVendor = null;

	/** Sensor manager instance */
	private SensorManager mSensorManager;
	/** Selected sensor */
	private Sensor sensor = null;

	private final SensorEventListener sensorEventListener = new SensorEventListener() {

		final AtomicBoolean writing = new AtomicBoolean(false);

		@Override
		public void onSensorChanged(final SensorEvent event) {
			if (writing.getAndSet(true))
				return;
			final String data;
			{
				final StringBuffer buffer = new StringBuffer();
				boolean first = true;
				for (final float value : event.values) {
					if (first)
						first = false;
					else
						buffer.append(", ");
					buffer.append(value);
				}
				data = buffer.toString();
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						tvReading.setText(data);
						tvPower.setText(getString(R.string.power) + ": "
								+ sensor.getPower() + " mA");
					} finally {
						writing.set(false);
					}
				}
			});
		}

		@Override
		public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);

		// Check if sensor is given
		final Intent intent = getIntent();
		final String sensorName = intent.getExtras().getString("sensor");

		this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		this.sensor = getSensor(sensorName);
		if (this.sensor == null) {
			Toast.makeText(this, R.string.no_sensor, Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		} else {

			this.tvSensorName = (TextView) findViewById(R.id.tvSensor);
			this.tvReading = (TextView) findViewById(R.id.tvCurrentReading);
			this.tvPower = (TextView) findViewById(R.id.tvSensorPower);
			this.tvResolution = (TextView) findViewById(R.id.tvSensorResolution);
			this.tvVendor = (TextView) findViewById(R.id.tvSensorVendor);

			this.tvSensorName.setText(sensor.getName());
			this.tvPower.setText(getString(R.string.power) + ": "
					+ sensor.getPower() + " mA");
			this.tvResolution.setText(getString(R.string.resolution) + ": "
					+ sensor.getResolution());
			this.tvVendor.setText(getString(R.string.vendor) + ": "
					+ sensor.getVendor());
			this.tvReading.setText(getString(R.string.waiting_data));

			startReadout();
		}

	}

	public synchronized void startReadout() {
		if (this.sensor == null)
			return;
		this.mSensorManager.registerListener(sensorEventListener, this.sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public synchronized void stopReadout() {
		this.mSensorManager.unregisterListener(sensorEventListener);
	}

	/**
	 * Get all {@link Sensor} instance from {@link SensorManager} and select the
	 * sensor that matched the given name
	 * 
	 * @param name
	 *            Name of the sensor to search for
	 * @return {@link Sensor} or null if not found
	 */
	private Sensor getSensor(final String name) {
		if (name == null || name.isEmpty())
			return null;
		for (final Sensor sensor : mSensorManager
				.getSensorList(Sensor.TYPE_ALL)) {
			if (sensor.getName().equalsIgnoreCase(name)) {
				return sensor;
			}
		}
		return null;
	}

	@Override
	protected void onStop() {
		this.stopReadout();
		super.onStop();
	}

	@Override
	protected void onPause() {
		this.stopReadout();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.startReadout();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor, menu);
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
}
