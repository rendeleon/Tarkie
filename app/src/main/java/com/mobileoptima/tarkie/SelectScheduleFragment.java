package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.callback.Interface.OnSelectScheduleCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ScheduleObj;
import com.mobileoptima.model.ScheduleTimeObj;

import java.util.ArrayList;

public class SelectScheduleFragment extends Fragment implements OnClickListener {

	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private OnSelectScheduleCallback selectScheduleCallback;
	private Spinner spinnerSchedule;
	private ScheduleTimeObj selectedSched;
	private ScheduleObj schedule;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.select_schedule_layout, container, false);
		spinnerSchedule = (Spinner) view.findViewById(R.id.spinnerSchedule);
		view.findViewById(R.id.btnCancelSelectSched).setOnClickListener(this);
		view.findViewById(R.id.btnOKSelectSched).setOnClickListener(this);
		final ArrayList<ScheduleTimeObj> schedTimeList = Data.loadScheduleTime(db);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(main, R.layout.spinner_selected_item);
		adapter.setDropDownViewResource(R.layout.spinner_selection_item);
		adapter.add("Select Schedule");
		for(ScheduleTimeObj obj : schedTimeList) {
			if(!obj.isDayOff) {
				adapter.add(CodePanUtils.getNormalTime(obj.in, false) + " - " + CodePanUtils.getNormalTime(obj.out, false));
			}
			else {
				adapter.add("REST DAY");
			}
		}
		spinnerSchedule.setAdapter(adapter);
		spinnerSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				int index = position - 1;
				if(index >= 0) {
					selectedSched = schedTimeList.get(index);
				}
				else {
					selectedSched = null;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		if(schedule != null) {
			int index = 0;
			for(ScheduleTimeObj obj : schedTimeList) {
				if(obj.in.equals(schedule.timeIn) && obj.out.equals(schedule.timeOut)) {
					index = schedTimeList.indexOf(obj);
					break;
				}
			}
			spinnerSchedule.setSelection(index + 1);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnCancelSelectSched:
				manager.popBackStack();
				break;
			case R.id.btnOKSelectSched:
				if(selectedSched != null) {
					manager.popBackStack();
					String today = CodePanUtils.getDate();
					String scheduleID = TarkieLib.saveSchedule(db, selectedSched, today);
					if(scheduleID != null && selectScheduleCallback != null) {
						ScheduleObj schedule = new ScheduleObj();
						schedule.ID = scheduleID;
						schedule.timeIn = selectedSched.in;
						schedule.timeOut = selectedSched.out;
						schedule.scheduleDate = today;
						schedule.isDayOff = selectedSched.isDayOff;
						selectScheduleCallback.onSelectSchedule(schedule);
					}
				}
				else {
					CodePanUtils.alertToast(main, "Please select a schedule.");
				}
				break;
		}
	}

	public void setOnSelectScheduleCallback(OnSelectScheduleCallback selectScheduleCallback) {
		this.selectScheduleCallback = selectScheduleCallback;
	}

	public void setSchedule(ScheduleObj schedule) {
		this.schedule = schedule;
	}
}
