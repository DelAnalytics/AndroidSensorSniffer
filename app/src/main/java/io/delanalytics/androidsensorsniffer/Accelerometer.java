package io.delanalytics.androidsensorsniffer;
import android.bluetooth.BluetoothAdapter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Accelerometer extends JobService {
    private SensorManager mSensorManager;
    private Accelerometer.AccelerometerSniff mAccelerometer;
    private static final String TAG = "accelerometer_sniffer";
    private int records_added;
    BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
    String deviceName = myDevice.getName();

    @Override
    public boolean onStartJob(JobParameters job) {
        records_added = 0;
        Log.i(TAG, "Started Collecting accelerometer Data");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = new AccelerometerSniff();
        mAccelerometer.start();
        return false;

    }

    @Override
    public  boolean onStopJob(JobParameters job){
        mAccelerometer.stop();
        return false;
    }


    class AccelerometerSniff implements SensorEventListener {
        private Sensor mAccelerometer;
        protected FirebaseFirestore db;

        public AccelerometerSniff() {
            mAccelerometer = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER);
        }

        public void start() {
            db = FirebaseFirestore.getInstance();
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }

        public void stop() {
            // make sure to turn our sensor registration off when the activity is paused
            Log.i(TAG,"I am unregistered");
            mSensorManager.unregisterListener(this);
        }

        public Map<String, Object> createDataObject(float[] data) {
            Map<String, Object> acc = new HashMap<>();
            acc.put("Gx", data[0]);
            acc.put("Gy", data[1]);
            acc.put("Gz", data[2]);
            acc.put("device_id", myDevice.hashCode());
            acc.put("EventTs", new Date().toString());
            return acc;
        }

        public void onSensorChanged(SensorEvent event) {
            Map<String, Object> accc = createDataObject(event.values);
            db.collection("accelerometer").add(accc);
            records_added += 1;
            if (records_added >= R.integer.NUMBER_OF_RECORDS){
                stop();
                }
            }


        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }


}