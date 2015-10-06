package ch.pboos.alarm.server.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

/**
 * Created by pboos on 06/10/15.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_ALARM = "ch.pboos.alarm.server.broadcast.ACTION_ALARM";

    private static final String ACTION_STOP = "ch.pboos.alarm.server.broadcast.ACTION_STOP";

    private static MediaPlayer mediaPlayer;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_ALARM.equals(intent.getAction())) {

            mediaPlayer = MediaPlayer.create(context, getAlarmUri());
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            Log.i("AlarmServer", "after ringtone.play");
        } else if (ACTION_STOP.equals(intent.getAction())) {
            cancelAlarm(context);
        }
        // TODO have a timeout of like 5 minutes to turn off again!
    }

    private Uri getAlarmUri() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alert == null) {
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if (alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }


    public static void scheduleAlarm(Context context, int secondsUntilAlarm) {
        long alarmTime = System.currentTimeMillis() + secondsUntilAlarm * 1000;
        getAlarmManager(context).set(AlarmManager.RTC_WAKEUP,
                alarmTime,
                getAlarmBroadcast(context));

        getAlarmManager(context).set(AlarmManager.RTC_WAKEUP,
                alarmTime + 5 * 60 * 1000,
                getAlarmStopBroadcast(context));
    }

    public static void cancelAlarm(Context context) {
        getAlarmManager(context).cancel(getAlarmBroadcast(context));
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }


    private static PendingIntent getAlarmStopBroadcast(Context context) {
        return getPendingIntent(context, ACTION_STOP);
    }

    private static PendingIntent getAlarmBroadcast(Context context) {
        return getPendingIntent(context, ACTION_ALARM);
    }

    private static PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.setAction(action);

        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
