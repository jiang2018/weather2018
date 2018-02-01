package com.example.fantasy.weather2018.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Fantasy on 2018/1/31.
 * 发起http请求
 *
 *  OkHttpClient client = new OkHttpClient();
 Request request = new Request.Builder()//设置
 .url("http://www.baidu.com")
 .build();


 //发送数据则：RequestBody r =new FormBody.Builder() .add()....  .buile();
 //Request request = new Request.Builder().uri.....post(r).buile();

 Response response = client.newCall(request).execute();//发送请求并返回数据

 String responseData = response.body().string();
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String add,okhttp3.Callback callback){

        OkHttpClient client =new OkHttpClient();
        Request request = new Request.Builder().url(add).build();
        client.newCall(request).enqueue(callback);


    }



}
