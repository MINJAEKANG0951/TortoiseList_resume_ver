package simple.planner.realmObjects.realmHelpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import io.realm.Realm;
import simple.planner.realmObjects.TODOAlarm;
import simple.planner.realmObjects.TODOListItem;

public class TODOListItemHelper
{
    private static final String TAG = TODOListItemHelper.class.getSimpleName();

    public static String createTODO(@NonNull String title, String content) {

        if(title.length()==0) return null;

        Realm realm = null;
        String todoId = null;
        try
        {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            {
                TODOListItem item = new TODOListItem();
                item.setTitle(title);
                item.setContent(content);
                realm.copyToRealm(item);
                todoId = item.getId();
            }
            realm.commitTransaction();
        }
        catch(Exception e)
        {
            Log.e(TAG,e.toString());
        }
        finally
        {
            if(realm!=null) {
                if(realm.isInTransaction()) realm.cancelTransaction();
                if(!realm.isClosed())       realm.close();
            }
        }

        return todoId;
    }


    public static boolean deleteTODO(@NonNull String TodoId) {

        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            {
                TODOListItem item = realm.where(TODOListItem.class)
                        .equalTo("id",TodoId)
                        .findFirst();
                if(item == null) return false;

                TODOAlarm registeredAlarm = item.getAlarm();
                if(registeredAlarm != null) return false;

                item.deleteFromRealm();
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

    public static boolean updateTODO(@NonNull String TodoId, @NonNull String title, String content, boolean done) {

        if(title.length()==0) return false;

        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            {
                TODOListItem item = realm.where(TODOListItem.class).equalTo("id",TodoId).findFirst();
                if(item==null) return false;

                item.setTitle(title);
                item.setContent(content);
                item.setDone(done);
                item.setUpdated( System.currentTimeMillis() );
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



    public static TODOListItem readTODO (@NonNull String TodoId) {

        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            TODOListItem item = realm.where(TODOListItem.class).equalTo("id",TodoId).findFirst();
            return item;

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


}
