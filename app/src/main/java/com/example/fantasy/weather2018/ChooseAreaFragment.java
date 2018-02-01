package com.example.fantasy.weather2018;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.fantasy.weather2018.db.City;
import com.example.fantasy.weather2018.db.County;
import com.example.fantasy.weather2018.db.Province;
import com.example.fantasy.weather2018.util.HttpUtil;
import com.example.fantasy.weather2018.util.Utility;

import org.litepal.crud.DataSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Fantasy on 2018/1/31.
 * 显示出地点
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE =0;
    public static final int LEVEL_CITY =1;
    public static final int LEVEL_COUNTY =2;

    private ProgressDialog progressDialog;

    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;//选择的省
    private City selectedCity;
    private int currentLevel;
    private static final String TAG = "地点碎片测试";
    @Nullable
    @Override//碎片初始化，适配器初始化
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.choose_area,container,false);
        titleText=view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);

        adapter =new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        ////构建适配器 对象    该子项布局用于显示一段文本
        listView.setAdapter(adapter);
        Log.d(TAG, "碎片初始化  "+Thread.currentThread().getName());
        return view;
    }


    @Override//与碎片有关的视图创建完成时 设置点击事件   position为被点击的具体项
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    //provinceList为返回列表中指定位置的元素对象,其实就是在对数据库进行操作
                     Log.d(TAG, "点击的城市是"+selectedProvince.getProvinceName()+"线程ID为"+ Thread.currentThread().getName() );
                    queryCities();
                }else  if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    Log.d(TAG, "点击的县是"+selectedCity.getCityName()+"线程ID为"+ Thread.currentThread().getName() );
                    queryCounties();

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                } else if (currentLevel ==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();

    }

    //查询省级数据，优先查询数据库
    private void queryProvinces() {
        Log.d(TAG, "查询省数据："+Thread.currentThread().getName());
        titleText.setText("中国 2018");
        backButton.setVisibility(View.GONE);//此级使按钮不可见
        provinceList = DataSupport.findAll(Province.class);//查询所有省数据
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());//把省份的名字传入 dataList
            }
            adapter.notifyDataSetChanged();//更新适配器数据
            listView.setSelection(0);//将列表移动到指定的Position处
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList  = DataSupport.where("provinceId = ? ",String.valueOf(selectedProvince.getId())).find(City.class);//查询数据
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city  : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode=selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address, "city");
        }
    }
    private void queryCounties() {

        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList  = DataSupport.where("cityId =?",String.valueOf(selectedCity.getId())).find(County.class);//查询数据
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county  : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {

            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode =selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address, "county");
            Log.d(TAG, "queryCounties:address "+address);
        }
    }
    //从服务器查询各级数据：
    private void queryFromServer(String address,final String type) {

        showProgressDialog();//从服务器查时显示出等待的对话框
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override//自动转到了子线程处理
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, "从服务器查询数据  线程： "+Thread.currentThread().getName()+"\n数据为"+responseText);
                boolean result =false;
                if("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);//把数据解析并储存至数据库
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if(result){ //通过runOnUiThread()方法回到主线程
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Log.d(TAG, "进度对话框处理   线程： "+Thread.currentThread().getName());
                            if("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }

            }

            @Override
            public void onFailure(final Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"没网络啊，数据库里又没有",Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }



    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载中....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }

    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }


}
