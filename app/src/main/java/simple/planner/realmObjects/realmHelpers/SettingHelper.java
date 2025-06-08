package simple.planner.realmObjects.realmHelpers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import io.realm.Realm;
import simple.planner.realmObjects.Setting;
import simple.planner.services.LockScreenService;

public class SettingHelper {

    private static final String TAG = SettingHelper.class.getSimpleName();

    public static String getLanguageCode() {

        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            Setting setting = realm.where(Setting.class).findFirst();
            if(setting == null) return null;
            return setting.getLanguageCode();
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

    public static boolean setLanguageCode(String languageCode) {
        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
                Setting setting = realm.where(Setting.class).findFirst();
                if(setting == null) return false;
                setting.setLanguageCode(languageCode);
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


    public static boolean setNotificationOnOff(boolean onOff) {

        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
                Setting setting = realm.where(Setting.class).findFirst();
                if(setting == null) return false;
                setting.setNotificationOn(onOff);
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

    public static boolean isNotificationOn() {

        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            Setting setting = realm.where(Setting.class).findFirst();
            if(setting==null) return false;
            return setting.isNotificationOn();
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


    public boolean setLockScreenModeOnOff(boolean onOff) {

        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
                Setting setting = realm.where(Setting.class).findFirst();
                if(setting != null) {
                    setting.setLockScreenModeOn(onOff);
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
                if(realm.isInTransaction()) { realm.cancelTransaction(); }
                if(!realm.isClosed())       { realm.close(); }
            }
        }

    }


    public static boolean isLockScreenModeOn() {
        Realm realm = null;
        try
        {
            realm = Realm.getDefaultInstance();
            Setting setting = realm.where(Setting.class).findFirst();
            if(setting==null) return false;
            return setting.isLockScreenModeOn();
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



}
