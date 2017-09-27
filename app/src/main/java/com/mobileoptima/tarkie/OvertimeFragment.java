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
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.Result;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.OvertimeReasonObj;
import com.mobileoptima.model.ScheduleObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;

import java.text.NumberFormat;
import java.util.ArrayList;

public class OvertimeFragment extends Fragment implements OnClickListener {

	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private LinearLayout llReasonsOvertime;
	private CodePanTextField etRemarksOvertime;
	private ArrayList<OvertimeReasonObj> reasonList;
	private TimeOutObj timeOut;
	private NumberFormat nf;
	private long totalWorkHrs, eligibleOT;
	private String startDate, startTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.overtime_layout, container, false);
		CodePanLabel tvReasonTitleOvertime, tvEligibleHrsOvertime, tvTotalWorkHrsOvertime,
				tvTimeOutOvertime, tvTimeInOvertime, tvScheduleOvertime, tvDateOvertime,
				tvTimeInTitleOvertime, tvTimeOutTitleOvertime, tvRemarksTitleOvertime;
		llReasonsOvertime = (LinearLayout) view.findViewById(R.id.llReasonsOvertime);
		etRemarksOvertime = (CodePanTextField) view.findViewById(R.id.etRemarksOvertime);
		tvReasonTitleOvertime = (CodePanLabel) view.findViewById(R.id.tvReasonTitleOvertime);
		tvEligibleHrsOvertime = (CodePanLabel) view.findViewById(R.id.tvEligibleHrsOvertime);
		tvTotalWorkHrsOvertime = (CodePanLabel) view.findViewById(R.id.tvTotalWorkHrsOvertime);
		tvTimeOutOvertime = (CodePanLabel) view.findViewById(R.id.tvTimeOutOvertime);
		tvTimeInOvertime = (CodePanLabel) view.findViewById(R.id.tvTimeInOvertime);
		tvScheduleOvertime = (CodePanLabel) view.findViewById(R.id.tvScheduleOvertime);
		tvDateOvertime = (CodePanLabel) view.findViewById(R.id.tvDateOvertime);
		tvTimeInTitleOvertime = (CodePanLabel) view.findViewById(R.id.tvTimeInTitleOvertime);
		tvTimeOutTitleOvertime = (CodePanLabel) view.findViewById(R.id.tvTimeOutTitleOvertime);
		tvRemarksTitleOvertime = (CodePanLabel) view.findViewById(R.id.tvRemarksTitleOvertime);
		view.findViewById(R.id.btnBackOvertime).setOnClickListener(this);
		view.findViewById(R.id.btnSaveOvertime).setOnClickListener(this);
		String strTimeOut = main.getConvention(Convention.TIME_OUT) + ":";
		String strTimeIn = main.getConvention(Convention.TIME_IN) + ":";
		TimeInObj timeIn = timeOut.timeIn;
		ScheduleObj schedule = timeIn.schedule;
		String schedIn = CodePanUtils.getNormalTime(schedule.timeIn, false);
		String schedOut = CodePanUtils.getNormalTime(schedule.timeOut, false);
		String range = schedIn + " - " + schedOut;
		String schedStatus = !schedule.isDayOff ? range : "Rest Day";
		tvDateOvertime.setText(CodePanUtils.getCalendarDate(timeIn.dDate, false, true));
		tvScheduleOvertime.setText(schedStatus);
		tvTimeInOvertime.setText(CodePanUtils.getNormalTime(timeIn.dTime, false));
		tvTimeOutOvertime.setText(CodePanUtils.getNormalTime(timeOut.dTime, false));
		tvTimeOutTitleOvertime.setText(strTimeOut);
		tvTimeInTitleOvertime.setText(strTimeIn);
		tvTotalWorkHrsOvertime.setText(CodePanUtils.millisToHours(totalWorkHrs));
		tvEligibleHrsOvertime.setText(CodePanUtils.millisToHours(eligibleOT));
		CodePanUtils.requiredField(tvReasonTitleOvertime);
		CodePanUtils.requiredField(tvRemarksTitleOvertime);
		loadReasons(db);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBackOvertime:
				manager.popBackStack();
				break;
			case R.id.btnSaveOvertime:
				if(hasSelectedReason()) {
					String remarks = etRemarksOvertime.getText().toString().trim();
					if(!remarks.isEmpty()) {
						saveOT(remarks);
					}
					else {
						CodePanUtils.alertToast(main, R.string.ot_remarks_hint);
					}
				}
				else {
					CodePanUtils.alertToast(main, R.string.ot_reason_required);
				}
				break;
		}
	}

	public void setTimeOut(TimeOutObj timeOut) {
		this.timeOut = timeOut;
	}

	public void setTotalWorkHrs(long totalWorkHrs) {
		this.totalWorkHrs = totalWorkHrs;
	}

	public void setEligibleOT(long eligibleOT) {
		this.eligibleOT = eligibleOT;
	}

	private void loadReasons(final SQLiteAdapter db) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				reasonList = Data.loadOvertimeReasons(db);
				loadReasonHandler.obtainMessage().sendToTarget();
			}
		});
		thread.start();
	}

	Handler loadReasonHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			llReasonsOvertime.removeAllViews();
			LayoutInflater inflater = main.getLayoutInflater();
			View view = getView();
			if(view != null) {
				ViewGroup container = (ViewGroup) view.getParent();
				for(final OvertimeReasonObj reason : reasonList) {
					View vChoice = inflater.inflate(R.layout.multiple_selection_item, container, false);
					LinearLayout llChoiceMS = (LinearLayout) vChoice.findViewById(R.id.llChoiceMS);
					CodePanLabel tvChoiceMS = (CodePanLabel) vChoice.findViewById(R.id.tvChoiceMS);
					final CheckBox cbChoiceMS = (CheckBox) vChoice.findViewById(R.id.cbChoiceMS);
					final int index = reasonList.indexOf(reason);
					tvChoiceMS.setText(reason.name);
					llChoiceMS.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(!cbChoiceMS.isChecked()) {
								cbChoiceMS.setChecked(true);
								reasonList.get(index).isChecked = true;
							}
							else {
								cbChoiceMS.setChecked(false);
								reasonList.get(index).isChecked = false;
							}
						}
					});
					llReasonsOvertime.addView(vChoice);
				}
			}
			return false;
		}
	});

	private boolean hasSelectedReason() {
		if(reasonList != null) {
			for(OvertimeReasonObj reason : reasonList) {
				if(reason.isChecked) {
					return true;
				}
			}
		}
		return false;
	}

	private void saveOT(final String remarks) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					float sec = eligibleOT / 1000;
					float min = sec / 60;
					float hrs = min / 60;
					TimeInObj timeIn = timeOut.timeIn;
					boolean result = TarkieLib.saveOT(db, main.getGps(), timeIn.ID, startDate, startTime,
							timeOut.dDate, timeOut.dTime, remarks, nf.format(hrs), reasonList);
					if(result) {
						result = TarkieLib.setPendingOT(db, timeOut.ID, false);
						saveOTHandler.obtainMessage(result ? Result.SUCCESS : Result.FAILED).sendToTarget();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	Handler saveOTHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
				manager.popBackStack();
				main.updateSyncCount();
				CodePanUtils.alertToast(main, "Overtime has been successfully saved.");
				break;
				case Result.FAILED:
					CodePanUtils.alertToast(main, "Failed to save overtime.");
					break;
			}
			return true;
		}
	});

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
}
