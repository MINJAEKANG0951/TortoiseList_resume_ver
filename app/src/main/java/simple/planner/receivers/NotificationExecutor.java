package simple.planner.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import simple.planner.R;
import simple.planner.realmObjects.Setting;
import simple.planner.realmObjects.TODOAlarm;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.realmObjects.realmHelpers.SettingHelper;
import simple.planner.sharedPreferences.PrefsUtil;

public class NotificationExecutor extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        localeSet(context);


        String TodoId = intent.getStringExtra("TodoId");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            Realm realm = Realm.getDefaultInstance();

            try
            {

                TODOListItem todo = realm.where(TODOListItem.class).equalTo("id",TodoId).findFirst();
                if(todo == null) return;
                TODOAlarm alarm   = todo.getAlarm();
                if(alarm == null) return;

                String channelId            = context.getString(R.string.notification_channel_id);
                String notificationTitle    = context.getString(R.string.notification_title);
                String TodoTitle            = todo.getTitle();
                int notificationId          = alarm.getId();

                realm.executeTransaction(r->{
                    alarm.setRang(true);        // just give a change to realm data
                });

                Setting setting = realm.where(Setting.class).findFirst();
                if(setting == null) return;
                if(!setting.isNotificationOn()) return;                     // do nothing if the notification is off.

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId) // to send notification to user, you need to have a channel.
                        .setSmallIcon(R.drawable.ic_stat_notification_icon)
                        .setContentTitle(notificationTitle)
                        .setContentText(TodoTitle)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {return;}
                notificationManager.notify(notificationId, builder.build());

            }
            catch(Exception e)
            {
                Log.e("error setting 'ring' to true : ",e.toString());
            }
            finally
            {
                realm.close();
            }

        });

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
