package io.delanalytics.androidsensorsniffer;
import android.bluetooth.BluetoothAdapter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Gyroscope extends JobService {
    private SensorManager mSensorManager;
    private Gyroscope.GyroscopeSniff mGyroscope;
    private static final String TAG = "gyroscope_sniffer";
    private int records_added;


    @Override
    public boolean onStartJob(JobParameters params) {
        records_added = 0;
        Log.i(TAG, "Started Collecting Gyroscope Data");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGyroscope = new GyroscopeSniff();
        mGyroscope.start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mGyroscope.stop();
        return false;
    }


    class GyroscopeSniff implements SensorEventListener {
        private Sensor mGyroscope;
        protected FirebaseFirestore db;
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();

        public GyroscopeSniff() {
            mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        public void start() {
            // enable our sensor when the activity is resumed, ask for
            // 10 ms updates.
            db = FirebaseFirestore.getInstance();
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);


        }

        public Map<String, Object> createDataObject(float[] data) {
            Map<String, Object> gyr = new HashMap<>();
            gyr.put("Gx", data[0]);
            gyr.put("Gy", data[1]);
            gyr.put("Gz", data[2]);
            gyr.put("device_id", deviceName.hashCode());
            gyr.put("EventTs", new Date().toString());
            return gyr;
        }

        public void stop() {
            // make sure to turn our sensor off when the activity is paused
            mSensorManager.unregisterListener(this);
        }

        public void onSensorChanged(SensorEvent event) {
            Map<String, Object> gyro = createDataObject(event.values);
            db.collection("gyroscope").add(gyro);
            records_added += 1;
            if (records_added >= R.integer.NUMBER_OF_RECORDS) {
                stop();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }


}