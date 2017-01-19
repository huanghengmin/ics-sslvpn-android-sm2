package com.zd.vpn.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.zd.vpn.OnBootReceiver;

import java.util.Calendar;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static final String TAG = "Alarm Receiver";

    private AlarmManager alarmMgr;

    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, AlarmSchedulingService.class);

        startWakefulService(context, service);
    }


   /* 调用 AlarmManager 需要注意的参数
    AlarmManager.RTC：硬件闹钟，不唤醒手机（也可能是其它设备）休眠；当手机休眠时不发射闹钟。
    AlarmManager.RTC_WAKEUP：硬件闹钟，当闹钟发躰时唤醒手机休眠；
    AlarmManager.ELAPSED_REALTIME：真实时间流逝闹钟，不唤醒手机休眠；当手机休眠时不发射闹钟。
    AlarmManager.ELAPSED_REALTIME_WAKEUP：真实时间流逝闹钟，当闹钟发躰时唤醒手机休眠；
    AlarmManager.POWER_OFF_WAKEUP：能唤醒系统，他是一种关机闹铃，就是说设备在关机状态下也可以唤醒系统，所以我们把它称为关机闹铃。

    RTC和ELAPSED最大的差别
    前者可以通过修改手机时间触发闹钟事件，后者要通过真实时间的流逝，即使在休眠状态，时间也会被计算。所以上面的代码使用 ELAPSED_REALTIME_WAKEUP
    在使用 ELAPSED 时使用 SystemClock.elapsedRealtime() 获取时间
    在使用 ETC 时使用 System.currentTimeMillis() 获取时间*/

    public void setAlarm(Context context) {

        /*alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 8:30 a.m.
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);*/

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        long triggerAtTime = SystemClock.elapsedRealtime();
        //首次运行在5分钟以后
        triggerAtTime += 5 * 60 * 1000;
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, 5 * 60 * 1000, alarmIntent);

        // Set the alarm to fire at approximately 8:30 a.m., according to the device's
        // clock, and to repeat once a day.

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
       /* ComponentName receiver = new ComponentName(context, OnBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/
    }

    public void cancelAlarm(Context context) {
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        /*ComponentName receiver = new ComponentName(context, OnBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);*/
    }
}
