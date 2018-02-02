package com.example.fantasy.weather2018.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fantasy on 2018/2/1.
 * "daily_forecast":  该中包含的为数组
 [{
 "astro":{"mr":"19:17","ms":"07:47","sr":"07:22","ss":"18:23"},
 "cond":{"code_d":"103","code_n":"101","txt_d":"晴间多云","txt_n":"多云"},
 "date":"2018-02-01",
 "hum":"46",
 "pcpn":"0.0",
 "pop":"0",
 "pres":"1025",
 "tmp":{"max":"12","min":"5"},
 "uv":"7",
 "vis":"16",
 "wind":{"deg":"16","dir":"东北风","sc":"微风","spd":"3"}},



 {...}，
 {....}],
 *
 */

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;


    public class More {
        @SerializedName("txt_d")
        public String info;
    }

    public class Temperature {

        public String max;
        public String min;
    }
}
