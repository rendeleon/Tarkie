package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnSelectStoreCallback;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.StoreObj;

public class SelectStoreFragment extends Fragment implements OnClickListener, OnSelectStoreCallback {

	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private OnSelectStoreCallback selectStoreCallback;
	private CodePanButton btnNameSelectStore;
	private CodePanLabel tvTitleSelectStore;
	private StoreObj store;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.select_store_layout, container, false);
		btnNameSelectStore = (CodePanButton) view.findViewById(R.id.btnNameSelectStore);
		tvTitleSelectStore = (CodePanLabel) view.findViewById(R.id.tvTitleSelectStore);
		view.findViewById(R.id.btnCancelSelectStore).setOnClickListener(this);
		view.findViewById(R.id.btnOKSelectStore).setOnClickListener(this);
		btnNameSelectStore.setOnClickListener(this);
		StoreObj store = TarkieLib.getDefaulStore(db);
		if(store != null) {
			this.store = store;
			btnNameSelectStore.setText(store.name);
		}
		String strStore = main.getConvention(Convention.STORES);
		if(strStore != null && !strStore.isEmpty()) {
			tvTitleSelectStore.setText("SELECT " + strStore);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnNameSelectStore:
				StoresFragment stores = new StoresFragment();
				stores.setOnSelectStoreCallback(this);
				main.addSlidingToMain(stores, this);
				break;
			case R.id.btnOKSelectStore:
				if(store != null) {
					manager.popBackStack();
					if(TarkieLib.setDefaultStore(db, store.ID) && selectStoreCallback != null){
						selectStoreCallback.onSelectStore(store);
					}
				}
				else {
					CodePanUtils.alertToast(main, "Please select a store.");
				}
				break;
			case R.id.btnCancelSelectStore:
				manager.popBackStack();
				break;
		}
	}

	@Override
	public void onSelectStore(StoreObj store) {
		manager.popBackStack();
		this.store = store;
		btnNameSelectStore.setText(store.name);
	}

	public void setOnSelectStoreCallback(OnSelectStoreCallback selectStoreCallback) {
		this.selectStoreCallback = selectStoreCallback;
	}
}
