package com.mobileoptima.core;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.codepan.database.Condition;
import com.codepan.database.Condition.Operator;
import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.model.GpsObj;
import com.codepan.model.TimeObj;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.Incident;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreaksObj;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.EmployeeObj;
import com.mobileoptima.model.OvertimeReasonObj;
import com.mobileoptima.model.ScheduleObj;
import com.mobileoptima.model.ScheduleTimeObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TaskStatusObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.model.VisitObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;
import com.mobileoptima.tarkie.AlertDialogFragment;
import com.mobileoptima.tarkie.MainActivity;
import com.mobileoptima.tarkie.R;

import net.sqlcipher.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mobileoptima.schema.Tables.TB.COMPANY;
import static com.mobileoptima.schema.Tables.TB.CONVENTION;
import static com.mobileoptima.schema.Tables.TB.EMPLOYEE;

public class TarkieLib {

	public static void createTables(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		List<TB> tableList = Arrays.asList(TB.values());
		for(TB tb : tableList) {
			String table = Tables.getName(tb);
			binder.createTable(table, Tables.create(tb));
		}
		binder.finish();
	}

	public static boolean isAuthorized(SQLiteAdapter db) {
		return db.isRecordExists("SELECT ID FROM " + Tables.getName(TB.API_KEY) + " WHERE ID = 1");
	}

	public static boolean isLoggedIn(SQLiteAdapter db) {
		return db.isRecordExists("SELECT ID FROM " + Tables.getName(TB.CREDENTIALS) + " WHERE ID = 1"
				+ " AND isLogOut = 0");
	}

	public static String getApiKey(SQLiteAdapter db) {
		return db.getString("SELECT apiKey FROM " + Tables.getName(TB.API_KEY) + " WHERE ID = 1");
	}

	public static String getCompanyLogo(SQLiteAdapter db) {
		return db.getString("SELECT logoUrl FROM " + Tables.getName(TB.COMPANY) + " LIMIT 1");
	}

	public static String getEmployeeID(SQLiteAdapter db) {
		return db.getString("SELECT empID FROM " + Tables.getName(TB.CREDENTIALS) + " WHERE ID = 1");
	}

	public static String getGroupID(SQLiteAdapter db) {
		return db.getString("SELECT e.groupID FROM " + Tables.getName(TB.EMPLOYEE) + " e, " +
				Tables.getName(TB.CREDENTIALS) + " c ON c.empID = e.ID WHERE c.ID = 1");
	}

	public static String getLastUpdate(SQLiteAdapter db, int typeID) {
		return db.getString("SELECT dDate FROM " + Tables.getName(TB.UPDATE_HISTORY) +
				" WHERE typeID = '" + typeID + "' AND empID = '" + getEmployeeID(db) + "'");
	}

	public static String getStoreID(SQLiteAdapter db, String webStoreID) {
		return db.getString("SELECT ID FROM " + Tables.getName(TB.STORES) + " WHERE webStoreID = '"
				+ webStoreID + "'");
	}

	public static String getSyncBatchID(SQLiteAdapter db) {
		return db.getString("SELECT syncBatchID FROM " + Tables.getName(TB.SYNC_BATCH) + " WHERE ID = 1");
	}

	public static String addStore(SQLiteAdapter db, StoreObj store) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", getEmployeeID(db)));
		query.add(new FieldValue("dDate", CodePanUtils.getDate()));
		query.add(new FieldValue("dTime", CodePanUtils.getTime()));
		query.add(new FieldValue("name", store.name));
		query.add(new FieldValue("address", store.address));
		query.add(new FieldValue("shareWith", store.shareWith));
		query.add(new FieldValue("isFromWeb", store.isFromWeb));
		query.add(new FieldValue("isFromTask", store.isFromTask));
		query.add(new FieldValue("gpsLongitude", store.longitude));
		query.add(new FieldValue("gpsLatitude", store.latitude));
		query.add(new FieldValue("radius", store.radius));
		query.add(new FieldValue("isSync", store.isSync));
		query.add(new FieldValue("isUpdate", store.isUpdate));
		query.add(new FieldValue("isWebUpdate", store.isWebUpdate));
		query.add(new FieldValue("webStoreID", store.webStoreID));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		String storeID = binder.insert(Tables.getName(TB.STORES), query);
		binder.finish();
		return storeID;
	}

	public static boolean saveHistory(SQLiteAdapter db, int typeID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String empID = getEmployeeID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", CodePanUtils.getDate()));
		query.add(new FieldValue("dTime", CodePanUtils.getTime()));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("typeID", typeID));
		String table = Tables.getName(TB.UPDATE_HISTORY);
		String sql = "SELECT ID FROM " + table + " WHERE typeID = '" + typeID + "' " +
				"AND empID = '" + empID + "'";
		if(!db.isRecordExists(sql)) {
			binder.insert(table, query);
		}
		else {
			String updateHistoryID = db.getString(sql);
			binder.update(table, query, updateHistoryID);
		}
		return binder.finish();
	}

	public static String getStoreName(SQLiteAdapter db, String storeID) {
		return db.getString("SELECT name FROM " + Tables.getName(TB.STORES) + " WHERE ID = '" +
				storeID + "'");
	}

	public static boolean editStore(SQLiteAdapter db, StoreObj store) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("name", store.name));
		query.add(new FieldValue("address", store.address));
		query.add(new FieldValue("shareWith", store.shareWith));
		query.add(new FieldValue("gpsLongitude", store.longitude));
		query.add(new FieldValue("gpsLatitude", store.latitude));
		query.add(new FieldValue("radius", store.radius));
		query.add(new FieldValue("isWebUpdate", store.isWebUpdate));
		query.add(new FieldValue("isUpdate", true));
		binder.update(Tables.getName(TB.STORES), query, store.ID);
		return binder.finish();
	}

	public static boolean isTimeIn(SQLiteAdapter db) {
		return db.isRecordExists("SELECT ID FROM " + Tables.getName(TB.TIME_IN) + " WHERE empID = '"
				+ getEmployeeID(db) + "' AND isTimeOut = 0");
	}

	public static void alertDialog(MainActivity main, int title, int message) {
		final FragmentManager manager = main.getSupportFragmentManager();
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(title);
		alert.setDialogMessage(message);
		alert.setPositiveButton("OK", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
			}
		});
		main.addFadingToMain(alert);
	}

	public static void alertDialog(MainActivity main, String title, String message) {
		final FragmentManager manager = main.getSupportFragmentManager();
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(title);
		alert.setDialogMessage(message);
		alert.setPositiveButton("OK", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
			}
		});
		main.addFadingToMain(alert);
	}

	public static boolean logout(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isLogOut", true));
		binder.update(Tables.getName(TB.CREDENTIALS), query, 1);
		return binder.finish();
	}

	public static String saveGps(SQLiteAdapter db, GpsObj gps) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("gpsDate", gps.date));
		query.add(new FieldValue("gpsTime", gps.time));
		query.add(new FieldValue("gpsLongitude", gps.longitude));
		query.add(new FieldValue("gpsLatitude", gps.latitude));
		query.add(new FieldValue("isEnabled", gps.isEnabled));
		query.add(new FieldValue("withHistory", gps.withHistory));
		query.add(new FieldValue("isValid", gps.isValid));
		String gpsID = binder.insert(Tables.getName(TB.GPS), query);
		binder.finish();
		return gpsID;
	}

	public static String getTimeInID(SQLiteAdapter db) {
		String empID = getEmployeeID(db);
		String table = Tables.getName(TB.TIME_IN);
		String query = "SELECT ID FROM " + table + " WHERE isTimeOut = 0 " +
				"AND empID = '" + empID + "' ORDER BY ID DESC LIMIT 1";
		return db.getString(query);
	}

	public static boolean saveIncident(SQLiteAdapter db, GpsObj gps, int incidentID,
									   int value) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		if(incidentID == Incident.TAMPERED_TIME) {
			TimeObj server = TimeSecurity.getServerTime(db);
			dDate = server.date;
			dTime = server.time;
		}
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", getEmployeeID(db)));
		query.add(new FieldValue("gpsID", saveGps(db, gps)));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		query.add(new FieldValue("incidentID", incidentID));
		query.add(new FieldValue("timeInID", getTimeInID(db)));
		query.add(new FieldValue("value", value));
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		String table = Tables.getName(TB.INCIDENT_REPORT);
		if(!db.isRecordExists("SELECT ID FROM " + table + " WHERE dDate = '" + dDate + "' AND " +
				"dTime = '" + dTime + "' AND incidentID = '" + incidentID + "'")) {
			binder.insert(table, query);
		}
		return binder.finish();
	}

	public static boolean saveIncident(SQLiteAdapter db, GpsObj gps, int incidentID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String timeInID = getTimeInID(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		if(incidentID == Incident.TAMPERED_TIME) {
			TimeObj server = TimeSecurity.getServerTime(db);
			dDate = server.date;
			dTime = server.time;
		}
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", getEmployeeID(db)));
		query.add(new FieldValue("gpsID", saveGps(db, gps)));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		query.add(new FieldValue("incidentID", incidentID));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		String table = Tables.getName(TB.INCIDENT_REPORT);
		if(!db.isRecordExists("SELECT ID FROM " + table + " WHERE dDate = '" + dDate + "' AND " +
				"dTime = '" + dTime + "' AND incidentID = '" + incidentID + "'")) {
			binder.insert(table, query);
		}
		return binder.finish();
	}

	public static String getConvention(SQLiteAdapter db, String name) {
		String convention = db.getString("SELECT convention FROM " + Tables.getName(CONVENTION) + " WHERE name = ' " + name + "'");
		if(convention == null || convention.isEmpty() || convention.equalsIgnoreCase(Convention.DEFAULT)) {
			Context context = db.getContext();
			switch(name) {
				case Convention.TIME_IN:
					convention = context.getString(R.string.time_in);
					break;
				case Convention.TIME_OUT:
					convention = context.getString(R.string.time_out);
					break;
				case Convention.STORES:
					convention = context.getString(R.string.stores);
					break;
				case Convention.VISIT:
					convention = context.getString(R.string.visit);
					break;
			}
		}
		return CodePanUtils.capitalizeWord(convention);
	}

	public static boolean isTimeOut(SQLiteAdapter db) {
		return db.isRecordExists("SELECT ID FROM " + Tables.getName(TB.TIME_IN) + " WHERE empID = '"
				+ getEmployeeID(db) + "' AND " + "(ID = '" + getTimeInID(db) + "' OR dDate = '"
				+ CodePanUtils.getDate() + "') AND isTimeOut = 1");
	}

	public static boolean isSettingEnabled(SQLiteAdapter db, String code) {
		return db.getInt("SELECT sg.value FROM " + Tables.getName(TB.SETTINGS) + " s, "
				+ Tables.getName(TB.SETTINGS_GROUP) + " sg WHERE " + "sg.groupID = '"
				+ getGroupID(db) + "' AND sg.settingsID = s.ID " + "AND s.code = '" + code + "'") == 1;
	}

	public static boolean setDefaultStore(SQLiteAdapter db, String storeID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(TB.STORES);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDefault", false));
		query.add(new Condition("isDefault", true));
		binder.update(table, query);
		query.clearAll();
		query.add(new FieldValue("isDefault", true));
		binder.update(table, query, storeID);
		return binder.finish();
	}

	public static ScheduleObj getSchedule(SQLiteAdapter db, String date) {
		ScheduleObj schedule = null;
		Cursor cursor = db.read("SELECT ID, timeIn, timeOut, webScheduleID, isDayOff FROM "
				+ Tables.getName(TB.SCHEDULE) + " WHERE scheduleDate = '" + date + "' AND empID = '"
				+ getEmployeeID(db) + "' AND isActive = 1");
		while(cursor.moveToNext()) {
			schedule = new ScheduleObj();
			schedule.ID = cursor.getString(0);
			schedule.timeIn = cursor.getString(1);
			schedule.timeOut = cursor.getString(2);
			schedule.webScheduleID = cursor.getString(3);
			schedule.isDayOff = cursor.getInt(4) == 1;
			schedule.scheduleDate = date;
		}
		return schedule;
	}

	public static String saveSchedule(SQLiteAdapter db, ScheduleTimeObj time, String date) {
		String scheduleID;
		SQLiteBinder binder = new SQLiteBinder(db);
		String empID = getEmployeeID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("timeIn", time.in));
		query.add(new FieldValue("timeOut", time.out));
		query.add(new FieldValue("shiftID", time.shiftID));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		query.add(new FieldValue("isDayOff", time.isDayOff));
		query.add(new FieldValue("isFromWeb", false));
		query.add(new FieldValue("isSync", false));
		String table = Tables.getName(TB.SCHEDULE);
		String sql = "SELECT ID FROM " + table + " WHERE scheduleDate = '" + date + "' AND empID = '"
				+ empID + "' AND isActive = 1";
		if(!db.isRecordExists(sql)) {
			query.add(new FieldValue("empID", empID));
			query.add(new FieldValue("dDate", CodePanUtils.getDate()));
			query.add(new FieldValue("dTime", CodePanUtils.getTime()));
			query.add(new FieldValue("scheduleDate", date));
			scheduleID = binder.insert(table, query);
		}
		else {
			scheduleID = db.getString(sql);
			binder.update(table, query, scheduleID);
		}
		binder.finish();
		return scheduleID;
	}

	public static StoreObj getDefaulStore(SQLiteAdapter db) {
		StoreObj store = null;
		Cursor cursor = db.read("SELECT ID, name, address, gpsLongitude, gpsLatitude, radius FROM "
				+ Tables.getName(TB.STORES) + " WHERE isDefault = 1");
		while(cursor.moveToNext()) {
			store = new StoreObj();
			store.ID = cursor.getString(0);
			store.name = cursor.getString(1);
			store.address = cursor.getString(2);
			store.longitude = cursor.getDouble(3);
			store.latitude = cursor.getDouble(4);
			store.radius = cursor.getInt(5);
		}
		cursor.close();
		return store;
	}

	public static boolean saveTimeIn(SQLiteAdapter db, GpsObj gps, String storeID,
									 String scheduleID, String photo) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String empID = getEmployeeID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("dDate", CodePanUtils.getDate()));
		query.add(new FieldValue("dTime", CodePanUtils.getTime()));
		query.add(new FieldValue("photo", photo));
		query.add(new FieldValue("gpsID", saveGps(db, gps)));
		query.add(new FieldValue("storeID", storeID));
		query.add(new FieldValue("scheduleID", scheduleID));
		query.add(new FieldValue("batteryLevel", CodePanUtils.getBatteryLevel(db.getContext())));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		String table = Tables.getName(TB.TIME_IN);
		if(!db.isRecordExists("SELECT ID FROM " + table + " WHERE empID = '" + empID + "' " +
				"AND isTimeOut = 0")) {
			binder.insert(table, query);
		}
		return binder.finish();
	}

	public static boolean backUpDatabase(Context context, boolean external) {
		return CodePanUtils.extractDatabase(context, App.DB_BACKUP, App.DB, external);
	}

	public static AttendanceObj getAttendance(SQLiteAdapter db, String timeInID) {
		AttendanceObj attendance = new AttendanceObj();
		long totalBreak = getBreakDuration(db, timeInID);
		Cursor cursor = db.read("SELECT i.dDate, i.dTime, i.isTimeOut, o.dDate, o.dTime, o.signature, "
				+ "s.ID, s.timeIn, s.timeOut, s.scheduleDate, s.isDayOff FROM " + Tables.getName(TB.TIME_IN)
				+ " i LEFT JOIN " + Tables.getName(TB.TIME_OUT) + " o "
				+ "ON o.timeInID = i.ID LEFT JOIN " + Tables.getName(TB.SCHEDULE)
				+ " s ON s.ID = i.scheduleID " + "WHERE i.ID = '" + timeInID + "'");
		while(cursor.moveToNext()) {
			TimeInObj in = new TimeInObj();
			in.ID = timeInID;
			in.dDate = cursor.getString(0);
			in.dTime = cursor.getString(1);
			in.isTimeOut = cursor.getInt(2) == 1;
			TimeOutObj timeOut = new TimeOutObj();
			timeOut.dDate = cursor.getString(3);
			timeOut.dTime = cursor.getString(4);
			timeOut.signature = cursor.getString(5);
			String scheduleID = cursor.getString(6);
			if(scheduleID != null) {
				ScheduleObj schedule = new ScheduleObj();
				schedule.ID = scheduleID;
				schedule.timeIn = cursor.getString(7);
				schedule.timeOut = cursor.getString(8);
				schedule.scheduleDate = cursor.getString(9);
				schedule.isDayOff = cursor.getInt(10) == 1;
				in.schedule = schedule;
			}
			timeOut.timeIn = in;
			attendance.timeOut = timeOut;
			attendance.totalBreak = totalBreak;
		}
		cursor.close();
		return attendance;
	}

	private static long getBreakDuration(SQLiteAdapter db, String timeInID) {
		long totalBreak = 0L;
		Cursor cursor = db.read("SELECT i.dDate, i.dTime, o.dDate, o.dTime FROM "
				+ Tables.getName(TB.BREAK_IN) + " i LEFT JOIN " + Tables.getName(TB.BREAK_OUT)
				+ " o ON o.breakInID = i.ID WHERE i.timeInID = '" + timeInID + "'");
		while(cursor.moveToNext()) {
			String dateIn = cursor.getString(0);
			String TimeIn = cursor.getString(1);
			String dateOut = cursor.getString(2);
			String timeOut = cursor.getString(3);
			totalBreak += CodePanUtils.dateTimeToMillis(dateOut, timeOut) - CodePanUtils.dateTimeToMillis(dateIn, TimeIn);
		}
		cursor.close();
		return totalBreak;
	}

	public static String saveTimeOut(SQLiteAdapter db, GpsObj gps, String dDate, String dTime,
									 String storeID, String photo, String signature, int battery) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String timeInID = getTimeInID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("photo", photo));
		query.add(new FieldValue("gpsID", saveGps(db, gps)));
		query.add(new FieldValue("storeID", storeID));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("signature", signature));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		String timeOutID = binder.insert(Tables.getName(TB.TIME_OUT), query);
		if(timeOutID != null) {
			query.clearAll();
			query.add(new FieldValue("isTimeOut", true));
			binder.update(Tables.getName(TB.TIME_IN), query, timeInID);
		}
		binder.finish();
		return timeOutID;
	}

	public static boolean updateSignature(SQLiteAdapter db, TimeOutObj timeOut) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("signature", timeOut.signature));
		query.add(new FieldValue("isSignatureUpload", false));
		binder.update(Tables.getName(TB.TIME_OUT), query, timeOut.ID);
		return binder.finish();
	}

	public static EmployeeObj getEmployee(SQLiteAdapter db, String empID) {
		EmployeeObj emp = new EmployeeObj();
		Cursor cursor = db.read("SELECT employeeNo, firstName, lastName, email, mobile, imageUrl FROM "
				+ Tables.getName(TB.EMPLOYEE) + " WHERE ID = '" + empID + "'");
		while(cursor.moveToNext()) {
			emp.ID = empID;
			emp.employeeNo = cursor.getString(0);
			emp.firstName = cursor.getString(1);
			emp.lastName = cursor.getString(2);
			emp.fullName = emp.firstName + " " + emp.lastName;
			emp.email = cursor.getString(3);
			emp.mobile = cursor.getString(4);
			emp.imageUrl = cursor.getString(5);
		}
		cursor.close();
		return emp;
	}

	public static TimeInObj getTimeIn(SQLiteAdapter db) {
		TimeInObj timeIn = null;
		Cursor cursor = db.read("SELECT i.ID, i.dDate, i.dTime, s.ID, s.name FROM "
				+ Tables.getName(TB.TIME_IN) + " i LEFT JOIN " + Tables.getName(TB.STORES)
				+ " s ON s.ID = i.storeID WHERE i.isTimeOut = 0 AND i.empID = '"
				+ getEmployeeID(db) + "' ORDER BY i.ID DESC LIMIT 1");
		while(cursor.moveToNext()) {
			timeIn = new TimeInObj();
			timeIn.ID = cursor.getString(0);
			timeIn.dDate = cursor.getString(1);
			timeIn.dTime = cursor.getString(2);
			String storeID = cursor.getString(3);
			if(storeID != null) {
				StoreObj store = new StoreObj();
				store.ID = storeID;
				store.name = cursor.getString(4);
				timeIn.store = store;
			}
		}
		cursor.close();
		return timeIn;
	}

	public static boolean isBreakDone(SQLiteAdapter db, String breakID) {
		return db.isRecordExists("SELECT ID FROM " + Tables.getName(TB.BREAK_IN)
				+ " WHERE breakID = '" + breakID + "' AND timeInID = '" + getTimeInID(db)
				+ "' AND empID = '" + getEmployeeID(db) + "'");
	}

	public static boolean hasBreak(SQLiteAdapter db) {
		return db.isRecordExists("SELECT ID FROM " + Tables.getName(TB.BREAK));
	}

	public static boolean saveBreakIn(SQLiteAdapter db, GpsObj gps, BreaksObj breaks) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", getEmployeeID(db)));
		query.add(new FieldValue("dDate", CodePanUtils.getDate()));
		query.add(new FieldValue("dTime", CodePanUtils.getTime()));
		query.add(new FieldValue("gpsID", saveGps(db, gps)));
		query.add(new FieldValue("batteryLevel", CodePanUtils.getBatteryLevel(db.getContext())));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		query.add(new FieldValue("breakID", breaks.ID));
		query.add(new FieldValue("timeInID", getTimeInID(db)));
		binder.insert(Tables.getName(TB.BREAK_IN), query);
		return binder.finish();
	}

	public static BreakInObj getBreakIn(SQLiteAdapter db) {
		BreakInObj breakIn = new BreakInObj();
		Cursor cursor = db.read("SELECT ID, dDate, dTime FROM " + Tables.getName(TB.BREAK_IN)
				+ " WHERE timeInId = '" + getTimeInID(db) + "' AND empID = '" + getEmployeeID(db)
				+ "' AND isBreakOut = 0");
		while(cursor.moveToNext()) {
			breakIn.ID = cursor.getString(0);
			breakIn.dDate = cursor.getString(1);
			breakIn.dTime = cursor.getString(2);
		}
		cursor.close();
		return breakIn;
	}

	public static boolean isBreakIn(SQLiteAdapter db) {
		return db.isRecordExists("SELECT ID FROM " + Tables.getName(TB.BREAK_IN) + " WHERE empID = '"
				+ getEmployeeID(db) + "' AND timeInID = '" + getTimeInID(db) + "' AND isBreakOut = 0");
	}

	public static BreaksObj getBreak(SQLiteAdapter db, String breakInID) {
		BreaksObj breaks = new BreaksObj();
		Cursor cursor = db.read("SELECT b.ID, b.name, b.duration FROM " + Tables.getName(TB.BREAK)
				+ " b, " + Tables.getName(TB.BREAK_IN) + " i WHERE i.ID = '" + breakInID
				+ "' AND b.ID = i.breakID");
		while(cursor.moveToNext()) {
			breaks.ID = cursor.getString(0);
			breaks.name = cursor.getString(1);
			breaks.duration = cursor.getInt(2);
		}
		cursor.close();
		return breaks;
	}

	public static boolean saveBreakOut(SQLiteAdapter db, GpsObj gps, BreakInObj breakIn) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", CodePanUtils.getDate()));
		query.add(new FieldValue("dTime", CodePanUtils.getTime()));
		query.add(new FieldValue("gpsID", saveGps(db, gps)));
		query.add(new FieldValue("batteryLevel", CodePanUtils.getBatteryLevel(db.getContext())));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		query.add(new FieldValue("breakInID", breakIn.ID));
		if(binder.insert(Tables.getName(TB.BREAK_OUT), query) != null) {
			query.clearAll();
			query.add(new FieldValue("isBreakOut", true));
			binder.update(Tables.getName(TB.BREAK_IN), query, breakIn.ID);
		}
		return binder.finish();
	}

	public static boolean setPendingOT(SQLiteAdapter db, String timeOutID, boolean hasPendingOT) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("hasPendingOT", hasPendingOT));
		binder.update(Tables.getName(TB.TIME_OUT), query, timeOutID);
		return binder.finish();
	}

	public static float getSettingsValue(SQLiteAdapter db, String code) {
		return db.getFloat("SELECT sg.value FROM " + Tables.getName(TB.SETTINGS) + " s, "
				+ Tables.getName(TB.SETTINGS_GROUP) + " sg WHERE sg.groupID = '" + getGroupID(db)
				+ "' AND sg.settingsID = s.ID AND s.code = '" + code + "'");
	}

	public static boolean saveOT(SQLiteAdapter db, GpsObj gps, String timeInID, String startDate,
								 String startTime, String endDate, String endTime, String remarks,
								 String duration, ArrayList<OvertimeReasonObj> reasonList) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(TB.OVERTIME);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("gpsID", saveGps(db, gps)));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		query.add(new FieldValue("dDate", CodePanUtils.getDate()));
		query.add(new FieldValue("dTime", CodePanUtils.getTime()));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("startDate", startDate));
		query.add(new FieldValue("startTime", startTime));
		query.add(new FieldValue("endDate", endDate));
		query.add(new FieldValue("endTime", endTime));
		query.add(new FieldValue("remarks", remarks));
		query.add(new FieldValue("duration", duration));
		String reason = "";
		for(OvertimeReasonObj obj : reasonList) {
			if(obj.isChecked) {
				reason += obj.ID;
				if(reasonList.indexOf(obj) < reasonList.size() - 1) {
					reason += ",";
				}
			}
		}
		query.add(new FieldValue("reason", reason));
		String sql = "SELECT ID FROM " + table + " WHERE timeInID = '" + timeInID + "'";
		if(!db.isRecordExists(sql)) {
			binder.insert(table, query);
		}
		else {
			binder.update(table, query, db.getString(sql));
		}
		return binder.finish();
	}

	public static int getCountSyncTotal(SQLiteAdapter db) {
		return getCountSync(db) + getCountPhotoUpload(db) + getCountSignatureUpload(db) + getCountWebUpdate(db);
	}

	public static int getCountSync(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tbList = new ArrayList<>();
		tbList.add(TB.TIME_IN);
		tbList.add(TB.TIME_OUT);
		tbList.add(TB.BREAK_IN);
		tbList.add(TB.BREAK_OUT);
		tbList.add(TB.OVERTIME);
		tbList.add(TB.TASK);
		tbList.add(TB.CHECK_IN);
		tbList.add(TB.CHECK_OUT);
		SQLiteQuery query = new SQLiteQuery();
		for(TB tb : tbList) {
			query.clearAll();
			query.add(new Condition("isSync", false));
			switch(tb) {
				case TASK:
					query.add(new Condition("isFromWeb", false));
					query.add(new Condition("isDelete", false));
					break;
			}
			count += db.getInt("SELECT COUNT(ID) FROM " + Tables.getName(tb) + " WHERE "
					+ query.getConditions());
		}
		return count;
	}

	public static int getCountWebUpdate(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tbList = new ArrayList<>();
		tbList.add(TB.TASK);
		SQLiteQuery query = new SQLiteQuery();
		for(TB tb : tbList) {
			query.clearAll();
			query.add(new Condition("isSync", true));
			query.add(new Condition("isWebUpdate", false));
			switch(tb) {
				case TASK:
					query.add(new Condition("isDelete", false));
					query.add(new Condition("isUpdate", true));
					break;
			}
			count += db.getInt("SELECT COUNT(ID) FROM " + Tables.getName(tb) + " WHERE "
					+ query.getConditions());
		}
		return count;
	}

	public static int getCountPhotoUpload(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tbList = new ArrayList<>();
		tbList.add(TB.TIME_IN);
		tbList.add(TB.TIME_OUT);
		tbList.add(TB.CHECK_IN);
		tbList.add(TB.CHECK_OUT);
		tbList.add(TB.PHOTO);
		SQLiteQuery query = new SQLiteQuery();
		for(TB tb : tbList) {
			query.clearAll();
			switch(tb) {
				case PHOTO:
					query.add(new Condition("isUpload", false));
					query.add(new Condition("isSignature", false));
					query.add(new Condition("isDelete", false));
					query.add(new Condition("fileName", Operator.NOT_NULL));
					break;
				case CHECK_IN:
				case CHECK_OUT:
					query.add(new Condition("isUpload", false));
					query.add(new Condition("photo", Operator.NOT_NULL));
					break;
				case TIME_IN:
				case TIME_OUT:
					query.add(new Condition("isPhotoUpload", false));
					query.add(new Condition("photo", Operator.NOT_NULL));
					break;
			}
			count += db.getInt("SELECT COUNT(ID) FROM " + Tables.getName(tb) + " WHERE "
					+ query.getConditions());
		}
		return count;
	}

	public static int getCountSignatureUpload(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tbList = new ArrayList<>();
		tbList.add(TB.TIME_OUT);
		tbList.add(TB.PHOTO);
		SQLiteQuery query = new SQLiteQuery();
		for(TB tb : tbList) {
			query.clearAll();
			switch(tb) {
				case PHOTO:
					query.add(new Condition("isUpload", false));
					query.add(new Condition("isSignature", true));
					query.add(new Condition("isDelete", false));
					query.add(new Condition("fileName", Operator.NOT_NULL));
					break;
				case TIME_OUT:
					query.add(new Condition("isPhotoUpload", false));
					query.add(new Condition("signature", Operator.NOT_NULL));
					break;
			}
			count += db.getInt("SELECT COUNT(ID) FROM " + Tables.getName(tb) + " WHERE "
					+ query.getConditions());
		}
		return count;
	}

	public static boolean updateStatusSync(SQLiteAdapter db, TB tb, String recID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isSync", true));
		binder.update(Tables.getName(tb), query, recID);
		return binder.finish();
	}

	public static boolean updateLastSynced(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", CodePanUtils.getDate()));
		query.add(new FieldValue("dTime", CodePanUtils.getTime()));
		binder.update(Tables.getName(TB.SYNC_BATCH), query, 1);
		return binder.finish();
	}

	public static TaskObj addTask(SQLiteAdapter db, StoreObj store) {
		TaskObj task = new VisitObj();
		SQLiteBinder binder = new SQLiteBinder(db);
		String dDate = CodePanUtils.getDate();
		String empID = getEmployeeID(db);
		String table = Tables.getName(TB.TASK);
		SQLiteQuery query = new SQLiteQuery();
		if(store != null) {
			query.add(new FieldValue("storeID", store.ID));
			query.add(new FieldValue("name", store.name));
		}
		else {
			String sql = "SELECT COUNT(ID) FROM " + table + " WHERE isFromWeb = 0 AND " +
					"'" + dDate + "' BETWEEN startDate AND endDate AND empID = '" + empID + "'";
			int count = db.getInt(sql) + 1;
			task.name = "New " + getConvention(db, Convention.VISIT) + " " + count;
		}
		query.add(new FieldValue("name", task.name));
		query.add(new FieldValue("dateCreated", dDate));
		query.add(new FieldValue("timeCreated", CodePanUtils.getTime()));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("startDate", dDate));
		query.add(new FieldValue("endDate", dDate));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		task.ID = binder.insert(table, query);
		binder.finish();
		return task;
	}

	public static boolean isCheckIn(SQLiteAdapter db) {
		Log.e("query", "SELECT i.ID FROM " + Tables.getName(TB.CHECK_IN) + " i, " +
				Tables.getName(TB.TASK) + " t WHERE t.isCheckIn = 1 AND t.isCheckOut = 0 " +
				"AND i.empID = '" + getEmployeeID(db) + "' AND i.taskID = t.ID ORDER BY i.ID DESC LIMIT 1");
		return db.isRecordExists("SELECT i.ID FROM " + Tables.getName(TB.CHECK_IN) + " i, " +
				Tables.getName(TB.TASK) + " t WHERE t.isCheckIn = 1 AND t.isCheckOut = 0 " +
				"AND i.empID = '" + getEmployeeID(db) + "' AND i.taskID = t.ID ORDER BY i.ID DESC LIMIT 1");
	}

	public static String getPendingVisits(SQLiteAdapter db) {
		StringBuilder builder = new StringBuilder();
		Cursor cursor = db.read("SELECT t.name FROM " + Tables.getName(TB.CHECK_IN) + " i, " +
				Tables.getName(TB.TASK) + " t WHERE t.isCheckIn = 1 AND t.isCheckOut = 0 " +
				"AND i.empID = '" + getEmployeeID(db) + "' AND i.taskID = t.ID ORDER BY i.ID");
		int count = cursor.getCount();
		while(cursor.moveToNext()) {
			String name = cursor.getString(0);
			if(count > 1) {
				int position = cursor.getPosition();
				if(position < count - 1) {
					if(position == count - 2) {
						name += " and ";
					}
					else {
						name += ", ";
					}
				}
			}
			builder.append(name);
		}
		cursor.close();
		return builder.toString();
	}

	public static boolean saveCheckIn(SQLiteAdapter db, CheckInObj checkIn) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		String taskID = checkIn.task.ID;
		checkIn.dDate = CodePanUtils.getDate();
		checkIn.dTime = CodePanUtils.getTime();
		query.add(new FieldValue("dDate", checkIn.dDate));
		query.add(new FieldValue("dTime", checkIn.dTime));
		query.add(new FieldValue("empID", getEmployeeID(db)));
		query.add(new FieldValue("taskID", taskID));
		query.add(new FieldValue("timeInID", getTimeInID(db)));
		query.add(new FieldValue("batteryLevel", CodePanUtils.getBatteryLevel(db.getContext())));
		query.add(new FieldValue("gpsID", saveGps(db, checkIn.gps)));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		query.add(new FieldValue("photo", checkIn.photo));
		query.add(new FieldValue("isSync", false));
		query.add(new FieldValue("isUpload", false));
		String table = Tables.getName(TB.CHECK_IN);
		if(!db.isRecordExists("SELECT ID FROM " + table + " WHERE taskID = '" + taskID + "'")) {
			if(binder.insert(table, query) != null) {
				query.clearAll();
				query.add(new FieldValue("isCheckIn", true));
				binder.update(Tables.getName(TB.TASK), query, taskID);
			}
		}
		return binder.finish();
	}

	public static String getCheckInID(SQLiteAdapter db, String visitID) {
		return db.getString("SELECT ID FROM " + Tables.getName(TB.CHECK_IN) + " WHERE taskID = '" +
				visitID + "'");
	}

	public static boolean isVisitFinalized(SQLiteAdapter db, String taskID) {
		return db.getInt("SELECT ti.isTimeOut FROM " + Tables.getName(TB.TASK) + " t, " +
				Tables.getName(TB.CHECK_IN) + " ci, " + Tables.getName(TB.TIME_IN) + " ti WHERE t.ID = '" +
				taskID + "' AND ci.taskID = t.ID AND ti.ID = ci.timeInID") == 1;
	}

	public static boolean editTask(SQLiteAdapter db, StoreObj store, VisitObj visit, String notes) {
		SQLiteBinder binder = new SQLiteBinder(db);
//		String te = Tables.getName(TB.TASK_ENTRY);
//		String tp = Tables.getName(TB.TASK_PHOTO);
//		String ti = Tables.getName(TB.TASK_INVENTORY);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("notes", notes));
		query.add(new FieldValue("isWebUpdate", false));
		if(store != null) {
			query.add(new FieldValue("name", store.name));
			query.add(new FieldValue("storeID", store.ID));
			query.add(new FieldValue("isUpdate", true));
		}
		binder.update(Tables.getName(TB.TASK), query, visit.ID);
//		query.clearAll();
//		query.add(new FieldValue("isTag", false));
//		query.add(new Condition("taskID", visit.ID));
//		binder.update(ti, query);
//		binder.update(Tables.getName(TB.TASK_FORM), query);
//		binder.update(tp, query);
//		for(InventoryObj inventory : inventoryList) {
//			InventoryTypeObj type = inventory.type;
//			TaskInventoryObj taskInventory = inventory.taskInventory;
//			query.clearAll();
//			query.add(new FieldValue("inventoryID", inventory.ID));
//			query.add(new FieldValue("isTag", true));
//			if(!type.isTagged) {
//				query.add(new FieldValue("taskID", taskID));
//				query.add(new FieldValue("inventoryType", type.value));
//				binder.insert(ti, query);
//			}
//			else {
//				binder.update(ti, query, taskInventory.ID);
//			}
//		}
//		for(EntryObj entry : entryList) {
//			FormObj form = entry.form;
//			TaskFormObj taskForm = form.taskForm;
//			query.clearAll();
//			query.add(new FieldValue("isTag", true));
//			if(!form.isTagged) {
//				taskForm = new TaskFormObj();
//				query.add(new FieldValue("formID", form.ID));
//				query.add(new FieldValue("taskID", taskID));
//				taskForm.ID = binder.insert(tf, query);
//			}
//			else {
//				binder.update(tf, query, taskForm.ID);
//			}
//			if(entry.ID != null) {
//				query.clearAll();
//				query.add(new Field("ID"));
//				query.add(new Condition("entryID", entry.ID));
//				query.add(new Condition("taskFormID", taskForm.ID));
//				query.add(new FieldValue("entryID", entry.ID));
//				query.add(new FieldValue("taskFormID", taskForm.ID));
//				String sql = query.select(te);
//				if(!db.isRecordExists(sql)) {
//					binder.insert(te, query);
//				}
//			}
//		}
//		for(PhotoObj photo : photoList) {
//			query.clearAll();
//			query.add(new Field("ID"));
//			query.add(new Condition("photoID", photo.ID));
//			query.add(new Condition("taskID", taskID));
//			query.add(new FieldValue("isTag", true));
//			String sql = query.select(tp);
//			if(!db.isRecordExists(sql)) {
//				query.add(new FieldValue("photoID", photo.ID));
//				query.add(new FieldValue("taskID", taskID));
//				binder.insert(tp, query);
//			}
//			else {
//				binder.update(tp, query);
//			}
//		}
		return binder.finish();
	}

	public static boolean saveCheckOut(SQLiteAdapter db, CheckOutObj checkOut, TaskStatusObj status) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		CheckInObj checkIn = checkOut.checkIn;
		checkOut.dDate = CodePanUtils.getDate();
		checkOut.dTime = CodePanUtils.getTime();
		query.add(new FieldValue("dDate", checkOut.dDate));
		query.add(new FieldValue("dTime", checkOut.dTime));
		query.add(new FieldValue("checkInID", checkOut.checkIn.ID));
		query.add(new FieldValue("batteryLevel", CodePanUtils.getBatteryLevel(db.getContext())));
		query.add(new FieldValue("gpsID", saveGps(db, checkOut.gps)));
		query.add(new FieldValue("syncBatchID", getSyncBatchID(db)));
		query.add(new FieldValue("photo", checkOut.photo));
		String checkOutID = binder.insert(Tables.getName(TB.CHECK_OUT), query);
		if(checkOutID != null) {
			TaskObj task = checkIn.task;
			query.clearAll();
			query.add(new FieldValue("isCheckOut", true));
			query.add(new FieldValue("status", status.name));
			if(task.notes != null) {
				if(status.notes != null && !status.notes.isEmpty()) {
					task.notes += " " + status.notes;
				}
			}
			else {
				task.notes = status.notes;
			}
			query.add(new FieldValue("notes", task.notes));
			binder.update(Tables.getName(TB.TASK), query, task.ID);
		}
		return binder.finish();
	}

	public static boolean updateStatusUpload(SQLiteAdapter db, TB tb, String recID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isUpload", true));
		binder.update(Tables.getName(tb), query, recID);
		return binder.finish();
	}

	public static boolean updateStatusPhotoUpload(SQLiteAdapter db, TB tb, String recID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isPhotoUpload", true));
		binder.update(Tables.getName(tb), query, recID);
		return binder.finish();
	}

	public static boolean updateStatusSignatureUpload(SQLiteAdapter db, TB tb, String timeInID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isSignatureUpload", true));
		query.add(new Condition("timeInID", timeInID));
		binder.update(Tables.getName(tb), query);
		return binder.finish();
	}

	public static String getCompanyName(SQLiteAdapter db) {
		return db.getString("SELECT name FROM " + Tables.getName(COMPANY) + " LIMIT 1");
	}

	public static String getEmployeeName(SQLiteAdapter db, String empID, boolean lastNameFirst) {
		String name = null;
		Cursor cursor = db.read("SELECT firstName, lastName FROM " + Tables.getName(EMPLOYEE) + " WHERE ID = '" + empID + "'");
		while(cursor.moveToNext()) {
			String firstName = cursor.getString(0);
			String lastName = cursor.getString(1);
			if(lastNameFirst) {
				name = lastName + ", " + firstName;
			}
			else {
				name = firstName + " " + lastName;
			}
		}
		cursor.close();
		return name;
	}
	public static String getBackupFileName(SQLiteAdapter db) {
		String fileName = getCompanyName(db) + "_" + getEmployeeName(db, getEmployeeID(db), false) +
				"_" + CodePanUtils.getDate() + "_" + CodePanUtils.getTime() + "_" +
				CodePanUtils.getVersionName(db.getContext()) + ".zip";
		fileName = fileName.replace(" ", "_");
		fileName = fileName.replace(":", "-");
		return fileName;
	}

	public static boolean updateStatusWebUpdate(SQLiteAdapter db, TB tb, String recID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isWebUpdate", true));
		binder.update(Tables.getName(tb), query, recID);
		return binder.finish();
	}
}
