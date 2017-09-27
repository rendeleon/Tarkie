package com.mobileoptima.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mobileoptima.cache.SQLiteCache;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.RequestCode;
import com.mobileoptima.core.TarkieLib;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class MainService extends Service implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

	private final long UPDATE_INTERVAL = 5000;
	private final long FASTEST_UPDATE_INTERVAL = 1000;
	private final float ACCURACY = 100;

	private final IBinder binder = new LocalBinder();
	private GoogleApiClient googleApiClient;
	private LocationManager locationManager;
	private LocationRequest locationRequest;
	private Location location;
	private LocalBroadcastManager broadcastManager;
	private SQLiteAdapter db;
	private boolean isRunning;
	private long lastLocationUpdate;

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopLocationUpdates();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		stopLocationUpdates();
		if(TarkieLib.isTimeIn(db)) {
			restartService();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if(CodePanUtils.isPermissionGranted(this)) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			broadcastManager = LocalBroadcastManager.getInstance(this);
			db = SQLiteCache.getDatabase(this, App.DB);
			db.openConnection();
			buildGoogleApiClient();
			googleApiClient.connect();
		}
		else {
			stopSelf();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(CodePanUtils.isPermissionGranted(this)) {
			SQLiteAdapter db = getDatabase();
			if(TarkieLib.isTimeIn(db)) {
				setRunning(true);
				requestLocationUpdates();
			}
			else {
				setRunning(false);
			}
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onConnected(Bundle bundle) {
		if(location == null) {
			try {
				location = FusedLocationApi.getLastLocation(googleApiClient);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
		requestLocationUpdates();
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
	}

	@Override
	public void onLocationChanged(Location location) {
		this.lastLocationUpdate = SystemClock.elapsedRealtime();
		this.location = location;
		Log.e("getLongitude", String.valueOf(location.getLongitude()));
		Log.e("getLatitude", String.valueOf(location.getLatitude()));
	}

	public class LocalBinder extends Binder {
		public MainService getService() {
			return MainService.this;
		}
	}

	protected synchronized void buildGoogleApiClient() {
		googleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		createLocationRequest();
	}

	protected void createLocationRequest() {
		locationRequest = new LocationRequest();
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	protected void requestLocationUpdates() {
		if(googleApiClient.isConnected()) {
			try {
				FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	public SQLiteAdapter getDatabase() {
		if(db == null) {
			db = SQLiteCache.getDatabase(this, App.DB);
			db.openConnection();
		}
		return this.db;
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}

	public GpsObj getGps() {
		return CodePanUtils.getGps(this, location, lastLocationUpdate, UPDATE_INTERVAL, ACCURACY);
	}

	protected void stopLocationUpdates() {
		if(googleApiClient.isConnected()){
			FusedLocationApi.removeLocationUpdates(googleApiClient, this);
		}
	}

	public void restartService() {
		Intent intent = new Intent(this, getClass());
		intent.setPackage(getPackageName());
		PendingIntent pi = PendingIntent.getService(this,
				RequestCode.SERVICE, intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long trigger = SystemClock.elapsedRealtime() + 1000;
		alarm.set(AlarmManager.ELAPSED_REALTIME, trigger, pi);
	}
}
