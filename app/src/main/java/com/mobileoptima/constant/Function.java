package com.mobileoptima.constant;

public class Function {

	public enum Action {
		AUTHORIZE_DEVICE,
		LOGIN,
		UPDATE_MASTER_FILE,
		SYNC_DATA,
		SEND_BACK_UP
	}

	public static String getTitle(Action action) {
		String title = null;
		switch(action) {
			case AUTHORIZE_DEVICE:
				title = "Authorization";
				break;
			case LOGIN:
				title = "Login";
				break;
			case UPDATE_MASTER_FILE:
				title = "Update Master File";
				break;
			case SYNC_DATA:
				title = "Sync Data";
				break;
			case SEND_BACK_UP:
				title = "Send Back-up";
				break;
		}
		return title;
	}
}
