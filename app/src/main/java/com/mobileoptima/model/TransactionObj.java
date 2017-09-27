package com.mobileoptima.model;

import com.codepan.model.GpsObj;
public class TransactionObj {

	public String ID;
	public GpsObj gps;
	public String dDate;
	public String dTime;
	public EmployeeObj emp;
	public String syncBatchID;
	public boolean isSync;
	public boolean isFromWeb;
	public int batteryLevel;
}
