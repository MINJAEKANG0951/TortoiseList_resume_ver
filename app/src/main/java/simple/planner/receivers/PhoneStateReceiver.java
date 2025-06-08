package simple.planner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {

    public static boolean onThePhone = false;
    private static final String TAG = PhoneStateReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            // Get the state of the phone
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state == null) {
                return;
            }

            // Log and handle each state
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))  // Phone is ringing
            {

                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                onThePhone = true;
                Log.i(TAG, "Incoming call from: " + incomingNumber);

            }
            else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                onThePhone = true;
                Log.i(TAG, "Call off hook");

            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {
                onThePhone = false;
                Log.i(TAG, "Phone idle");
            }
        }
    }

    public static boolean isOnThePhone() {
        return onThePhone;
    }
}
