package net.lzzy.practicesonline.activities.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.strictmode.InstanceCountViolation;

import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.practicesonline.activities.utils.DateTimeUtils;

import java.util.Date;

/**
 * @author lzzy_gxy
 * @date 2019/4/24
 * Description:
 */
public class UserCookies {
    public static final String KEY_TIME = "keytime";
    private SharedPreferences spTime;
    public static final UserCookies INSTANCE = new UserCookies();

    private UserCookies() {
        spTime = AppUtils.getContext()
                .getSharedPreferences("refresh_time", Context.MODE_PRIVATE);
    }

    public static UserCookies getInstance() {
        return INSTANCE;
    }

    public void updateeLastRefreshTime() {
        String time = DateTimeUtils.DATE_TIME_FORMAT.format(new Date());
        spTime.edit().putString(KEY_TIME, time).apply();
    }

    public String getLastRefreshTime() {
        return spTime.getString(KEY_TIME,"");
    }
}
