package com.mobileoptima.callback;

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.ScheduleObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskStatusObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.model.VisitObj;

public class Interface {

	public interface OnInitializeCallback {
		void onInitialize(SQLiteAdapter db);
	}

	public interface OnResultCallback {
		void onResult(boolean result);
	}

	public interface OnLoginCallback {
		void onLogin();
	}

	public interface OnGPSFixedCallback {
		void onGPSFixed(GpsObj gps);
	}

	public interface OnCountdownFinishCallback {
		void onCountdownFinish();
	}

	public interface OnSelectStoreCallback {
		void onSelectStore(StoreObj store);
	}

	public interface OnSelectScheduleCallback {
		void onSelectSchedule(ScheduleObj schedule);
	}

	public interface OnTimeInCallback {
		void onTimeIn(TimeInObj timeIn);
	}

	public interface OnRetakePhotoCallback {
		void onRetakePhoto();
	}

	public interface OnBackPressedCallback {
		void onBackPressed();
	}

	public interface OnTimeOutCallback {
		void onTimeOut(TimeOutObj timeOut);
	}

	public interface OnCheckInCallback {
		void onCheckIn(CheckInObj checkIn);
	}

	public interface OnCheckOutCallback {
		void onCheckOut(CheckOutObj checkOut);
	}

	public interface OnSaveVisitCallback {
		void onSaveVisit(VisitObj visit);
	}

	public interface OnSelectStatusCallback {
		void onSelectStatus(TaskStatusObj status);
	}
}