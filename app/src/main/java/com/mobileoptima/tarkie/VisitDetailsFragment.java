package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnCheckInCallback;
import com.mobileoptima.callback.Interface.OnCheckOutCallback;
import com.mobileoptima.callback.Interface.OnSaveVisitCallback;
import com.mobileoptima.callback.Interface.OnSelectStatusCallback;
import com.mobileoptima.callback.Interface.OnSelectStoreCallback;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.constant.Result;
import com.mobileoptima.constant.Settings;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskStatusObj;
import com.mobileoptima.model.VisitObj;

import java.util.ArrayList;

import static com.mobileoptima.constant.Settings.NOTES_IN_VISITS;
import static com.mobileoptima.constant.Settings.RESTRICT_EDIT_VISIT_AFTER_CHECK_OUT;

public class VisitDetailsFragment extends Fragment implements OnClickListener, OnSelectStoreCallback,
		OnCheckInCallback, OnCheckOutCallback, OnBackPressedCallback, OnFragmentCallback, OnSelectStatusCallback {
//			llPhotosVisitDetails

	private OnSaveVisitCallback saveVisitCallback;
	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private FrameLayout flStoreVisitDetails;
	private CodePanButton btnCheckInVisitDetails, btnCheckOutVisitDetails, btnSaveVisitDetails;
	private CodePanLabel tvSaveVisitDetails, tvStoreNameVisitDetails, tvAddressVisitDetails;
	private CodePanTextField etNotesVisitDetails;
	private VisitObj visit;
	private StoreObj store;
	private CheckOutObj checkOut;
	private boolean isFinalized, withChanges;
	private String strStore, strVisit, strTimeIn;

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
		db = main.getDatabase();
		manager = main.getSupportFragmentManager();
		strStore = main.getConvention(Convention.STORES);
		strVisit = main.getConvention(Convention.VISIT);
		strTimeIn = main.getConvention(Convention.TIME_IN);
		isFinalized = TarkieLib.isVisitFinalized(db, visit.ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.visit_details_layout, container, false);
		tvSaveVisitDetails = (CodePanLabel) view.findViewById(R.id.tvSaveVisitDetails);
		tvStoreNameVisitDetails = (CodePanLabel) view.findViewById(R.id.tvStoreNameVisitDetails);
		tvAddressVisitDetails = (CodePanLabel) view.findViewById(R.id.tvAddressVisitDetails);
		btnCheckInVisitDetails = (CodePanButton) view.findViewById(R.id.btnCheckInVisitDetails);
		btnCheckOutVisitDetails = (CodePanButton) view.findViewById(R.id.btnCheckOutVisitDetails);
		etNotesVisitDetails = (CodePanTextField) view.findViewById(R.id.etNotesVisitDetails);
		flStoreVisitDetails = (FrameLayout) view.findViewById(R.id.flStoreVisitDetails);
		CodePanLabel tvHeaderVisitDetails = (CodePanLabel) view.findViewById(R.id.tvHeaderVisitDetails);
		btnSaveVisitDetails = (CodePanButton) view.findViewById(R.id.btnSaveVisitDetails);
		btnCheckInVisitDetails.setOnClickListener(this);
		btnCheckOutVisitDetails.setOnClickListener(this);
		btnSaveVisitDetails.setOnClickListener(this);
		view.findViewById(R.id.btnStoreVisitDetails).setOnClickListener(this);
		view.findViewById(R.id.btnBackVisitDetails).setOnClickListener(this);
		if(visit != null) {
			StoreObj store = visit.store;
			if(store != null) {
				tvStoreNameVisitDetails.setText(store.name);
				tvAddressVisitDetails.setText(store.address);
				tvHeaderVisitDetails.setText(strVisit);
			}
			else {
				String strStore = this.strStore + " Name";
				tvStoreNameVisitDetails.setText(strStore);
				tvAddressVisitDetails.setText(R.string.address);
				tvHeaderVisitDetails.setText(visit.name);
			}
			if(!visit.isFromWeb) {
				flStoreVisitDetails.setVisibility(View.VISIBLE);
			}
			else {
				flStoreVisitDetails.setVisibility(View.GONE);
			}
			if(visit.isCheckIn) {
				CheckInObj checkIn = visit.checkIn;
				setCheckInTime(checkIn.dTime);
			}
			if(visit.isCheckOut) {
				CheckOutObj checkOut = visit.checkOut;
				setCheckOutTime(checkOut.dTime);
			}
			else {
				btnCheckOutVisitDetails.setEnabled(visit.isCheckIn);
			}
			checkEditable();
			etNotesVisitDetails.setText(visit.notes);
			etNotesVisitDetails.addTextChangedListener(new TextWatcher() {
				String lastText = null;

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					lastText = s.toString();
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(visit.isFromWeb && visit.notes != null && !visit.notes.isEmpty()) {
						String text = s.toString();
						if(text.length() < visit.notesLimit) {
							etNotesVisitDetails.setText(visit.notes.substring(0, visit.notesLimit));
							etNotesVisitDetails.setSelection(visit.notesLimit);
						}
						else {
							if(!text.startsWith(visit.notes.substring(0, visit.notesLimit))) {
								etNotesVisitDetails.setText(lastText);
								etNotesVisitDetails.setSelection(visit.notesLimit);
							}
						}
					}
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		}
		btnSaveVisitDetails.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v.getId() == R.id.btnSaveVisitDetails) {
					if(event.getAction() == MotionEvent.ACTION_DOWN) {
						tvSaveVisitDetails.setPressed(true);
					}
					if(event.getAction() == MotionEvent.ACTION_UP) {
						tvSaveVisitDetails.setPressed(false);
						if(store != null) {
							String notes = etNotesVisitDetails.getText().toString().trim();
							if(!TarkieLib.isSettingEnabled(db, NOTES_IN_VISITS) || !notes.isEmpty()) {
								saveTask(db, notes);
							}
							else {
								TarkieLib.alertDialog(main, R.string.required_notes_title,
										R.string.required_notes_message);
							}
						}
						else {
							TarkieLib.alertDialog(main, strStore + " Required", "Please select a " +
									strStore.toLowerCase());
						}
					}
				}
				return true;
			}
		});
		main.overrideBackPress(true);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnStoreVisitDetails:
				main.overrideBackPress(false);
				StoresFragment store = new StoresFragment();
				store.setOnSelectStoreCallback(this);
				main.addSlidingToMain(store, this);
				break;
			case R.id.btnBackVisitDetails:
				onBackPressed();
				break;
			case R.id.btnCheckInVisitDetails:
				main.overrideBackPress(false);
				if(!TarkieLib.isTimeIn(db)) {
					TarkieLib.alertDialog(main, strTimeIn + "Required", "Please " +
							strTimeIn.toLowerCase() + " first before you check in.");
					break;
				}
				if(!TarkieLib.isSettingEnabled(db, Settings.PARALLEL_CHECK_IN_AND_OUT) || TarkieLib.isCheckIn(db)) {
					String title = TarkieLib.getPendingVisits(db);
					String message = getString(R.string.currently_check_in_message);
					String font = getString(R.string.proxima_nova_semi_bold);
					int start = message.indexOf("$");
					int end = start + title.length();
					message = message.replace("$title", title);
					ArrayList<SpannableMap> list = new ArrayList<>();
					list.add(new SpannableMap(main, font, start, end));
					AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle(R.string.currently_check_in_title);
					alert.setSpannableList(list);
					alert.setDialogMessage(message);
					alert.setPositiveButton("OK", new OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
						}
					});
					main.addFadingToMain(alert);
					break;
				}
				if(!visit.isCheckIn) {
					String date = CodePanUtils.getDate();
					String time = CodePanUtils.getTime();
					if(!TarkieLib.isSettingEnabled(db, Settings.CHECK_IN_PHOTO)) {
						CheckInObj checkIn = new CheckInObj();
						checkIn.gps = main.getGps();
						checkIn.dDate = date;
						checkIn.dTime = time;
						checkIn.task = visit;
						onCheckIn(checkIn);
					}
					else {
						CameraFragment camera = new CameraFragment();
						camera.setOnCheckInCallback(this);
						camera.isFrontCamDefault(true);
						camera.setTask(visit);
						camera.setGps(main.getGps());
						camera.setImageType(ImageType.CHECK_IN);
						main.addSlidingToMain(camera, this);
					}
				}
				break;
			case R.id.btnCheckOutVisitDetails:
				main.overrideBackPress(false);
				main.setOnBackPressedCallback(null);
				if(!isFilledUp()) {
					TarkieLib.alertDialog(main, R.string.unfilled_up_visit_title, R.string.unfilled_up_visit_message);
					break;
				}
				if(!visit.isCheckOut) {
					String date = CodePanUtils.getDate();
					String time = CodePanUtils.getTime();
					if(!TarkieLib.isSettingEnabled(db, Settings.CHECK_OUT_PHOTO)) {
						CheckOutObj checkOut = new CheckOutObj();
						checkOut.gps = main.getGps();
						checkOut.dDate = date;
						checkOut.dTime = time;
						CheckInObj checkIn = new CheckInObj();
						checkIn.ID = TarkieLib.getCheckInID(db, visit.ID);
						checkIn.task = visit;
						checkOut.checkIn = checkIn;
						onCheckOut(checkOut);
					}
					else {
						CameraFragment camera = new CameraFragment();
						camera.setOnCheckOutCallback(this);
						camera.isFrontCamDefault(true);
						camera.setTask(visit);
						camera.setGps(main.getGps());
						camera.setImageType(ImageType.CHECK_OUT);
						main.addSlidingToMain(camera, this);
					}
				}
				break;

		}
	}

	@Override
	public void onBackPressed() {
		if(withChanges) {
			AlertDialogFragment alert = new AlertDialogFragment();
			alert.setOnFragmentCallback(this);
			alert.setDialogTitle(R.string.save_changes_title);
			alert.setDialogMessage(R.string.save_changes_message);
			alert.setPositiveButton("Save", new OnClickListener() {
				@Override
				public void onClick(View v) {
					saveTask(db, etNotesVisitDetails.getText().toString().trim());
					manager.popBackStack();
					manager.popBackStack();
				}
			});
			alert.setNegativeButton("Discard", new OnClickListener() {
				@Override
				public void onClick(View v) {
					manager.popBackStack();
					manager.popBackStack();
				}
			});
			main.addFadingToMain(alert);
		}
		else {
			manager.popBackStack();
		}
	}

	@Override
	public void onFragment(boolean status) {
		main.overrideBackPress(!status && withChanges);
		if(!status) {
			main.setOnBackPressedCallback(this);
		}
	}

	@Override
	public void onSelectStore(StoreObj store) {
		manager.popBackStack();
		this.store = store;
		tvStoreNameVisitDetails.setText(store.name);
		tvAddressVisitDetails.setText(store.address);
		Log.e("withChanges", "true");
		setWithChanges(true);
	}

	@Override
	public void onCheckIn(final CheckInObj checkIn) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(TarkieLib.saveCheckIn(db, checkIn)) {
						checkInHandler.obtainMessage(Result.SUCCESS, checkIn).sendToTarget();
					}
					else {
						checkInHandler.obtainMessage(Result.FAILED, checkIn).sendToTarget();
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
	public void onCheckOut(CheckOutObj checkOut) {
		this.checkOut = checkOut;
		visit.notes = etNotesVisitDetails.getText().toString();
		VisitStatusFragment status = new VisitStatusFragment();
		status.setTitle(visit.name);
		status.setStore(visit.store);
		status.setHasNotes(hasNotes());
		status.setOnSelectStatusCallback(this);
		main.addFadingToMain(status);
	}

	@Override
	public void onSelectStatus(final TaskStatusObj status) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(TarkieLib.saveCheckOut(db, checkOut, status)) {
						checkOutHandler.obtainMessage(Result.SUCCESS, checkOut).sendToTarget();
					}
					else {
						checkOutHandler.obtainMessage(Result.FAILED, checkOut).sendToTarget();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	Handler checkInHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					CheckInObj checkIn = (CheckInObj) msg.obj;
					visit.checkIn = checkIn;
					visit.isCheckIn = true;
					setCheckInTime(checkIn.dTime);
					btnCheckOutVisitDetails.setEnabled(true);
					if(store != null) {
						TarkieLib.setDefaultStore(db, store.ID);
					}
					CodePanUtils.alertToast(main, "Check-in successful.");
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(main, "Failed to save check-in.");
					break;
			}
			return true;
		}
	});

	Handler checkOutHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					setWithChanges(false);
					CheckOutObj checkOut = (CheckOutObj) msg.obj;
					visit.checkOut = checkOut;
					visit.isCheckOut = true;
					checkEditable();
					setCheckOutTime(checkOut.dTime);
					if(saveVisitCallback != null) {
						saveVisitCallback.onSaveVisit(visit);
					}
					etNotesVisitDetails.setText(visit.notes);
					CodePanUtils.alertToast(main, "Check-out successful.");
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(main, "Failed to save check-out.");
					break;
			}
			return true;
		}
	});

	public void setVisit(VisitObj visit) {
		this.visit = visit;
		this.store = visit.store;
	}

	private void setCheckInTime(String time) {
		String checkIn = "IN - " + CodePanUtils.getNormalTime(time, false);
		btnCheckInVisitDetails.setText(checkIn);
		btnCheckInVisitDetails.setEnabled(false);
	}

	private void setCheckOutTime(String time) {
		String out = "OUT - " + CodePanUtils.getNormalTime(time, false);
		btnCheckOutVisitDetails.setText(out);
		btnCheckOutVisitDetails.setEnabled(false);
	}

	private boolean isFilledUp() {
		return !TarkieLib.isSettingEnabled(db, RESTRICT_EDIT_VISIT_AFTER_CHECK_OUT) || visit.isUpdate;
	}

	private void checkEditable() {
		if(!isEditable()) {
			etNotesVisitDetails.setEnabled(false);
			tvSaveVisitDetails.setVisibility(View.GONE);
			btnSaveVisitDetails.setVisibility(View.GONE);
		}
		else {
			etNotesVisitDetails.setEnabled(true);
			btnSaveVisitDetails.setVisibility(View.VISIBLE);
			tvSaveVisitDetails.setVisibility(View.VISIBLE);
		}
	}

	private boolean isEditable() {
		return !(TarkieLib.isSettingEnabled(db, RESTRICT_EDIT_VISIT_AFTER_CHECK_OUT) && visit.isCheckOut)
				&& !isFinalized;
	}

	private void setWithChanges(boolean withChanges) {
		this.withChanges = withChanges;
		main.overrideBackPress(withChanges);
	}

	private void saveTask(final SQLiteAdapter db, final String notes) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(TarkieLib.editTask(db, store, visit, notes.replace("'", "''"))) {
						visit.notes = notes;
						if(store != null) {
							visit.store = store;
							visit.name = store.name;
							visit.isUpdate = true;
						}
						saveTaskHandler.obtainMessage(Result.SUCCESS, visit).sendToTarget();
					}
					else {
						saveTaskHandler.obtainMessage(Result.FAILED, visit).sendToTarget();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	Handler saveTaskHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					VisitObj visit = (VisitObj) msg.obj;
					if(saveVisitCallback != null) {
						saveVisitCallback.onSaveVisit(visit);
					}
					if(visit.isCheckIn && !visit.isCheckOut && store != null) {
						TarkieLib.setDefaultStore(db, store.ID);
					}
					manager.popBackStack();
					CodePanUtils.alertToast(main, strVisit + " has been successfully saved.");
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(main, "Failed to update " + strVisit.toLowerCase() + ".");
					break;
			}
			return false;
		}
	});

	private boolean hasNotes() {
		if(visit.notes != null) {
			int length = visit.notes.length();
			return length > visit.notesLimit;
		}
		return false;
	}

	public void setOnSaveVisitCallback(OnSaveVisitCallback saveVisitCallback) {
		this.saveVisitCallback = saveVisitCallback;
	}
}
