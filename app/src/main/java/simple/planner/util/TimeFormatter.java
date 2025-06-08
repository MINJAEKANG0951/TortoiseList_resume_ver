package simple.planner.util;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatter {

    @SuppressLint("SimpleDateFormat")
    public static String toString(long timestamp, @Nullable String languageCode) {

        SimpleDateFormat sdf = null;

        if(languageCode == null) {
            sdf = new SimpleDateFormat("H:mma M/dd/yyyy");
        } else if(languageCode.equals("ko")) {
            sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        } else {
            sdf = new SimpleDateFormat("H:mma M/dd/yyyy");
        }

        Date date = new Date(timestamp);
        return sdf.format(date).toLowerCase();
    }

    @SuppressLint("SimpleDateFormat")
    public static long toLong(String formattedDate, @Nullable String languageCode) {

        SimpleDateFormat dateFormat;

        if(languageCode == null) {
            dateFormat = new SimpleDateFormat("H:mma M/dd/yyyy");
        } else if(languageCode.equals("ko")) {
            dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        } else {
            dateFormat = new SimpleDateFormat("H:mma M/dd/yyyy");
        }

        try {
            Date date = dateFormat.parse(formattedDate);
            if(date == null) return 0;
            return date.getTime();
        }
        catch (Exception e) {
            return 0;
        }
    }


}
