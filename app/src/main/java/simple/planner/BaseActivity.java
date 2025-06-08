package simple.planner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String langCode = prefs.getString("languageCode", "");

        if(TextUtils.isEmpty(langCode)) {
            langCode = "en";
        }

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);
        Context context = newBase.createConfigurationContext(config);

        super.attachBaseContext(context);
    }

}
