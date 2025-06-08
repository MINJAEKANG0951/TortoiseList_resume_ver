package simple.planner.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import simple.planner.BaseActivity;
import simple.planner.R;
import simple.planner.activities.TODOListActivity.TODOListActivity;

public class PermissionActivity extends BaseActivity {

    private Context context;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private String[] permissions;
    private Button startPermission;

    @SuppressLint("ObsoleteSdkInt")
    private ActivityResultLauncher<Intent> overlayPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(context)) {
                        // Permission Granted
                        requestScheduleExactAlarmPermission();
                    } else {
                        // Permission Denied
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }

            });


    private ActivityResultLauncher<Intent> scheduleExactAlarmPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                // This block is executed when returning from settings.
                // You can check if the permission has been granted by calling canDrawOverlays again.

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if (Build.VERSION.SDK_INT >= 34) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        // Permission Granted
                        requestTheOtherPermissions();
                    } else {
                        // Permission Denied
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        this.context = this;
        permissions = getIntent().getStringArrayExtra("permissions");
        startPermission = findViewById(R.id.startPermission);
        startPermission.setOnClickListener(v->{
            requestDrawOverlayPermission();
        });



    }

    @SuppressLint("ObsoleteSdkInt")
    private void requestDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent goOverlaySetting = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            overlayPermissionLauncher.launch(goOverlaySetting);
        } else {
            requestScheduleExactAlarmPermission();
        }
    }

    private void requestScheduleExactAlarmPermission() {
        // since Android api level 34(sdk version 12) instead of asking permission,
        // you gotta take user to Setting where they can allow for Schedule_Exact_Alarm
        if (Build.VERSION.SDK_INT >= 34) {
            Intent goOverlaySetting = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getPackageName()));
            scheduleExactAlarmPermissionLauncher.launch(goOverlaySetting);
        } else {
            requestTheOtherPermissions();
        }
    }

    private void requestTheOtherPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean requestResult = true;

        if (requestCode == PERMISSIONS_REQUEST_CODE)
        {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    requestResult = false;
                    break;
                }
            }
        }

        if(requestResult) {
          Intent intent = new Intent(context, TODOListActivity.class);
          startActivity(intent);
          finish();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_permission_denied), Toast.LENGTH_SHORT).show();
        }

    }



}
