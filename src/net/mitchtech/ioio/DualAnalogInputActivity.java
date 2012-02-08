package net.mitchtech.ioio;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import net.mitchtech.ioio.dualanaloginput.R;
import android.os.Bundle;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

public class DualAnalogInputActivity extends AbstractIOIOActivity {
	private final int ANALOG_X_PIN = 34;
	private final int ANALOG_Y_PIN = 35;

	TextView mXTextView, mYTextView;

	SeekBar mXSeekBar, mYSeekBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		mXTextView = (TextView) findViewById(R.id.tvX);
		mYTextView = (TextView) findViewById(R.id.tvY);

		mXSeekBar = (SeekBar) findViewById(R.id.sbX);
		mYSeekBar = (SeekBar) findViewById(R.id.sbY);

	}

	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		private AnalogInput mXInput, mYInput;

		@Override
		public void setup() throws ConnectionLostException {
			try {
				mXInput = ioio_.openAnalogInput(ANALOG_X_PIN);
				mYInput = ioio_.openAnalogInput(ANALOG_Y_PIN);
			} catch (ConnectionLostException e) {
				throw e;
			}
		}

		@Override
		public void loop() throws ConnectionLostException {
			try {
				final float xValue = mXInput.read();
				final float yValue = mYInput.read();

				setSeekBars((int) (xValue * 100), (int) (yValue * 100));
				setText(Float.toString((xValue * 100)), Float.toString((yValue * 100)));
				sleep(10);
			} catch (InterruptedException e) {
				ioio_.disconnect();
			} catch (ConnectionLostException e) {
				throw e;
			}
		}
	}

	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}

	private void setSeekBars(final int xValue, final int yValue) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mXSeekBar.setProgress(xValue);
				mYSeekBar.setProgress(yValue);
			}
		});
	}

	private void setText(final String xText, final String yText) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mXTextView.setText(xText);
				mYTextView.setText(yText);
			}
		});
	}
}