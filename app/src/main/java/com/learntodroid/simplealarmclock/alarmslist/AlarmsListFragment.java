package com.learntodroid.simplealarmclock.alarmslist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.learntodroid.simplealarmclock.data.Alarm;
import com.learntodroid.simplealarmclock.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AlarmsListFragment extends Fragment implements OnToggleAlarmListener {
    private AlarmRecyclerListAdapter alarmRecyclerListAdapter;
    private AlarmsListViewModel alarmsListViewModel;
    private RecyclerView alarmsRecyclerView;
    private Chip addAlarm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTmp();
        alarmRecyclerListAdapter = new AlarmRecyclerListAdapter(this);

        alarmsListViewModel = ViewModelProviders.of(this).get(AlarmsListViewModel.class);
        alarmsListViewModel.getAlarmsLiveData().observe(this, alarms -> {
            if (alarms != null) {
                alarmRecyclerListAdapter.submitList(new ArrayList<>(alarms));
            }
        });

//        FirebaseFirestore.getInstance().collection("alarms").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                List<Alarm> alarms = queryDocumentSnapshots.toObjects(Alarm.class);
//                alarmRecyclerListAdapter.submitList(alarms);
//                Log.i("test", "chay vao day" + System.currentTimeMillis());
//            }
//        });


    }

    public void setTmp() {
        Alarm alarm = new Alarm(0, 0, 0, "title", 0, false, false, false, false, false, false, false, false, false);
        FirebaseFirestore.getInstance().collection("alarms").document(String.valueOf(alarm.getAlarmId())).set(alarm, SetOptions.merge());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listalarms, container, false);

        alarmsRecyclerView = view.findViewById(R.id.fragment_listalarms_recylerView);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmsRecyclerView.setAdapter(alarmRecyclerListAdapter);
        alarmsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        addAlarm = view.findViewById(R.id.fragment_listalarms_addAlarm);
        addAlarm.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_alarmsListFragment_to_createAlarmFragment));

        new ItemTouchHelper(new AlarmsTouchHelperCallBack((position, direction) -> {
            Alarm alarm = alarmRecyclerListAdapter.getAlarm(position);
            alarmsListViewModel.delete(alarm);
        })).attachToRecyclerView(alarmsRecyclerView);

        return view;
    }

    @Override
    public void onToggle(Alarm alarm) {
        if (alarm.isStarted()) {
            alarm.cancelAlarm(getContext());
            alarmsListViewModel.update(alarm);
        } else {
            alarm.schedule(getContext());
            alarmsListViewModel.update(alarm);
        }
    }
}