package com.mobileoptima.tarkie;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnLoginCallback;
import com.mobileoptima.constant.Key;
import com.mobileoptima.core.TarkieLib;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;

import static com.mobileoptima.constant.Function.Action.LOGIN;

public class LoginFragment extends Fragment implements ImageLoadingListener, OnRefreshCallback {

	private MainActivity main;
	private SQLiteAdapter db;
	private FragmentManager manager;
	private OnRefreshCallback refreshCallback;
	private OnLoginCallback loginCallback;
	private CodePanTextField etUsernameLogin, etPasswordLogin;
	private ImageView ivLogoLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login_layout, container, false);
		ivLogoLogin = (ImageView) view.findViewById(R.id.ivLogoLogin);
		etUsernameLogin = (CodePanTextField) view.findViewById(R.id.etUsernameLogin);
		etPasswordLogin = (CodePanTextField) view.findViewById(R.id.etPasswordLogin);
		final CodePanButton btnLogin = (CodePanButton) view.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = etUsernameLogin.getText().toString().trim();
				String password = etPasswordLogin.getText().toString().trim();
				if(!username.isEmpty() && !password.isEmpty()) {
					if(CodePanUtils.hasInternet(main)) {
						LoadingFragment loadingFragment = new LoadingFragment();
						loadingFragment.setOnRefreshCallback(LoginFragment.this);
						loadingFragment.setAction(LOGIN);
						HashMap<String, String> map = new HashMap<>();
						map.put(Key.USERNAME, username);
						map.put(Key.PASSWORD, password);
						loadingFragment.setMap(map);
						main.addToMain(loadingFragment);
					}
					else {
						CodePanUtils.alertToast(main, "Internet connection required.");
					}
				}
				else {
					CodePanUtils.alertToast(main, "Please input username and password.");
				}
			}
		});
		etPasswordLogin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					btnLogin.performClick();
				}
				return true;
			}
		});
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		String logoUrl = TarkieLib.getCompanyLogo(db);
		if(logoUrl != null && !logoUrl.isEmpty()) {
			CodePanUtils.displayImage(ivLogoLogin, logoUrl, this);
		}
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
		if(bitmap != null) {
			float ratio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
			ivLogoLogin.getLayoutParams().width = (int) ((float) ivLogoLogin.getHeight() * ratio);
		}
	}

	@Override
	public void onLoadingStarted(String imageUri, View view) {
	}

	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
	}

	@Override
	public void onRefresh() {
		manager.popBackStack();
		if(refreshCallback != null) {
			refreshCallback.onRefresh();
		}
		if(loginCallback != null) {
			loginCallback.onLogin();
		}
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	public void setOnLoginCallback(OnLoginCallback loginCallback) {
		this.loginCallback = loginCallback;
	}
}
