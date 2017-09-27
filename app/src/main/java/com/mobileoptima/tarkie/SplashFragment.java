package com.mobileoptima.tarkie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.cache.SQLiteCache;
import com.mobileoptima.callback.Interface.OnInitializeCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.constant.RequestCode;

public class SplashFragment extends Fragment {

	private final int DELAY = 2000;
	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private OnInitializeCallback initializeCallback;
	private boolean isPause;

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isPause) {
			checkPermission();
		}
		isPause = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		checkPermission();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.splash_layout, container, false);
	}

	private void checkPermission() {
		if(CodePanUtils.isPermissionGranted(main)) {
			if(CodePanUtils.isOnBackStack(main, DialogTag.PERMISSION)) {
				manager.popBackStack();
			}
			init();
		}
		else {
			if(CodePanUtils.isPermissionHidden(main)) {
				if(!CodePanUtils.isOnBackStack(main, DialogTag.PERMISSION)) {
					showPermissionNote();
				}
			}
			else {
				CodePanUtils.requestPermission(main, RequestCode.PERMISSION);
			}
		}
	}

	private void init() {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					db = SQLiteCache.getDatabase(main, App.DB);
					db.openConnection();
//					db.execQuery("delete from " + Tables.getName(Tables.TB.TASK));
//					db.execQuery("update " + Tables.getName(Tables.TB.CHECK_OUT) + " SET isSync = 0");
					Thread.sleep(DELAY);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	public void showPermissionNote() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(R.string.permission_title);
		alert.setDialogMessage(R.string.permission_message);
		alert.setPositiveButton("Settings", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(Uri.parse("package:" + main.getPackageName()));
				main.startActivity(intent);
			}
		});
		alert.setNegativeButton("Exit", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
				main.finish();
			}
		});
		main.addFadingToMain(alert, DialogTag.PERMISSION);
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(!isPause) {
				manager.popBackStack();
				if(initializeCallback != null) {
					initializeCallback.onInitialize(db);
				}
			}
			return true;
		}
	});

	public void setOnInitializeCallback(OnInitializeCallback initializeCallback) {
		this.initializeCallback = initializeCallback;
	}
}
