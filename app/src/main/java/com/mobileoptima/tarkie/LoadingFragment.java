package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.ProgressWheel;
import com.mobileoptima.callback.Interface.OnResultCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.constant.Function;
import com.mobileoptima.constant.Function.Action;
import com.mobileoptima.constant.Key;
import com.mobileoptima.core.Process;
import com.mobileoptima.core.TarkieLib;

import java.util.HashMap;

public class LoadingFragment extends Fragment implements OnResultCallback, OnErrorCallback {

	private OnRefreshCallback refreshCallback;
	private ProgressWheel progressLoadingDialog;
	private CodePanLabel tvCountLoadingDialog;
	private HashMap<String, String> map;
	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private Process process;
	private Action action;
	private String successMsg, failedMsg, error;
	private boolean isDone;
	private int progress, max;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
		process = new Process(this);
		process.setOnErrorCallback(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.loading_dialog_layout, container, false);
		progressLoadingDialog = (ProgressWheel) view.findViewById(R.id.progressLoadingDialog);
		tvCountLoadingDialog = (CodePanLabel) view.findViewById(R.id.tvCountLoadingDialog);
		CodePanLabel tvTitleLoadingDialog = (CodePanLabel) view.findViewById(R.id.tvTitleLoadingDialog);
		String title = null;
		switch(action) {
			case AUTHORIZE_DEVICE:
				setMax(14);
				successMsg = "Authorization successful.";
				failedMsg = "Failed to authorize the device.";
				title = "Authorizing Device";
				process.authorizeDevice(db, CodePanUtils.getDeviceID(db.getContext()), map.get(Key.AUTH_CODE));
				break;
			case LOGIN:
				setMax(10);
				successMsg = "Login successful.";
				failedMsg = "Failed to login.";
				title = "Validating Account";
				process.login(db, map.get(Key.USERNAME), map.get(Key.PASSWORD));
				break;
			case UPDATE_MASTER_FILE:
				setMax(24);
				successMsg = "Update master list successful.";
				failedMsg = "Failed to update master file.";
				title = "Updating Master File";
				process.updateMasterFile(db);
				break;
			case SYNC_DATA:
				setMax(TarkieLib.getCountSyncTotal(db) + 2);
				successMsg = "Sync Data successful.";
				failedMsg = "Failed to sync data.";
				title = "Syncing Data";
				process.syncData(db);
				break;
			case SEND_BACK_UP:
				setMax(5);
				successMsg = "Send back-up successful.";
				failedMsg = "Failed to send back-up.";
				title = "Sending Back-up Data";
				String fileName = TarkieLib.getBackupFileName(db);
				process.sendBackUp(db, fileName);
				break;
		}
		tvTitleLoadingDialog.setText(title);
		return view;
	}

	@Override
	public void onResult(boolean result) {
		if(result) {
			updateProgress();
			if(progress >= max) {
				isDone = true;
				showResult(successMsg);
				process.stop();
			}
		}
		else {
			isDone = true;
			showResult(error != null ? error : failedMsg);
			process.stop();
		}
	}

	@Override
	public void onError(String error, String params, String response, boolean showError) {
		CodePanUtils.setErrorMsg(main, error, params, response, App.BACKUP, App.ERROR_PWD);
		if(showError) {
			this.error = error;
		}
	}

	public void showResult(String message) {
		if(!CodePanUtils.isOnBackStack(main, DialogTag.RESULT)) {
			manager.popBackStack();
			String title = Function.getTitle(action);
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(title);
			alert.setDialogMessage(message);
			alert.setPositiveButton("OK", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					manager.popBackStack();
					if(refreshCallback != null) {
						refreshCallback.onRefresh();
					}
//					if(timeValidationCallback != null && result) {
//						timeValidationCallback.onTimeValidated();
//					}
				}
			});
			main.addFadingToMain(alert, DialogTag.RESULT);
		}
	}

	public void setAction(Action action) {
		this.action = action;
	}

	private void setMax(int max) {
		this.max = max;
		progress = 0;
		setProgress(false, "0");
	}

	private void setProgress(boolean isIncProgress, String percentage) {
		if(!isIncProgress) {
			progressLoadingDialog.setmax(max);
		}
		else {
			progressLoadingDialog.incrementProgress();
			progressLoadingDialog.setText(percentage);
		}
		tvCountLoadingDialog.setText(progress + "/" + max);
	}

	private void updateProgress() {
		progress++;
		setProgress(true, (int) (((float) progress/ (float) max) * 100f) + "%");
	}

	public void setMap(HashMap<String, String> map) {
		this.map = map;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}
}
