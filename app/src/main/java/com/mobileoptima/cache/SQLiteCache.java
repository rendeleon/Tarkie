package com.mobileoptima.cache;

import android.content.Context;
import android.util.Log;

import com.codepan.callback.Interface.OnCreateDatabaseCallback;
import com.codepan.callback.Interface.OnUpgradeDatabaseCallback;
import com.codepan.database.SQLiteAdapter;
import com.mobileoptima.constant.App;
import com.mobileoptima.core.TarkieLib;

import java.util.Hashtable;

public class SQLiteCache {

	private static final Hashtable<String, SQLiteAdapter> CACHE = new Hashtable<String, SQLiteAdapter>();

	private static native String getCipherKey();

	static {
		System.loadLibrary("ndkLib");
	}

	public static SQLiteAdapter getDatabase(Context context, String name) {
		synchronized(CACHE) {
			if(!CACHE.containsKey(name)) {
				String password = getCipherKey();
				Log.e("password", password);
				SQLiteAdapter db = new SQLiteAdapter(context, name, password, App.DB_PWD, App.DB_VERSION);
				db.setOnCreateDatabaseCallback(new OnCreateDatabaseCallback() {
					@Override
					public void onCreateDatabase(SQLiteAdapter db) {
						Log.e("onCreateDatabase", "here");
						TarkieLib.createTables(db);
				}
				});
				db.setOnUpgradeDatabaseCallback(new OnUpgradeDatabaseCallback() {
					@Override
					public void onUpgradeDatabase(SQLiteAdapter db, int o, int n) {
						Log.e("onUpgradeDatabase", "here");
						TarkieLib.createTables(db);
//						TarkieLib.updateTables(db, o, n);
					}
				});
				CACHE.put(name, db);
			}
		}
		return CACHE.get(name);
	}
}
