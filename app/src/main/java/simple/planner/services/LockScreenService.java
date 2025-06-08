package simple.planner.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Locale;

import simple.planner.R;
import simple.planner.activities.LockScreenActivity.LockScreenActivity;
import simple.planner.activities.SettingActivity;
import simple.planner.receivers.PhoneStateReceiver;

public class LockScreenService extends Service {        // This is how to make Foreground Service

    private BroadcastReceiver screenOnReceiver;
    private static boolean isServiceRunning = false;
    public static boolean isIsServiceRunning() {
        return isServiceRunning;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        localeSet(this);

        startForeground(ServiceConstants.LOCK_SCREEN_SERVICE_NOTIFICATION_ID, createServiceNotification());
        screenOnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction()==null) return;


                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && !PhoneStateReceiver.isOnThePhone()) {
                    Intent intentForLockScreen = new Intent(context, LockScreenActivity.class);
                    intentForLockScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentForLockScreen);
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOnReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(isServiceRunning) {  return START_STICKY; }
        isServiceRunning = true;


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (screenOnReceiver != null) {
            unregisterReceiver(screenOnReceiver);
            screenOnReceiver = null;
        }
        isServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Notification createServiceNotification() {
        Intent notificationIntent = new Intent(this, SettingActivity.class);
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, ServiceConstants.SERVICE_NOTIFICATION_PENDING_INTENT_REQUEST_CODE, notificationIntent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getResources().getString(R.string.service_notification_channel_id))
                .setSmallIcon(R.drawable.ic_stat_notification_icon)
                .setContentTitle(getResources().getString(R.string.service_notification_title))
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }


    private void localeSet(Context context) {

        SharedPreferences prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String langCode = prefs.getString("languageCode", "");

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

}
