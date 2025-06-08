package simple.planner.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsUtil {

    public static void setLanguage(Context context, String languageCodeFromRealm) {
        SharedPreferences prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("languageCode", languageCodeFromRealm);
        editor.apply();
    }


}
