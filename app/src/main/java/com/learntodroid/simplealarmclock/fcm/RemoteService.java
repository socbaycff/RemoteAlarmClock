package com.learntodroid.simplealarmclock.fcm;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.learntodroid.simplealarmclock.data.Alarm;

import java.util.Map;
import java.util.Random;
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class RemoteService extends FirebaseMessagingService {
    public RemoteService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("------------------");
        Map<String, String> datas = remoteMessage.getData();
        int hour = Integer.parseInt(datas.get("hour"));
        int minute = Integer.parseInt(datas.get("minute"));
        String title = datas.get("title");
        scheduleAlarmOnline(hour,minute,title);
    }


    void scheduleAlarmOnline(int hour, int minute, String title) {
        int alarmId = new Random().nextInt(Integer.MAX_VALUE);

        Alarm alarm = new Alarm(
                alarmId,
                hour,
                minute,
                title,
                System.currentTimeMillis(),
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        );

        // createAlarmViewModel.insert(alarm);

        alarm.schedule(getApplicationContext());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        System.out.println("---------------------------");
        System.out.println(s);
    }
}
