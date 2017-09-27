package com.mobileoptima.tarkie;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.Settings;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.VisitObj;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import static android.view.View.GONE;

public class HomeFragment extends Fragment implements ImageLoadingListener, OnClickListener {

	private final long DELAY = 1000L;

	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private LinearLayout llScheduleHome;
	private ImageView ivLogoHome;
	private CodePanLabel tvTimeHome, tvPeriodHome, tvDateHome;
	private ArrayList<VisitObj> visitList;
	private String strTimeIn, strVisit;
	private Resources res;
	private int green;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		res = main.getResources();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		strTimeIn = main.getConvention(Convention.TIME_IN);
		green = res.getColor(R.color.theme_sec);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_layout, container, false);
		llScheduleHome = (LinearLayout) view.findViewById(R.id.llScheduleHome);
		ivLogoHome = (ImageView) view.findViewById(R.id.ivLogoHome);
		tvTimeHome = (CodePanLabel) view.findViewById(R.id.tvTimeHome);
		tvPeriodHome = (CodePanLabel) view.findViewById(R.id.tvPeriodHome);
		tvDateHome = (CodePanLabel) view.findViewById(R.id.tvDateHome);
		view.findViewById(R.id.btnAddVisitHome).setOnClickListener(this);
		updateLogo(TarkieLib.getCompanyLogo(db));
		startClock();
		loadSchedule(db);
		main.checkBreakIn();
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnAddVisitHome:
				strVisit = main.getConvention(Convention.VISIT);
//				if(!TarkieLib.isSettingEnabled(db, Settings.ALLOW_ADD_VISITS)) {
//					TarkieLib.alertDialog(main, "Not Allowed", "You are not allowed to add new " +
//							strVisit.toLowerCase() + ".");
//				}
//				else {
					AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Add " + strVisit);
					alert.setDialogMessage("Are you sure you want to add new " +
							strVisit.toLowerCase() + "?");
					alert.setPositiveButton("Yes", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
							addVisit();
						}
					});
					alert.setNegativeButton("No", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
						}
					});
					main.addFadingToMain(alert);
//				}
				break;
		}
	}

	@Override
	public void onLoadingStarted(String imageUri, View view) {
	}

	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		if(loadedImage != null) {
			float ratio = (float) loadedImage.getWidth() / (float) loadedImage.getHeight();
			view.getLayoutParams().width = (int) ((float) view.getLayoutParams().height * ratio);
		}
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
	}

	public void updateLogo(String logoUrl) {
		if(logoUrl != null) {
			CodePanUtils.displayImage(ivLogoHome, logoUrl, this);
		}
	}

	private void setDateTime() {
		String dDate = CodePanUtils.getDate();
		String time = CodePanUtils.getNormalTime(CodePanUtils.getTime(), false);
		String date = CodePanUtils.getCalendarDate(dDate, true, true);
		String day = CodePanUtils.getDay(dDate) + ",";
		String array[] = time.split(" ");
		tvTimeHome.setText(array[0]);
		tvPeriodHome.setText(array[1]);
		tvDateHome.setText(day + " " + date);
	}

	public SpannableStringBuilder getTimeInStatus() {
		TimeInObj timeIn = TarkieLib.getTimeIn(db);
		if(timeIn != null) {
			StoreObj store = timeIn.store;
			String font = getString(R.string.proxima_nova_semi_bold);
			String date = CodePanUtils.getCalendarDate(timeIn.dDate, true, true);
			String time = CodePanUtils.getNormalTime(timeIn.dTime, false);
			String status = "Your " + strTimeIn.toLowerCase() + " is at ";
			String in = date + ", " + time;
			int start = status.length();
			int end = start + in.length();
			status += in;
			ArrayList<SpannableMap> list = new ArrayList<>();
			list.add(new SpannableMap(main, font, start, end));
			if(store != null && store.name != null) {
				final String at = "\nat ";
				start = status.length() + at.length();
				end = start + store.name.length();
				status += at + store.name;
				list.add(new SpannableMap(main, font, start, end));
			}
			return CodePanUtils.customizeText(list, status);
		}
		return null;
	}

	private void startClock() {
		setDateTime();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setDateTime();
				handler.postDelayed(this, DELAY);
			}
		}, DELAY);
	}

	public void loadSchedule(final SQLiteAdapter db) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				visitList = Data.loadVisits(db, CodePanUtils.getDate(), true);
				scheduleHandler.sendMessage(scheduleHandler.obtainMessage());
			}
		});
		thread.start();
	}

	Handler scheduleHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			llScheduleHome.removeAllViews();
			LayoutInflater inflater = main.getLayoutInflater();
			View view = getView();
			if(view != null) {
				ViewGroup container = (ViewGroup) view.getParent();
				for(final VisitObj visit : visitList) {
					View child = getSchedule(inflater, container, visit);
					llScheduleHome.addView(child);
				}
				main.checkBreakIn();
			}
			return true;
		}
	});

	public View getSchedule(final LayoutInflater inflater, final ViewGroup container, final VisitObj visit) {
		View child = inflater.inflate(R.layout.schedule_list_item, container, false);
		CodePanLabel tvNameSchedule = (CodePanLabel) child.findViewById(R.id.tvNameSchedule);
		CodePanLabel tvAddressSchedule = (CodePanLabel) child.findViewById(R.id.tvAddressSchedule);
		CodePanButton btnItemSchedule = (CodePanButton) child.findViewById(R.id.btnItemSchedule);
		tvNameSchedule.setText(visit.name);
		if(visit.store != null) {
			StoreObj store = visit.store;
			tvAddressSchedule.setText(store.address);
		}
		else {
			tvAddressSchedule.setVisibility(GONE);
		}
		if(visit.isCheckIn) {
			tvNameSchedule.setTextColor(green);
		}
		btnItemSchedule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int index = visitList.indexOf(visit);
				VisitDetailsFragment details = new VisitDetailsFragment();
				details.setVisit(visit);
				details.setOnSaveVisitCallback(new Interface.OnSaveVisitCallback() {
					@Override
					public void onSaveVisit(VisitObj visit) {
						if(!visit.isCheckOut) {
							visitList.set(index, visit);
							View child = getSchedule(inflater, container, visit);
							llScheduleHome.removeViewAt(index);
							llScheduleHome.addView(child, index);
						}
						else {
							if(!visitList.isEmpty() && visitList.get(index).ID.equals(visit.ID)) {
								visitList.remove(index);
								llScheduleHome.removeViewAt(index);
							}
						}
						main.updateSyncCount();
						main.reloadVisits();
					}
				});
				main.addSlidingToMain(details);
			}
		});
		return child;
	}

	private void addVisit() {
		VisitObj visit = (VisitObj) TarkieLib.addTask(db, null);
		visitList.add(visit);
		LayoutInflater inflater = main.getLayoutInflater();
		View view = getView();
		if(view != null) {
			ViewGroup container = (ViewGroup) view.getParent();
			View child = getSchedule(inflater, container, visit);
			llScheduleHome.addView(child);
			CodePanUtils.alertToast(main, "You have added a new " + strVisit + ". Tap " +
					visit.name + " to edit.");
			main.updateSyncCount();
			main.reloadVisits();
		}
	}
}
