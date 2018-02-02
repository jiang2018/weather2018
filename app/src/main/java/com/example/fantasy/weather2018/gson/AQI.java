package com.example.fantasy.weather2018.gson;

/**
 * Created by Fantasy on 2018/2/1.
 * "aqi":{
 *  "city":{
 *      "aqi":"58","
 *      qlty":"良",
 *      " pm25":"41",
 *      "pm10":"62",
 *       "no2":"18",
 *       "so2":"15",
 *      "co":"0.45",
 *      "o3":"120"}},
 */

public class AQI {

    public AQICiyt city;

    public class AQICiyt {
        public String aqi;
        public String pm25;
    }
}
