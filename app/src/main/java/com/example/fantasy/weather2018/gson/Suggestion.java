package com.example.fantasy.weather2018.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fantasy on 2018/2/1.
 * "suggestion":{"air":
 {"brf":"良","txt":"气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。"},
 "comf":{"brf":"较舒适","txt":"今天夜间天气晴好，会感觉偏凉，舒适、宜人。"},
 "cw":{"brf":"较适宜","txt":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"},
 "drsg":{"brf":"较冷","txt":"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。"},
 "flu":{"brf":"较易发","txt":"昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。"},
 "sport":{"brf":"较适宜","txt":"阴天，较适宜进行各种户内外运动。"},
 "trav":{"brf":"适宜","txt":"天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。"},
 "uv":{"brf":"最弱","txt":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"}
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    private class Comfort {
        @SerializedName("txt")
        public String info;
    }

    private class CarWash {
        @SerializedName("txt")
        public Comfort info;
    }

    private class Sport {
        @SerializedName("txt")
        public Comfort info;
    }
}
