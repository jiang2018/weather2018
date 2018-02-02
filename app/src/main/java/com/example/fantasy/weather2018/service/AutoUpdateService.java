package com.example.fantasy.weather2018.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.fantasy.weather2018.WeatherActivity;
import com.example.fantasy.weather2018.gson.Weather;
import com.example.fantasy.weather2018.util.HttpUtil;
import com.example.fantasy.weather2018.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

public AutoUpdateService(){

}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateBingPic();
        updateWeather();
        //把更新的信息存入缓存即可

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 6*60*60*1000;//六小时
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        //SystemClock.elapsedRealtime()获取系统开机到现在的时间毫秒
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void  updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString =prefs.getString("weather",null);
        if(weatherString != null){
            //有缓存时更新
            Weather weather = Utility.handleWeatherResponse(weatherString);//用之前的数据确定该更新的对象
            String weatherId = weather.basic.weatherId;
            String Url ="http://guolin.tech/api/weather?cityid="+weatherId+"&key=d21b01ecf8ee4e07ab849f6aa4ef414e";
            HttpUtil.sendOkHttpRequest(Url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    final Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();

                    }

                }

            });
    }

    }

    private void updateBingPic(){
        String requestPic ="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic =response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();}


        });

    }



}
