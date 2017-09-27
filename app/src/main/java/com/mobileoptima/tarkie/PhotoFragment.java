package com.mobileoptima.tarkie;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.TouchImageView;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.callback.Interface.OnCheckInCallback;
import com.mobileoptima.callback.Interface.OnCheckOutCallback;
import com.mobileoptima.callback.Interface.OnRetakePhotoCallback;
import com.mobileoptima.callback.Interface.OnTimeInCallback;
import com.mobileoptima.callback.Interface.OnTimeOutCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.ScheduleObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;

public class PhotoFragment extends Fragment implements OnClickListener, OnBackPressedCallback, OnFragmentCallback {

	private OnTimeInCallback timeInCallback;
	private OnTimeOutCallback timeOutCallback;
	private OnRetakePhotoCallback retakePhotoCallback;
	private OnCheckInCallback checkInCallback;
	private OnCheckOutCallback checkOutCallback;
	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private TouchImageView ivPhoto;
	private CodePanButton btnRetakePhoto;
	private GpsObj gps;
	private ScheduleObj schedule;
	private TaskObj task;
	private String fileName;
	private int imageType;

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
		manager = main.getSupportFragmentManager();
		main.setOnBackPressedCallback(this);
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.photo_layout, container, false);
		ivPhoto = (TouchImageView) view.findViewById(R.id.ivPhoto);
		btnRetakePhoto = (CodePanButton) view.findViewById(R.id.btnRetakePhoto);
		CodePanLabel tvTitlePhoto = (CodePanLabel) view.findViewById(R.id.tvTitlePhoto);
		btnRetakePhoto.setOnClickListener(this);
		view.findViewById(R.id.btnBackPhoto).setOnClickListener(this);
		view.findViewById(R.id.btnUsePhoto).setOnClickListener(this);
		main.overrideBackPress(true);
		if(fileName != null && !fileName.isEmpty()) {
			Bitmap bitmap = CodePanUtils.getBitmapImage(main, App.FOLDER, fileName);
			ivPhoto.setImageBitmap(bitmap);
		}
		switch(imageType) {
			case ImageType.TIME_IN:
				timeInCallback = main;
				String strTimeIn = main.getConvention(Convention.TIME_IN);
				if(strTimeIn != null && !strTimeIn.isEmpty()) {
					tvTitlePhoto.setText(strTimeIn);
				}
				else {
					tvTitlePhoto.setText(R.string.time_in);
				}
				break;
			case ImageType.TIME_OUT:
				timeOutCallback = main;
				String strTimeOut = main.getConvention(Convention.TIME_OUT);
				if(strTimeOut != null && !strTimeOut.isEmpty()) {
					tvTitlePhoto.setText(strTimeOut);
				}
				else {
					tvTitlePhoto.setText(R.string.time_out);
				}
				break;
			case ImageType.CHECK_IN:
				tvTitlePhoto.setText(R.string.check_in);
				break;
			case ImageType.CHECK_OUT:
				tvTitlePhoto.setText(R.string.check_out);
				break;
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBackPhoto:
				onBackPressed();
				break;
			case R.id.btnUsePhoto:
				main.overrideBackPress(false);
				main.setOnBackPressedCallback(null);
				switch(imageType) {
					case ImageType.TIME_IN:
						if(timeInCallback != null) {
							TimeInObj in = new TimeInObj();
							in.gps = gps;
							in.photo = fileName;
							in.schedule = schedule;
							timeInCallback.onTimeIn(in);
						}
						break;
					case ImageType.TIME_OUT:
						if(timeOutCallback != null) {
							TimeOutObj out = new TimeOutObj();
							out.gps = gps;
							out.photo = fileName;
							timeOutCallback.onTimeOut(out);
						}
						break;
					case ImageType.CHECK_IN:
						manager.popBackStack();
						manager.popBackStack();
						if(checkInCallback != null) {
							CheckInObj checkIn = new CheckInObj();
							checkIn.gps = gps;
							checkIn.task = task;
							checkIn.photo = fileName;
							checkInCallback.onCheckIn(checkIn);
						}
						break;
					case ImageType.CHECK_OUT:
						manager.popBackStack();
						manager.popBackStack();
						if(checkOutCallback != null) {
							CheckOutObj checkOut = new CheckOutObj();
							checkOut.gps = gps;
							checkOut.photo = fileName;
							CheckInObj checkIn = new CheckInObj();
							checkIn.ID = TarkieLib.getCheckInID(db, task.ID);
							checkIn.task = task;
							checkOut.checkIn = checkIn;
							checkOutCallback.onCheckOut(checkOut);
						}
						break;
				}
				break;
			case R.id.btnRetakePhoto:
				switch(imageType) {
					default:
						if(fileName != null) {
							CodePanUtils.deleteFile(main, App.FOLDER, fileName);
						}
						break;
				}
				if(retakePhotoCallback != null) {
					retakePhotoCallback.onRetakePhoto();
				}
				break;
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialogFragment alert = new AlertDialogFragment();
		alert.setOnFragmentCallback(this);
		alert.setDialogTitle("Discard Photo");
		alert.setDialogMessage("Are you sure you want to discard this photo?");
		alert.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnRetakePhoto.performClick();
				manager.popBackStack();
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

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setGps(GpsObj gps) {
		this.gps = gps;
	}

	public void setSchedule(ScheduleObj schedule) {
		this.schedule = schedule;
	}

	public void setOnRetakePhotoCallback(OnRetakePhotoCallback retakePhotoCallback) {
		this.retakePhotoCallback = retakePhotoCallback;
	}

	public void setOnCheckInCallback(OnCheckInCallback checkInCallback) {
		this.checkInCallback = checkInCallback;
	}

	public void setTask(TaskObj task) {
		this.task = task;
	}

	public void setOnCheckOutCallback(OnCheckOutCallback checkOutCallback) {
		this.checkOutCallback = checkOutCallback;
	}
}
