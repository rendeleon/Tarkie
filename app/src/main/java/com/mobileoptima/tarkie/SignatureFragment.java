package com.mobileoptima.tarkie;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnSignCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.SignatureView;
import com.mobileoptima.constant.App;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;

public class SignatureFragment extends Fragment implements OnClickListener {

	private OnSignCallback signCallback;
	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private SignatureView svSignature;
	private AttendanceObj attendance;
	private boolean isTimeOut;
	private String signature;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.signature_layout, container, false);
		svSignature = (SignatureView) view.findViewById(R.id.svSignature);
		view.findViewById(R.id.btnBackSignature).setOnClickListener(this);
		view.findViewById(R.id.btnClearSignature).setOnClickListener(this);
		view.findViewById(R.id.btnCancelSignature).setOnClickListener(this);
		view.findViewById(R.id.btnSaveSignature).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBackSignature:
				manager.popBackStack();
				break;
			case R.id.btnClearSignature:
				svSignature.clear();
				break;
			case R.id.btnCancelSignature:
				manager.popBackStack();
				break;
			case R.id.btnSaveSignature:
				if(!svSignature.isEmpty()) {
					String fileName = System.currentTimeMillis() + ".jpg";
					boolean result = svSignature.exportFile(main.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath(),
							fileName, svSignature.getWidth(), svSignature.getHeight());
					if(result) {
						manager.popBackStack();
						if(signCallback != null) {
							signCallback.onSign(fileName);
						}
						if(signature != null) {
							CodePanUtils.deleteFile(main, App.FOLDER, signature);
						}
						if(attendance != null) {
							TimeOutObj out = attendance.timeOut;
							TimeInObj in = out.timeIn;
							if(in != null && !isTimeOut) {
								out.signature = fileName;
								result = TarkieLib.updateSignature(db, out);
								if(result) {
									main.updateSyncCount();
									CodePanUtils.alertToast(main, "Signature updated");
								}
							}
						}
					}
				}
				else {
					CodePanUtils.alertToast(main, "Signature updated");
				}
				break;
		}
	}

	public void setOnSignCallback(OnSignCallback signCallback) {
		this.signCallback = signCallback;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setAttendance(AttendanceObj attendance) {
		this.attendance = attendance;
	}

	public void setTimeOut(boolean timeOut) {
		isTimeOut = timeOut;
	}
}
