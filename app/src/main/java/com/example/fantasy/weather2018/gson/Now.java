package com.example.fantasy.weather2018.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fantasy on 2018/2/1.
 * "now":{
 * "cond":{"code":"100","txt":"晴"},
 * "fl":"8",
 * "hum":"32",
 * "pcpn":"0.0",
 * "pres":"1021",
 * "tmp":"10",
 * "vis":"8",
 * "wind":{"deg":"359","dir":"北风","sc":"微风","spd":"5"}},
 *
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;


    public class More {
        @SerializedName("txt")
        public String info;
    }
}
