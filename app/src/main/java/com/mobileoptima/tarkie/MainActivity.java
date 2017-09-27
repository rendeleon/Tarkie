package com.mobileoptima.tarkie;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.cache.SQLiteCache;
import com.mobileoptima.callback.Interface.OnCountdownFinishCallback;
import com.mobileoptima.callback.Interface.OnGPSFixedCallback;
import com.mobileoptima.callback.Interface.OnInitializeCallback;
import com.mobileoptima.callback.Interface.OnLoginCallback;
import com.mobileoptima.callback.Interface.OnSelectScheduleCallback;
import com.mobileoptima.callback.Interface.OnSelectStoreCallback;
import com.mobileoptima.callback.Interface.OnTimeInCallback;
import com.mobileoptima.callback.Interface.OnTimeOutCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.constant.Function.Action;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.constant.Incident;
import com.mobileoptima.constant.ProcessName;
import com.mobileoptima.constant.Result;
import com.mobileoptima.constant.TabType;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreaksObj;
import com.mobileoptima.model.EmployeeObj;
import com.mobileoptima.model.ScheduleObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.service.MainService;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.mobileoptima.constant.Settings.ALLOW_SELECT_SCHEDULE;
import static com.mobileoptima.constant.Settings.MINIMUM_OT;
import static com.mobileoptima.constant.Settings.MULTIPLE_TIME_IN;
import static com.mobileoptima.constant.Settings.TIME_IN_OUT_STORE;

public class MainActivity extends FragmentActivity implements OnInitializeCallback, OnRefreshCallback,
		OnLoginCallback, OnClickListener, ServiceConnection, OnGPSFixedCallback, OnCountdownFinishCallback,
		OnSelectStoreCallback, OnSelectScheduleCallback, OnTimeInCallback, OnTimeOutCallback {

	private OnBackPressedCallback backPressedCallback;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private SQLiteAdapter db;
	private DrawerLayout dlMain;
	private RelativeLayout rlMain, rlMenuMain, rlSyncMain;
	private LinearLayout llTimeInMain, llStoresMain;
	private FrameLayout flContainerMain, flHomeMain, flVisitMain, flAddVisitMain;
	private CodePanButton btnMenuMain, btnHomeMain, btnVisitMain, btnAddVisitMain;
	private CodePanLabel tvStoresMain, tvTimeInMain, tvEmployeeNameMain, tvEmployeeNoMain, tvSyncMain,
			tvVisitMain, tvAddVisitMain;
	private ImageView ivTimeInMain, ivTimeOutMain, ivLogoMain, ivEmployeeMain;
	private MainService service;
	private boolean isInitialized, isServiceConnected, isPause, isGpsOff, isOverrideBackPress;
	private String tabType = TabType.DEFAULT, strTimeIn, strTimeOut, strStores, strVisit;
	private int minOTHr;

	@Override
	protected void onStart() {
		super.onStart();
		if(isInitialized) {
			bindService();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(isInitialized) {
			unbindService();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
		if(isInitialized) {
			updateSyncCount();
			checkTimeIn();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
		if(isInitialized) {
			checkTimeIn();
		}
	}

	//
//
//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		manager = getSupportFragmentManager();
		dlMain = (DrawerLayout) findViewById(R.id.dlMain);
		rlMain = (RelativeLayout) findViewById(R.id.rlMain);
		rlMenuMain = (RelativeLayout) findViewById(R.id.rlMenuMain);
		rlSyncMain = (RelativeLayout) findViewById(R.id.rlSyncMain);
		llTimeInMain = (LinearLayout) findViewById(R.id.llTimeInMain);
		flContainerMain = (FrameLayout) findViewById(R.id.flContainerMain);
		flHomeMain = (FrameLayout) findViewById(R.id.flHomeMain);
		flVisitMain = (FrameLayout) findViewById(R.id.flVisitMain);
		flAddVisitMain = (FrameLayout) findViewById(R.id.flAddVisitMain);
		btnMenuMain = (CodePanButton) findViewById(R.id.btnMenuMain);
		btnHomeMain = (CodePanButton) findViewById(R.id.btnHomeMain);
		btnVisitMain = (CodePanButton) findViewById(R.id.btnVisitMain);
		btnAddVisitMain = (CodePanButton) findViewById(R.id.btnAddVisitMain);
		tvStoresMain = (CodePanLabel) findViewById(R.id.tvStoresMain);
		tvTimeInMain = (CodePanLabel) findViewById(R.id.tvTimeInMain);
		tvEmployeeNameMain = (CodePanLabel) findViewById(R.id.tvEmployeeNameMain);
		tvEmployeeNoMain = (CodePanLabel) findViewById(R.id.tvEmployeeNoMain);
		tvSyncMain = (CodePanLabel) findViewById(R.id.tvSyncMain);
		tvVisitMain = (CodePanLabel) findViewById(R.id.tvVisitMain);
		tvAddVisitMain = (CodePanLabel) findViewById(R.id.tvAddVisitMain);
		ivTimeInMain = (ImageView) findViewById(R.id.ivTimeInMain);
		ivTimeOutMain = (ImageView) findViewById(R.id.ivTimeOutMain);
		ivLogoMain = (ImageView) findViewById(R.id.ivLogoMain);
		ivEmployeeMain = (ImageView) findViewById(R.id.ivEmployeeMain);
		llTimeInMain.setOnClickListener(this);
		btnMenuMain.setOnClickListener(this);
		btnHomeMain.setOnClickListener(this);
		btnVisitMain.setOnClickListener(this);
		btnAddVisitMain.setOnClickListener(this);
		findViewById(R.id.llStoresMain).setOnClickListener(this);
		findViewById(R.id.llUpdateMasterfileMain).setOnClickListener(this);
		findViewById(R.id.llSendBackupMain).setOnClickListener(this);
		findViewById(R.id.llLogoutMain).setOnClickListener(this);
		findViewById(R.id.llBreaksMain).setOnClickListener(this);
		findViewById(R.id.btnSyncMain).setOnClickListener(this);
		btnAddVisitMain.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					tvAddVisitMain.setPressed(true);
				}
				if(event.getAction() == MotionEvent.ACTION_UP) {
					tvAddVisitMain.setPressed(false);
				}
				return true;
			}
		});
		minOTHr = getResources().getInteger(R.integer.min_overtime_hour);
		dlMain.setScrimColor(getResources().getColor(R.color.black_trans_twenty));
		init(savedInstanceState);
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnMenuMain:
				if(dlMain.isDrawerOpen(rlMenuMain)) {
					dlMain.closeDrawer(rlMenuMain);
				}
				else {
					dlMain.openDrawer(rlMenuMain);
				}
				break;
			case R.id.llTimeInMain:
				dlMain.closeDrawer(rlMenuMain);
				if(!TarkieLib.isTimeOut(db) || TarkieLib.isTimeIn(db) ||
						TarkieLib.isSettingEnabled(db, MULTIPLE_TIME_IN)) {
					if(!TarkieLib.isTimeIn(db)) {
						if(isGpsSecured()) {
							GpsObj gps = getGps();
							if(!gps.isValid) {
								SearchGPSFragment searchGPSFragment = new SearchGPSFragment();
								searchGPSFragment.setOnGpsFixedCallback(this);
								searchGPSFragment.setOnCountdownFinishCallback(this);
								addFadingToMain(searchGPSFragment);
							}
							else {
								onGPSFixed(gps);
							}
						}
						else {
							isGpsOff = true;
						}
					}
					else {
						AlertDialogFragment alert = new AlertDialogFragment();
						alert.setDialogTitle("Confirm " + strTimeOut);
						alert.setDialogMessage("Do you want to " + strTimeOut.toLowerCase() + "?");
						alert.setPositiveButton("Yes", new OnClickListener() {
							@Override
							public void onClick(View v) {
								manager.popBackStack();
								SelectStoreFragment selectStore = new SelectStoreFragment();
								selectStore.setOnSelectStoreCallback(MainActivity.this);
								addFadingToMain(selectStore, Tag.SELECT_STORE);
							}
						});
						alert.setNegativeButton("Cancel", new OnClickListener() {
							@Override
							public void onClick(View v) {
								manager.popBackStack();
							}
						});
						addFadingToMain(alert);
					}
				}
				else {
					String timeIn = strTimeIn.toLowerCase();
					TarkieLib.alertDialog(this, "Already " + strTimeIn, "You've already " + timeIn
							+ ", you're not allowed to " + timeIn + " anymore.");
				}
				break;
			case R.id.llLogoutMain:
				dlMain.closeDrawer(rlMenuMain);
				if(!TarkieLib.isTimeIn(db)) {
					final AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle(R.string.logout_title);
					alert.setDialogMessage(R.string.logout_message);
					alert.setPositiveButton("Yes", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
							if(TarkieLib.logout(db)) {
//								TarkieLib.saveIncident(db, null, Incident.LOG_OUT);
//								analytics.trackLogout();
								manager.popBackStack();
								onRefresh();
							}
						}
					});
					alert.setNegativeButton("Cancel", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
						}
					});
					addFadingToMain(alert);
				}
				else {
					TarkieLib.alertDialog(this, R.string.cannot_logout_title, R.string.cannot_logout_message);
				}
				break;
			case R.id.llUpdateMasterfileMain:
				dlMain.closeDrawer(rlMenuMain);
				if(!CodePanUtils.hasInternet(this)) {
					CodePanUtils.alertToast(this, R.string.no_internet_message);
				}
				else {
					AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle(R.string.update_master_file_title);
					alert.setDialogMessage(R.string.update_master_file_message);
					alert.setPositiveButton("Yes", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
							LoadingFragment loading = new LoadingFragment();
							loading.setAction(Action.UPDATE_MASTER_FILE);
							loading.setOnRefreshCallback(MainActivity.this);
							addFadingToMain(loading);
						}
					});
					alert.setNegativeButton("No", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
						}
					});
					addFadingToMain(alert);
				}
				break;
			case R.id.llStoresMain:
				dlMain.closeDrawer(rlMenuMain);
				StoresFragment store = new StoresFragment();
				addSlidingToMain(store, null);
				break;
			case R.id.btnHomeMain:
				if(!tabType.equals(TabType.HOME)) {
					Fragment current = manager.findFragmentByTag(tabType);
					Fragment old = manager.findFragmentByTag(TabType.HOME);
					transaction = manager.beginTransaction();
					if(old != null) {
						transaction.show(old);
					}
					else {
						HomeFragment home = new HomeFragment();
						transaction.add(R.id.flContainerMain, home, TabType.HOME);
					}
					if(current != null) {
						transaction.hide(current);
					}
					transaction.commit();
				}
				setTabType(TabType.HOME);
				break;
			case R.id.llBreaksMain:
				dlMain.closeDrawer(rlMenuMain);
				if(TarkieLib.isTimeIn(db)) {
					if(TarkieLib.hasBreak(db)) {
						BreaksFragment breaks = new BreaksFragment();
						addFadingToMain(breaks);
					}
					else {
						CodePanUtils.alertToast(this, R.string.no_break);
					}
				}
				else {
					CodePanUtils.alertToast(this, "Please " + strTimeIn.toLowerCase()
							+ " first before taking your break.");
				}
				break;
			case R.id.btnSyncMain:
				if(!CodePanUtils.hasInternet(this)) {
					CodePanUtils.alertToast(this, R.string.no_internet_message);
					break;
				}
				if(CodePanUtils.isThreadRunning(ProcessName.SYNC_DATA)) {
					TarkieLib.alertDialog(this, R.string.currently_syncing_title, R.string.currently_syncing_message);
					break;
				}
				int count = TarkieLib.getCountSyncTotal(db);
				if(count == 0) {
					CodePanUtils.alertToast(this, R.string.no_data_to_sync_message);
				}
				else {
					String transactions = count == 1 ? "transaction" : "transactions";
					AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Sync Data");
					alert.setDialogMessage("You have " + count + " unsaved " + transactions + ". " +
							"Do you want to send it to the server now?");
					alert.setPositiveButton("Yes", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
							LoadingFragment loading = new LoadingFragment();
							loading.setAction(Action.SYNC_DATA);
							loading.setOnRefreshCallback(MainActivity.this);
							addFadingToMain(loading);
						}
					});
					alert.setNegativeButton("Cancel", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
						}
					});
					addFadingToMain(alert);
				}
				break;
			case R.id.btnVisitMain:
				if(!tabType.equals(TabType.VISITS)) {
					Fragment current = manager.findFragmentByTag(tabType);
					Fragment old = manager.findFragmentByTag(TabType.VISITS);
					transaction = manager.beginTransaction();
					if(old != null) {
						transaction.show(old);
					}
					else {
						VisitsFragment visits = new VisitsFragment();
						transaction.add(R.id.flContainerMain, visits, TabType.VISITS);
					}
					if(current != null) {
						transaction.hide(current);
					}
					transaction.commit();
				}
				setTabType(TabType.VISITS);
				break;
			case R.id.llSendBackupMain:
				dlMain.closeDrawer(rlMenuMain);
				if(!CodePanUtils.hasInternet(this)) {
					CodePanUtils.alertToast(this, R.string.no_internet_message);
				}
				else {
					AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Send Back-up");
					alert.setDialogMessage("Do you want to send back-up?");
					alert.setPositiveButton("Yes", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
							LoadingFragment loading = new LoadingFragment();
							loading.setAction(Action.SEND_BACK_UP);
							loading.setOnRefreshCallback(MainActivity.this);
							addFadingToMain(loading);
						}
					});
					alert.setNegativeButton("Cancel", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
						}
					});
					addFadingToMain(alert);
				}
				break;
		}
	}

	@Override
	public void onInitialize(SQLiteAdapter db) {
		CodePanUtils.withGooglePlayServices(this);
		this.isInitialized = true;
		this.db = db;
		bindService();
		authenticate();
		setDefaultTab();
		setConventions();
		updateUser();
		setLogo();
		setTabs();
		reloadSchedule();
		updateSyncCount();
		backUpDB(true);
		// test
//		Thread thread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				String dimen = "";
//				for(int i = 1; i < 101; i++) {
//					dimen += "<dimen name=\"ndp" + i + "\">-" + i + "dp</dimen>\n";
//				}
//				int chunkCount = dimen.length() / 4000;
//				for(int c = 0; c <= chunkCount; c++) {
//					int max = 4000 * (c + 1);
//					if(max >= dimen.length()) {
//						Log.v("tag", "chunk " + c + " of " + chunkCount + ":\n" + dimen.substring(4000 * c));
//					} else {
//						Log.v("tag", "chunk " + c + " of " + chunkCount + ":\n" + dimen.substring(4000 * c, max));
//					}
//				}
//			}
//		});
//		thread.start();
	}

	@Override
	public void onRefresh() {
		bindService();
		authenticate();
		setDefaultTab();
		setConventions();
		setLogo();
		setTabs();
		updateUser();
		reloadSchedule();
		updateSyncCount();
		reloadVisits();
	}

	@Override
	public void onBackPressed() {
		if(isInitialized) {
			if(isOverrideBackPress) {
				if(backPressedCallback != null) {
					backPressedCallback.onBackPressed();
				}
			}
			else {
				int count = manager.getBackStackEntryCount();
				if(!tabType.equals(TabType.HOME) && count == 0) {
					setDefaultTab();
				}
				else {
					super.onBackPressed();
				}
			}
		}
		else {
			this.finish();
		}
	}

	@Override
	public void onLogin() {
		// TODO
		updateUser();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		service = ((MainService.LocalBinder) binder).getService();
		isServiceConnected = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		isServiceConnected = false;
	}

	@Override
	public void onGPSFixed(GpsObj gps) {
//		if(!TarkieLib.isSettingEnabled(db, TIME_IN_OUT_STORE)) {
//			onSelectStore(null);
//		}
//		else {
		SelectStoreFragment selectStore = new SelectStoreFragment();
		selectStore.setOnSelectStoreCallback(this);
		addFadingToMain(selectStore);
//		}
	}

	@Override
	public void onCountdownFinish() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(R.string.no_gps_signal_title);
		alert.setDialogMessage(R.string.no_gps_signal_message);
		alert.setPositiveButton("Retry", new OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
				llTimeInMain.performClick();
			}
		});
		alert.setNegativeButton("Proceed", new OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
				onGPSFixed(getGps());
			}
		});
		addFadingToMain(alert, DialogTag.MOCK);
	}

	@Override
	public void onSelectStore(StoreObj store) {
		if(!TarkieLib.isTimeIn(db)) {
			ScheduleObj schedule = TarkieLib.getSchedule(db, CodePanUtils.getDate());
			if(TarkieLib.isSettingEnabled(db, ALLOW_SELECT_SCHEDULE)) {
				SelectScheduleFragment selectSched = new SelectScheduleFragment();
				selectSched.setSchedule(schedule);
				selectSched.setOnSelectScheduleCallback(this);
				addFadingToMain(selectSched);
			}
			else {
				onSelectSchedule(schedule);
			}
		}
		else {
			String date = CodePanUtils.getDate();
			String time = CodePanUtils.getTime();
			GpsObj gps = getGps();
//			if(TarkieLib.isSettingEnabled(db, TIME_OUT_PHOTO)) {
			CameraFragment camera = new CameraFragment();
			camera.setGps(gps);
			camera.isFrontCamDefault(true);
			camera.setImageType(ImageType.TIME_OUT);
			addSlidingToMain(camera, null);
//			}
//			else {
//				// TODO onTimeOut
//			}
		}
	}

	@Override
	public void onSelectSchedule(ScheduleObj schedule) {
		GpsObj gps = getGps();
//		if(TarkieLib.isSettingEnabled(db, TIME_IN_PHOTO)) {
		CameraFragment camera = new CameraFragment();
		camera.setGps(gps);
		camera.setSchedule(schedule);
		camera.setImageType(ImageType.TIME_IN);
		camera.isFrontCamDefault(true);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.replace(R.id.rlMain, camera);
		transaction.addToBackStack(null);
		transaction.commit();
//		}
//		else {
//			// TODO onTimeIn
//		}
	}

	@Override
	public void onTimeIn(final TimeInObj timeIn) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String storeID = null;
					if(TarkieLib.isSettingEnabled(db, TIME_IN_OUT_STORE)) {
						StoreObj store = TarkieLib.getDefaulStore(db);
						storeID = store != null ? store.ID : null;
						timeIn.store = store;
					}
					String schedID = timeIn.schedule != null ? timeIn.schedule.ID : null;
					if(TarkieLib.saveTimeIn(db, timeIn.gps, storeID, schedID, timeIn.photo)) {
						int battery = CodePanUtils.getBatteryLevel(MainActivity.this);
						// TODO analytics.trackTimeIn(battery);
						TarkieLib.saveIncident(db, timeIn.gps, Incident.TIME_IN_BATTERY_LEVEL, battery);
						timeInHandler.obtainMessage(Result.SUCCESS).sendToTarget();
					}
					else {
						timeInHandler.obtainMessage(Result.FAILED).sendToTarget();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	@Override
	public void onTimeOut(TimeOutObj timeOut) {
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		timeOut.dDate = dDate;
		timeOut.dTime = dTime;
		AttendanceObj attendance = TarkieLib.getAttendance(db, TarkieLib.getTimeInID(db));
		attendance.timeOut.dDate = dDate;
		attendance.timeOut.dTime = dTime;
		SummaryFragment summary = new SummaryFragment();
		summary.setIsTimeOut(true);
		summary.setAttendance(attendance);
		summary.setPhoto(timeOut.photo);
		summary.setGps(timeOut.gps);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		Fragment photo = manager.findFragmentByTag(Tag.PHOTO);
		if(photo != null) {
			transaction.add(R.id.rlMain, summary);
			transaction.hide(photo);
		}
		else {
			transaction.replace(R.id.rlMain, summary);
		}
		transaction.addToBackStack(null);
		transaction.commit();
	}

	Handler timeInHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					Fragment fragment = manager.findFragmentByTag(TabType.HOME);
					if(fragment != null) {
						HomeFragment home = (HomeFragment) fragment;
						SpannableStringBuilder ssb = home.getTimeInStatus();
						CodePanUtils.alertToast(MainActivity.this, ssb, Toast.LENGTH_LONG);
					}
					manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
					checkTimeIn();
					updateSyncCount();
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(MainActivity.this, "Failed to save " +
							strTimeIn.toLowerCase() + ".");
					break;
			}
			return true;
		}
	});

	private void init(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			if(isInitialized) {
				checkRevokedPermissions();
			}
			else {
				this.finish();
				overridePendingTransition(0, 0);
				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
		}
		else {
			SplashFragment splash = new SplashFragment();
			splash.setOnInitializeCallback(this);
			addToMain(splash);
		}
	}

	public void checkRevokedPermissions() {
		if(!CodePanUtils.isPermissionGranted(this)) {
			manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
			Intent intent = new Intent(this, getClass());
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			this.finish();
		}
	}

	public SQLiteAdapter getDatabase() {
		if(db == null) {
			db = SQLiteCache.getDatabase(this, App.DB);
		}
		return db;
	}

	private void bindService() {
		Intent service = new Intent(this, MainService.class);
		bindService(service, this, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		if(isServiceConnected) {
			unbindService(this);
		}
	}

	public GpsObj getGps() {
		if(!isServiceConnected) {
			bindService();
		}
		return service.getGps();
	}

	public void authenticate() {
		if(!TarkieLib.isAuthorized(db)) {
			AuthorizationFragment authFragment = new AuthorizationFragment();
			authFragment.setOnRefreshCallback(this);
			replaceMain(authFragment);
		}
		else {
			if(!TarkieLib.isLoggedIn(db)) {
				LoginFragment loginFragment = new LoginFragment();
				loginFragment.setOnRefreshCallback(this);
				loginFragment.setOnLoginCallback(this);
				replaceMain(loginFragment);
			}
		}
	}

	private void setConventions() {
		strTimeIn = TarkieLib.getConvention(db, Convention.TIME_IN);
		strTimeOut = TarkieLib.getConvention(db, Convention.TIME_OUT);
		strStores = TarkieLib.getConvention(db, Convention.STORES);
		strVisit = TarkieLib.getConvention(db, Convention.VISIT);
		tvStoresMain.setText(strStores);
		tvVisitMain.setText(strVisit);
		checkTimeIn();
	}

	public void checkTimeIn() {
		boolean isTimeIn = TarkieLib.isTimeIn(db);
		Intent service = new Intent(this, MainService.class);
		if(!isTimeIn) {
			tvTimeInMain.setText(strTimeIn);
			ivTimeInMain.setVisibility(View.VISIBLE);
			ivTimeOutMain.setVisibility(View.GONE);
			stopService(service);
		}
		else {
			tvTimeInMain.setText(strTimeOut);
			ivTimeInMain.setVisibility(View.GONE);
			ivTimeOutMain.setVisibility(View.VISIBLE);
			startService(service);
		}
	}

	private void setLogo() {
		String logoUrl = TarkieLib.getCompanyLogo(db);
		if(logoUrl != null && !logoUrl.isEmpty()) {
			CodePanUtils.displayImage(ivLogoMain, logoUrl);
			Fragment fragment = manager.findFragmentByTag(TabType.HOME);
			if(fragment != null) {
				HomeFragment home = (HomeFragment) fragment;
				home.updateLogo(logoUrl);
			}
		}
	}

	public boolean isGpsSecured() {
		if(!CodePanUtils.isGpsEnabled(this)) {
			if(!CodePanUtils.isOnBackStack(this, DialogTag.GPS) && !isPause) {
				final AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle(R.string.gps_title);
				alert.setDialogMessage(R.string.gps_message);
				alert.setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(View view) {
						manager.popBackStack();
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});
				alert.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(View view) {
						manager.popBackStack();
						if(TarkieLib.isTimeIn(db)) {
							finish();
						}
					}
				});
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
						R.anim.fade_in, R.anim.fade_out);
				transaction.add(R.id.rlMain, alert, DialogTag.GPS);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		}
		else if(CodePanUtils.isMockEnabled(this)) {
			if(!CodePanUtils.isOnBackStack(this, DialogTag.MOCK) && !isPause) {
				final AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle(R.string.mock_title);
				alert.setDialogMessage(R.string.mock_message);
				alert.setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(View view) {
						manager.popBackStack();
						Intent intent = new Intent(Settings.ACTION_SETTINGS);
						startActivity(intent);
					}
				});
				alert.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(View view) {
						manager.popBackStack();
						if(TarkieLib.isTimeIn(db)) {
							finish();
						}
					}
				});
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
						R.anim.fade_in, R.anim.fade_out);
				transaction.add(R.id.rlMain, alert, DialogTag.MOCK);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		}
		else {
			Fragment gps = manager.findFragmentByTag(DialogTag.GPS);
			Fragment mock = manager.findFragmentByTag(DialogTag.GPS);
			transaction = manager.beginTransaction();
			if(gps != null) transaction.remove(gps);
			if(mock != null) transaction.remove(gps);
			transaction.commit();
			return true;
		}
		return false;
	}

	public void backUpDB(final boolean external) {
		if(!CodePanUtils.isThreadRunning(ProcessName.BACK_UP_DB)) {
			Thread bg = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if(TarkieLib.isAuthorized(db)) {
							boolean result = TarkieLib.backUpDatabase(MainActivity.this, external);
							if(external && result) {
//								analytics.trackBackUpData();
								backUpDBHandler.obtainMessage().sendToTarget();
							}
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			bg.setName(ProcessName.BACK_UP_DB);
			bg.start();
		}
	}

	Handler backUpDBHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			Log.e("Back up", "Data has been successfully backed-up.");
			return true;
		}
	});

	public void addToMain(Fragment fragment) {
		transaction = manager.beginTransaction();
		transaction.add(R.id.rlMain, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void replaceMain(Fragment fragment) {
		transaction = manager.beginTransaction();
		transaction.replace(R.id.rlMain, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void addFadingToMain(Fragment fragment) {
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void addFadingToMain(Fragment fragment, String tag) {
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, fragment, tag);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void addSlidingToMain(Fragment fragment) {
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void addSlidingToMain(Fragment fragment, Fragment fragmentToHide) {
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, fragment);
		if(fragmentToHide != null) {
			transaction.hide(fragmentToHide);
		}
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void addSlidingToMain(Fragment fragment, Fragment fragmentToHide, String tag) {
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, fragment, tag);
		if(fragmentToHide != null) {
			transaction.hide(fragmentToHide);
		}
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void overrideBackPress(boolean overrideBackPress) {
		isOverrideBackPress = overrideBackPress;
	}

	public void setOnBackPressedCallback(OnBackPressedCallback backPressedCallback) {
		this.backPressedCallback = backPressedCallback;
	}

	public String getConvention(String type) {
		String convention = null;
		switch(type) {
			case Convention.STORES:
				convention = strStores;
				break;
			case Convention.TIME_IN:
				convention = strTimeIn;
				break;
			case Convention.TIME_OUT:
				convention = strTimeOut;
				break;
			case Convention.VISIT:
				convention = strVisit;
				break;
		}
		return convention;
	}

	public MainService getService() {
		return service;
	}

	private void setTabType(String tabType) {
		flHomeMain.setVisibility(View.GONE);
		flVisitMain.setVisibility(View.GONE);
		flAddVisitMain.setVisibility(View.GONE);
		rlSyncMain.setVisibility(View.GONE);
		switch(tabType) {
			case TabType.HOME:
				flHomeMain.setVisibility(View.VISIBLE);
				flVisitMain.setVisibility(View.VISIBLE);
				rlSyncMain.setVisibility(View.VISIBLE);
				flAddVisitMain.setVisibility(View.GONE);
				break;
			case TabType.VISITS:
				flHomeMain.setVisibility(View.VISIBLE);
				flVisitMain.setVisibility(View.VISIBLE);
				rlSyncMain.setVisibility(View.GONE);
				flAddVisitMain.setVisibility(View.GONE);
				break;
		}
		this.tabType = tabType;
	}

	private void setDefaultTab() {
		if(btnHomeMain != null) {
			btnHomeMain.performClick();
		}
	}

	public void updateUser() {
		String empID = TarkieLib.getEmployeeID(db);
		if(empID != null) {
			EmployeeObj employee = TarkieLib.getEmployee(db, empID);
			tvEmployeeNameMain.setText(employee.fullName);
			tvEmployeeNoMain.setText(employee.employeeNo);
			CodePanUtils.displayImage(ivEmployeeMain, employee.imageUrl, R.drawable.ic_user_placeholder);
		}
	}

	public RelativeLayout getMainParent() {
		return this.rlMain;
	}

	public void checkBreakIn() {
		if(TarkieLib.isBreakIn(db)) {
			if(!CodePanUtils.isOnBackStack(this, DialogTag.BREAK)) {
				manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
				BreakInObj in = TarkieLib.getBreakIn(db);
				BreaksObj breaks = TarkieLib.getBreak(db, in.ID);
				BreakTimeFragment breakTime = new BreakTimeFragment();
				breakTime.setBreaks(breaks);
				addFadingToMain(breakTime, DialogTag.BREAK);
			}
		}
	}

	public void checkOvertime(TimeOutObj timeOut, boolean isCheck) {
		TimeInObj timeIn = timeOut.timeIn;
		ScheduleObj schedule = timeIn.schedule;
		if(schedule != null) {
			long in = CodePanUtils.dateTimeToMillis(timeIn.dDate, timeIn.dTime);
			long out = CodePanUtils.dateTimeToMillis(timeOut.dDate, timeOut.dTime);
			long schedIn = CodePanUtils.timeToMillis(schedule.timeIn);
			long schedOut = CodePanUtils.timeToMillis(schedule.timeOut);
			long schedHrsMillis = schedOut - schedIn;
//			long schedHrsMillis = 0;
			long totalWorkHrsMillis = out - in;
			if(totalWorkHrsMillis >= schedHrsMillis) {
				float minOTHr = TarkieLib.getSettingsValue(db, MINIMUM_OT);
				minOTHr = minOTHr != 0 ? minOTHr : this.minOTHr;
				long overtime = totalWorkHrsMillis - schedHrsMillis;
				if(overtime > (minOTHr * 3600000)) {
//				if(overtime > 0) {
					boolean result = isCheck || TarkieLib.setPendingOT(db, timeOut.ID, true);
					if(result && !CodePanUtils.isOnBackStack(this, Tag.OVERTIME)) {
						long startMillis = in + schedHrsMillis;
						OvertimeFragment otFragment = new OvertimeFragment();
						otFragment.setStartDate(CodePanUtils.getDate(startMillis));
						otFragment.setStartTime(CodePanUtils.getTime(startMillis));
						otFragment.setTimeOut(timeOut);
						otFragment.setEligibleOT(overtime);
						otFragment.setTotalWorkHrs(totalWorkHrsMillis);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(0, 0, R.anim.slide_in_ltr, R.anim.slide_out_ltr);
						transaction.add(R.id.rlMain, otFragment, Tag.OVERTIME);
						transaction.addToBackStack(null);
						transaction.commit();
					}
				}
			}
		}
	}

	public void updateSyncCount() {
		int count = TarkieLib.getCountSyncTotal(db);
		if(count > 0) {
			tvSyncMain.setVisibility(View.VISIBLE);
			tvSyncMain.setText(String.valueOf(count));
			int res = count > 99 ? R.dimen.dp8 : R.dimen.dp10;
			tvSyncMain.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(res));
		}
		else {
			tvSyncMain.setVisibility(View.GONE);
		}
	}

	public void reloadVisits() {
		Fragment fragment = manager.findFragmentByTag(TabType.VISITS);
		if(fragment != null) {
			VisitsFragment visits = (VisitsFragment) fragment;
//			String date = visits.getSelectedDate();
			String date = CodePanUtils.getDate();
//			if(date != null) {
			visits.loadVisits(db, date);
//			}
		}
	}

	private void setTabs() {
		flVisitMain.setVisibility(View.VISIBLE);
	}

	public void reloadSchedule() {
		Fragment fragment = manager.findFragmentByTag(TabType.HOME);
		if(fragment != null) {
			Log.e("reloadSchedule", "reloadSchedule");
			HomeFragment home = (HomeFragment) fragment;
			home.loadSchedule(db);
		}
	}
}

