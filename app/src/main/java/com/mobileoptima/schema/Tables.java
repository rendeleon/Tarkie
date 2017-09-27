package com.mobileoptima.schema;

import com.codepan.database.Field;
import com.codepan.database.SQLiteQuery;
import com.codepan.database.SQLiteQuery.DataType;

public class Tables {

	public enum TB {
		API_KEY,
		SYNC_BATCH,
		CREDENTIALS,
		COMPANY,
		EMPLOYEE,
		BREAK,
		STORES,
		CONVENTION,
		GPS,
		TIME_IN,
		TIME_OUT,
		BREAK_IN,
		BREAK_OUT,
		OVERTIME,
		OVERTIME_REASON,
		BREAK_ALERT,
		INCIDENT,
		INCIDENT_REPORT,
		TIME_SECURITY,
		LOCATION,
		PHOTO,
		EXPENSE,
		EXPENSE_DEFAULT,
		EXPENSE_FUEL_CONSUMPTION,
		EXPENSE_REPORT_DETAILS,
		EXPENSE_TYPE,
		EXPENSE_TYPE_CATEGORY,
		FORMS,
		FIELDS,
		CHOICES,
		ENTRIES,
		ANSWERS,
		CUSTOM_FIELD_DATA,
		TASK_FORM,
		TASK_ENTRY,
		TASK_PHOTO,
		EXPENSE_FUEL_PURCHASE,
		EXPENSE_REPORT,
		TASK_INVENTORY,
		TASK,
		CHECK_IN,
		CHECK_OUT,
		SETTINGS,
		SETTINGS_GROUP,
		STORE_CONTACTS,
		SCHEDULE,
		SCHEDULE_TIME,
		TRACKING,
		UPDATE_HISTORY,
		FCM,
		MODULES,
		SKU,
		SKU_UOM,
		SKU_CATEGORY,
		SKU_BRAND,
		SKU_SUB_BRAND,
		SKU_COMPETITORS,
		SKU_TRACKING,
		SKU_AVAILABILITY,
		SKU_AVAILABILITY_DETAILS,
		SKU_AVAILABILITY_PHOTO,
		SKU_PRICE_AUDIT,
		SKU_PRICE_AUDIT_DETAILS,
		SKU_PRICE_AUDIT_COMPETITORS,
		SKU_PRICE_AUDIT_PHOTO,
		SKU_OUT_OF_STOCK,
		SKU_OUT_OF_STOCK_DETAILS,
		SKU_NEAR_OUT_OF_STOCK,
		SKU_NEAR_OUT_OF_STOCK_DETAILS,
		SKU_TRACKING_DETAILS,
		BATCH_CODE_TRACKING,
		BATCH_CODE_TRACKING_DETAILS,
		SKU_STORE
	}

	public static String getName(TB tb) {
		String name = null;
		switch(tb) {
			case API_KEY:
				name = "api_key_tb";
				break;
			case SYNC_BATCH:
				name = "sync_batch_tb";
				break;
			case CREDENTIALS:
				name = "credentials_tb";
				break;
			case COMPANY:
				name = "company_tb";
				break;
			case EMPLOYEE:
				name = "employee_tb";
				break;
			case BREAK:
				name = "break_tb";
				break;
			case STORES:
				name = "stores_tb";
				break;
			case CONVENTION:
				name = "convention_tb";
				break;
			case GPS:
				name = "gps_tb";
				break;
			case TIME_IN:
				name = "time_in_tb";
				break;
			case TIME_OUT:
				name = "time_out_tb";
				break;
			case BREAK_IN:
				name = "break_in_tb";
				break;
			case BREAK_OUT:
				name = "break_out_tb";
				break;
			case OVERTIME:
				name = "overtime_tb";
				break;
			case OVERTIME_REASON:
				name = "overtime_reason_tb";
				break;
			case BREAK_ALERT:
				name = "break_alert_tb";
				break;
			case INCIDENT:
				name = "incident_tb";
				break;
			case INCIDENT_REPORT:
				name = "incident_report_tb";
				break;
			case TIME_SECURITY:
				name = "time_security_tb";
				break;
			case LOCATION:
				name = "location_tb";
				break;
			case PHOTO:
				name = "photo_tb";
				break;
			case EXPENSE:
				name = "expense_tb";
				break;
			case EXPENSE_DEFAULT:
				name = "expense_default_tb";
				break;
			case EXPENSE_FUEL_CONSUMPTION:
				name = "expense_fuel_consumption_tb";
				break;
			case EXPENSE_FUEL_PURCHASE:
				name = "expense_fuel_purchase_tb";
				break;
			case EXPENSE_REPORT:
				name = "expense_report_tb";
				break;
			case EXPENSE_REPORT_DETAILS:
				name = "expense_report_details_tb";
				break;
			case EXPENSE_TYPE:
				name = "expense_type_tb";
				break;
			case EXPENSE_TYPE_CATEGORY:
				name = "expense_type_category_tb";
				break;
			case FORMS:
				name = "forms_tb";
				break;
			case FIELDS:
				name = "fields_tb";
				break;
			case CHOICES:
				name = "choices_tb";
				break;
			case ENTRIES:
				name = "entries_tb";
				break;
			case ANSWERS:
				name = "answers_tb";
				break;
			case CUSTOM_FIELD_DATA:
				name = "custom_field_data_tb";
				break;
			case TASK_ENTRY:
				name = "task_entry_tb";
				break;
			case TASK_FORM:
				name = "task_form_tb";
				break;
			case TASK_PHOTO:
				name = "task_photo_tb";
				break;
			case TASK_INVENTORY:
				name = "task_inventory_tb";
				break;
			case TASK:
				name = "task_tb";
				break;
			case CHECK_IN:
				name = "check_in_tb";
				break;
			case CHECK_OUT:
				name = "check_out_tb";
				break;
			case SETTINGS:
				name = "settings_tb";
				break;
			case SETTINGS_GROUP:
				name = "settings_group_tb";
				break;
			case STORE_CONTACTS:
				name = "store_contacts_tb";
				break;
			case SCHEDULE:
				name = "schedule_tb";
				break;
			case SCHEDULE_TIME:
				name = "schedule_time_tb";
				break;
			case TRACKING:
				name = "tracking_tb";
				break;
			case UPDATE_HISTORY:
				name = "update_history_tb";
				break;
			case FCM:
				name = "fcm_tb";
				break;
			case MODULES:
				name = "modules_tb";
				break;
			case SKU:
				name = "sku_tb";
				break;
			case SKU_UOM:
				name = "sku_uom_tb";
				break;
			case SKU_CATEGORY:
				name = "sku_category_tb";
				break;
			case SKU_BRAND:
				name = "sku_brand_tb";
				break;
			case SKU_SUB_BRAND:
				name = "sku_sub_brand_tb";
				break;
			case SKU_COMPETITORS:
				name = "sku_competitors_tb";
				break;
			case SKU_TRACKING:
				name = "sku_tracking_tb";
				break;
			case SKU_TRACKING_DETAILS:
				name = "sku_tracking_details_tb";
				break;
			case SKU_AVAILABILITY:
				name = "sku_availability_tb";
				break;
			case SKU_AVAILABILITY_DETAILS:
				name = "sku_availability_sku_tb";
				break;
			case SKU_AVAILABILITY_PHOTO:
				name = "sku_availability_photo_tb";
				break;
			case SKU_PRICE_AUDIT:
				name = "sku_price_audit_tb";
				break;
			case SKU_PRICE_AUDIT_DETAILS:
				name = "sku_price_audit_sku_tb";
				break;
			case SKU_PRICE_AUDIT_COMPETITORS:
				name = "sku_price_audit_competitors_tb";
				break;
			case SKU_PRICE_AUDIT_PHOTO:
				name = "sku_price_audit_photo_tb";
				break;
			case SKU_OUT_OF_STOCK:
				name = "sku_out_of_stock_tb";
				break;
			case SKU_OUT_OF_STOCK_DETAILS:
				name = "sku_out_of_stock_sku_tb";
				break;
			case SKU_NEAR_OUT_OF_STOCK:
				name = "sku_near_out_of_stock_tb";
				break;
			case SKU_NEAR_OUT_OF_STOCK_DETAILS:
				name = "sku_near_out_of_stock_sku_tb";
				break;
			case BATCH_CODE_TRACKING:
				name = "batch_code_tracking_tb";
				break;
			case BATCH_CODE_TRACKING_DETAILS:
				name = "batch_code_tracking_details_tb";
				break;
			case SKU_STORE:
				name = "sku_store_tb";
				break;
		}
		return name;
	}

	public static SQLiteQuery create(TB tb) {
		SQLiteQuery query = new SQLiteQuery();
		switch(tb) {
			case API_KEY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("apiKey", DataType.TEXT));
				query.add(new Field("authorizationCode", DataType.TEXT));
				query.add(new Field("deviceID", DataType.TEXT));
				break;
			case SYNC_BATCH:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				break;
			case CREDENTIALS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("isLogOut", 0));
				query.add(new Field("isNewUser", 0));
				break;
			case COMPANY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("code", DataType.TEXT));
				query.add(new Field("address", DataType.TEXT));
				query.add(new Field("email", DataType.TEXT));
				query.add(new Field("mobile", DataType.TEXT));
				query.add(new Field("landline", DataType.TEXT));
				query.add(new Field("logoUrl", DataType.TEXT));
				query.add(new Field("expirationDate", DataType.TEXT));
				break;
			case EMPLOYEE:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("employeeNo", DataType.TEXT));
				query.add(new Field("firstName", DataType.TEXT));
				query.add(new Field("lastName", DataType.TEXT));
				query.add(new Field("email", DataType.TEXT));
				query.add(new Field("mobile", DataType.TEXT));
				query.add(new Field("groupID", DataType.INTEGER));
				query.add(new Field("isApprover", DataType.INTEGER));
				query.add(new Field("isActive", DataType.INTEGER));
				query.add(new Field("imageUrl", DataType.TEXT));
				query.add(new Field("storeID", DataType.INTEGER));
				break;
			case BREAK:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("duration", DataType.INTEGER));
				break;
			case STORES:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("gpsLongitude", DataType.TEXT));
				query.add(new Field("gpsLatitude", DataType.TEXT));
				query.add(new Field("address", DataType.TEXT));
				query.add(new Field("radius", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("shareWith", DataType.TEXT));
				query.add(new Field("webStoreID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("class1ID", DataType.INTEGER));
				query.add(new Field("class2ID", DataType.INTEGER));
				query.add(new Field("class3ID", DataType.INTEGER));
				query.add(new Field("isDefault", 0));
				query.add(new Field("isFromWeb", 0));
				query.add(new Field("isFromTask", 0));
				query.add(new Field("isSync", 0));
				query.add(new Field("isUpdate", 0));
				query.add(new Field("isWebUpdate", 0));
				query.add(new Field("isActive", 1));
				query.add(new Field("isTag", 1));
				break;
			case CONVENTION:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("convention", DataType.TEXT));
				break;
			case GPS:
				query.add(new Field("ID", true));
				query.add(new Field("gpsDate", DataType.TEXT));
				query.add(new Field("gpsTime", DataType.TEXT));
				query.add(new Field("gpsLongitude", DataType.TEXT));
				query.add(new Field("gpsLatitude", DataType.TEXT));
				query.add(new Field("isEnabled", DataType.INTEGER));
				query.add(new Field("withHistory", DataType.INTEGER));
				query.add(new Field("isValid", DataType.INTEGER));
				break;
			case TIME_IN:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("scheduleID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isPhotoUpload", 0));
				query.add(new Field("isTimeOut", 0));
				query.add(new Field("isSync", 0));
				break;
			case TIME_OUT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("signature", DataType.TEXT));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSignatureUpload", 0));
				query.add(new Field("isPhotoUpload", 0));
				query.add(new Field("hasPendingOT", 0));
				query.add(new Field("isSync", 0));
				break;
			case BREAK_IN:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("breakID", DataType.INTEGER));
				query.add(new Field("isBreakOut", 0));
				query.add(new Field("isSync", 0));
				break;
			case BREAK_OUT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("breakInID", DataType.INTEGER));
				query.add(new Field("isSync", 0));
				break;
			case OVERTIME:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("reason", DataType.TEXT));
				query.add(new Field("remarks", DataType.TEXT));
				query.add(new Field("startDate", DataType.TEXT));
				query.add(new Field("startTime", DataType.TEXT));
				query.add(new Field("endDate", DataType.TEXT));
				query.add(new Field("endTime", DataType.TEXT));
				query.add(new Field("duration", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSync", 0));
				break;
			case OVERTIME_REASON:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				break;
			case BREAK_ALERT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("breakInID", DataType.INTEGER));
				query.add(new Field("incidentReportID", DataType.INTEGER));
				break;
			case INCIDENT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				break;
			case INCIDENT_REPORT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("incidentID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("value", DataType.TEXT));
				query.add(new Field("isPending", 0));
				query.add(new Field("isSync", 0));
				break;
			case TIME_SECURITY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("isTimeChanged", 0));
				query.add(new Field("isDateChanged", 0));
				query.add(new Field("isTimeZoneChanged", 0));
				query.add(new Field("isTimeUnknown", 0));
				query.add(new Field("isTimeCheck", 0));
				query.add(new Field("isBootIncomplete", 0));
				query.add(new Field("isValidated", 0));
				query.add(new Field("serverTime", DataType.TEXT));
				query.add(new Field("elapsedTime", DataType.TEXT));
				query.add(new Field("shutDownTime", DataType.TEXT));
				query.add(new Field("updateElapsedTime", DataType.TEXT));
				query.add(new Field("timeZoneID", DataType.TEXT));
				query.add(new Field("bootDelay", DataType.TEXT));
				break;
			case LOCATION:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("longitude", DataType.TEXT));
				query.add(new Field("latitude", DataType.TEXT));
				query.add(new Field("provider", DataType.TEXT));
				query.add(new Field("accuracy", DataType.TEXT));
				break;
			case PHOTO:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("fileName", DataType.TEXT));
				query.add(new Field("webPhotoID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSignature", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isUpload", 0));
				query.add(new Field("isActive", 0));
				break;
			case EXPENSE:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("amount", DataType.TEXT));
				query.add(new Field("origin", DataType.TEXT));
				query.add(new Field("destination", DataType.TEXT));
				query.add(new Field("notes", DataType.TEXT));
				query.add(new Field("typeID", DataType.INTEGER));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isReimbursable", 1));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isUpdate", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isSync", 0));
				query.add(new Field("isWebUpdate", 0));
				query.add(new Field("isWebDelete", 0));
				break;
			case EXPENSE_FUEL_CONSUMPTION:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("expenseID", DataType.INTEGER));
				query.add(new Field("rate", DataType.TEXT));
				query.add(new Field("start", DataType.TEXT));
				query.add(new Field("end", DataType.TEXT));
				query.add(new Field("startPhoto", DataType.TEXT));
				query.add(new Field("endPhoto", DataType.TEXT));
				query.add(new Field("isKilometer", 1));
				query.add(new Field("isStartPhotoUpload", 0));
				query.add(new Field("isStartPhotoDelete", 0));
				query.add(new Field("isStartPhotoThumbnail", 0));
				query.add(new Field("isEndPhotoUpload", 0));
				query.add(new Field("isEndPhotoDelete", 0));
				query.add(new Field("isEndPhotoThumbnail", 0));
				break;
			case EXPENSE_FUEL_PURCHASE:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("expenseID", DataType.INTEGER));
				query.add(new Field("start", DataType.TEXT));
				query.add(new Field("liters", DataType.TEXT));
				query.add(new Field("price", DataType.TEXT));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("startPhoto", DataType.TEXT));
				query.add(new Field("withOR", 1));
				query.add(new Field("isPhotoThumbnail", 0));
				query.add(new Field("isPhotoUpload", 0));
				query.add(new Field("isPhotoDelete", 0));
				query.add(new Field("isStartPhotoThumbnail", 0));
				query.add(new Field("isStartPhotoUpload", 0));
				query.add(new Field("isStartPhotoDelete", 0));
				break;
			case EXPENSE_DEFAULT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("expenseID", DataType.INTEGER));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("withOR", 1));
				query.add(new Field("isPhotoThumbnail", 0));
				query.add(new Field("isPhotoUpload", 0));
				query.add(new Field("isPhotoDelete", 0));
				break;
			case EXPENSE_REPORT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("subject", DataType.TEXT));
				query.add(new Field("message", DataType.TEXT));
				query.add(new Field("signature", DataType.TEXT));
				query.add(new Field("reportNo", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("approverID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSignatureUpload", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isSync", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isWebSubmit", 0));
				query.add(new Field("isWebUpdate", 0));
				break;
			case EXPENSE_REPORT_DETAILS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("reportID", DataType.INTEGER));
				query.add(new Field("isTag", 1));
				break;
			case EXPENSE_TYPE:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("categoryID", DataType.INTEGER));
				query.add(new Field("isRequired", 0));
				query.add(new Field("isActive", 1));
				break;
			case EXPENSE_TYPE_CATEGORY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("isActive", 1));
				break;
			case FORMS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("description", DataType.TEXT));
				query.add(new Field("dateCreated", DataType.TEXT));
				query.add(new Field("timeCreated", DataType.TEXT));
				query.add(new Field("groupID", DataType.INTEGER));
				query.add(new Field("logoUrl", DataType.TEXT));
				query.add(new Field("category", DataType.TEXT));
				query.add(new Field("status", DataType.TEXT));
				query.add(new Field("type", DataType.TEXT));
				query.add(new Field("isActive", 1));
				query.add(new Field("showInVisit", 0));
				break;
			case FIELDS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("description", DataType.TEXT));
				query.add(new Field("type", DataType.TEXT));
				query.add(new Field("formID", DataType.INTEGER));
				query.add(new Field("orderNo", DataType.INTEGER));
				query.add(new Field("isRequired", DataType.INTEGER));
				query.add(new Field("customFieldID", DataType.INTEGER));
				query.add(new Field("isActive", 1));
				break;
			case CHOICES:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("code", DataType.TEXT));
				query.add(new Field("fieldID", DataType.INTEGER));
				break;
			case ENTRIES:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("formID", DataType.INTEGER));
				query.add(new Field("referenceNo", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("webEntryID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("isFromWeb", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isSync", 0));
				break;
			case ANSWERS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("value", DataType.TEXT));
				query.add(new Field("entryID", DataType.INTEGER));
				query.add(new Field("fieldID", DataType.INTEGER));
				query.add(new Field("isUpdate", 0));
				break;
			case CUSTOM_FIELD_DATA:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("value", DataType.TEXT));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("isEditable", DataType.INTEGER));
				query.add(new Field("customFieldID", DataType.INTEGER));
				break;
			case TASK_FORM:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("formID", DataType.INTEGER));
				query.add(new Field("taskID", DataType.INTEGER));
				query.add(new Field("isFromWeb", 0));
				query.add(new Field("isTag", 1));
				break;
			case TASK_ENTRY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("entryID", DataType.INTEGER));
				query.add(new Field("taskFormID", DataType.INTEGER));
				break;
			case TASK_INVENTORY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("taskID", DataType.INTEGER));
				query.add(new Field("inventoryID", DataType.INTEGER));
				query.add(new Field("inventoryType", DataType.INTEGER));
				query.add(new Field("isTag", 1));
				break;
			case TASK_PHOTO:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("photoID", DataType.INTEGER));
				query.add(new Field("taskID", DataType.INTEGER));
				query.add(new Field("isTag", 1));
				break;
			case TASK:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("dateCreated", DataType.TEXT));
				query.add(new Field("timeCreated", DataType.TEXT));
				query.add(new Field("startDate", DataType.TEXT));
				query.add(new Field("endDate", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("webTaskID", DataType.INTEGER));
				query.add(new Field("notesLimit", DataType.INTEGER));
				query.add(new Field("notes", DataType.TEXT));
				query.add(new Field("status", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isFromWeb", 0));
				query.add(new Field("isCheckIn", 0));
				query.add(new Field("isCheckOut", 0));
				query.add(new Field("isUpdate", 0));
				query.add(new Field("isWebUpdate", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isWebDelete", 0));
				break;
			case CHECK_IN:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("taskID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isUpload", 0));
				break;
			case CHECK_OUT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("checkInID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isUpload", 0));
				break;
			case SETTINGS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("code", DataType.TEXT));
				break;
			case SETTINGS_GROUP:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("settingsID", DataType.INTEGER));
				query.add(new Field("groupID", DataType.INTEGER));
				query.add(new Field("value", DataType.TEXT));
				break;
			case STORE_CONTACTS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("designation", DataType.TEXT));
				query.add(new Field("mobile", DataType.TEXT));
				query.add(new Field("landline", DataType.TEXT));
				query.add(new Field("email", DataType.TEXT));
				query.add(new Field("birthday", DataType.TEXT));
				query.add(new Field("remarks", DataType.TEXT));
				query.add(new Field("webContactID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isFromWeb", 0));
				query.add(new Field("isSync", 0));
				query.add(new Field("isUpdate", 0));
				query.add(new Field("isWebUpdate", 0));
				break;
			case SCHEDULE:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("scheduleDate", DataType.TEXT));
				query.add(new Field("timeIn", DataType.TEXT));
				query.add(new Field("timeOut", DataType.TEXT));
				query.add(new Field("isDayOff", DataType.INTEGER));
				query.add(new Field("webScheduleID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("shiftID", DataType.INTEGER));
				query.add(new Field("isFromWeb", 0));
				query.add(new Field("isActive", 1));
				query.add(new Field("isSync", 0));
				break;
			case SCHEDULE_TIME:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("timeIn", DataType.TEXT));
				query.add(new Field("timeOut", DataType.TEXT));
				query.add(new Field("color", DataType.TEXT));
				query.add(new Field("shiftID", DataType.INTEGER));
				query.add(new Field("isActive", 1));
				break;
			case TRACKING:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("isSync", 0));
				break;
			case UPDATE_HISTORY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("typeID", DataType.INTEGER));
				break;
			case FCM:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("token", DataType.TEXT));
				query.add(new Field("senderID", DataType.TEXT));
				query.add(new Field("lastUpdate", DataType.TEXT));
				break;
			case MODULES:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("module", DataType.TEXT));
				query.add(new Field("isEnabled", DataType.INTEGER));
				break;
			case SKU:
				query.add(new Field("ID", true));
				query.add(new Field("code", DataType.TEXT));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("uom1ID", DataType.INTEGER));
				query.add(new Field("uom2ID", DataType.INTEGER));
				query.add(new Field("conversion", DataType.TEXT));
				query.add(new Field("brandID", DataType.INTEGER));
				query.add(new Field("subBrandID", DataType.INTEGER));
				query.add(new Field("categoryID", DataType.INTEGER));
				query.add(new Field("isTop", DataType.INTEGER));
				query.add(new Field("isActive", DataType.INTEGER));
				query.add(new Field("srp", DataType.TEXT));
				query.add(new Field("isAll", 0));
				break;
			case SKU_UOM:
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("description", DataType.TEXT));
				query.add(new Field("assignTo", DataType.TEXT));
				query.add(new Field("isActive", DataType.INTEGER));
				break;
			case SKU_BRAND:
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("isActive", DataType.INTEGER));
				break;
			case SKU_SUB_BRAND:
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("isActive", DataType.INTEGER));
				break;
			case SKU_COMPETITORS:
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("description", DataType.TEXT));
				query.add(new Field("brandID", DataType.INTEGER));
				query.add(new Field("subBrandID", DataType.INTEGER));
				query.add(new Field("categoryID", DataType.INTEGER));
				query.add(new Field("isActive", DataType.INTEGER));
				query.add(new Field("srp", DataType.TEXT));
				break;
			case SKU_CATEGORY:
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("isActive", DataType.INTEGER));
				break;
			case SKU_TRACKING:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("webInventoryID", DataType.INTEGER));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("dateStart", DataType.TEXT));
				query.add(new Field("dateEnd", DataType.TEXT));
				query.add(new Field("referenceNo", DataType.TEXT));
				query.add(new Field("status", DataType.TEXT));
				query.add(new Field("skuCount", DataType.INTEGER));
				query.add(new Field("signature", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isDelete", 0));
				break;
			case SKU_TRACKING_DETAILS:
				query.add(new Field("ID", true));
				query.add(new Field("skuTrackingID", DataType.INTEGER));
				query.add(new Field("skuID", DataType.INTEGER));
				query.add(new Field("uomID", DataType.INTEGER));
				query.add(new Field("delivery", DataType.TEXT));
				query.add(new Field("endingWH", DataType.TEXT));
				query.add(new Field("endingSA", DataType.TEXT));
				break;
			case SKU_AVAILABILITY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("referenceNo", DataType.TEXT));
				query.add(new Field("signature", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("webInventoryID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isSignatureUpload", 0));
				break;
			case SKU_AVAILABILITY_DETAILS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("skuID", DataType.INTEGER));
				query.add(new Field("categoryID", DataType.INTEGER));
				query.add(new Field("availabilityID", DataType.INTEGER));
				query.add(new Field("status", DataType.TEXT));
				query.add(new Field("isTag", 1));
				break;
			case SKU_AVAILABILITY_PHOTO:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("photoID", DataType.INTEGER));
				query.add(new Field("categoryID", DataType.INTEGER));
				query.add(new Field("availabilityID", DataType.INTEGER));
				query.add(new Field("isTag", 1));
				break;
			case SKU_PRICE_AUDIT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("referenceNo", DataType.TEXT));
				query.add(new Field("signature", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("webInventoryID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isSignatureUpload", 0));
				break;
			case SKU_PRICE_AUDIT_DETAILS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("price", DataType.TEXT));
				query.add(new Field("skuID", DataType.INTEGER));
				query.add(new Field("categoryID", DataType.INTEGER));
				query.add(new Field("priceAuditID", DataType.INTEGER));
				break;
			case SKU_PRICE_AUDIT_COMPETITORS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("price", DataType.TEXT));
				query.add(new Field("competitorSKUID", DataType.INTEGER));
				query.add(new Field("categoryID", DataType.INTEGER));
				query.add(new Field("priceAuditID", DataType.INTEGER));
				break;
			case SKU_PRICE_AUDIT_PHOTO:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("photoID", DataType.INTEGER));
				query.add(new Field("categoryID", DataType.INTEGER));
				query.add(new Field("priceAuditID", DataType.INTEGER));
				query.add(new Field("isTag", 1));
				break;
			case SKU_OUT_OF_STOCK:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("referenceNo", DataType.TEXT));
				query.add(new Field("signature", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("webInventoryID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isSignatureUpload", 0));
				break;
			case SKU_OUT_OF_STOCK_DETAILS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("skuID", DataType.INTEGER));
				query.add(new Field("outOfStockID", DataType.INTEGER));
				query.add(new Field("status", DataType.TEXT));
				query.add(new Field("isTag", 1));
				break;
			case SKU_NEAR_OUT_OF_STOCK:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("referenceNo", DataType.TEXT));
				query.add(new Field("signature", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("webInventoryID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isSignatureUpload", 0));
				break;
			case SKU_NEAR_OUT_OF_STOCK_DETAILS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("skuID", DataType.INTEGER));
				query.add(new Field("nearOutOfStockID", DataType.INTEGER));
				query.add(new Field("status", DataType.TEXT));
				query.add(new Field("isTag", 1));
				break;
			case BATCH_CODE_TRACKING:
				query.add(new Field("ID", true));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("webInventoryID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("skuCount", DataType.INTEGER));
				query.add(new Field("referenceNo", DataType.TEXT));
				query.add(new Field("status", DataType.TEXT));
				query.add(new Field("signature", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isDelete", 0));
				break;
			case BATCH_CODE_TRACKING_DETAILS:
				query.add(new Field("ID", true));
				query.add(new Field("batchCodeTrackingID", DataType.INTEGER));
				query.add(new Field("skuID", DataType.INTEGER));
				query.add(new Field("batchCode", DataType.TEXT));
				query.add(new Field("expirationDate", DataType.TEXT));
				query.add(new Field("quantityWH", DataType.TEXT));
				query.add(new Field("quantitySA", DataType.TEXT));
				break;
			case SKU_STORE:
				query.add(new Field("ID", true));
				query.add(new Field("skuID", DataType.INTEGER));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("isTag", 1));
				break;
		}
		return query;
	}
}
