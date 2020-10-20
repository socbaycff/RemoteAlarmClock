package com.learntodroid.simplealarmclock.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<Alarm>> alarmsLiveData;
    private FirebaseFirestore fb;

    public AlarmRepository(Application application) {
        AlarmDatabase db = AlarmDatabase.getDatabase(application);
        alarmDao = db.alarmDao();
        fb = FirebaseFirestore.getInstance();

        fb.collection("alarms").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Alarm> alarms = queryDocumentSnapshots.toObjects(Alarm.class);
                        alarmDao.deleteAll();
                        alarms.forEach(new Consumer<Alarm>() {
                            @Override
                            public void accept(Alarm alarm) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        alarmDao.insert(alarm);
                                    }

                                }).start();

                            }
                        });
                    }
                }).start();


            }
        });
        alarmsLiveData = alarmDao.getAlarms();

    }

    public void insert(Alarm alarm) {
        fb.collection("alarms").document(String.valueOf(alarm.getAlarmId())).set(alarm,SetOptions.merge());
        AlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.insert(alarm);
        });
    }

    public void update(Alarm alarm) {
        fb.collection("alarms").document(String.valueOf(alarm.getAlarmId())).set(alarm, SetOptions.merge());
        AlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.update(alarm);
        });
    }

    public void delete(Alarm alarm) {
        fb.collection("alarms").document(String.valueOf(alarm.getAlarmId())).delete();
    }

    public LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmsLiveData;
    }
}
