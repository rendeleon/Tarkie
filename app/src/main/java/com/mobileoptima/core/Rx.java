package com.mobileoptima.core;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.database.Condition;
import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Settings;
import com.mobileoptima.constant.StoreClass;
import com.mobileoptima.constant.TaskStatus;
import com.mobileoptima.constant.UpdateHistory;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Rx {

	public static boolean authorizeDevice(SQLiteAdapter db, String deviceID, String authCode, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("tablet_id", deviceID);
			paramsObj.put("authorization_code", authCode);
			paramsObj.put("api_key", App.API_KEY);
			paramsObj.put("device_type", App.OS_TYPE);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "authorization-request", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
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
				try {
					SQLiteQuery query = new SQLiteQuery();
					String table = Tables.getName(TB.API_KEY);
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						query.clearAll();
						query.add(new FieldValue("apiKey", dataObj.getString("api_key")));
						query.add(new FieldValue("authorizationCode", authCode));
						query.add(new FieldValue("deviceID", deviceID));
						if(!db.isRecordExists("SELECT ID FROM " + table + " WHERE ID = 1")) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, 1);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getSyncBatchID(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-sync-batch-id", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.SYNC_BATCH);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String sql = "SELECT ID FROM " + table + " WHERE ID = 1";
								query.clearAll();
								query.add(new FieldValue("syncBatchID", dataObj.getString("sync_batch_id")));
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, 1);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getCompany(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-company", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String c = Tables.getName(TB.COMPANY);
						String m = Tables.getName(TB.MODULES);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String coID = dataObj.getString("company_id");
								String company = dataObj.getString("company_name");
								String address = dataObj.getString("address");
								String logoUrl = dataObj.getString("company_logo");
								CodePanUtils.clearImageUrl(db.getContext(), logoUrl);
								company = CodePanUtils.handleUniCode(company);
								address = CodePanUtils.handleUniCode(address);
								query.clearAll();
								query.add(new FieldValue("ID", coID));
								query.add(new FieldValue("name", company));
								query.add(new FieldValue("address", address));
								query.add(new FieldValue("logoUrl", logoUrl));
								query.add(new FieldValue("mobile", dataObj.getString("mobile")));
								query.add(new FieldValue("code", dataObj.getString("company_code")));
								query.add(new FieldValue("landline", dataObj.getString("landline")));
								query.add(new FieldValue("expirationDate", dataObj.getString("expiration_date")));
								String sql = "SELECT ID FROM " + c + " WHERE ID = '" + coID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(c, query);
								}
								else {
									binder.update(c, query, db.getString(sql));
								}
								JSONArray moduleArray = dataObj.optJSONArray("modules");
								if(moduleArray != null) {
									query.clearAll();
									query.add(new FieldValue("isEnabled", false));
									binder.update(m, query);
									for(int mi = 0; mi < moduleArray.length(); mi++) {
										String module = moduleArray.getString(i);
										query.clearAll();
										query.add(new FieldValue("module", module));
										query.add(new FieldValue("isEnabled", true));
										sql = "SELECT ID FROM " + m + " WHERE module = '" + module + "'";
										if(!db.isRecordExists(sql)) {
											binder.insert(m, query);
										}
										else {
											binder.update(m, query, db.getString(sql));
										}
									}
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getConvention(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-naming-convention", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.CONVENTION);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								JSONArray namesArr = dataObj.names();
								for(int n = 0; n < namesArr.length(); n++) {
									String name = namesArr.getString(n);
									query.clearAll();
									query.add(new FieldValue("name", name));
									query.add(new FieldValue("convention", dataObj.getString(name)));
									String sql = "SELECT ID FROM " + table + " WHERE name = '" + name + "'";
									if(!db.isRecordExists(sql)) {
										binder.insert(table, query);
									}
									else {
										binder.update(table, query, db.getString(sql));
									}
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getBreaks(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-breaks", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.BREAK);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String webBreakID = dataObj.getString("break_id");
								query.clearAll();
								query.add(new FieldValue("ID", webBreakID));
								query.add(new FieldValue("name", dataObj.getString("break_name")));
								query.add(new FieldValue("duration", dataObj.getInt("duration")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + webBreakID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, db.getString(sql));
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getOvertimeReasons(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-overtime-reasons", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.OVERTIME_REASON);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String webOTReasonID = dataObj.getString("overtime_reason_id");
								query.clearAll();
								query.add(new FieldValue("ID", webOTReasonID));
								query.add(new FieldValue("name", dataObj.getString("overtime_reason")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + webOTReasonID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, db.getString(sql));
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getScheduleTime(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-schedule-time", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.SCHEDULE_TIME);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String timeSchedID = dataObj.getString("time_schedule_id");
								query.clearAll();
								query.add(new FieldValue("ID", timeSchedID));
								query.add(new FieldValue("timeIn", dataObj.getString("time_in")));
								query.add(new FieldValue("timeOut", dataObj.getString("time_out")));
								query.add(new FieldValue("color", dataObj.getString("color")));
								query.add(new FieldValue("shiftID", dataObj.getString("shift_type_id")));
								query.add(new FieldValue("isActive", dataObj.getInt("is_active")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + timeSchedID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, db.getString(sql));
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getIncidents(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-alert-types", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.INCIDENT);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String alertTypeID = dataObj.getString("alert_type_id");
								query.clearAll();
								query.add(new FieldValue("ID", alertTypeID));
								query.add(new FieldValue("name", dataObj.getString("alert_type")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + alertTypeID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, db.getString(sql));
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getExpenseTypeCategories(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-expense-categories", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.EXPENSE_TYPE_CATEGORY);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							query.clearAll();
							query.add(new FieldValue("isActive", false));
							binder.update(table, query);
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String expenseCategoryId = dataObj.getString("expense_category_id");
								query.clearAll();
								query.add(new FieldValue("ID", expenseCategoryId));
								query.add(new FieldValue("name", dataObj.getString("expense_category_name")));
								query.add(new FieldValue("isActive", true));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + expenseCategoryId + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, db.getString(sql));
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getSKU(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-inventory-sku", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.SKU);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String skuID = dataObj.getString("sku_id");
								query.clearAll();
								query.add(new FieldValue("ID", skuID));
								query.add(new FieldValue("code", dataObj.getString("sku_code")));
								query.add(new FieldValue("name", dataObj.getString("sku_name")));
								query.add(new FieldValue("uom1ID", dataObj.getString("uom_1_id")));
								query.add(new FieldValue("uom2ID", dataObj.getString("uom_2_id")));
								query.add(new FieldValue("conversion", dataObj.getString("conversion")));
								query.add(new FieldValue("categoryID", dataObj.getString("category_id")));
								query.add(new FieldValue("brandID", dataObj.getString("brand_id")));
								query.add(new FieldValue("subBrandID", dataObj.getString("sub_brand_id")));
								query.add(new FieldValue("isTop", dataObj.getString("is_top_sku")));
								query.add(new FieldValue("isActive", dataObj.getString("is_active")));
								query.add(new FieldValue("srp", dataObj.getString("srp")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + skuID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, db.getString(sql));
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getSKUUOM(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-inventory-uom", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.SKU_UOM);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String uomID = dataObj.getString("uom_id");
								String name = dataObj.getString("uom_name");
								String description = dataObj.getString("uom_description");
								name = CodePanUtils.handleUniCode(name);
								description = CodePanUtils.handleUniCode(description);
								query.clearAll();
								query.add(new FieldValue("ID", uomID));
								query.add(new FieldValue("name", name));
								query.add(new FieldValue("description", description));
								query.add(new FieldValue("assignTo", dataObj.getString("assign_to")));
								query.add(new FieldValue("isActive", dataObj.getString("is_active")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + uomID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, uomID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getSKUCategory(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-inventory-category", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.SKU_CATEGORY);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String categoryID = dataObj.getString("category_id");
								String name = dataObj.getString("category_name");
								name = CodePanUtils.handleUniCode(name);
								query.clearAll();
								query.add(new FieldValue("ID", categoryID));
								query.add(new FieldValue("name", name));
								query.add(new FieldValue("isActive", dataObj.getString("is_active")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + categoryID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, categoryID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getSKUBrand(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-inventory-brand", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.SKU_BRAND);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String brandID = dataObj.getString("brand_id");
								String name = dataObj.getString("brand_name");
								name = CodePanUtils.handleUniCode(name);
								query.clearAll();
								query.add(new FieldValue("ID", brandID));
								query.add(new FieldValue("name", name));
								query.add(new FieldValue("isActive", dataObj.getString("is_active")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + brandID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, brandID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getSKUSubBrand(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-inventory-sub-brand", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.SKU_SUB_BRAND);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String subBrandID = dataObj.getString("sub_brand_id");
								String name = dataObj.getString("sub_brand_name");
								name = CodePanUtils.handleUniCode(name);
								query.clearAll();
								query.add(new FieldValue("ID", subBrandID));
								query.add(new FieldValue("name", name));
								query.add(new FieldValue("isActive", dataObj.getString("is_active")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + subBrandID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, subBrandID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getSKUCompetitors(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-inventory-competitor-sku", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.SKU_COMPETITORS);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String competitorSKUID = dataObj.getString("competitor_sku_id");
								String name = dataObj.getString("competitor_sku_name");
								name = CodePanUtils.handleUniCode(name);
								query.clearAll();
								query.add(new FieldValue("ID", competitorSKUID));
								query.add(new FieldValue("name", name));
								query.add(new FieldValue("description", dataObj.getString("competitor_sku_description")));
								query.add(new FieldValue("categoryID", dataObj.getString("category_id")));
								query.add(new FieldValue("brandID", dataObj.getString("brand_id")));
								query.add(new FieldValue("subBrandID", dataObj.getString("sub_brand_id")));
								query.add(new FieldValue("srp", dataObj.getString("srp")));
								query.add(new FieldValue("isActive", dataObj.getString("is_active")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + competitorSKUID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, competitorSKUID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean login(SQLiteAdapter db, String username, String password, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("employee_number", username);
			paramsObj.put("password", password);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(App.WEB_API + "login", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						try {
							SQLiteQuery query = new SQLiteQuery();
							String table = Tables.getName(TB.CREDENTIALS);
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								query.clearAll();
								query.add(new FieldValue("empID", dataObj.getString("employee_id")));
								query.add(new FieldValue("isLogOut", false));
								query.add(new FieldValue("dDate", CodePanUtils.getDate()));
								query.add(new FieldValue("dTime", CodePanUtils.getTime()));
								if(!db.isRecordExists("SELECT ID FROM " + table + " WHERE ID = 1")) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, 1);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getEmployees(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-employee-details", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						try {
							SQLiteQuery query = new SQLiteQuery();
							String table = Tables.getName(TB.EMPLOYEE);
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String empID = dataObj.getString("employee_id");
								String firstName = dataObj.getString("firstname");
								String lastName = dataObj.getString("lastname");
								String employeeNo = dataObj.getString("employee_number");
								String imageUrl = dataObj.getString("picture_url");
								firstName = CodePanUtils.handleUniCode(firstName);
								lastName = CodePanUtils.handleUniCode(lastName);
								employeeNo = CodePanUtils.handleUniCode(employeeNo);
								imageUrl = CodePanUtils.handleUniCode(imageUrl);
								CodePanUtils.clearImageUrl(db.getContext(), imageUrl);
								query.clearAll();
								query.add(new FieldValue("ID", empID));
								query.add(new FieldValue("firstName", firstName));
								query.add(new FieldValue("lastName", lastName));
								query.add(new FieldValue("employeeNo", employeeNo));
								query.add(new FieldValue("groupID", dataObj.getString("team_id")));
								query.add(new FieldValue("mobile", dataObj.getString("mobile")));
								query.add(new FieldValue("email", dataObj.getString("email")));
								query.add(new FieldValue("isApprover", dataObj.getString("is_approver").equals("1")));
								query.add(new FieldValue("isActive", dataObj.getString("is_active").equals("yes")));
								query.add(new FieldValue("imageUrl", imageUrl));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + empID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, empID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getStores(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String empID = TarkieLib.getEmployeeID(db);
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("employee_id", empID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-stores-for-app", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						try {
							String table = Tables.getName(TB.STORES);
							SQLiteQuery query = new SQLiteQuery();
							query.add(new Condition("empID", empID));
							query.add(new Condition("isFromTask", false));
							query.add(new Condition("isTag", true));
							query.add(new FieldValue("isTag", false));
							binder.update(table, query);
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String name = dataObj.getString("short_name");
								String address = dataObj.getString("address");
								String webStoreID = dataObj.getString("store_id");
								int class1ID = dataObj.getInt("store_class_1_id");
								int class2ID = dataObj.getInt("store_class_2_id");
								int class3ID = dataObj.getInt("store_class_3_id");
								name = CodePanUtils.handleUniCode(name);
								address = CodePanUtils.handleUniCode(address);
								query.clearAll();
								query.add(new FieldValue("name", name));
								query.add(new FieldValue("empID", empID));
								query.add(new FieldValue("address", address));
								query.add(new FieldValue("webStoreID", webStoreID));
								query.add(new FieldValue("gpsLongitude", dataObj.getDouble("longitude")));
								query.add(new FieldValue("gpsLatitude", dataObj.getDouble("latitude")));
								query.add(new FieldValue("radius", dataObj.getInt("geo_fence_radius")));
								query.add(new FieldValue("isActive", dataObj.getInt("is_active")));
								query.add(new FieldValue("isWebUpdate", true));
								query.add(new FieldValue("isSync", true));
								query.add(new FieldValue("isTag", true));
								if(class1ID != 0) {
									query.add(new FieldValue("class1ID", class1ID));
								}
								if(class2ID != 0) {
									query.add(new FieldValue("class2ID", class2ID));
								}
								if(class3ID != 0) {
									query.add(new FieldValue("class3ID", class3ID));
								}
								String sql = "SELECT ID FROM " + table + " WHERE webStoreID = '" + webStoreID + "'";
								if(!db.isRecordExists(sql)) {
									String dDate = CodePanUtils.getDate();
									String dTime = CodePanUtils.getTime();
									query.add(new FieldValue("dDate", dDate));
									query.add(new FieldValue("dTime", dTime));
									query.add(new FieldValue("isFromWeb", true));
									binder.insert(table, query);
								}
								else {
									String storeID = db.getString(sql);
									binder.update(table, query, storeID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getSchedule(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String start = CodePanUtils.getDate();
			String end = CodePanUtils.getDateAfter(start, 15);
			String empID = TarkieLib.getEmployeeID(db);
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("employee_id", empID);
			paramsObj.put("start_date", start);
			paramsObj.put("end_date", end);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-schedule", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						try {
							String table = Tables.getName(TB.SCHEDULE);
							SQLiteQuery query = new SQLiteQuery();
							query.add(new FieldValue("empID", empID));
							query.add(new FieldValue("isActive", false));
							query.add(new Condition("scheduleDate", start, end, Condition.Operator.BETWEEN));
							binder.update(table, query);
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String scheduleDate = dataObj.getString("date");
								String webScheduleID = dataObj.getString("schedule_id");
								query.clearAll();
								query.add(new FieldValue("timeIn", dataObj.getString("time_in")));
								query.add(new FieldValue("timeOut", dataObj.getString("time_out")));
								query.add(new FieldValue("isDayOff", dataObj.getString("is_day_off")));
								query.add(new FieldValue("shiftID", dataObj.getString("shift_type_id")));
								query.add(new FieldValue("isActive", true));
								query.add(new FieldValue("isSync", true));
								String sql = "SELECT ID FROM " + table + " WHERE webScheduleID = '" +
										webScheduleID + "' AND scheduleDate = '" + scheduleDate + "'";
								if(!db.isRecordExists(sql)) {
									String dDate = CodePanUtils.getDate();
									String dTime = CodePanUtils.getTime();
									query.add(new FieldValue("dDate", dDate));
									query.add(new FieldValue("dTime", dTime));
									query.add(new FieldValue("isFromWeb", true));
									query.add(new FieldValue("scheduleDate", scheduleDate));
									query.add(new FieldValue("webScheduleID", webScheduleID));
									query.add(new FieldValue("empID", dataObj.getString("employee_id")));
									binder.insert(table, query);
								}
								else {
									String scheduleID = db.getString(sql);
									binder.update(table, query, scheduleID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getForms(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String groupID = TarkieLib.getGroupID(db);
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("team_id", groupID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-forms", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						try {
							String table = Tables.getName(TB.FORMS);
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String formID = dataObj.getString("form_id");
								String category = dataObj.getString("form_type");
								String name = dataObj.getString("form_name");
								String description = dataObj.getString("form_description");
								String logoUrl = dataObj.getString("form_logo");
								name = CodePanUtils.handleUniCode(name);
								description = CodePanUtils.handleUniCode(description);
								category = CodePanUtils.handleUniCode(category);
								CodePanUtils.clearImageUrl(db.getContext(), logoUrl);
								query.clearAll();
								query.add(new FieldValue("name", name));
								query.add(new FieldValue("description", description));
								query.add(new FieldValue("groupID", groupID));
								query.add(new FieldValue("logoUrl", logoUrl));
								query.add(new FieldValue("category", category));
								query.add(new FieldValue("type", category));
								query.add(new FieldValue("dateCreated", dataObj.getString("form_date_created")));
								query.add(new FieldValue("timeCreated", dataObj.getString("form_time_created")));
								query.add(new FieldValue("status", dataObj.getString("form_status")));
								query.add(new FieldValue("isActive", dataObj.getString("form_is_active")));
								query.add(new FieldValue("showInVisit", dataObj.getString("form_show_in_all_visit")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + formID + "'";
								if(!db.isRecordExists(sql)) {
									query.add(new FieldValue("ID", formID));
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, formID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getFields(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String groupID = TarkieLib.getGroupID(db);
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("team_id", groupID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-form-fields", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						try {
							String table = Tables.getName(TB.FIELDS);
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String fieldID = dataObj.getString("field_id");
								String type = dataObj.getString("field_type");
								String name = dataObj.getString("field_name");
								String description = dataObj.getString("field_description");
								name = CodePanUtils.handleUniCode(name);
								description = CodePanUtils.handleUniCode(description);
								type = CodePanUtils.handleUniCode(type);
								query.clearAll();
								query.add(new FieldValue("name", name));
								query.add(new FieldValue("description", description));
								query.add(new FieldValue("type", type));
								query.add(new FieldValue("formID", dataObj.getString("field_form_id")));
								query.add(new FieldValue("orderNo", dataObj.getString("field_order_number")));
								query.add(new FieldValue("isRequired", dataObj.getString("field_is_required")));
								query.add(new FieldValue("customFieldID", dataObj.getInt("custom_field_id") != 0 ?
										dataObj.getString("custom_field_id") : null));
								query.add(new FieldValue("isActive", dataObj.getString("field_is_active")));
								String sql = "SELECT ID FROM " + table + " WHERE ID = '" + fieldID + "'";
								if(!db.isRecordExists(sql)) {
									query.add(new FieldValue("ID", fieldID));
									binder.insert(table, query);
								}
								else {
									binder.update(table, query, fieldID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getCustomFieldData(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("sync_date", TarkieLib.getLastUpdate(db, UpdateHistory.CUSTOM_FIELD_DATA));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-custom-field-data", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						try {
							String table = Tables.getName(TB.CUSTOM_FIELD_DATA);
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String webStoreID = dataObj.getString("store_id");
								String storeID = TarkieLib.getStoreID(db, webStoreID);
								if(storeID == null) {
									StoreObj store = new StoreObj();
									store.isSync = true;
									store.isFromWeb = true;
									store.isFromTask = true;
									store.isWebUpdate = true;
									store.webStoreID = webStoreID;
									storeID = TarkieLib.addStore(db, store);
								}
								JSONArray customDataArray = dataObj.getJSONArray("custom_data");
								for(int c = 0; c < customDataArray.length(); c++) {
									JSONObject customDataObj = customDataArray.getJSONObject(c);
									String fieldID = customDataObj.getString("custom_field_id");
									String value = customDataObj.getString("custom_field_value");
									value = CodePanUtils.handleUniCode(value);
									query.clearAll();
									query.add(new FieldValue("value", value));
									query.add(new FieldValue("storeID", storeID));
									query.add(new FieldValue("customFieldID", fieldID));
									String sql = "SELECT ID FROM " + table + " WHERE storeID = '" + storeID + "' " +
											"AND customFieldID = '" + fieldID + "'";
									if(!db.isRecordExists(sql)) {
										binder.insert(table, query);
									}
									else {
										String customFieldID = db.getString(sql);
										binder.update(table, query, customFieldID);
									}
								}
							}
							result = binder.finish();
							if(result) {
								TarkieLib.saveHistory(db, UpdateHistory.CUSTOM_FIELD_DATA);
							}
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getTasks(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String start = CodePanUtils.getDate();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("employee_id", TarkieLib.getEmployeeID(db));
			paramsObj.put("start_date", start);
			paramsObj.put("end_date", CodePanUtils.getDateAfter(start, 15));
			paramsObj.put("get_deleted", "yes");
			paramsObj.put("status", TaskStatus.PENDING);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-itinerary", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						try {
							String t = Tables.getName(TB.TASK);
							String tf = Tables.getName(TB.TASK_FORM);
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String webStoreID = dataObj.getString("store_id");
								String webTaskID = dataObj.getString("itinerary_id");
								String storeID = TarkieLib.getStoreID(db, webStoreID);
								String title = dataObj.getString("store_short_name");
								String address = dataObj.getString("store_address");
								String notes = dataObj.getString("notes");
								notes = CodePanUtils.handleUniCode(notes);
								notes = CodePanUtils.handleHTMLEntities(notes, false);
								title = CodePanUtils.handleUniCode(title);
								address = CodePanUtils.handleUniCode(address);
								String unescapeNotes = StringEscapeUtils.unescapeHtml4(notes);
								int notesLimit = 0;
								if(unescapeNotes != null) {
									notesLimit = unescapeNotes.replace("''", "'").length();
								}
								StoreObj store = new StoreObj();
								store.name = title;
								store.address = address;
								store.longitude = dataObj.getDouble("store_longitude");
								store.latitude = dataObj.getDouble("store_latitude");
								store.radius = dataObj.getInt("store_radius");
								store.isWebUpdate = true;
								if(storeID != null) {
									store.ID = storeID;
									String name = TarkieLib.getStoreName(db, storeID);
									if(name == null) {
										TarkieLib.editStore(db, store);
									}
								}
								else {
									store.isSync = true;
									store.isFromWeb = true;
									store.isFromTask = true;
									store.webStoreID = webStoreID;
									storeID = TarkieLib.addStore(db, store);
								}
								query.clearAll();
								query.add(new FieldValue("name", title));
								query.add(new FieldValue("notes", notes));
								query.add(new FieldValue("storeID", storeID));
								query.add(new FieldValue("notesLimit", notesLimit));
								query.add(new FieldValue("empID", dataObj.getString("employee_id")));
								query.add(new FieldValue("startDate", dataObj.getString("start_date")));
								query.add(new FieldValue("endDate", dataObj.getString("end_date")));
								query.add(new FieldValue("isDelete", dataObj.getInt("is_deleted")));
								query.add(new FieldValue("isWebUpdate", true));
								query.add(new FieldValue("isUpdate", true));
								query.add(new FieldValue("isSync", true));
								String sql = "SELECT ID FROM " + t + " WHERE webTaskID = '" + webTaskID + "'";
								String taskID = null;
								if(!db.isRecordExists(sql)) {
									query.add(new FieldValue("dateCreated", dataObj.getString("date_created")));
									query.add(new FieldValue("timeCreated", dataObj.getString("time_created")));
									query.add(new FieldValue("webTaskID", webTaskID));
									query.add(new FieldValue("isFromWeb", true));
									taskID = binder.insert(t, query);
								}
								else {
									taskID = db.getString(sql);
									binder.update(t, query, taskID);
								}
								JSONArray formArray = dataObj.getJSONArray("forms");
								for(int f = 0; f < formArray.length(); f++) {
									JSONObject formObj = formArray.getJSONObject(f);
									String formID = formObj.getString("form_id");
									query.clearAll();
									query.add(new FieldValue("taskID", taskID));
									query.add(new FieldValue("formID", formID));
									sql = "SELECT ID FROM " + tf + " WHERE taskID = '" + taskID + "' AND " +
											"formID = '" + formID + "'";
									if(!db.isRecordExists(sql)) {
										query.add(new FieldValue("isFromWeb", true));
										binder.insert(tf, query);
									}
									else {
										String taskFormID = db.getString(sql);
										query.add(new FieldValue("isTag", true));
										binder.update(tf, query, taskFormID);
									}
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getSettings(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-settings", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String s = Tables.getName(TB.SETTINGS);
						String sg = Tables.getName(TB.SETTINGS_GROUP);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String code = dataObj.getString("settings_code");
								String settingsID;
								String sql = "SELECT ID FROM " + s + " WHERE code = '" + code + "'";
								query.clearAll();
								query.add(new FieldValue("code", code));
								if(!db.isRecordExists(sql)) {
									settingsID = binder.insert(s, query);
								}
								else {
									settingsID = db.getString(sql);
									binder.update(s, query, settingsID);
								}
								query.clearAll();
								query.add(new Condition("settingsID", settingsID));
								query.add(new FieldValue("value", false));
								binder.update(sg, query);
								JSONArray teamArray = new JSONArray();
								String team = dataObj.getString("team_id");
								if(team != null && !team.isEmpty()) {
									if(team.equals("allteams")) {
										String groupID = TarkieLib.getGroupID(db);
										if(groupID != null) {
											teamArray.put(groupID);
										}
									}
									else {
										teamArray = dataObj.optJSONArray("team_id");
									}
									for(int g = 0; g < teamArray.length(); g++) {
										String teamID = teamArray.getString(g);
										sql = "SELECT ID FROM " + sg + " WHERE settingsID = " + settingsID + " AND groupID = " + teamID;
										query.clearAll();
										query.add(new FieldValue("settingsID", settingsID));
										query.add(new FieldValue("groupID", teamID));
										switch(code) {
											case Settings.MINIMUM_OT:
												query.add(new FieldValue("value", dataObj.getString("settings_value")));
												break;
											default:
												query.add(new FieldValue("value", true));
												break;
										}
										if(!db.isRecordExists(sql)) {
											binder.insert(sg, query);
										}
										else {
											String settingsGroupID = db.getString(sql);
											binder.update(sg, query, settingsGroupID);
										}
									}
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getServerTime(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-server-time", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						try {
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String array[] = dataObj.getString("date_time").split(" ");
								String date = CodePanUtils.formatDate(array[0]);
								String time = CodePanUtils.formatTime(array[1]);
								long timestamp = dataObj.getLong("timestamp") * 1000;
								result = TimeSecurity.updateServerTime(db, date, time, timestamp);
							}
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
						}
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

	public static boolean getContactPerson(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			paramsObj.put("employee_id", TarkieLib.getEmployeeID(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-store-contact-person-for-app", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String table = Tables.getName(TB.STORE_CONTACTS);
						try {
							SQLiteQuery query = new SQLiteQuery();
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String webContactID = dataObj.getString("contact_id");
								String webStoreID = dataObj.getString("store_id");
								String storeID = TarkieLib.getStoreID(db, webStoreID);
								if(storeID == null) {
									StoreObj store = new StoreObj();
									store.isSync = true;
									store.isFromWeb = true;
									store.isFromTask = true;
									store.isWebUpdate = true;
									store.webStoreID = webStoreID;
									storeID = TarkieLib.addStore(db, store);
								}
								query.clearAll();
								query.add(new FieldValue("storeID", storeID));
								query.add(new FieldValue("webContactID", webContactID));
								query.add(new FieldValue("name", dataObj.getString("name")));
								query.add(new FieldValue("designation", dataObj.getString("designation")));
								query.add(new FieldValue("mobile", dataObj.getString("mobile")));
								query.add(new FieldValue("landline", dataObj.getString("telephone")));
								query.add(new FieldValue("email", dataObj.getString("email")));
								query.add(new FieldValue("birthday", dataObj.getString("birthdate")));
								query.add(new FieldValue("remarks", dataObj.getString("remarks")));
								query.add(new FieldValue("isWebUpdate", true));
								query.add(new FieldValue("isSync", true));
								String sql = "SELECT ID FROM " + table + " WHERE webContactID = '" + webContactID + "'";
								if(!db.isRecordExists(sql)) {
									query.add(new FieldValue("isFromWeb", true));
									binder.insert(table, query);
								}
								else {
									String contactID = db.getString(sql);
									binder.update(table, query, contactID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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

	public static boolean getSKUStoreAssign(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", TarkieLib.getApiKey(db));
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(App.WEB_API + "get-inventory-sku-store-assign", paramsObj, TIMEOUT);
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
					int recNo = initObj.getInt("recno");
					if(initObj.getString("status").equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(initObj.getString("message"), params, response, true);
						}
					}
					if(hasData) {
						SQLiteBinder binder = new SQLiteBinder(db);
						String s = Tables.getName(TB.SKU);
						String ss = Tables.getName(TB.SKU_STORE);
						try {
							SQLiteQuery query = new SQLiteQuery();
							query.add(new Condition("isTag", true));
							query.add(new FieldValue("isTag", false));
							binder.update(ss, query);
							JSONArray dataArray = responseObj.getJSONArray("data");
							for(int d = 0; d < dataArray.length(); d++) {
								JSONObject dataObj = dataArray.getJSONObject(d);
								String skuID = dataObj.getString("sku_id");
								if(dataObj.isNull("is_all") || dataObj.getInt("is_all") == 0) {
									JSONArray storeArray = dataObj.optJSONArray("store_id");
									JSONArray class1Array = dataObj.optJSONArray("store_class_1");
									JSONArray class2Array = dataObj.optJSONArray("store_class_2");
									JSONArray class3Array = dataObj.optJSONArray("store_class_3");
									if(storeArray != null) {
										for(int sa = 0; sa < storeArray.length(); sa++) {
											String webStoreID = storeArray.getString(sa);
											String storeID = TarkieLib.getStoreID(db, webStoreID);
											if(storeID != null) {
												query.clearAll();
												query.add(new FieldValue("skuID", skuID));
												query.add(new FieldValue("storeID", storeID));
												query.add(new FieldValue("isTag", true));
												String sql = "SELECT ID FROM " + ss + " WHERE skuID = '"
														+ skuID + "' AND storeID = '" + storeID + "'";
												if(!db.isRecordExists(sql)) {
													binder.insert(ss, query);
												}
												else {
													String skuStoreID = db.getString(sql);
													binder.update(ss, query, skuStoreID);
												}
											}
										}
									}
									if(class1Array != null) {
										for(int c = 0; c < class1Array.length(); c++) {
											String classID = class1Array.getString(c);
											ArrayList<StoreObj> storeList = Data.loadStores(db, classID, StoreClass.CLASS_1);
											for(StoreObj store : storeList) {
												query.clearAll();
												query.add(new FieldValue("skuID", skuID));
												query.add(new FieldValue("storeID", store.ID));
												query.add(new FieldValue("isTag", true));
												String sql = "SELECT ID FROM " + ss + " WHERE skuID = '"
														+ skuID + "' AND storeID = '" + store.ID + "'";
												if(!db.isRecordExists(sql)) {
													binder.insert(ss, query);
												}
												else {
													String skuStoreID = db.getString(sql);
													binder.update(ss, query, skuStoreID);
												}
											}
										}
									}
									if(class2Array != null) {
										for(int c = 0; c < class2Array.length(); c++) {
											String classID = class2Array.getString(c);
											ArrayList<StoreObj> storeList = Data.loadStores(db, classID, StoreClass.CLASS_2);
											for(StoreObj store : storeList) {
												query.clearAll();
												query.add(new FieldValue("skuID", skuID));
												query.add(new FieldValue("storeID", store.ID));
												query.add(new FieldValue("isTag", true));
												String sql = "SELECT ID FROM " + ss + " WHERE skuID = '"
														+ skuID + "' AND storeID = '" + store.ID + "'";
												if(!db.isRecordExists(sql)) {
													binder.insert(ss, query);
												}
												else {
													String skuStoreID = db.getString(sql);
													binder.update(ss, query, skuStoreID);
												}
											}
										}
									}
									if(class3Array != null) {
										for(int c = 0; c < class3Array.length(); c++) {
											String classID = class3Array.getString(c);
											ArrayList<StoreObj> storeList = Data.loadStores(db, classID, StoreClass.CLASS_3);
											for(StoreObj store : storeList) {
												query.clearAll();
												query.add(new FieldValue("skuID", skuID));
												query.add(new FieldValue("storeID", store.ID));
												query.add(new FieldValue("isTag", true));
												String sql = "SELECT ID FROM " + ss + " WHERE skuID = '"
														+ skuID + "' AND storeID = '" + store.ID + "'";
												if(!db.isRecordExists(sql)) {
													binder.insert(ss, query);
												}
												else {
													String skuStoreID = db.getString(sql);
													binder.update(ss, query, skuStoreID);
												}
											}
										}
									}
								}
								else {
									query.clearAll();
									query.add(new FieldValue("isAll", true));
									binder.update(s, query, skuID);
								}
							}
							result = binder.finish();
						}
						catch(JSONException je) {
							je.printStackTrace();
							if(errorCallback != null) {
								errorCallback.onError(je.getMessage(), params, response, false);
							}
							binder.finish();
						}
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
}
