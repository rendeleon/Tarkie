package com.mobileoptima.model;

import java.util.ArrayList;

public class StoreObj extends TransactionObj {

	public String name;
	public String address;
	public String header;
	public String footer;
	public String webStoreID;
	public String shareWith;
	public int radius;
	public double longitude;
	public double latitude;
	public boolean isHeader;
	public boolean isRemoveDivider;
	public boolean isUpdate;
	public boolean isWebUpdate;
	public boolean isFromTask;
	public ArrayList<EmployeeObj> employeeList;
}
