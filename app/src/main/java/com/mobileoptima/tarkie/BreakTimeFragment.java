package com.mobileoptima.tarkie;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.BlurBuilder;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreaksObj;

public class BreakTimeFragment extends Fragment implements OnFragmentCallback, OnBackPressedCallback {

	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private ImageView ivBgBreakTime;
	private CodePanLabel tvTimeBreakTime;
	private BreaksObj breaks;
	private BreakInObj breakIn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		main.setOnBackPressedCallback(this);
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		breakIn = TarkieLib.getBreakIn(db);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.break_time_layout, container, false);
		ivBgBreakTime = (ImageView) view.findViewById(R.id.ivBgBreakTime);
		tvTimeBreakTime = (CodePanLabel) view.findViewById(R.id.tvTimeBreakTime);
		CodePanLabel tvTitleBreakTime = (CodePanLabel) view.findViewById(R.id.tvTitleBreakTime);
		CodePanLabel tvMessageBreakTime = (CodePanLabel) view.findViewById(R.id.tvMessageBreakTime);
		view.findViewById(R.id.btnDoneBreakTime).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle("End Break");
				alert.setDialogMessage("Do you really want to end your break?");
				alert.setOnFragmentCallback(BreakTimeFragment.this);
				alert.setPositiveButton("Yes", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						manager.popBackStack();
						saveBreakOut(db, main.getGps(), breakIn);
					}
				});
				alert.setNegativeButton("Cancel", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						manager.popBackStack();
					}
				});
				main.addFadingToMain(alert);
			}
		});
		if(breaks != null) {
			tvTitleBreakTime.setText(breaks.name);
			String duration = breaks.duration > 1 ? breaks.duration + " mins" : "1 min";
			tvMessageBreakTime.setText("You are currently on\n " + breaks.name + ". (" + duration + ")");
		}
		setBackground();
		updateTimeLeft();
		startTimer();
		return view;
	}

	@Override
	public void onFragment(boolean status) {
		main.overrideBackPress(!status);
	}

	@Override
	public void onBackPressed() {
		main.finish();
	}

	private void setBackground() {
		View view = main.getMainParent();
		Bitmap bitmap = BlurBuilder.blur(view);
		if(bitmap != null) {
			ivBgBreakTime.setImageBitmap(bitmap);
		}
	}

	public void setBreaks(BreaksObj breaks) {
		this.breaks = breaks;
	}

	private void startTimer() {
		final long delay = 1000L;
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				updateTimeLeft();
				handler.postDelayed(this, delay);
			}
		}, delay);
	}

	private long getTimeLeft() {
		long timeLeft = 0L;
		if(breakIn != null && breaks != null) {
			long actual = getActualDuration();
			long duration = breaks.duration * 60 * 1000;
			actual = actual < 0 ? actual + 86400 : actual;
			timeLeft = duration - actual;
		}
		return timeLeft;
	}

	private long getActualDuration() {
		long current = System.currentTimeMillis();
		long in = CodePanUtils.dateTimeToMillis(breakIn.dDate, breakIn.dTime);
		return current - in;
	}

	private void updateTimeLeft() {
		long timeLeft = getTimeLeft();
		String time = CodePanUtils.millisToHours(timeLeft) + " left";
		if(tvTimeBreakTime != null) {
			tvTimeBreakTime.setText(time);
		}
	}

	public void saveBreakOut(final SQLiteAdapter db, final GpsObj gps, final BreakInObj breakIn) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(TarkieLib.saveBreakOut(db, gps, breakIn)) {
						long duration = breaks.duration * 60 * 1000;
						long actual = getActualDuration();
						if(actual > duration) {
							long difference = actual - duration;
							int minutes = (int) (difference / 60000);
							// TODO TarkieLib.updateIncident();
						}
						// TODO CodePanUtils.cancelAlarm();
						handler.sendMessage(handler.obtainMessage());
					}
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
			manager.popBackStack();
			main.updateSyncCount();
			return true;
		}
	});
}
