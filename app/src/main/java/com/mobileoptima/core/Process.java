package com.mobileoptima.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.callback.Interface.OnResultCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.ProcessName;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakOutObj;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.OvertimeObj;
import com.mobileoptima.model.PhotoObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;

public class Process {

	private OnErrorCallback errorCallback;
	private OnResultCallback resultCallback;
	private boolean result;
	private Thread thread;

	public Process() {
	}

	public Process(OnResultCallback resultCallback) {
		this.resultCallback = resultCallback;
	}

	public void authorizeDevice(final SQLiteAdapter db, final String deviceID, final String authCode) {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.authorizeDevice(db, deviceID, authCode, errorCallback);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.getSyncBatchID(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getCompany(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getConvention(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getBreaks(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getOvertimeReasons(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getScheduleTime(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getIncidents(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getExpenseTypeCategories(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKU(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUUOM(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUCategory(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUBrand(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUSubBrand(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUCompetitors(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getServerTime(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setName(ProcessName.AUTHORIZATION);
		thread.start();
	}

	public void login(final SQLiteAdapter db, final String username, final String password) {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.login(db, username, password, errorCallback);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.getEmployees(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getStores(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSchedule(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getForms(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getFields(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getCustomFieldData(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getTasks(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSettings(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getServerTime(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setName(ProcessName.LOGIN);
		thread.start();
	}

	public void updateMasterFile(final SQLiteAdapter db) {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getCompany(db, errorCallback);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.getConvention(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getEmployees(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSettings(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getStores(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getContactPerson(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getBreaks(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getOvertimeReasons(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getScheduleTime(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getIncidents(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSchedule(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getForms(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getFields(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getCustomFieldData(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getTasks(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getExpenseTypeCategories(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKU(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUStoreAssign(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUUOM(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUCategory(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUBrand(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUSubBrand(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSKUCompetitors(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getServerTime(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setName(ProcessName.UPDATE_MASTER_FILE);
		thread.start();
	}

	public void syncData(final SQLiteAdapter db) {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getSyncBatchID(db, errorCallback);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					for(TimeInObj timeIn : Data.loadTimeInSync(db)) {
						if(result) {
							result = Tx.syncTimeIn(db, timeIn, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(TimeOutObj timeOut : Data.loadTimeOutSync(db)) {
						if(result) {
							result = Tx.syncTimeOut(db, timeOut, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(BreakInObj breakIn : Data.loadBreakInSync(db)) {
						if(result) {
							result = Tx.syncBreakIn(db, breakIn, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(BreakOutObj breakOut : Data.loadBreakOutSync(db)) {
						if(result) {
							result = Tx.syncBreakOut(db, breakOut, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(OvertimeObj ot : Data.loadOTSync(db)) {
						if(result) {
							result = Tx.syncOT(db, ot, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(TaskObj task : Data.loadTaskSync(db)) {
						if(result) {
							result = Tx.syncTask(db, task, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(TaskObj task : Data.loadTaskUpdate(db)) {
						if(result) {
							result = Tx.updateTask(db, task, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(CheckInObj checkIn : Data.loadCheckInSync(db)) {
						if(result) {
							result = Tx.syncCheckIn(db, checkIn, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(CheckOutObj checkOut : Data.loadCheckOutSync(db)) {
						if(result) {
							result = Tx.syncCheckOut(db, checkOut, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(PhotoObj photo : Data.loadTimeInPhotoUpload(db)) {
						if(result) {
							result = Tx.uploadTimeInPhoto(db, photo, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(PhotoObj photo : Data.loadTimeOutPhotoUpload(db)) {
						if(result) {
							result = Tx.uploadTimeOutPhoto(db, photo, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(CheckInObj checkIn : Data.loadCheckInPhotoUpload(db)) {
						if(result) {
							result = Tx.uploadCheckInPhoto(db, checkIn, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(CheckOutObj checkOut : Data.loadCheckOutPhotoUpload(db)) {
						if(result) {
							result = Tx.uploadCheckOutPhoto(db, checkOut, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(PhotoObj photo : Data.loadSignatureUpload(db)) {
						if(result) {
							result = Tx.uploadSignature(db, photo, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					if(result) {
						result = TarkieLib.updateLastSynced(db);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setName(ProcessName.SYNC_DATA);
		thread.start();
	}

	public void sendBackUp(final SQLiteAdapter db, final String fileName) {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = CodePanUtils.decryptTextFile(db.getContext(), App.BACKUP, App.ERROR_PWD);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = CodePanUtils.extractDatabase(db.getContext(), App.BACKUP, App.DB, false);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = CodePanUtils.zipFolder(db.getContext(), App.BACKUP, App.FOLDER, fileName);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Tx.uploadSendBackUp(db, fileName, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = CodePanUtils.deleteFilesInDir(db.getContext(), App.BACKUP);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
//					if(result) {
//						result = TarkieLib.purgeData(db);
//						Thread.sleep(250);
//						handler.sendMessage(handler.obtainMessage());
//					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setName(ProcessName.SEND_BACK_UP);
		thread.start();
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(resultCallback != null) {
				resultCallback.onResult(result);
			}
			return true;
		}
	});

	public void stop() {
		if(thread != null && thread.isAlive()) {
			thread.interrupt();
		}
	}

	public void setOnErrorCallback(OnErrorCallback errorCallback) {
		this.errorCallback = errorCallback;
	}
}
