package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnCountdownFinishCallback;
import com.mobileoptima.callback.Interface.OnGPSFixedCallback;
import com.mobileoptima.constant.App;

public class SearchGPSFragment extends Fragment {

	private MainActivity main;
	private FragmentManager manager;
	private OnGPSFixedCallback gpsFixedCallback;
	private OnCountdownFinishCallback countdownFinishCallback;
	private ImageView ivLoadingSearchGPS;
	private CodePanLabel tvCountDownSearchGPS;
	private Animation animation;
	private Thread thread;
	private GpsObj gps;
	private boolean runThread, isGPSFixed, isPause, isPending, withCounter;
	private int countdown;

	@Override
	public void onResume() {
		super.onResume();
		if(isPause && isPending) {
			isPending = false;
			showResult();
		}
		isPause = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSearch();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_gps_layout, container, false);
		tvCountDownSearchGPS = (CodePanLabel) view.findViewById(R.id.tvCountDownSearchGPS);
		ivLoadingSearchGPS = (ImageView) view.findViewById(R.id.ivLoadingSearchGPS);
		animation = AnimationUtils.loadAnimation(main, R.anim.rotate_clockwise);
		searchGPS();
		return view;
	}

	private void searchGPS() {
		countdown = App.GPS_COUNTDOWN;
		withCounter = true;
		ivLoadingSearchGPS.startAnimation(animation);
		tvCountDownSearchGPS.setText(String.valueOf(countdown));
		runThread = true;
		isGPSFixed = false;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					while(runThread) {
						GpsObj obj = main.getGps();
						if(obj.isValid) {
							isGPSFixed = true;
							gps = obj;
						}
						handler.sendMessage(handler.obtainMessage());
						Thread.sleep(1000);
						countdown = countdown != 0 ? countdown - 1 : countdown;
					}
				}
				catch(SecurityException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(runThread) {
				checkGpsStatus();
			}
			return true;
		}
	});

	private void checkGpsStatus() {
		if(CodePanUtils.isGpsEnabled(main)) {
			if(isGPSFixed) {
				stopSearch();
				if(!isPause) {
					showResult();
				}
				else {
					isPending = true;
				}
			}
			else {
				if(withCounter) {
					tvCountDownSearchGPS.setText(String.valueOf(countdown));
					if(countdown == 0) {
						if(!isPause) {
							showResult();
						}
						else {
							isPending = true;
						}
					}
				}
			}
		}
		else {
			if(!isPause) {
				manager.popBackStack();
			}
		}
		tvCountDownSearchGPS.setText(String.valueOf(countdown));
	}

	private void stopSearch() {
		ivLoadingSearchGPS.clearAnimation();
		runThread = false;
		if(thread.isAlive()) {
			thread.interrupt();
		}
	}

	public void showResult() {
		manager.popBackStack();
		if(isGPSFixed) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(R.string.gps_acquired_title);
			alert.setDialogMessage(R.string.gps_acquired_message);
			alert.setPositiveButton("Yes", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					manager.popBackStack();
					if(isGPSFixed && gpsFixedCallback != null) {
						gpsFixedCallback.onGPSFixed(gps);
					}
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
		else {
			if(countdownFinishCallback != null) {
				countdownFinishCallback.onCountdownFinish();
			}
		}
	}

	public void setOnGpsFixedCallback(OnGPSFixedCallback gpsFixedCallback) {
		this.gpsFixedCallback = gpsFixedCallback;
	}

	public void setOnCountdownFinishCallback(OnCountdownFinishCallback countdownFinishCallback) {
		this.countdownFinishCallback = countdownFinishCallback;
	}
}
