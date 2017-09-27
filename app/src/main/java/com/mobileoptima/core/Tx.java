package com.mobileoptima.core;

import android.content.Context;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.App;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakOutObj;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.EmployeeObj;
import com.mobileoptima.model.OvertimeObj;
import com.mobileoptima.model.OvertimeReasonObj;
import com.mobileoptima.model.PhotoObj;
import com.mobileoptima.model.ScheduleObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Tx {

	public static boolean syncTimeIn(SQLiteAdapter db, TimeInObj timeIn, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			GpsObj gps = timeIn.gps;
			EmployeeObj emp = timeIn.emp;
			StoreObj store = timeIn.store;
			ScheduleObj schedule = timeIn.schedule;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("date_in", timeIn.dDate);
			paramsObj.put("time_in", timeIn.dTime);
			paramsObj.put("gps_date", gps.date);
			paramsObj.put("gps_time", gps.time);
			paramsObj.put("latitude", gps.latitude);
			paramsObj.put("longitude", gps.longitude);
			paramsObj.put("is_valid", gps.isValid ? "yes" : "no");
			paramsObj.put("store_id", store.webStoreID);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("local_record_id", timeIn.ID);
			paramsObj.put("sync_batch_id", timeIn.syncBatchID);
			paramsObj.put("battery_level", timeIn.batteryLevel);
			paramsObj.put("schedule_id", schedule.webScheduleID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "time-in", paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.TIME_IN, timeIn.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncTimeOut(SQLiteAdapter db, TimeOutObj timeOut, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			GpsObj gps = timeOut.gps;
			EmployeeObj emp = timeOut.emp;
			StoreObj store = timeOut.store;
			TimeInObj timeIn = timeOut.timeIn;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("date_out", timeOut.dDate);
			paramsObj.put("time_out", timeOut.dTime);
			paramsObj.put("gps_date", gps.date);
			paramsObj.put("gps_time", gps.time);
			paramsObj.put("latitude", gps.latitude);
			paramsObj.put("longitude", gps.longitude);
			paramsObj.put("is_valid", gps.isValid ? "yes" : "no");
			paramsObj.put("store_id", store.webStoreID);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("local_record_id", timeOut.ID);
			paramsObj.put("sync_batch_id", timeOut.syncBatchID);
			paramsObj.put("local_record_id_in", timeIn.ID);
			paramsObj.put("sync_batch_id_in", timeIn.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "time-out", paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.TIME_OUT, timeOut.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncBreakIn(SQLiteAdapter db, BreakInObj breakIn, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			GpsObj gps = breakIn.gps;
			EmployeeObj emp = breakIn.emp;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("date_in", breakIn.dDate);
			paramsObj.put("time_in", breakIn.dTime);
			paramsObj.put("gps_date", gps.date);
			paramsObj.put("gps_time", gps.time);
			paramsObj.put("latitude", gps.latitude);
			paramsObj.put("longitude", gps.longitude);
			paramsObj.put("is_valid", gps.isValid ? "yes" : "no");
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("local_record_id", breakIn.ID);
			paramsObj.put("sync_batch_id", breakIn.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "break-in", paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.BREAK_IN, breakIn.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncBreakOut(SQLiteAdapter db, BreakOutObj breakOut, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			GpsObj gps = breakOut.gps;
			EmployeeObj emp = breakOut.emp;
			BreakInObj breakIn = breakOut.breakIn;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("date_out", breakOut.dDate);
			paramsObj.put("time_out", breakOut.dTime);
			paramsObj.put("gps_date", gps.date);
			paramsObj.put("gps_time", gps.time);
			paramsObj.put("latitude", gps.latitude);
			paramsObj.put("longitude", gps.longitude);
			paramsObj.put("is_valid", gps.isValid ? "yes" : "no");
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("local_record_id", breakOut.ID);
			paramsObj.put("sync_batch_id", breakOut.syncBatchID);
			paramsObj.put("local_record_id_in", breakIn.ID);
			paramsObj.put("sync_batch_id_in", breakIn.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "break-out", paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.BREAK_OUT, breakOut.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncOT(SQLiteAdapter db, OvertimeObj ot, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			TimeInObj timeIn = ot.timeIn;
			EmployeeObj emp = timeIn.emp;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("remarks", ot.remarks);
			paramsObj.put("overtime_hours", ot.hours);
			paramsObj.put("local_record_id", ot.ID);
			paramsObj.put("sync_batch_id", ot.syncBatchID);
			paramsObj.put("local_record_id_in", timeIn.ID);
			paramsObj.put("sync_batch_id_in", timeIn.syncBatchID);
			JSONArray reasonArray = new JSONArray();
			for(OvertimeReasonObj reason : ot.reasonList) {
				reasonArray.put(reason.ID);
			}
			paramsObj.put("reason_id", reasonArray);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "add-overtime", paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.OVERTIME, ot.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncTask(SQLiteAdapter db, TaskObj task, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			StoreObj store = task.store;
			EmployeeObj emp = task.emp;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("date_created", task.dDate);
			paramsObj.put("time_created", task.dTime);
			paramsObj.put("itinerary_id", task.webTaskID);
			paramsObj.put("store_id", store.webStoreID);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("created_by", emp.ID);
			paramsObj.put("start_date", task.startDate);
			paramsObj.put("end_date", task.endDate);
			paramsObj.put("notes", task.notes);
			paramsObj.put("local_record_id", task.ID);
			paramsObj.put("sync_batch_id", task.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "add-itinerary-visit", paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						hasData = initObj.getInt("recno") > 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
			if(hasData) {
				SQLiteBinder binder = new SQLiteBinder(db);
				JSONArray dataArray = responseObj.getJSONArray("data");
				for(int i = 0; i < dataArray.length(); i++) {
					try {
						SQLiteQuery query = new SQLiteQuery();
						JSONObject dataObj = dataArray.getJSONObject(i);
						if(dataObj.getInt("itinerary_id") != 0) {
							query.add(new FieldValue("webTaskID", dataObj.getString("itinerary_id")));
						}
						query.add(new FieldValue("isSync", true));
						query.add(new FieldValue("isWebUpdate", true));
						binder.update(Tables.getName(TB.TASK), query, task.ID);
					}
					catch(Exception e) {
						e.printStackTrace();
						if(errorCallback != null) {
							errorCallback.onError(e.getMessage(), params, response, false);
						}
						return false;
					}
				}
				result = binder.finish();
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncCheckIn(SQLiteAdapter db, CheckInObj checkIn, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			EmployeeObj emp = checkIn.emp;
			TaskObj task = checkIn.task;
			GpsObj gps = checkIn.gps;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("date_in", checkIn.dDate);
			paramsObj.put("time_in", checkIn.dTime);
			paramsObj.put("gps_date", gps.date);
			paramsObj.put("gps_time", gps.time);
			paramsObj.put("latitude", gps.latitude);
			paramsObj.put("longitude", gps.longitude);
			paramsObj.put("is_valid", gps.isValid ? "yes" : "no");
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("itinerary_id", task.webTaskID);
			paramsObj.put("local_record_id", checkIn.ID);
			paramsObj.put("sync_batch_id", checkIn.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "check-in", paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.CHECK_IN, checkIn.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean updateTask(SQLiteAdapter db, TaskObj task, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			StoreObj store = task.store;
			EmployeeObj emp = task.emp;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("itinerary_id", task.webTaskID);
			paramsObj.put("store_id", store.webStoreID);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("start_date", task.startDate);
			paramsObj.put("end_date", task.endDate);
			paramsObj.put("notes", task.notes);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "edit-itinerary-visit", paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusWebUpdate(db, TB.TASK, task.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncCheckOut(SQLiteAdapter db, CheckOutObj checkOut, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			EmployeeObj emp = checkOut.emp;
			CheckInObj checkIn = checkOut.checkIn;
			TaskObj task = checkIn.task;
			GpsObj gps = checkOut.gps;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("date_out", checkOut.dDate);
			paramsObj.put("time_out", checkOut.dTime);
			paramsObj.put("gps_date", gps.date);
			paramsObj.put("gps_time", gps.time);
			paramsObj.put("latitude", gps.latitude);
			paramsObj.put("longitude", gps.longitude);
			paramsObj.put("is_valid", gps.isValid ? "yes" : "no");
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("itinerary_id", task.webTaskID);
			paramsObj.put("status", task.status);
			paramsObj.put("local_record_id", checkOut.ID);
			paramsObj.put("sync_batch_id", checkOut.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "check-out", paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.CHECK_OUT, checkOut.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadTimeInPhoto(SQLiteAdapter db, PhotoObj photo, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("local_record_id", photo.ID);
			paramsObj.put("sync_batch_id", photo.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + photo.fileName;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusPhotoUpload(db, TB.TIME_IN, photo.ID);
			}
			response = CodePanUtils.uploadFile(App.WEB_FILES + "upload-time-in-photo", params,
					"image", "image/png", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusPhotoUpload(db, TB.TIME_IN, photo.ID);
						if(result) {
							CodePanUtils.deleteFile(db.getContext(), App.FOLDER, photo.fileName);
						}
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadTimeOutPhoto(SQLiteAdapter db, PhotoObj photo, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("local_record_id", photo.ID);
			paramsObj.put("sync_batch_id", photo.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + photo.fileName;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusPhotoUpload(db, TB.TIME_OUT, photo.ID);
			}
			response = CodePanUtils.uploadFile(App.WEB_FILES + "upload-time-out-photo", params,
					"image", "image/png", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusPhotoUpload(db, TB.TIME_OUT, photo.ID);
						if(result) {
							CodePanUtils.deleteFile(db.getContext(), App.FOLDER, photo.fileName);
						}
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadCheckInPhoto(SQLiteAdapter db, CheckInObj checkIn, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			TaskObj task = checkIn.task;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("itinerary_id", task.webTaskID);
			paramsObj.put("type", "check-in");
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + checkIn.photo;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusPhotoUpload(db, TB.CHECK_IN, checkIn.ID);
			}
			response = CodePanUtils.uploadFile(App.WEB_FILES + "upload-check-in-out-photo", params,
					"image", "image/png", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusUpload(db, TB.CHECK_IN, checkIn.ID);
						if(result) {
							CodePanUtils.deleteFile(db.getContext(), App.FOLDER, checkIn.photo);
						}
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadCheckOutPhoto(SQLiteAdapter db, CheckOutObj checkOut, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			CheckInObj checkIn = checkOut.checkIn;
			TaskObj task = checkIn.task;
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("itinerary_id", task.webTaskID);
			paramsObj.put("type", "check-out");
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + checkOut.photo;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusPhotoUpload(db, TB.CHECK_OUT, checkOut.ID);
			}
			response = CodePanUtils.uploadFile(App.WEB_FILES + "upload-check-in-out-photo", params,
					"image", "image/png", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusUpload(db, TB.CHECK_OUT, checkOut.ID);
						if(result) {
							CodePanUtils.deleteFile(db.getContext(), App.FOLDER, checkOut.photo);
						}
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadSignature(SQLiteAdapter db, PhotoObj photo, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("local_record_id", photo.ID);
			paramsObj.put("sync_batch_id", photo.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + photo.fileName;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusSignatureUpload(db, TB.TIME_OUT, photo.ID);
			}
			response = CodePanUtils.uploadFile(App.WEB_FILES + "upload-signature-photo", params,
					"image", "image/png", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(!responseObj.isNull("error")) {
				JSONObject errorObj = responseObj.getJSONObject("error");
				if(errorCallback != null) {
					errorCallback.onError(errorObj.getString("message"), params, response, true);
				}
			}
			else {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					if(initObj.getString("status").equals("ok")) {
						result = TarkieLib.updateStatusSignatureUpload(db, TB.TIME_OUT, photo.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
						return false;
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadSendBackUp(SQLiteAdapter db, String fileName, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String phpFile = "backup.php";
		String url = "https://www.tarkie.com/API/2.3/" + phpFile;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			//String apiKey = TarkieLib.getAPIKey(db);
			String empID = TarkieLib.getEmployeeID(db);
			String apiKey = "75TvNCip314ts6l1Q1N9i2F3BcRWr090y31W54G279UxaoQx5Z";
			paramsObj.put("action", "upload-backup");
			paramsObj.put("api_key", apiKey);
			paramsObj.put("user_id", empID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + fileName;
			File file = new File(path);
			response = CodePanUtils.uploadFile(url, params, "backup", "application/zip", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = true;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}
}
