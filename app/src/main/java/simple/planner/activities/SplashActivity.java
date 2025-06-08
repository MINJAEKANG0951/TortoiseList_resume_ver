package simple.planner.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.DateIntervalInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

import io.realm.Realm;
import simple.planner.R;
import simple.planner.activities.TODOListActivity.TODOListActivity;
import simple.planner.realmObjects.Setting;
import simple.planner.services.LockScreenService;
import simple.planner.sharedPreferences.PrefsUtil;

public class SplashActivity extends AppCompatActivity {

    private Context context;
    private Realm realm;
    private final int SPLASH_DISPLAY_LENGTH = 1500;

    private String [] permissions;
    private boolean userValidation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.context = this;
        this.realm = Realm.getDefaultInstance();
        boolean newUser = this.realm.where(Setting.class).findFirst() == null;

        backPressSetting();

        permissions = new String[] {
            android.Manifest.permission.READ_PHONE_STATE
        };

        if (Build.VERSION.SDK_INT >= 31 && Build.VERSION.SDK_INT < 34) {
            permissions = Add(permissions, Manifest.permission.SCHEDULE_EXACT_ALARM);           // exact alarm
        }

        if (Build.VERSION.SDK_INT >= 33) {
            permissions = Add(permissions, Manifest.permission.POST_NOTIFICATIONS);             // notification
        }

        userValidation = true;
        if (newUser) {
            if( applyDefaultSetting() ) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_user_created), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_failed_to_create_user), Toast.LENGTH_SHORT).show();
                userValidation = false;
            }
        };


        new Handler().postDelayed(() ->
        {
            if(!userValidation)
            {
                finish();
            }
            else
            {

                if(areAllPermissionsAllowed(permissions)) {

                    saveSettingValuesInSharedReferences();
                    startLockScreenWhenOn();
                    Intent intent = new Intent(context, TODOListActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    saveSettingValuesInSharedReferences();
                    Intent intent = new Intent(context, PermissionActivity.class);
                    intent.putExtra("permissions", permissions);
                    startActivity(intent);
                    finish();

                }

            }
        }, SPLASH_DISPLAY_LENGTH);


    }

    private static <T> T[] Add(T[] formerArray, T newElement) {
        T[] newArray = Arrays.copyOf(formerArray, formerArray.length + 1);
        newArray[formerArray.length] = newElement;
        return newArray;
    }

    private boolean applyDefaultSetting() {
        try
        {
            Setting appSetting = realm.where(Setting.class).findFirst();
            if (appSetting == null) {
                realm.beginTransaction();
                Setting setting = new Setting();
                realm.copyToRealm(setting);
                realm.commitTransaction();
            }
            return true;
        }
        catch (Exception e)
        {
            Log.e("error while initializing user basic setting : ",e.toString());
            if( realm.isInTransaction() ) realm.cancelTransaction();
            return false;
        }
    }

    private boolean areAllPermissionsAllowed(String[] permissions) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            return false;   // draw overlay + SYSTEM_ALERT_WINDOW  해야 background 에서 activity 실행 가능!!!!
        }

        if(Build.VERSION.SDK_INT >= 34) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if(!alarmManager.canScheduleExactAlarms()) {
                return false;
            }
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;

        /**
             code to request permissions -   when a user has already permitted, and if you ask once more, nothing happens.
             just onRequestPermissionsResults will be called, and you get PackageManager.PERMISSION_GRANTED in grantResults(whether a user has just permitted or permitted before)
         **/

        // ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE); code to ask permission

    }

    private void saveSettingValuesInSharedReferences() {

        Setting settingValues = realm.where(Setting.class).findFirst();
        if(settingValues==null) return;

        // language code 저장 - 나중에 아마 다른것도 저장
        String languageCode = settingValues.getLanguageCode();
        PrefsUtil.setLanguage(context, languageCode);
    }

    private void startLockScreenWhenOn() {

        Setting settingValues = realm.where(Setting.class).findFirst();
        if(settingValues == null) return;
        if(settingValues.isLockScreenModeOn() && !LockScreenService.isIsServiceRunning()) {
            Intent lockScreenIntent = new Intent(context, LockScreenService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(lockScreenIntent);
            } else {
                startService(lockScreenIntent);
            }
        }

    }


    private void backPressSetting() {
        // Create a callback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event here
            }
        };

        // Add the callback to the dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(realm!=null) {
            if(!realm.isClosed()) realm.close();
            realm = null;
        }
    }



}
