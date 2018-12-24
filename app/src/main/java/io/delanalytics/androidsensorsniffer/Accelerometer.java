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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Accelerometer extends IntentService {
    private SensorManager mSensorManager;
    private Accelerometer.AccelerometerSniff mAccelerometer;
    private static final String TAG = "accelerometer_sniffer";

    public Accelerometer() {
        super("Accelerometer");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Started Collecting Data");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer =new AccelerometerSniff();
        mAccelerometer.start();

    }



    class AccelerometerSniff implements SensorEventListener {
        private Sensor mAccelerometer;
        protected FirebaseFirestore db;

        public AccelerometerSniff() {
            mAccelerometer = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER);
        }

        public void start() {
            // enable our sensor when the activity is resumed, ask for
            // 10 ms updates.

            db = FirebaseFirestore.getInstance();
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }

        public void stop() {
            // make sure to turn our sensor off when the activity is paused
            mSensorManager.unregisterListener(this);
        }

        public Map<String, Object> createDataObject( float[] data){
            Map<String, Object> acc = new HashMap<>();
            acc.put("Gx", data[0]);
            acc.put("Gy",data[1]);
            acc.put("Gz", data[2]);
            acc.put("device_id", "test");
            acc.put("EventTs", new Date().toString());
            return acc;
        }
        public void onSensorChanged(SensorEvent event) {
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            String sensor = event.sensor.getName();
            Log.i(TAG, "Sensor is triggered " + sensor);
            switch (sensor) {
                case "K6DS3TR Accelerometer":
                    Log.d(TAG, "i AM GDSLKJGH");
                    Map<String, Object> accc = createDataObject(event.values);
                    db.collection("accelerometer").add(accc);

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