package com.example.fantasy.weather2018;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fantasy.weather2018.gson.Forecast;
import com.example.fantasy.weather2018.gson.Weather;
import com.example.fantasy.weather2018.service.AutoUpdateService;
import com.example.fantasy.weather2018.util.HttpUtil;
import com.example.fantasy.weather2018.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "测试_主天气";
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;

    public DrawerLayout drawerLayout;
    private Button navButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        //设置5.0以上背景图全屏：
        if(Build.VERSION.SDK_INT >=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }



        //初始化控件
        bingPicImg = findViewById(R.id.bing_pic);

        titleCity =findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText =findViewById(R.id.weather_info_text);

        weatherLayout = findViewById(R.id.weather_layout);//超出屏幕可滑动的布局  ScrollView
        forecastLayout =findViewById(R.id.forecast_layout);
        aqiText =findViewById(R.id.aqi_text);
        pm25Text =findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);

        swipeRefresh = findViewById(R.id.refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置下拉颜色

        //设置导航按钮和滑动显示
        drawerLayout = findViewById(R.id.drawer);
        navButton =findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString =prefs.getString("weather",null);

        //处理背景图：
        String Pic = prefs.getString("bing_pic",null);
        if (Pic != null){
            Glide.with(this).load(Pic).into(bingPicImg);
        }else {
            loadBingPic();
        }

        final String weatherId;

        if(weatherString != null){
            //有缓存时直接解析数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
            weatherId = weather.basic.weatherId;
        }else {
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);//请求数据时先把界面隐藏
            requestWeather(weatherId);
        }
        //下拉监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

    }

    private void loadBingPic() {
        String requestPic ="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic =response.body().string();//必应每日图片地址
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });


    }

    //根据天气id请求天气信息
    public void requestWeather(final String id){
        Log.d(TAG, "requestWeather: id is :"+id);
        String Url ="http://guolin.tech/api/weather?cityid="+id+"&key=d21b01ecf8ee4e07ab849f6aa4ef414e";
        HttpUtil.sendOkHttpRequest(Url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(WeatherActivity.this,"获取天气信息失败啦，缓存里又没有",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//说明刷新结束，并隐藏进度条
                    }

                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();

                final Weather weather =Utility.handleWeatherResponse(responseText);
                Log.d(TAG, "onResponse: data:"+responseText+"\nweather is "+weather);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//回主线程
                        if(weather != null && "ok".equals(weather.status)){
                            //将返回的数据存到了SharedPreferences中
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();



                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败啦 ",Toast.LENGTH_SHORT).show();


                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });


            }
        });
        loadBingPic();

    }

    //天气显示界面
    private void showWeatherInfo(Weather weather) {
        //启动服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

        String cityName =weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];//2018-02-01 22:52
        String degree =weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);

        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);


        forecastLayout.removeAllViews();//removeAllViews()能移除掉子视图


        for (Forecast forecast :weather.forecastList){//动态加载
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text );

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi !=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度："+weather.suggestion.comfort.info;
        String carWash = "洗车指数："+weather.suggestion.carWash.info;
        String sport = "运动建议："+weather.suggestion.sport.info;
        carWashText.setText(carWash);
        comfortText.setText(comfort);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);//显示出界面




    }
}
