package io.delanalytics.androidsensorsniffer;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;

import java.util.concurrent.TimeUnit;

public class SensorDashboard extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("I have fired!");
        Intent intent = new Intent(this, Rotation.class);
        startService(intent);

    }
}