package com.mobileoptima.tarkie;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.codepan.cache.TypefaceCache;
import com.codepan.callback.Interface.OnCameraErrorCallback;
import com.codepan.callback.Interface.OnCaptureCallback;
import com.codepan.camera.CameraSurfaceView;
import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.FocusIndicatorView;
import com.mobileoptima.callback.Interface.OnCheckInCallback;
import com.mobileoptima.callback.Interface.OnCheckOutCallback;
import com.mobileoptima.callback.Interface.OnRetakePhotoCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ScheduleObj;
import com.mobileoptima.model.TaskObj;

public class CameraFragment extends Fragment implements OnClickListener, OnCameraErrorCallback,
		OnCaptureCallback, OnRetakePhotoCallback {

	private final String flashMode = Camera.Parameters.FLASH_MODE_OFF;
	private final long TRANS_DELAY = 300;
	private final long FADE_DELAY = 750;

	private OnCheckInCallback checkInCallback;
	private OnCheckOutCallback checkOutCallback;
	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private FrameLayout flCamera;
	private FocusIndicatorView dvCamera;
	private View vCamera;
	private CameraSurfaceView surfaceView;
	private CodePanButton btnSwitchCamera;
	private GpsObj gps;
	private ScheduleObj schedule;
	private TaskObj task;
	private int cameraSelection, imageType;

	@Override
	public void onResume() {
		super.onResume();
		if(surfaceView != null && surfaceView.getCamera() == null) {
			setCamera(0);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.camera_layout, container, false);
		flCamera = (FrameLayout) view.findViewById(R.id.flCamera);
		dvCamera = (FocusIndicatorView) view.findViewById(R.id.dvCamera);
		vCamera = view.findViewById(R.id.vCamera);
		btnSwitchCamera = (CodePanButton) view.findViewById(R.id.btnSwitchCamera);
		view.findViewById(R.id.btnBackCamera).setOnClickListener(this);
		view.findViewById(R.id.btnCaptureCamera).setOnClickListener(this);
		view.findViewById(R.id.btnSwitchCamera).setOnClickListener(this);
		setCamera(TRANS_DELAY);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBackCamera:
				manager.popBackStack();
				break;
			case R.id.btnCaptureCamera:
				if(surfaceView != null && !surfaceView.isCaptured()) {
					surfaceView.takePicture();
				}
				break;
			case R.id.btnSwitchCamera:
				if(cameraSelection == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					cameraSelection = Camera.CameraInfo.CAMERA_FACING_BACK;
				}
				else {
					cameraSelection = Camera.CameraInfo.CAMERA_FACING_FRONT;
				}
				setCamera(0);
				break;
		}
	}

	@Override
	public void onCameraError() {
		AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(R.string.camera_error_title);
		alert.setDialogMessage(R.string.camera_error_message);
		alert.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
				manager.popBackStack();
			}
		});
		main.addFadingToMain(alert);
	}

	@Override
	public void onCapture(String fileName) {
		surfaceView.stopCamera();
		PhotoFragment photo = new PhotoFragment();
		photo.setOnRetakePhotoCallback(this);
		photo.setOnCheckInCallback(checkInCallback);
		photo.setOnCheckOutCallback(checkOutCallback);
		photo.setImageType(imageType);
		photo.setFileName(fileName);
		photo.setGps(gps);
		photo.setTask(task);
		photo.setSchedule(schedule);
		main.addSlidingToMain(photo, this, Tag.PHOTO);
	}

	@Override
	public void onRetakePhoto() {
		manager.popBackStack();
		setCamera(TRANS_DELAY);
	}

	private void setCamera(long delay) {
		if(vCamera != null) {
			vCamera.setVisibility(View.VISIBLE);
		}
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(surfaceView != null) {
					surfaceView.stopCamera();
				}
				Resources res = main.getResources();
				int color = res.getColor(R.color.white);
				int width = res.getInteger(R.integer.pic_width);
				int height = res.getInteger(R.integer.pic_height);
				int size = res.getDimensionPixelSize(R.dimen.fifteen);
				Typeface typeface = TypefaceCache.get(main.getAssets(), main.getString(R.string.proxima_nova_mid));
				surfaceView = new CameraSurfaceView(main, CameraFragment.this, cameraSelection,
						flashMode, App.FOLDER, CodePanUtils.getMaxWidth(main), CodePanUtils.getMaxHeight(main));
				surfaceView.setOnCaptureCallback(CameraFragment.this);
				surfaceView.setFocusIndicatorView(dvCamera);
				surfaceView.fullScreenToContainer(flCamera);
				surfaceView.timeStamp(typeface, size, color);
				surfaceView.setMaxPictureSize(width, height);
				surfaceView.setPrefix(TarkieLib.getEmployeeID(db));
				if(flCamera.getChildCount() > 1) {
					flCamera.removeViewAt(0);
				}
				flCamera.addView(surfaceView, 0);
				CodePanUtils.fadeOut(vCamera, FADE_DELAY);
				if(surfaceView.getNoOfCamera() == 1) {
					btnSwitchCamera.setVisibility(View.GONE);
				}
			}
		}, delay);
	}

	public void isFrontCamDefault(boolean isFront) {
		cameraSelection = isFront ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
	}

	public void setGps(GpsObj gps) {
		this.gps = gps;
	}

	public void setSchedule(ScheduleObj schedule) {
		this.schedule = schedule;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public void setTask(TaskObj task) {
		this.task = task;
	}

	public void setOnCheckInCallback(OnCheckInCallback checkInCallback) {
		this.checkInCallback = checkInCallback;
	}

	public void setOnCheckOutCallback(OnCheckOutCallback checkOutCallback) {
		this.checkOutCallback = checkOutCallback;
	}
}
