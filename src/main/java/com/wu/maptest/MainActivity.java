package com.wu.maptest;

import android.app.Activity;
import android.location.Location;
import android.location.LocationProvider;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;

public class MainActivity extends Activity {
    private LocationManagerProxy locationManagerProxy;
    private TextView adressview;
    private TextView wheatherview;
    private MapView mapView;
    private AMap amap;
    private LocationSource.OnLocationChangedListener mlistener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adressview = (TextView) findViewById(R.id.adress);
        wheatherview = (TextView) findViewById(R.id.weather);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        amap =mapView.getMap();

//        amap.set
        //初始化
        locationManagerProxy =LocationManagerProxy.getInstance(this);

        initWeather();
        setMapUp();
    }

    public void setMapUp(){
        amap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mlistener =onLocationChangedListener;
                initMap();
            }

            @Override
            public void deactivate() {

            }
        });
        amap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        amap.setMyLocationEnabled(true); //是否触发定位。
//        amap.setMY
    }

    /**
     * 初始化天气
     */
    public void initWeather(){
        locationManagerProxy.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, new AMapLocalWeatherListener() {
            @Override
            public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
                Log.i("weather", aMapLocalWeatherLive.getWeather());
                wheatherview.setText(aMapLocalWeatherLive.getWeather()+"\n市："+aMapLocalWeatherLive.getCity()
                        +"\n风向："+aMapLocalWeatherLive.getWindDir()+
                        "\n风力："+aMapLocalWeatherLive.getWindPower()+"\n湿度："
                        +aMapLocalWeatherLive.getHumidity());
            }

            @Override
            public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 初始化地图控件。
     */
    public  void initMap(){

        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法
        //其中如果间隔时间为-1，则定位只定一次
        locationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        if (aMapLocation!=null&&aMapLocation.getAMapException().getErrorCode() == 0){
                            Log.i("city",aMapLocation.getCity());
                            Toast.makeText(MainActivity.this, aMapLocation.getCity(),Toast.LENGTH_SHORT).show();
                            adressview.setText(aMapLocation.getAddress());
                            Log.i("adress", aMapLocation.getAddress());
                            Log.i("Latitude",String.valueOf(aMapLocation.getLatitude()));
                            Log.i("Longitude",String.valueOf(aMapLocation.getLongitude()));
                            mlistener.onLocationChanged(aMapLocation); //将定位数据返回到回调函数。
                        }else{
                            Toast.makeText(MainActivity.this,"定位失败！",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
        locationManagerProxy.setGpsEnable(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
