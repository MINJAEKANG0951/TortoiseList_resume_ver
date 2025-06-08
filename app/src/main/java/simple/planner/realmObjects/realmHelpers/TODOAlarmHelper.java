package simple.planner.realmObjects.realmHelpers;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import io.realm.Realm;
import simple.planner.realmObjects.TODOAlarm;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.receivers.NotificationExecutor;

public class TODOAlarmHelper {

    private static final String TAG = TODOAlarmHelper.class.getSimpleName();

     /**        update/insert an alarm to TodoItem          **/
    public static boolean putAlarm(@NonNull Context context, String TodoId, long when) {

        // REALM
        Realm realm = null;

        try
        {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            {
                TODOListItem item   = realm.where(TODOListItem.class).equalTo("id", TodoId).findFirst();
                if(item==null) return false;

                TODOAlarm formerAlarm = item.getAlarm();
                if(formerAlarm != null) {
                    cancelNotification(context, item.getId(), formerAlarm.getId());
                    item.setAlarm(null);
                    formerAlarm.deleteFromRealm();
                }

                TODOAlarm newAlarm = realm.createObject(TODOAlarm.class, TODOAlarm.getNextId());
                newAlarm.setScheduled(when);
                newAlarm.setRang(false);
                item.setAlarm(newAlarm);
                scheduleNotification(context, item.getId(), newAlarm.getId(), newAlarm.getScheduled());
            }
            realm.commitTransaction();
            return true;
        }
        catch(Exception e)
        {
            Log.e(TAG,e.toString());
            return false;
        }
        finally
        {
            if(realm!=null) {
                if(realm.isInTransaction()) realm.cancelTransaction();
                if(!realm.isClosed())       realm.close();
            }
        }

    }


    /**        delete an alarm from TodoItem          **/
    public static boolean deleteAlarm(@NonNull Context context, String TodoId) {

        Realm realm = null;

        try
        {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            {
                TODOListItem item   = realm.where(TODOListItem.class).equalTo("id", TodoId).findFirst();
                if(item == null) return false;

                TODOAlarm alarm     = item.getAlarm();
                if(alarm != null) {
                    cancelNotification(context, item.getId(), alarm.getId());
                    item.setAlarm(null);
                    alarm.deleteFromRealm();
                }
            }
            realm.commitTransaction();
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG,e.toString());
            return false;
        }
        finally
        {
            if(realm!=null) {
                if(realm.isInTransaction()) realm.cancelTransaction();
                if(!realm.isClosed())       realm.close();
            }
        }

    }

    public static TODOAlarm readAlarm(String TodoId) {

        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            TODOListItem item    = realm.where(TODOListItem.class).equalTo("id", TodoId).findFirst();
            if(item.getAlarm()==null) return null;
            return item.getAlarm();
        }
        catch(Exception e)
        {
            Log.e(TAG,e.toString());
            return null;
        }
        finally
        {
            if(realm!=null) {
                if(realm.isInTransaction()) realm.cancelTransaction();
                if(!realm.isClosed())       realm.close();
            }
        }

    }



    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleNotification(Context context, String TodoId, int alarmId, long when) {

        try
        {
            Intent intent = new Intent(context, NotificationExecutor.class);
            intent.putExtra("TodoId", TodoId);

            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, flags);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, when, pendingIntent);
        }
        catch(Exception e)
        {
            Log.e(TAG,e.toString());
        }

    }

    public static void cancelNotification(Context context, String TodoId, int alarmId) { // create the same pendingIntent and execute cancel method in alarmManager

        try
        {
            Intent intent = new Intent(context, NotificationExecutor.class);
            intent.putExtra("TodoId", TodoId);

            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, flags);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
        catch(Exception e)
        {
            Log.e(TAG,e.toString());
        }

    }





}
