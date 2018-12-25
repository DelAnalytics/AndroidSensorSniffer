package io.delanalytics.androidsensorsniffer;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Rotation extends JobService {
    private SensorManager mSensorManager;
    private Rotation.RotationSniff mRotation;
    private static final String TAG = "rotation_sniffer";
    private int records_added;
    BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
    String deviceName = myDevice.getName();


    @Override
    public boolean onStartJob(JobParameters params) {
        records_added = 0;
        Log.i(TAG, "Started Collecting Rotation Data");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotation = new RotationSniff();
        mRotation.start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mRotation.stop();
        return false;
    }

    class RotationSniff implements SensorEventListener {
        private Sensor mRotation;
        protected FirebaseFirestore db;

        public RotationSniff() {
            mRotation = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ROTATION_VECTOR);
        }

        public void start() {
            db = FirebaseFirestore.getInstance();
            mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);

        }

        public Map<String, Object> createDataObject(float[] data) {
            Map<String, Object> rot = new HashMap<>();
            rot.put("x*sin(θ/2)", data[0]);
            rot.put("y*sin(θ/2) ", data[1]);
            rot.put("z*sin(θ/2) ", data[2]);
            rot.put("cos(θ/2)", data[3]);
            rot.put("Est_heading", data[4]);
            rot.put("device_id", deviceName.hashCode());
            rot.put("EventTs", new Date().toString());
            return rot;
        }

        public void stop() {
            // make sure to turn our sensor off when the activity is paused
            mSensorManager.unregisterListener(this);
        }

        public void onSensorChanged(SensorEvent event) {
            Map<String, Object> rot = createDataObject(event.values);
            db.collection("rotation").add(rot);
            records_added += 1;
            if (records_added >= R.integer.NUMBER_OF_RECORDS) {
                stop();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

}