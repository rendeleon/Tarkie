package com.mobileoptima.tarkie;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.callback.Interface.OnSignCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.Incident;
import com.mobileoptima.constant.Result;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.service.MainService;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.mobileoptima.constant.Settings.TIME_IN_OUT_STORE;

public class SummaryFragment extends Fragment implements OnClickListener, OnBackPressedCallback,
		OnFragmentCallback, OnSignCallback {

	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private FrameLayout flSignatureSummary, flEditSummary;
	private ImageView ivSignatureSummary;
	private CodePanButton btnAddSignature;
	private CodePanLabel tvNetWorkHrsSummary, tvTotalBreakSummary, tvTotalWorkHrsSummary,
			tvTimeOutSummary, tvTimeInSummary;
	private AttendanceObj attendance;
	private GpsObj gps;
	private boolean isTimeOut;
	private String photo, signature, strTimeOut;

	@Override
	public void onResume() {
		super.onResume();
		main.overrideBackPress(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		main.overrideBackPress(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		main.overrideBackPress(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		main.setOnBackPressedCallback(this);
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.summary_layout, container, false);
		flSignatureSummary = (FrameLayout) view.findViewById(R.id.flSignatureSummary);
		flEditSummary = (FrameLayout) view.findViewById(R.id.flEditSummary);
		ivSignatureSummary = (ImageView) view.findViewById(R.id.ivSignatureSummary);
		ivSignatureSummary = (ImageView) view.findViewById(R.id.ivSignatureSummary);
		btnAddSignature = (CodePanButton) view.findViewById(R.id.btnAddSignature);
		tvNetWorkHrsSummary = (CodePanLabel) view.findViewById(R.id.tvNetWorkHrsSummary);
		tvTotalBreakSummary = (CodePanLabel) view.findViewById(R.id.tvTotalBreakSummary);
		tvTotalWorkHrsSummary = (CodePanLabel) view.findViewById(R.id.tvTotalWorkHrsSummary);
		tvTimeOutSummary = (CodePanLabel) view.findViewById(R.id.tvTimeOutSummary);
		tvTimeInSummary = (CodePanLabel) view.findViewById(R.id.tvTimeInSummary);
		CodePanLabel tvTimeInTitleSummary = (CodePanLabel) view.findViewById(R.id.tvTimeInTitleSummary);
		CodePanLabel tvTimeOutTitleSummary = (CodePanLabel) view.findViewById(R.id.tvTimeOutTitleSummary);
		btnAddSignature.setOnClickListener(this);
		view.findViewById(R.id.btnTimeOutSummary).setOnClickListener(this);
		view.findViewById(R.id.btnCancelSummary).setOnClickListener(this);
		view.findViewById(R.id.btnEditSummary).setOnClickListener(this);
		view.findViewById(R.id.btnBackSummary).setOnClickListener(this);
		String strTimeIn = main.getConvention(Convention.TIME_IN);
		if(strTimeIn != null && !strTimeIn.isEmpty()) {
			tvTimeInTitleSummary.setText(strTimeIn);
		}
		strTimeOut = main.getConvention(Convention.TIME_OUT);
		if(strTimeOut != null && !strTimeOut.isEmpty()) {
			tvTimeOutTitleSummary.setText(strTimeOut);
		}
		main.overrideBackPress(true);
		computeSummary();
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnAddSignature:
				SignatureFragment signature = new SignatureFragment();
				signature.setOnSignCallback(this);
				signature.setSignature(this.signature);
				signature.setAttendance(attendance);
				main.addSlidingToMain(signature, this);
				break;
			case R.id.btnTimeOutSummary:
				if(attendance != null) {
//					if(!TarkieLib.isSettingEnabled(db, Settings.TIME_OUT_SIGNATURE) || signature != null) {
					if(TarkieLib.isTimeIn(db)) {
						saveTimeOut();
					}
//					}
//					else {
//						TarkieLib.alertDialog(main, "Signature Required", "Please add a signature.");
//					}
				}
				break;
			case R.id.btnCancelSummary:
				onBackPressed();
				break;
			case R.id.btnEditSummary:
				break;
			case R.id.btnBackSummary:
				manager.popBackStack();
				break;
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialogFragment alert = new AlertDialogFragment();
		alert.setOnFragmentCallback(this);
		alert.setDialogTitle("Cancel " + strTimeOut);
		alert.setDialogMessage("Are you sure you want to cancel " + strTimeOut.toLowerCase() + "?");
		alert.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(photo != null) {
					CodePanUtils.deleteFile(main, App.FOLDER, photo);
				}
				manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
			}
		});
		alert.setNegativeButton("No", new OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
			}
		});
		main.addFadingToMain(alert);
	}

	@Override
	public void onFragment(boolean status) {
		main.overrideBackPress(!status);
	}

	private void computeSummary() {
		if(attendance != null) {
			TimeOutObj timeOutObj = attendance.timeOut;
			TimeInObj timeInObj = timeOutObj.timeIn;
			String dateOut = CodePanUtils.getCalendarDate(timeOutObj.dDate, false, true);
			String timeOut = CodePanUtils.getNormalTime(timeOutObj.dTime, false);
			if(timeInObj != null) {
				long millisIn = CodePanUtils.dateTimeToMillis(timeInObj.dDate, timeInObj.dTime);
				long millisOut = CodePanUtils.dateTimeToMillis(timeOutObj.dDate, timeOutObj.dTime);
				long totalBreak = attendance.totalBreak;
				long millisWork = millisOut - millisIn;
				long millisNet = millisWork - totalBreak;
				String dateIn = CodePanUtils.getCalendarDate(timeInObj.dDate, false, true);
				String timeIn = CodePanUtils.getNormalTime(timeInObj.dTime, false);
				String breakHours = CodePanUtils.millisToHours(totalBreak);
				String workHours = CodePanUtils.millisToHours(millisWork);
				String netHours = CodePanUtils.millisToHours(millisNet);
				tvTimeInSummary.setText(dateIn + ", " + timeIn);
				tvTimeOutSummary.setText(dateOut + ", " + timeOut);
				tvTotalBreakSummary.setText(breakHours);
				tvTotalWorkHrsSummary.setText(workHours);
				tvNetWorkHrsSummary.setText(netHours);
			}
			else {
				tvTimeOutSummary.setText(dateOut + ", " + timeOut);
			}
		}
	}

	public void setAttendance(AttendanceObj attendance) {
		this.attendance = attendance;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	private void saveTimeOut() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String storeID = null;
					if(TarkieLib.isSettingEnabled(db, TIME_IN_OUT_STORE)) {
						StoreObj store = TarkieLib.getDefaulStore(db);
						storeID = store != null ? store.ID : null;
					}
					TimeOutObj timeOut = attendance.timeOut;
					int battery = CodePanUtils.getBatteryLevel(main);
					TarkieLib.saveIncident(db, gps, Incident.TIME_OUT_BATTERY_LEVEL, battery);
					timeOut.ID = TarkieLib.saveTimeOut(db, gps, timeOut.dDate, timeOut.dTime, storeID,
							photo, signature, battery);
					if(timeOut.ID != null) {
						timeOutHandler.obtainMessage(Result.SUCCESS, timeOut).sendToTarget();
					}
					else {
						timeOutHandler.obtainMessage(Result.FAILED).sendToTarget();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	Handler timeOutHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
			switch(msg.what) {
				case Result.SUCCESS:
					CodePanUtils.alertToast(main, strTimeOut + " successful");
					main.checkTimeIn();
					main.updateSyncCount();
					MainService service = main.getService();
					service.setRunning(false);
//					service.syncData(db);
					TimeOutObj out = (TimeOutObj) msg.obj;
					main.checkOvertime(out, false);
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(main, "Failed to save " + strTimeOut.toLowerCase() + ".");
					break;
			}
			return true;
		}
	});

	public void setGps(GpsObj gps) {
		this.gps = gps;
	}

	@Override
	public void onSign(String fileName) {
		if(fileName != null) {
			this.signature = fileName;
			Bitmap bitmap = CodePanUtils.getBitmapImage(main, App.FOLDER, fileName);
			ivSignatureSummary.setImageBitmap(bitmap);
			flSignatureSummary.setVisibility(View.VISIBLE);
			btnAddSignature.setVisibility(View.GONE);
		}
	}

	public void setIsTimeOut(boolean timeOut) {
		isTimeOut = timeOut;
	}
}
