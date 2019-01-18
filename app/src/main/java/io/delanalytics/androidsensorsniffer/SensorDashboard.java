package io.delanalytics.androidsensorsniffer;
import android.app.Activity;
import android.os.Bundle;
import com.firebase.jobdispatcher.*;
import com.google.firebase.FirebaseApp;

public class SensorDashboard extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        System.out.println("I have fired!");
        Job myJob = dispatcher.newJobBuilder()
                .setService(Accelerometer.class) // the JobService that will be called
                .setTag("Accelerator")        // uniquely identifies the job
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        1,2
                ))
                .setReplaceCurrent(true)
                .build();
        dispatcher.mustSchedule(myJob);

    }
}