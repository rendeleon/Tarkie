package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.adapter.BreaksAdapter;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.BreaksObj;

import java.util.ArrayList;

public class BreaksFragment extends Fragment {

	private final int SAVE = 1;
	private final int LOAD = 2;

	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private BreaksAdapter adapter;
	private ListView lvBreaks;
	private ArrayList<BreaksObj> breakList;
	private boolean result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.breaks_layout, container, false);
		lvBreaks = (ListView) view.findViewById(R.id.lvBreaks);
		lvBreaks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final BreaksObj breaks = breakList.get(position);
				if(!breaks.isDone) {
					AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Confirm Break");
					alert.setDialogMessage("Do you want to take your " + breaks.name + " now?");
					alert.setPositiveButton("Yes", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							GpsObj gps = main.getGps();
							saveBreakIn(db, gps, breaks);
						}
					});
					alert.setNegativeButton("No", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
						}
					});
					main.addFadingToMain(alert);
				}
				else {
					CodePanUtils.alertToast(main, "You've already used " + breaks.name);
				}
			}
		});
		view.findViewById(R.id.rlBreaks).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
			}
		});
		loadBreaks(db);
		return view;
	}

	private void loadBreaks(final SQLiteAdapter db) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					breakList = Data.loadBreaks(db);
					handler.obtainMessage(LOAD).sendToTarget();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case SAVE:
					if(result) {
						BreaksObj breaks = (BreaksObj) msg.obj;
						manager.popBackStack();
						manager.popBackStack();
						BreakTimeFragment breakTime = new BreakTimeFragment();
						breakTime.setBreaks(breaks);
						final FragmentTransaction transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, breakTime, DialogTag.BREAK);
						transaction.addToBackStack(null);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								transaction.commit();
							}
						}, 250);
						main.updateSyncCount();
					}
					else {
						CodePanUtils.alertToast(main, "Failed to save break.");
					}
					break;
				case LOAD:
					adapter = new BreaksAdapter(main, breakList);
					lvBreaks.setAdapter(adapter);
					break;
			}
			return true;
		}
	});

	private void saveBreakIn(final SQLiteAdapter db, final GpsObj gps, final BreaksObj breaks) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					result = TarkieLib.saveBreakIn(db, gps, breaks);
					if(result) {
						if(breaks.duration > 0) {
							// TODO CodePanUtils.setAlarm();
						}
					}
					handler.obtainMessage(SAVE, breaks).sendToTarget();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}
