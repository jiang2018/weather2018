package com.example.fantasy.weather2018.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fantasy on 2018/2/1.
 *
 * aqi
 *basic
 * daily_forecast
 *now
 *status
 *suggestion
 *
 * "basic":{"
 * city":"桂林","cnty":"中国",
 * "id":"CN101300501","
 * lat":"25.2742157",
 * "lon":"110.29911804",
 * "update":{"loc":"2018-02-01 18:07","utc":"2018-02-01 10:07"}},
 *
 *  */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")//注解 JSON字段与java字段建立映射
    public String weatherId;

    public Updare update;


    private class Updare {
        @SerializedName("loc")
        public String updateTime;
    }
}
