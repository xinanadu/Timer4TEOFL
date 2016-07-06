package com.symbio.android.webview.timer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent intentAlarmReceiver = new Intent(context, AlarmReceiver.class);
            SharedPreferences prefs = context.getSharedPreferences("alarm",
                    Activity.MODE_PRIVATE);
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(context, 0, intentAlarmReceiver, 0);

            long prefsMorning = prefs.getLong("MORNING", 0);
            if (prefsMorning > 0) {
                intentAlarmReceiver.putExtra("flag", "MORNING");
                alarmMgr.setInexactRepeating(AlarmManager.RTC, prefsMorning,
                        AlarmManager.INTERVAL_DAY, pendingIntentAlarm);
                setReceiver(context, true);
            }

            long prefsNight = prefs.getLong("NIGHT", 0);
            if (prefsMorning > 0) {
                intentAlarmReceiver.putExtra("flag", "NIGHT");
                alarmMgr.setInexactRepeating(AlarmManager.RTC, prefsNight,
                        AlarmManager.INTERVAL_DAY, pendingIntentAlarm);
                setReceiver(context, true);
            }

        } else {
//            Intent intentActivity = new Intent(context, MainActivity.class);
//            intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intentActivity);
            String str = intent.getStringExtra("flag");
            if (TextUtils.equals(str, "NIGHT")) {
                str += "Go to Sleep";
            } else if (TextUtils.equals(str, "MORNING")) {
                str += "Wake up";
            }
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("TOEFL")
                            .setContentText(str);
// Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
            int nId = Integer.parseInt(new SimpleDateFormat("MMddHHmmss").format(new Date()));
            mNotificationManager.notify(nId, mBuilder.build());
        }
    }

    private void setReceiver(Context context, boolean enabled) {
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
