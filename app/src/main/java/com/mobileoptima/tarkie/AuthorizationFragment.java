package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.codepan.callback.Interface;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.constant.Key;

import java.util.HashMap;

import static com.mobileoptima.constant.Function.Action.AUTHORIZE_DEVICE;

public class AuthorizationFragment extends Fragment implements OnRefreshCallback {

	private OnRefreshCallback refreshCallback;
	private CodePanTextField etAuthCodeAuthorize;
	private CodePanButton btnAuthorize;
	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.authorization_layout, container, false);
		etAuthCodeAuthorize = (CodePanTextField) view.findViewById(R.id.etAuthCodeAuthorize);
		btnAuthorize = (CodePanButton) view.findViewById(R.id.btnAuthorize);
		btnAuthorize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoadingFragment loadingFragment = new LoadingFragment();
				loadingFragment.setOnRefreshCallback(AuthorizationFragment.this);
				loadingFragment.setAction(AUTHORIZE_DEVICE);
				HashMap<String, String> map = new HashMap<>();
				map.put(Key.AUTH_CODE, etAuthCodeAuthorize.getText().toString());
				loadingFragment.setMap(map);
				main.addToMain(loadingFragment);
			}
		});
		etAuthCodeAuthorize.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					btnAuthorize.performClick();
				}
				return true;
			}
		});
		return view;
	}

	@Override
	public void onRefresh() {
		manager.popBackStack();
		if(refreshCallback != null) {
			refreshCallback.onRefresh();
		}
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}
}
