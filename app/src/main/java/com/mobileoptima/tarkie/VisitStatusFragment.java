package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnSelectStatusCallback;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.TaskStatus;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskStatusObj;

import java.util.ArrayList;

public class VisitStatusFragment extends Fragment implements OnClickListener {

	private OnSelectStatusCallback selectStatusCallback;
	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private Spinner spinnerVisitStatus;
	private ArrayAdapter<String> adapter;
	private ArrayList<TaskStatusObj> statusList;
	private TaskStatusObj status;
	private StoreObj store;
	private boolean hasNotes;
	private String title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.visit_status_layout, container, false);
		spinnerVisitStatus = (Spinner) view.findViewById(R.id.spinnerVisitStatus);
		CodePanLabel tvStoreVisitStatus, tvTitleVisitStatus;
		tvTitleVisitStatus = (CodePanLabel) view.findViewById(R.id.tvTitleVisitStatus);
		tvStoreVisitStatus = (CodePanLabel) view.findViewById(R.id.tvStoreVisitStatus);
		view.findViewById(R.id.btnCancelVisitStatus).setOnClickListener(this);
		view.findViewById(R.id.btnSaveVisitStatus).setOnClickListener(this);
		String title = "Please choose the status of your " + main.getConvention(Convention.VISIT).toLowerCase() + ":";
		tvTitleVisitStatus.setText(title);
		if(store != null) {
			tvStoreVisitStatus.setText(store.name);
		}
		else {
			tvStoreVisitStatus.setText(this.title);
		}
		loadStatus(db);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnSaveVisitStatus:
				if(status != null && !status.code.equals(TaskStatus.DEFAULT)) {
					manager.popBackStack();
					if(selectStatusCallback != null) {
						selectStatusCallback.onSelectStatus(status);
					}
				}
				else {
					CodePanUtils.alertToast(main, "Please select status.");
				}
				break;
			case R.id.btnCancelVisitStatus:
				manager.popBackStack();
				break;
		}
	}

	public void setStore(StoreObj store) {
		this.store = store;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setHasNotes(boolean hasNotes) {
		this.hasNotes = hasNotes;
	}

	public void setOnSelectStatusCallback(OnSelectStatusCallback selectStatusCallback) {
		this.selectStatusCallback = selectStatusCallback;
	}

	private void loadStatus(final SQLiteAdapter db) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					statusList = Data.loadStatus();
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
			adapter = new ArrayAdapter<>(main, R.layout.spinner_selected_item);
			adapter.setDropDownViewResource(R.layout.spinner_selection_item);
			for(TaskStatusObj status : statusList) {
				adapter.add(status.name);
			}
			spinnerVisitStatus.setAdapter(adapter);
			spinnerVisitStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					status = statusList.get(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			return true;
		}
	});
}
