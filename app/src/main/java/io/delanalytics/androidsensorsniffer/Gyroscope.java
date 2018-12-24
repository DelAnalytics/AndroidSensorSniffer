package io.delanalytics.androidsensorsniffer;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class Gyroscope extends IntentService {
    private SensorManager mSensorManager;
    private Gyroscope.GyroscopeSniff mGyroscope;
    private static final String TAG = "gyroscope_sniffer";

    public Gyroscope() {
        super("Gyroscope");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Started Collecting Data");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGyroscope =new GyroscopeSniff();
        mGyroscope.start();

    }


    class GyroscopeSniff implements SensorEventListener {
        private Sensor mGyroscope;


        public GyroscopeSniff() {
            mGyroscope = mSensorManager.getDefaultSensor( Sensor.TYPE_GYROSCOPE);
        }

        public void start() {
            // enable our sensor when the activity is resumed, ask for
            // 10 ms updates.
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        }

        public void stop() {
            // make sure to turn our sensor off when the activity is paused
            mSensorManager.unregisterListener(this);
        }

        public void onSensorChanged(SensorEvent event) {
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            String sensor = event.sensor.getName();
            Log.i(TAG, "Sensor is triggered " + sensor);
            switch (sensor) {
                case "K6DS3TR Gyroscope":
                    System.out.println("This is the Gyro  worker");
            }

        }


        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            System.out.println("I am triggered");
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}