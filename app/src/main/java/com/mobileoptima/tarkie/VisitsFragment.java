package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.VisitsAdapter;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.VisitObj;

import java.util.ArrayList;

public class VisitsFragment extends Fragment implements OnRefreshCallback {

	//
//			tvDateVisits
//	btnAddVisits
//			tvAddVisits
//
	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private VisitsAdapter adapter;
	private CodePanLabel tvDateVisits;
	private ArrayList<VisitObj> visitList;
	private ListView lvVisits;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.visits_layout, container, false);
		tvDateVisits = (CodePanLabel) view.findViewById(R.id.tvDateVisits);
		lvVisits = (ListView) view.findViewById(R.id.lvVisits);
		lvVisits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				VisitObj visit = visitList.get(position);
				VisitDetailsFragment details = new VisitDetailsFragment();
				details.setVisit(visit);
				details.setOnSaveVisitCallback(new Interface.OnSaveVisitCallback() {
					@Override
					public void onSaveVisit(VisitObj visit) {
						visitList.set(position, visit);
						lvVisits.invalidate();
						adapter.notifyDataSetChanged();
						main.updateSyncCount();
						main.reloadSchedule();
					}
				});
				main.addSlidingToMain(details);
			}
		});
		tvDateVisits.setText(CodePanUtils.getCalendarDate(CodePanUtils.getDate(), true, true));
		loadVisits(db, CodePanUtils.getDate());
		return view;
	}

	@Override
	public void onRefresh() {
		loadVisits(db, CodePanUtils.getDate());
	}

	public void loadVisits(final SQLiteAdapter db, final String date) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					visitList = Data.loadVisits(db, date, false);
					handler.sendMessage(handler.obtainMessage());
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
			adapter = new VisitsAdapter(main, visitList);
			lvVisits.setAdapter(adapter);
			return true;
		}
	});
}
