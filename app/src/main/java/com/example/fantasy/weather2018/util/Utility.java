package com.example.fantasy.weather2018.util;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.fantasy.weather2018.db.City;
import com.example.fantasy.weather2018.db.County;
import com.example.fantasy.weather2018.db.Province;
import com.example.fantasy.weather2018.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fantasy on 2018/1/31.
 *
 * 解析和处理服务器返回的各级数据
 *
 * JSONArray和JSONObject 把数据解析出来
 *
 *[{"id":1,"name":"北京"},{"id":2,"name":"上海"},{"id":3,"name":"天津"}...]
 *
 * http://guolin.tech/api/china/24
 * {"id":226,"name":"南宁"},{"id":227,"name":"崇左"},
 *
 * http://guolin.tech/api/china/24/230
 *{"id":1663,"name":"桂林","weather_id":"CN101300501"},
 *
 */










public class Utility {

    //将返回的数据解析成实体类
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();

            Log.d("测试","JSONObject处理后值weatherContent : "+weatherContent+"\n 对象值为");
            //其实只是去掉了 {"HeWeather": [

            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (Exception e){
        e.printStackTrace();
    }


        return null;
    }



    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){//字符串为null或者"" 返回true
            try {
                JSONArray allProvinces =new JSONArray(response);
                for (int i =0;i<allProvinces.length();i++){
                    JSONObject provinceObject =allProvinces.getJSONObject(i);

                    //对数据进行存储
                    Province province =new Province();
                    Log.d("测试", "province ="+province);
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                    return true;
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }return false;



    }

    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities =new JSONArray(response);
                for (int i =0;i<allCities.length();i++){
                    JSONObject cityObject =allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();

                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }return false;



    }

    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties =new JSONArray(response);
                Log.d("测值", "handleCountyResponse: response"+response);
                for (int i =0;i<allCounties.length();i++){
                    JSONObject countyObject =allCounties.getJSONObject(i);
                   County county=new County();
                   county.setCountyName(countyObject.getString("name"));
                   county.setWeatherId(countyObject.getString("weather_id"));
                    Log.d("测值", "handleCountyResponse:返回的 "+countyObject.getString("name"));
                    county.setCityId(cityId);
                    county.save();

                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }return false;



    }





}
