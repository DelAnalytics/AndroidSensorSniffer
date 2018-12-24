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

public class Rotation extends IntentService {
    private SensorManager mSensorManager;
    private Rotation.RotationSniff mRotation;
    private static final String TAG = "rotation_sniffer";

    public Rotation() {
        super("Rotation");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Started Collecting Data");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotation =new RotationSniff();
        mRotation.start();
    }


    class RotationSniff implements SensorEventListener {
        private Sensor mRotation;
        protected FirebaseFirestore db;

        public RotationSniff() {
            mRotation = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ROTATION_VECTOR);
        }

        public void start() {
            // enable our sensor when the activity is resumed, ask for
            // 10 ms updates.
            db = FirebaseFirestore.getInstance();
            mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);

        }
        public Map<String, Object> createDataObject(float[] data){
            Map<String, Object> rot = new HashMap<>();
            rot.put("x*sin(θ/2)", data[0]);
            rot.put("y*sin(θ/2) ",data[1]);
            rot.put("z*sin(θ/2) ", data[2]);
            rot.put("cos(θ/2)", data[3]);
            rot.put("Est_heading", data[4]);
            rot.put("device_id", "test");
            rot.put("EventTs", new Date().toString());
            return rot;
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
                case "Rotation Vector":
                    Map<String, Object> rot = createDataObject(event.values);
                    db.collection("rotation").add(rot);
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