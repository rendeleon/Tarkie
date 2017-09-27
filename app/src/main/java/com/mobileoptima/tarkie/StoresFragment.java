package com.mobileoptima.tarkie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.adapter.StoresAdapter;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.callback.Interface.OnSelectStoreCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.StoreObj;

import java.util.ArrayList;

public class StoresFragment extends Fragment implements OnClickListener {

	private MainActivity main;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private OnSelectStoreCallback selectStoreCallback;
	private ImageView ivLoadingStores;
	private CodePanTextField esSearchStores;
	private ListView lvStores;
	private ArrayList<StoreObj> storeList;
	private StoresAdapter adapter;
	private Animation anim;
	private String strSearch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		anim = AnimationUtils.loadAnimation(main, R.anim.rotate_clockwise);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.stores_layout, container, false);
		lvStores = (ListView) view.findViewById(R.id.lvStores);
		ivLoadingStores = (ImageView) view.findViewById(R.id.ivLoadingStores);
		esSearchStores = (CodePanTextField) view.findViewById(R.id.esSearchStores);
		view.findViewById(R.id.btnBackStores).setOnClickListener(this);
		lvStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StoreObj obj = storeList.get(position);
				CodePanUtils.hideKeyboard(esSearchStores, main);
				if(selectStoreCallback != null) {
					selectStoreCallback.onSelectStore(obj);
				}
			}
		});
		loadStores(db);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBackStores:
				manager.popBackStack();
				break;
		}
	}

	private void loadStores(final SQLiteAdapter db) {
		final String search = strSearch;
		ivLoadingStores.setAnimation(anim);
		ivLoadingStores.setVisibility(View.VISIBLE);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					storeList = Data.loadStores(db, search);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			ivLoadingStores.clearAnimation();
			ivLoadingStores.setVisibility(View.GONE);
			adapter = new StoresAdapter(main, storeList);
			lvStores.setAdapter(adapter);
			return true;
		}
	});

	public void setOnSelectStoreCallback(OnSelectStoreCallback selectStoreCallback) {
		this.selectStoreCallback = selectStoreCallback;
	}
}
