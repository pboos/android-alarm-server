package ch.pboos.alarm.server.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.net.ServerSocket;

import ch.pboos.alarm.server.MainActivity;
import ch.pboos.alarm.server.R;
import ch.pboos.alarm.server.broadcast.AlarmBroadcastReceiver;
import fi.iki.elonen.NanoHTTPD;

/**
 * Created by pboos on 06/10/15.
 */
public class NetworkListenerService extends Service {

    private static final int NOTIFICATION_ID = 1337;

    private static final String COMMAND_START = "START";

    private static final String COMMAND_STOP = "STOP";

    private AlarmWebServer mAlarmWebServer;

    private ServerSocket mServerSocket;

    @Override
    public IBinder onBind(Intent intent) {
        // we have nothing to do here...
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createForegroundNotification());

        if (mAlarmWebServer == null) {
            mAlarmWebServer = new AlarmWebServer();
            try {
                mAlarmWebServer.start();
            } catch (IOException e) {
                e.printStackTrace();
                mAlarmWebServer = null;
            }
        }

        return START_STICKY;
    }

    private Notification createForegroundNotification() {
        return new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setTicker("Alarm Server running")
                .setContentTitle("Alarm Server")
                .setContentText("running...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(
                        PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .build();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        AlarmBroadcastReceiver.cancelAlarm(this);
        closeServerSocket();
        if (mAlarmWebServer != null) {
            mAlarmWebServer.stop();
            mAlarmWebServer = null;
        }
    }

    private void closeServerSocket() {
        if (mServerSocket != null && !mServerSocket.isClosed()) {
            try {
                mServerSocket.close();
            } catch (IOException ignored) {
            }
            mServerSocket = null;
        }
    }

    class AlarmWebServer extends NanoHTTPD {

        public AlarmWebServer() {
            super(8080);
        }

        @Override
        public Response serve(IHTTPSession session) {
            if ("/start".equals(session.getUri())) {
                AlarmBroadcastReceiver.scheduleAlarm(NetworkListenerService.this, 5 * 60);
            } else if ("/stop".equals(session.getUri())) {
                AlarmBroadcastReceiver.cancelAlarm(NetworkListenerService.this);
            }

            return new Response("");
        }
    }
}
