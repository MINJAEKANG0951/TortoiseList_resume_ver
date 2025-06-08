package simple.planner;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SimplePlanner extends Application
{

    @Override
    public void onCreate() {
        super.onCreate();

        /** inactivate dark mode **/
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        /** create realm **/
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("simple_planner.realm")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);

        /** create a channel to send notification to the users **/
        createNormalChannel();

        /** create a channel to show notification while lock screen service is working **/
        createLockScreenServiceChannel();
    }


    private void createNormalChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId            = getString(R.string.notification_channel_id);
            String channelName          = getString(R.string.notification_channel_name);
            String channelDescription   = getString(R.string.notification_channel_description);

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    private void createLockScreenServiceChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId            = getString(R.string.service_notification_channel_id);
            String channelName          = getString(R.string.service_notification_channel_name);
            String channelDescription   = getString(R.string.service_notification_channel_description);

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }


}
