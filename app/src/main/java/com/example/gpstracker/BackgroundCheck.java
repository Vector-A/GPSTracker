package com.example.gpstracker;



import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class BackgroundCheck extends Service {
    private static final String TAG = "BackgroundCheck";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean signal = intent.getBooleanExtra("signal",Boolean.parseBoolean(""));
        if (signal) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);

            // Play the notification sound
            if (ringtone != null) {
                ringtone.play();
            }
            Toast.makeText(this, "You are out of set range", Toast.LENGTH_SHORT).show();
        }
        return START_NOT_STICKY; // Service will stop when done
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service Destroyed");
    }


}
