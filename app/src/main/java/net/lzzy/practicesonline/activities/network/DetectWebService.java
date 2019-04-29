package net.lzzy.practicesonline.activities.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import net.lzzy.practicesonline.activities.activities.PracticesActivity;
import net.lzzy.practicesonline.activities.models.Practice;
import net.lzzy.practicesonline.activities.utils.AppUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 与activity通信
 *
 * @author lzzy_gxy
 * @date 2019/4/28
 * Description:
 */
public class DetectWebService extends Service {
    public static final String EXTRA_REFRESH = "extraRefresh";
    private int localCount;
    public static final int NOTIFICATION_DETECT_ID = 0;
    private NotificationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //返回Binder对象
        localCount = intent.getIntExtra(PracticesActivity.EXTRA_LOCAL_COUNT, 0);
        return new DetectWebBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (manager != null) {
            manager.cancel(NOTIFICATION_DETECT_ID);
        }
        return super.onUnbind(intent);
    }

    public class DetectWebBinder extends Binder {
        private static final int FLAG_SERVER_EXCEPTION = 0;
        private static final int FLAG_DATA_CHANGED = 1;
        private static final int FLAG_DATA_SAME = 2;

        //后台执行任务

        public void detect() {
            AppUtils.getExecutor().execute(() -> {
                int flag = compareDate();
                if (flag == FLAG_SERVER_EXCEPTION) {
                    notifyUser("服务器无法连接", android.R.drawable.ic_menu_compass, false);

                } else if (flag == FLAG_DATA_CHANGED) {
                    notifyUser("远程服务器更新", android.R.drawable.ic_popup_sync, true);
                } else {
                    //清除任务
                    if (manager != null) {
                        manager.cancel(NOTIFICATION_DETECT_ID);
                    }
                }

            });
        }

        private void notifyUser(String info, int icon, boolean refresh) {
            Intent intent = new Intent(DetectWebService.this, PracticesActivity.class);
            intent.putExtra(EXTRA_REFRESH, refresh);
            PendingIntent pendingIntent = PendingIntent.getActivity(DetectWebService.this,
                    0, intent, PendingIntent.FLAG_ONE_SHOT);

            manager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
            /**
             *  实例化通知栏构造器
             */

            Notification notification;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notification = new Notification.Builder(DetectWebService.this, "0")
                        .setContentTitle("检测远程服务器")
                        //设置内容
                        .setContentText(info)
                        //设置小图标
                        .setSmallIcon(icon)
                        //设置通知时间
                        .setWhen(System.currentTimeMillis())
                        //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)

                        .build();
            } else {
                notification = new Notification.Builder(DetectWebService.this)
                        .setContentTitle("检测远程服务器")
                        .setContentText(info)
                        .setSmallIcon(icon)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .build();
            }
            if (manager != null) {
                manager.notify(NOTIFICATION_DETECT_ID, notification);
            }


        }

        private int compareDate() {
            try {
                List<Practice> remote = PracticeService.getPractices(PracticeService.getPracticesFromServer());
                if (remote.size() != localCount) {
                    return FLAG_DATA_CHANGED;
                } else {
                    return FLAG_DATA_SAME;
                }
            } catch (Exception e) {
                return FLAG_SERVER_EXCEPTION;
            }
        }
    }

}
