package simple.planner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import io.realm.Realm;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.realmObjects.realmHelpers.SettingHelper;
import simple.planner.services.LockScreenService;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            if(SettingHelper.isLockScreenModeOn() && !LockScreenService.isIsServiceRunning())
            {
                Intent lockScreenServiceIntent = new Intent(context, LockScreenService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(lockScreenServiceIntent);
                } else {
                    context.startService(lockScreenServiceIntent);
                }
            }

        }
    }
}
