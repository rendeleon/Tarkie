package com.mobileoptima.model;

import java.util.ArrayList;

public class OvertimeObj extends TransactionObj {

	public TimeInObj timeIn;
	public String startDate;
	public String startTime;
	public String endDate;
	public String endTime;
	public String remarks;
	public String hours;
	public float duration;
	public ArrayList<OvertimeReasonObj> reasonList;
}
