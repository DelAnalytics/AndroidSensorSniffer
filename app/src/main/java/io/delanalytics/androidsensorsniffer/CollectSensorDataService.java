package io.delanalytics.androidsensorsniffer;
import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class CollectSensorDataService extends IntentService {
    private SensorManager mSensorManager;
    private CollectSensorDataService.SensorDataCollection mSensorDataCollection;
    private static final String TAG = "androidsensorsniffer";

    public CollectSensorDataService() {
        super("CollectSensorDataService");
    }
    protected class SenorData{
        int accelleromter = 0;
    }

    public SenorData senorData = new SenorData();
    @Override
    protected void onHandleIntent(Intent intent){
        Log.i(TAG, "Started Collecting Data");
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensorDataCollection =new SensorDataCollection();
        mSensorDataCollection.start();

    }


    class SensorDataCollection implements SensorEventListener {
        private Sensor mRotationVectorSensor;
        private Sensor mAccelerometer;
        private Sensor mGyroscopic;
        private Sensor mAmbientTemp;
        private Sensor mAmbientLight;
        private Sensor mPoximity;




        public SensorDataCollection() {
            // find the rotation-vector sensor
            mRotationVectorSensor = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ROTATION_VECTOR);
            mAccelerometer = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER);
            mAmbientLight = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_LIGHT);
            mGyroscopic = mSensorManager.getDefaultSensor( Sensor.TYPE_GYROSCOPE);
            mAmbientTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            mPoximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        }
        public void start() {
            // enable our sensor when the activity is resumed, ask for
            // 10 ms updates.
            mSensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this,mAmbientTemp, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAmbientLight, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mPoximity,SensorManager.SENSOR_DELAY_NORMAL);

        }
        public void stop() {
            // make sure to turn our sensor off when the activity is paused
            mSensorManager.unregisterListener(this);
        }
        public void onSensorChanged(SensorEvent event) {
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            String sensor = event.sensor.getName();
            Log.i(TAG,"Sensor is triggered " + sensor);
            switch (sensor){
                case  "K6DS3TR Accelerometer" :  System.out.println(event.values);
            }

        }


        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            System.out.println("I am triggered");
        }

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;}

}