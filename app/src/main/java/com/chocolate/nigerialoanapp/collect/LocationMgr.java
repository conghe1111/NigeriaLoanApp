package com.chocolate.nigerialoanapp.collect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.chocolate.nigerialoanapp.BuildConfig;
import com.chocolate.nigerialoanapp.global.Constant;
import com.chocolate.nigerialoanapp.global.LocalConfig;
import com.chocolate.nigerialoanapp.log.LogSaver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationMgr {

    private static final String TAG = "LocationMgr";

    private static LocationMgr mManager;

    private Context mContext;

    private LocationMgr() {

    }

    public static LocationMgr getInstance() {
        if (mManager == null) {
            synchronized (LocationMgr.class) {
                if (mManager == null) {
                    mManager = new LocationMgr();
                }
            }
        }
        return mManager;
    }

    public void init(Context context) {
        mContext = context;
    }

    private LocationManager locationManager;


    public void getLocation() {
        //1.获取位置管理器
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers == null) {
            ToastUtils.showShort(" no location can use ...");
            return;
        }
        boolean canUseGps = providers.contains(LocationManager.GPS_PROVIDER);
        boolean canUseNetWork = providers.contains(LocationManager.NETWORK_PROVIDER);
        if (canUseGps) {
            getInfoByUseProvider(LocationManager.GPS_PROVIDER, true);
        }
        if (canUseNetWork) {
            getInfoByUseProvider(LocationManager.NETWORK_PROVIDER, false);
        }
        try {
            getWifiResult();
            if (Constant.IS_COLLECT) {
                LogSaver.logToFile(" start get location . " );
            }
        } catch (Exception e) {
            if (Constant.IS_COLLECT) {
                LogSaver.logToFile(" get location = " + e.toString());
            }
            if (BuildConfig.DEBUG) {
                throw e;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getInfoByUseProvider(String locationProvider, boolean isGps) {
        MyLocationListener curListener = isGps ? gpsLocationListener : netWorkLocationListener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //3.获取上次的位置，一般第一次运行，此值为null
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
//                    Toast.makeText(this, location.getLongitude() + " " +
//                            location.getLatitude() + "",Toast.LENGTH_SHORT).show();
                Log.v(TAG, "获取上次的位置-经纬度：" + location.getLongitude() + "   " + location.getLatitude());
                getAddress(location, isGps);
            } else {
                //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, curListener);
            }

        } else {
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                Log.v(TAG, "获取上次的位置-经纬度：" + location.getLongitude() + "   " + location.getLatitude());
                getAddress(location, isGps);
            } else {
                //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                locationManager.requestLocationUpdates(locationProvider, 3000, 1, curListener);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getWifiResult() {
        NetworkUtils.WifiScanResults wifiScanResult = NetworkUtils.getWifiScanResult();
        if (wifiScanResult == null || wifiScanResult.getAllResults() == null) {
            return;
        }
        mBSSIDs.clear();
        for (int i = 0; i < wifiScanResult.getAllResults().size(); i++) {
            ScanResult scanResult = wifiScanResult.getAllResults().get(i);
            if (scanResult == null || TextUtils.isEmpty(scanResult.BSSID)) {
                continue;
            }
            mBSSIDs.add(scanResult.BSSID);
        }
    }

    private MyLocationListener gpsLocationListener = new MyLocationListener(true);

    private MyLocationListener netWorkLocationListener = new MyLocationListener(false);

    private class MyLocationListener implements LocationListener {
        public boolean mIsGps;

        MyLocationListener(boolean isGps) {
            mIsGps = isGps;
        }

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                //如果位置发生变化，重新显示地理位置经纬度
//                Toast.makeText(TestLocationActivity.this, location.getLongitude() + " " +
//                        location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                getAddress(location, mIsGps);
            }
        }
    }

    private double gpsLongitude = 0;
    private double gpsLatitude = 0;
    private double netWorkLongitude = 0;
    private double netWorkLatitude = 0;
    private StringBuffer extra = new StringBuffer();
    private ArrayList<String> mBSSIDs = new ArrayList<>();

    private String gpsStr;

    //获取地址信息:城市、街道等信息
    private void getAddress(Location location, boolean mIsGps) {
        if (location != null) {
            if (mIsGps) {
                gpsLongitude = location.getLongitude();
                gpsLatitude = location.getLatitude();
                Log.v(TAG, " isGps longitude = " + location.getLongitude() + "latitude " + location.getLatitude());
            } else {
                netWorkLongitude = location.getLongitude();
                netWorkLatitude = location.getLatitude();
                Log.v(TAG, " longitude = " + location.getLongitude() + "latitude " + location.getLatitude());
            }

        }
        try {
            Pair<Double, Double> pair = getLocationInfo();
            double longitude = pair.first;
            double latitude = pair.second;
            if (longitude > 0d && latitude > 0d) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        SPUtils.getInstance().put(LocalConfig.LC_LONGITUDE, "" + longitude);
                        SPUtils.getInstance().put(LocalConfig.LC_LATITUDE, "" + latitude);
//                        OkGo.getInstance().addCommonHeaders(
//                                BuildRequestJsonUtils.Companion.buildHeaderLocation(
//                                        longitude + "", latitude + ""));
                        if (BuildConfig.DEBUG) {
                            Log.d("Test", " gps 1 = " + gpsLongitude + " gps 2 = " + gpsLatitude
                                    + "network 1 = " + netWorkLongitude + " network 2 = " + netWorkLatitude);
                        }
                    }
                });
            }
            getLocationAddress(new CallBack() {
                @Override
                public void onSuccess(Address address, String gps) {
                    if (!TextUtils.isEmpty(gps)) {
                        gpsStr = gps;
                    }
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Pair<Double, Double> getLocationInfo() {
        if (!Double.isNaN(gpsLongitude) && !Double.isNaN(gpsLatitude)
                && gpsLongitude > 0 && gpsLatitude > 0) {
            return new Pair<>(gpsLongitude, gpsLatitude);
        }
        if (!Double.isNaN(netWorkLongitude) && !Double.isNaN(netWorkLatitude)
                && netWorkLongitude > 0 && netWorkLatitude > 0) {
            return new Pair<>(netWorkLongitude, netWorkLatitude);
        }
        return new Pair<>(0d, 0d);
    }

    public void cacheGps(){
        getLocationAddress(new CallBack() {
            @Override
            public void onSuccess(Address address, String gps) {
                if (!TextUtils.isEmpty(gps)) {
                    gpsStr = gps;
                    LogSaver.logToFile("cache gps success.");
                    if (BuildConfig.DEBUG) {
                        Log.e("Test", "cache gps success = " + gpsStr);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    public void getLocationAddress(CallBack callBack) {
        Pair<Double, Double> pair = LocationMgr.getInstance().getLocationInfo();
        if ((pair.first == 0) || (pair.second == 0)) {
            String longStr = SPUtils.getInstance().getString(LocalConfig.LC_LONGITUDE, "");
            String latiStr = SPUtils.getInstance().getString(LocalConfig.LC_LATITUDE, "");
            boolean needReturn = true;
            if (!TextUtils.isEmpty(longStr) && !TextUtils.isEmpty(latiStr)) {
                try {
                    Double longL = Double.parseDouble(longStr);
                    Double latiL = Double.parseDouble(latiStr);
                    if (longL != 0d || latiL != 0d){
                        pair = new Pair<>(longL, latiL);
                        needReturn = false;
                    }
                } catch (Exception e) {

                }
            }
            if (needReturn) {
                if (Constant.IS_COLLECT) {
                    LogSaver.logToFile(" not get long lati " );
                }
                callBack.onFailure(new IllegalArgumentException("not get long lati "));
            }
        }
        Pair<Double, Double> finalPair = pair;
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Address>() {
            @Override
            public Address doInBackground() throws Throwable {
                List<Address> list = null;
                try {
                    Geocoder gc =  new Geocoder(Utils.getApp(),  Locale.getDefault());
                    list = gc.getFromLocation(finalPair.second, finalPair.first, 1);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.e("Test", "GPS get = ", e);
                    }
                    LogSaver.logToFile(" get gps failure = " + e.toString());

                }
                if (list != null && !list.isEmpty()) {
                    return list.get(0);
                }
                return null;
            }

            @Override
            public void onSuccess(Address address) {
                if (address != null) {
                    String addressJson = JSON.toJSONString(address);
                    if (Constant.IS_COLLECT) {
                        Log.e("Test", " get gps success = " + addressJson);
                        LogSaver.logToFile(" get gps success = " + addressJson);
                    }
                    SPUtils.getInstance().put(LocalConfig.LC_LOCATION_ADDRESS, addressJson);
                    callBack.onSuccess(address, addressJson);
                } else {
                    callBack.onFailure(new IllegalArgumentException("get address null"));
                }
            }
        });
    }

    public void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(netWorkLocationListener);
            locationManager.removeUpdates(gpsLocationListener);
        }
    }

    interface CallBack {
        void onSuccess(Address address, String json);

        void onFailure(Exception e);
    }
}
