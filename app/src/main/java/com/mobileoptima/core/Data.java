package com.mobileoptima.core;

import com.codepan.database.Condition;
import com.codepan.database.Condition.Operator;
import com.codepan.database.Field;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteQuery;
import com.codepan.model.GpsObj;
import com.mobileoptima.constant.StoreClass;
import com.mobileoptima.constant.TaskStatus;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakOutObj;
import com.mobileoptima.model.BreaksObj;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.EmployeeObj;
import com.mobileoptima.model.OvertimeObj;
import com.mobileoptima.model.OvertimeReasonObj;
import com.mobileoptima.model.PhotoObj;
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

import net.sqlcipher.Cursor;

import java.util.ArrayList;

public class Data {

	public static ArrayList<StoreObj> loadStores(SQLiteAdapter db, String classID, int storeClass) {
		ArrayList<StoreObj> storeList = new ArrayList<>();
		String table = Tables.getName(TB.STORES);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new Field("ID"));
		query.add(new Field("name"));
		switch(storeClass) {
			case StoreClass.CLASS_1:
				query.add(new Condition("class1ID", classID));
				break;
			case StoreClass.CLASS_2:
				query.add(new Condition("class2ID", classID));
				break;
			case StoreClass.CLASS_3:
				query.add(new Condition("class3ID", classID));
				break;
		}
		String sql = query.select(table);
		Cursor cursor = db.read(sql);
		while(cursor.moveToNext()) {
			StoreObj store = new StoreObj();
			store.ID = cursor.getString(0);
			store.name = cursor.getString(1);
			storeList.add(store);
		}
		cursor.close();
		return storeList;
	}

	public static ArrayList<StoreObj> loadStores(SQLiteAdapter db, String search) {
		ArrayList<StoreObj> storeList = new ArrayList<>();
		SQLiteQuery query = new SQLiteQuery();
		String lastHeader = "";
		query.add(new Condition("empID", TarkieLib.getEmployeeID(db)));
		query.add(new Condition("isTag", true));
		query.add(new Condition("isActive", true));
		query.add(new Condition("name", Operator.NOT_NULL));
		query.add(new Condition("name", Operator.NOT_EMPTY));
		if(search != null && !search.isEmpty()) {
			query.add(new Condition("name", search, Operator.LIKE));
		}
		String sql = "SELECT ID, name, address, radius, gpsLongitude, gpsLatitude, isFromWeb, " +
				"webStoreID, shareWith FROM " + Tables.getName(TB.STORES) + " WHERE " +
				query.getConditions() + " ORDER BY name";
		Cursor cursor = db.read(sql);
		while(cursor.moveToNext()) {
			StoreObj store = new StoreObj();
			String name = cursor.getString(1);
			store.ID = cursor.getString(0);
			store.name = name;
			store.address = cursor.getString(2);
			store.radius = cursor.getInt(3);
			store.longitude = cursor.getDouble(4);
			store.latitude = cursor.getDouble(5);
			store.isFromWeb = cursor.getInt(6) == 1;
			store.webStoreID = cursor.getString(7);
			store.shareWith = cursor.getString(8);
			String currentFirstChar = name.substring(0, 1);
			int position = cursor.getPosition();
			if(currentFirstChar.matches("[a-zA-Z]")) {
				if(position != 0) {
					if(!currentFirstChar.equals(lastHeader)) {
						store.isHeader = true;
						lastHeader = currentFirstChar;
						StoreObj obj = storeList.get(position - 1);
						obj.isRemoveDivider = true;
						storeList.set(position - 1, obj);
					}
					else {
						store.isHeader = false;
					}
					store.header = currentFirstChar;
				}
			}
			else {
				if(position == 0) {
					store.isHeader = true;
					store.header = "#";
				}
			}
			storeList.add(store);
		}
		cursor.close();
		return storeList;
	}

	public static ArrayList<ScheduleTimeObj> loadScheduleTime(SQLiteAdapter db) {
		ArrayList<ScheduleTimeObj> schedTimeList = new ArrayList<>();
		Cursor cursor = db.read("SELECT ID, timeIn, timeOut, color, shiftID FROM "
				+ Tables.getName(TB.SCHEDULE_TIME) + " WHERE isActive = 1 ORDER BY timeIn");
		while(cursor.moveToNext()) {
			ScheduleTimeObj sched = new ScheduleTimeObj();
			sched.ID = cursor.getString(0);
			sched.in = cursor.getString(1);
			sched.out = cursor.getString(2);
			sched.color = cursor.getString(3);
			sched.shiftID = cursor.getString(4);
			schedTimeList.add(sched);
		}
		cursor.close();
		ScheduleTimeObj rest = new ScheduleTimeObj();
		rest.in = "00:00:00";
		rest.out = "00:00:00";
		rest.isDayOff = true;
		schedTimeList.add(rest);
		return schedTimeList;
	}

	public static ArrayList<BreaksObj> loadBreaks(SQLiteAdapter db) {
		ArrayList<BreaksObj> breakList = new ArrayList<>();
		Cursor cursor = db.read("SELECT * FROM " + Tables.getName(TB.BREAK));
		while(cursor.moveToNext()) {
			BreaksObj breaks = new BreaksObj();
			breaks.ID = cursor.getString(0);
			breaks.name = cursor.getString(1);
			breaks.duration = cursor.getInt(2);
			breaks.isDone = TarkieLib.isBreakDone(db, breaks.ID);
			breakList.add(breaks);
		}
		cursor.close();
		return breakList;
	}

	public static ArrayList<OvertimeReasonObj> loadOvertimeReasons(SQLiteAdapter db) {
		ArrayList<OvertimeReasonObj> reasonList = new ArrayList<>();
		Cursor cursor = db.read("SELECT ID, name FROM " + Tables.getName(TB.OVERTIME_REASON));
		while(cursor.moveToNext()) {
			OvertimeReasonObj otReason = new OvertimeReasonObj();
			otReason.ID = cursor.getString(0);
			otReason.name = cursor.getString(1);
			otReason.isChecked = false;
			reasonList.add(otReason);
		}
		return reasonList;
	}

	public static ArrayList<TimeInObj> loadTimeInSync(SQLiteAdapter db) {
		ArrayList<TimeInObj> timeInList = new ArrayList<>();
		Cursor cursor = db.read("SELECT i.ID, i.empID, i.dDate, i.dTime, i.syncBatchID, "
				+ "i.batteryLevel, g.gpsDate, g.gpsTime, g.gpsLongitude, g.gpsLatitude, g.isValid, "
				+ "s.ID, s.webStoreID, sc.ID, sc.webScheduleID FROM " + Tables.getName(TB.TIME_IN)
				+ " i, " + Tables.getName(TB.GPS) + " g LEFT JOIN " + Tables.getName(TB.STORES)
				+ " s ON s.ID = i.storeID LEFT JOIN " + Tables.getName(TB.SCHEDULE)
				+ " sc ON sc.ID = i.scheduleID WHERE i.isSync = 0 AND g.ID = i.gpsID");
		while(cursor.moveToNext()) {
			TimeInObj timeIn = new TimeInObj();
			timeIn.ID = cursor.getString(0);
			EmployeeObj emp = new EmployeeObj();
			emp.ID = cursor.getString(1);
			timeIn.emp = emp;
			timeIn.dDate = cursor.getString(2);
			timeIn.dTime = cursor.getString(3);
			timeIn.syncBatchID = cursor.getString(4);
			timeIn.batteryLevel = cursor.getInt(5);
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(6);
			gps.time = cursor.getString(7);
			gps.longitude = cursor.getDouble(8);
			gps.latitude = cursor.getDouble(9);
			gps.isValid = cursor.getInt(10) == 1;
			timeIn.gps = gps;
			StoreObj store = new StoreObj();
			store.ID = cursor.getString(11);
			store.webStoreID = cursor.getString(12);
			timeIn.store = store;
			ScheduleObj schedule = new ScheduleObj();
			schedule.ID = cursor.getString(13);
			schedule.webScheduleID = cursor.getString(14);
			timeIn.schedule = schedule;
			timeInList.add(timeIn);
		}
		cursor.close();
		return timeInList;
	}

	public static ArrayList<TimeOutObj> loadTimeOutSync(SQLiteAdapter db) {
		ArrayList<TimeOutObj> timeOutList = new ArrayList<>();
		Cursor cursor = db.read("SELECT o.ID, o.dDate, o.dTime, o.syncBatchID, o.storeID, s.webStoreID, "
				+ "i.ID, i.syncBatchID, i.empID, g.gpsDate, g.gpsTime, g.gpsLongitude, g.gpsLatitude, "
				+ "g.isValid FROM " + Tables.getName(TB.TIME_OUT) + " o, " + Tables.getName(TB.TIME_IN)
				+ " i, " + Tables.getName(TB.GPS) + " g LEFT JOIN " + Tables.getName(TB.STORES)
				+ " s ON s.ID = o.storeID WHERE o.isSync = 0 AND g.ID = o.gpsID AND i.ID = o.timeInID");
		while(cursor.moveToNext()) {
			TimeOutObj timeOut = new TimeOutObj();
			timeOut.ID = cursor.getString(0);
			timeOut.dDate = cursor.getString(1);
			timeOut.dTime = cursor.getString(2);
			timeOut.syncBatchID = cursor.getString(3);
			StoreObj store = new StoreObj();
			store.ID = cursor.getString(4);
			store.webStoreID = cursor.getString(5);
			timeOut.store = store;
			TimeInObj timeIn = new TimeInObj();
			timeIn.ID = cursor.getString(6);
			timeIn.syncBatchID = cursor.getString(7);
			EmployeeObj emp = new EmployeeObj();
			emp.ID = cursor.getString(8);
			timeIn.emp = emp;
			timeOut.emp = emp;
			timeOut.timeIn = timeIn;
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(9);
			gps.time = cursor.getString(10);
			gps.longitude = cursor.getDouble(11);
			gps.latitude = cursor.getDouble(12);
			gps.isValid = cursor.getInt(13) == 1;
			timeOut.gps = gps;
			timeOutList.add(timeOut);
		}
		cursor.close();
		return timeOutList;
	}

	public static ArrayList<BreakInObj> loadBreakInSync(SQLiteAdapter db) {
		ArrayList<BreakInObj> breakInList = new ArrayList<>();
		String query = "SELECT i.ID, i.empID, i.dDate, i.dTime, i.syncBatchID, g.gpsDate, g.gpsTime,"
				+ " g.gpsLongitude, g.gpsLatitude, g.isValid FROM " + Tables.getName(TB.BREAK_IN)
				+ " i, " + Tables.getName(TB.GPS) + " g WHERE i.isSync = 0 AND g.ID = i.gpsID";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			BreakInObj in = new BreakInObj();
			in.ID = cursor.getString(0);
			EmployeeObj emp = new EmployeeObj();
			emp.ID = cursor.getString(1);
			in.emp = emp;
			in.dDate = cursor.getString(2);
			in.dTime = cursor.getString(3);
			in.syncBatchID = cursor.getString(4);
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(5);
			gps.time = cursor.getString(6);
			gps.longitude = cursor.getDouble(7);
			gps.latitude = cursor.getDouble(8);
			gps.isValid = cursor.getInt(9) == 1;
			in.gps = gps;
			breakInList.add(in);
		}
		cursor.close();
		return breakInList;
	}

	public static ArrayList<BreakOutObj> loadBreakOutSync(SQLiteAdapter db) {
		ArrayList<BreakOutObj> breakOutList = new ArrayList<>();
		String query = "SELECT o.ID, o.dDate, o.dTime, o.syncBatchID, i.ID, i.syncBatchID, i.empID, "
				+ "g.gpsDate, g.gpsTime, g.gpsLongitude, g.gpsLatitude, g.isValid FROM "
				+ Tables.getName(TB.BREAK_OUT) + " o, " + Tables.getName(TB.BREAK_IN) + " i, "
				+ Tables.getName(TB.GPS) + " g WHERE o.isSync = 0 AND g.ID = o.gpsID AND i.ID = o.breakInID";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			BreakOutObj out = new BreakOutObj();
			out.ID = cursor.getString(0);
			out.dDate = cursor.getString(1);
			out.dTime = cursor.getString(2);
			out.syncBatchID = cursor.getString(3);
			BreakInObj in = new BreakInObj();
			in.ID = cursor.getString(4);
			in.syncBatchID = cursor.getString(5);
			EmployeeObj emp = new EmployeeObj();
			emp.ID = cursor.getString(6);
			in.emp = emp;
			out.emp = emp;
			out.breakIn = in;
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(7);
			gps.time = cursor.getString(8);
			gps.longitude = cursor.getDouble(9);
			gps.latitude = cursor.getDouble(10);
			gps.isValid = cursor.getInt(11) == 1;
			out.gps = gps;
			breakOutList.add(out);
		}
		cursor.close();
		return breakOutList;
	}

	public static ArrayList<OvertimeObj> loadOTSync(SQLiteAdapter db) {
		ArrayList<OvertimeObj> overtimeList = new ArrayList<>();
		String query = "SELECT v.ID, v.duration, v.remarks, v.reason, v.syncBatchID, i.ID, i.syncBatchID, "
				+ "i.empID FROM " + Tables.getName(TB.OVERTIME) + " v, " + Tables.getName(TB.TIME_IN)
				+ " i WHERE i.ID = v.timeInID AND v.isSync = 0";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			OvertimeObj ot = new OvertimeObj();
			ot.ID = cursor.getString(0);
			ot.hours = cursor.getString(1);
			ot.remarks = cursor.getString(2);
			String reason = cursor.getString(3);
			if(reason != null && !reason.isEmpty()) {
				String[] reasonArray = reason.split(",");
				ArrayList<OvertimeReasonObj> reasonList = new ArrayList<>();
				for(String reasonID : reasonArray) {
					OvertimeReasonObj obj = new OvertimeReasonObj();
					obj.ID = reasonID;
					reasonList.add(obj);
				}
				ot.reasonList = reasonList;
			}
			ot.syncBatchID = cursor.getString(4);
			TimeInObj in = new TimeInObj();
			in.ID = cursor.getString(5);
			in.syncBatchID = cursor.getString(6);
			EmployeeObj emp = new EmployeeObj();
			emp.ID = cursor.getString(7);
			in.emp = emp;
			ot.timeIn = in;
			overtimeList.add(ot);
		}
		cursor.close();
		return overtimeList;
	}

	public static ArrayList<VisitObj> loadVisits(SQLiteAdapter db, String date, boolean isActive) {
		ArrayList<VisitObj> visitList = new ArrayList<>();
		SQLiteQuery query = new SQLiteQuery();
		query.add(new Condition("t.empID", TarkieLib.getEmployeeID(db)));
		if(isActive) {
			query.add(new Condition("t.isCheckOut", false));
		}
		Cursor cursor = db.read("SELECT t.ID, t.name, t.notes, t.notesLimit, t.isCheckIn, " +
				"t.isCheckOut, t.isFromWeb, t.isUpdate,  s.ID, s.name, s.address, i.ID, i.dDate, " +
				"i.dTime, o.ID, o.dDate, o.dTime FROM " + Tables.getName(TB.TASK) + " as t LEFT JOIN " +
				Tables.getName(TB.CHECK_IN) + " as i ON i.taskID = t.ID LEFT JOIN " +
				Tables.getName(TB.CHECK_OUT) + " as o ON o.checkInID = i.ID LEFT JOIN " +
				Tables.getName(TB.STORES) + " as s ON s.ID = t.storeID WHERE '" + date +
				"' BETWEEN t.startDate AND t.endDate AND " + query.getConditions() +
				" AND (t.isDelete = 0 OR t.isCheckIn = 1) ORDER BY t.ID DESC");
		while(cursor.moveToNext()) {
			VisitObj visit = new VisitObj();
			visit.ID = cursor.getString(0);
			visit.name = cursor.getString(1);
			visit.notes = cursor.getString(2);
			visit.notesLimit = cursor.getInt(3);
			visit.isCheckIn = cursor.getInt(4) == 1;
			visit.isCheckOut = cursor.getInt(5) == 1;
			visit.isFromWeb = cursor.getInt(6) == 1;
			visit.isUpdate = cursor.getInt(7) == 1;
			String storeID = cursor.getString(8);
			if(storeID != null) {
				StoreObj store = new StoreObj();
				store.ID = storeID;
				store.name = cursor.getString(9);
				store.address = cursor.getString(10);
				visit.store = store;
			}
			String checkInID = cursor.getString(11);
			if(checkInID != null && visit.isCheckIn) {
				CheckInObj checkIn = new CheckInObj();
				checkIn.ID = checkInID;
				checkIn.dDate = cursor.getString(12);
				checkIn.dTime = cursor.getString(13);
				visit.checkIn = checkIn;
			}
			String checkOutID = cursor.getString(14);
			if(checkOutID != null && visit.isCheckOut) {
				CheckOutObj checkOut = new CheckOutObj();
				checkOut.ID = checkOutID;
				checkOut.dDate = cursor.getString(15);
				checkOut.dTime = cursor.getString(16);
				visit.checkOut = checkOut;
			}
			visitList.add(visit);
		}
		cursor.close();
		return visitList;
	}

	public static ArrayList<TaskStatusObj> loadStatus() {
		ArrayList<TaskStatusObj> statusList = new ArrayList<>();
		TaskStatusObj def = new TaskStatusObj();
		def.code = TaskStatus.DEFAULT;
		def.name = "Select Status";
		statusList.add(def);
		TaskStatusObj completed = new TaskStatusObj();
		completed.code = TaskStatus.COMPLETED;
		completed.name = "Completed";
		statusList.add(completed);
		TaskStatusObj incomplete = new TaskStatusObj();
		incomplete.code = TaskStatus.NOT_COMPLETED;
		incomplete.name = "Not Completed";
		statusList.add(incomplete);
		TaskStatusObj cancel = new TaskStatusObj();
		cancel.code = TaskStatus.CANCELLED;
		cancel.name = "Cancelled";
		statusList.add(cancel);
		return statusList;
	}

	public static ArrayList<TaskObj> loadTaskSync(SQLiteAdapter db) {
		ArrayList<TaskObj> taskList = new ArrayList<>();
		Cursor cursor = db.read("SELECT t.ID, t.dateCreated, t.timeCreated, t.webTaskID, t.startDate, " +
				"t.endDate, t.notes, t.syncBatchID, t.empID, s.ID, s.webStoreID FROM " +
				Tables.getName(TB.TASK) + " t LEFT JOIN " + Tables.getName(TB.STORES) +
				" s ON s.ID = t.storeID WHERE t.isSync = 0");
		while(cursor.moveToNext()) {
			TaskObj task = new TaskObj();
			task.ID = cursor.getString(0);
			task.dDate = cursor.getString(1);
			task.dTime = cursor.getString(2);
			task.webTaskID = cursor.getString(3);
			task.startDate = cursor.getString(4);
			task.endDate = cursor.getString(5);
			task.notes = cursor.getString(6);
			task.syncBatchID = cursor.getString(7);
			EmployeeObj emp = new EmployeeObj();
			emp.ID = cursor.getString(8);
			task.emp = emp;
			StoreObj store = new StoreObj();
			store.ID = cursor.getString(9);
			store.webStoreID = cursor.getString(10);
			task.store = store;
			taskList.add(task);
		}
		cursor.close();
		return taskList;
	}

	public static ArrayList<TaskObj> loadTaskUpdate(SQLiteAdapter db) {
		ArrayList<TaskObj> taskList = new ArrayList<>();
		Cursor cursor = db.read("SELECT t.ID, t.webTaskID, t.startDate, t.endDate, t.notes, t.empID, " +
				"s.ID, s.webStoreID FROM " + Tables.getName(TB.TASK) + " t LEFT JOIN " +
				Tables.getName(TB.STORES) + " s ON s.ID = t.storeID WHERE t.isWebUpdate = 0 AND " +
				"t.isUpdate = 1 AND t.isSync = 1");
		while(cursor.moveToNext()) {
			TaskObj task = new TaskObj();
			task.ID = cursor.getString(0);
			task.webTaskID = cursor.getString(1);
			task.startDate = cursor.getString(2);
			task.endDate = cursor.getString(3);
			task.notes = cursor.getString(4);
			EmployeeObj emp = new EmployeeObj();
			emp.ID = cursor.getString(5);
			task.emp = emp;
			StoreObj store = new StoreObj();
			store.ID = cursor.getString(6);
			store.webStoreID = cursor.getString(7);
			task.store = store;
			taskList.add(task);
		}
		cursor.close();
		return taskList;
	}

	public static ArrayList<CheckInObj> loadCheckInSync(SQLiteAdapter db) {
		ArrayList<CheckInObj> checkInList = new ArrayList<>();
		Cursor cursor = db.read("SELECT i.ID, i.empID, i.dDate, i.dTime, i.syncBatchID, g.gpsDate, " +
				"g.gpsTime, g.gpsLongitude, g.gpsLatitude, g.isValid, t.ID, t.webTaskID " +
				"FROM " + Tables.getName(TB.CHECK_IN) + " i, " + Tables.getName(TB.GPS) + " g, " +
				Tables.getName(TB.TASK) + " t WHERE i.isSync = 0 AND g.ID = i.gpsID AND t.ID = i.taskID");
		while(cursor.moveToNext()) {
			CheckInObj checkIn = new CheckInObj();
			checkIn.ID = cursor.getString(0);
			EmployeeObj emp = new EmployeeObj();
			emp.ID = cursor.getString(1);
			checkIn.emp = emp;
			checkIn.dDate = cursor.getString(2);
			checkIn.dTime = cursor.getString(3);
			checkIn.syncBatchID = cursor.getString(4);
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(5);
			gps.time = cursor.getString(6);
			gps.longitude = cursor.getDouble(7);
			gps.latitude = cursor.getDouble(8);
			gps.isValid = cursor.getInt(9) == 1;
			checkIn.gps = gps;
			TaskObj task = new TaskObj();
			task.ID = cursor.getString(10);
			task.webTaskID = cursor.getString(11);
			checkIn.task = task;
			checkInList.add(checkIn);
		}
		cursor.close();
		return checkInList;
	}

	public static ArrayList<CheckOutObj> loadCheckOutSync(SQLiteAdapter db) {
		ArrayList<CheckOutObj> checkOutList = new ArrayList<>();
		Cursor cursor = db.read("SELECT o.ID, o.dDate, o.dTime, o.syncBatchID, g.gpsDate, g.gpsTime, " +
				"g.gpsLongitude, g.gpsLatitude, g.isValid, i.ID, i.empID, t.ID, t.webTaskID, " +
				"t.status FROM " + Tables.getName(TB.CHECK_OUT) + " o, " + Tables.getName(TB.GPS) +
				" g, " + Tables.getName(TB.CHECK_IN) + " i, " + Tables.getName(TB.TASK) +
				" t WHERE o.isSync = 0 AND g.ID = o.gpsID AND t.ID = i.taskID AND i.ID = o.checkInID");
		while(cursor.moveToNext()) {
			CheckOutObj out = new CheckOutObj();
			out.ID = cursor.getString(0);
			out.dDate = cursor.getString(1);
			out.dTime = cursor.getString(2);
			out.syncBatchID = cursor.getString(3);
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(4);
			gps.time = cursor.getString(5);
			gps.longitude = cursor.getDouble(6);
			gps.latitude = cursor.getDouble(7);
			gps.isValid = cursor.getInt(8) == 1;
			CheckInObj in = new CheckInObj();
			in.ID = cursor.getString(9);
			EmployeeObj emp = new EmployeeObj();
			emp.ID = cursor.getString(10);
			out.emp = emp;
			TaskObj task = new TaskObj();
			task.ID = cursor.getString(11);
			task.webTaskID = cursor.getString(12);
			task.status = cursor.getString(13);
			in.task = task;
			out.checkIn = in;
			out.gps = gps;
			checkOutList.add(out);
		}
		cursor.close();
		return checkOutList;
	}

	public static ArrayList<PhotoObj> loadTimeInPhotoUpload(SQLiteAdapter db) {
		ArrayList<PhotoObj> photoList = new ArrayList<>();
		Cursor cursor = db.read("SELECT ID, syncBatchID, photo FROM " + Tables.getName(TB.TIME_IN) +
				" WHERE isPhotoUpload = 0 AND photo NOT NULL");
		while(cursor.moveToNext()) {
			PhotoObj photo = new PhotoObj();
			photo.ID = cursor.getString(0);
			photo.syncBatchID = cursor.getString(1);
			photo.fileName = cursor.getString(2);
			photoList.add(photo);
		}
		cursor.close();
		return photoList;
	}

	public static ArrayList<PhotoObj> loadTimeOutPhotoUpload(SQLiteAdapter db) {
		ArrayList<PhotoObj> photoList = new ArrayList<>();
		Cursor cursor = db.read("SELECT ID, syncBatchID, photo FROM " + Tables.getName(TB.TIME_OUT) +
				" WHERE isPhotoUpload = 0 AND photo NOT NULL");
		while(cursor.moveToNext()) {
			PhotoObj photo = new PhotoObj();
			photo.ID = cursor.getString(0);
			photo.syncBatchID = cursor.getString(1);
			photo.fileName = cursor.getString(2);
			photoList.add(photo);
		}
		cursor.close();
		return photoList;
	}

	public static ArrayList<CheckInObj> loadCheckInPhotoUpload(SQLiteAdapter db) {
		ArrayList<CheckInObj> checkInList = new ArrayList<>();
		Cursor cursor = db.read("SELECT i.ID, i.photo, t.ID, t.webTaskID FROM " +
				Tables.getName(TB.CHECK_IN) + " i, " + Tables.getName(TB.TASK) +
				" t WHERE i.isUpload = 0 AND t.ID = i.taskID AND i.photo NOT NULL");
		while(cursor.moveToNext()) {
			CheckInObj checkIn = new CheckInObj();
			checkIn.ID = cursor.getString(0);
			checkIn.photo = cursor.getString(1);
			TaskObj task = new TaskObj();
			task.ID = cursor.getString(2);
			task.webTaskID = cursor.getString(3);
			checkIn.task = task;
			checkInList.add(checkIn);
		}
		cursor.close();
		return checkInList;
	}

	public static ArrayList<CheckOutObj> loadCheckOutPhotoUpload(SQLiteAdapter db) {
		ArrayList<CheckOutObj> checkOutList = new ArrayList<>();
		Cursor cursor = db.read("SELECT o.ID, o.photo, i.ID, t.ID, t.webTaskID FROM " +
				Tables.getName(TB.CHECK_OUT) + " o, " + Tables.getName(TB.TASK) + " t, " +
				Tables.getName(TB.CHECK_IN) + " i WHERE o.isUpload = 0 AND t.ID = i.taskID AND " +
				"o.photo NOT NULL AND i.ID = o.checkInID");
		while(cursor.moveToNext()) {
			CheckOutObj out = new CheckOutObj();
			out.ID = cursor.getString(0);
			out.photo = cursor.getString(1);
			CheckInObj in = new CheckInObj();
			in.ID = cursor.getString(2);
			TaskObj task = new TaskObj();
			task.ID = cursor.getString(3);
			task.webTaskID = cursor.getString(4);
			in.task = task;
			out.checkIn = in;
			checkOutList.add(out);
		}
		cursor.close();
		return checkOutList;
	}

	public static ArrayList<PhotoObj> loadSignatureUpload(SQLiteAdapter db) {
		ArrayList<PhotoObj> photoList = new ArrayList<>();
		Cursor cursor = db.read("SELECT i.ID, i.syncBatchID, o.signature FROM " +
				Tables.getName(TB.TIME_IN) + " i, " + Tables.getName(TB.TIME_OUT) +
				" o WHERE o.isSignatureUpload = 0 AND o.timeInID = i.ID AND o.signature NOT NULL");
		while(cursor.moveToNext()) {
			PhotoObj photo = new PhotoObj();
			photo.ID = cursor.getString(0);
			photo.syncBatchID = cursor.getString(1);
			photo.fileName = cursor.getString(2);
			photoList.add(photo);
		}
		cursor.close();
		return photoList;
	}
}
