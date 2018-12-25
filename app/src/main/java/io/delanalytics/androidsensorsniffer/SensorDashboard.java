package io.delanalytics.androidsensorsniffer;

import android.app.Activity;
import android.os.Bundle;
import com.firebase.jobdispatcher.*;
import com.google.firebase.FirebaseApp;

public class SensorDashboard extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        System.out.println("I have fired!");
        Job acc = dispatcher.newJobBuilder()
                .setService(Accelerometer.class) // the JobService that will be called
                .setTag("Accelerator")        // uniquely identifies the job
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        R.integer.SNIFF_DURATION,
                        R.integer.SNIFF_SLEEP
                ))
                .setReplaceCurrent(true)
                .build();
        Job gyr = dispatcher.newJobBuilder()
                .setService(Gyroscope.class) // the JobService that will be called
                .setTag("Gyroscope")        // uniquely identifies the job
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        R.integer.SNIFF_DURATION,
                        R.integer.SNIFF_SLEEP
                ))
                .setReplaceCurrent(true)
                .build();
                Job rot = dispatcher.newJobBuilder()
                .setService(Rotation.class) // the JobService that will be called
                .setTag("Rotation")        // uniquely identifies the job
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        R.integer.SNIFF_DURATION,
                        R.integer.SNIFF_SLEEP
                ))
                .setReplaceCurrent(true)
                .build();
        dispatcher.mustSchedule(acc);
        dispatcher.mustSchedule(rot);
        dispatcher.mustSchedule(gyr);

    }
}