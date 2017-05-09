package com.example.zeph.myweater;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.zeph.myweater.db.City;
import com.example.zeph.myweater.db.District;
import com.example.zeph.myweater.db.Province;
import com.example.zeph.myweater.util.HttpUtil;
import com.example.zeph.myweater.util.Utility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.litepal.crud.DataSupport;

public class ChooseAreaFragment extends Fragment {


  public static final int LEVEL_PROVINCE = 0;
  public static final int LEVEL_CITY = 1;
  public static final int LEVEL_DISTRICT = 2;

  private ListView listView;
  private Button backButton;
  private TextView tv_title;
  private ArrayAdapter<String> adapter;
  private List<String> dataList = new ArrayList<>();
  private List<Province> provinceList; // 省列表
  private List<City> cityList; // 市列表
  private List<District> districtList;  // 区列表
  private Province selectedProvince; // 选中省份
  private City selectedCity; // 选中城市
  private int currentLevel; // 当前选中等级
  private ProgressDialog progressDialog;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.choose_area, container, false);
    listView = (ListView) view.findViewById(R.id.area_list_view);
    backButton = (Button) view.findViewById(R.id.tbn_back);
    tv_title = (TextView) view.findViewById(R.id.txt_title);
    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
    listView.setAdapter(adapter);
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentLevel == LEVEL_PROVINCE) {
          selectedProvince = provinceList.get(position);
          queryCities();
        } else if (currentLevel == LEVEL_CITY) {
          selectedCity = cityList.get(position);
          queryDistrict();
        } else if (currentLevel == LEVEL_DISTRICT) {
          String weatherId = districtList.get(position).getWeatherId();
          if (getActivity() instanceof MainActivity) {
            Intent intent = new Intent(getActivity(), WeatherActivity.class);
            intent.putExtra("weather_id", weatherId);
            startActivity(intent);
            getActivity().finish();
          } else if (getActivity() instanceof WeatherActivity) {
            WeatherActivity activity = (WeatherActivity) getActivity();
            activity.drawerLayout.closeDrawers();
            activity.swipeRefresh.setRefreshing(true);
            activity.requestWeather(weatherId);
          }
        }
      }

    });
    backButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (currentLevel == LEVEL_CITY) {
          queryProvinces();
        } else if (currentLevel == LEVEL_DISTRICT) {
          queryCities();
        }
      }
    });
    queryProvinces();
  }

  private void queryProvinces() {
    tv_title.setText("中国");
    backButton.setVisibility(View.GONE);
    provinceList = DataSupport.findAll(Province.class);
    if (provinceList.size() > 0) {
      dataList.clear();
      for (Province province : provinceList) {
        dataList.add(province.getProvinceName());
      }
      adapter.notifyDataSetChanged();
      listView.setSelection(0);
      currentLevel = LEVEL_PROVINCE;
    } else {
      String address = "http://guolin.tech/api/china";
      queryFromServer(address, "province");
    }
  }

  private void queryCities() {
    tv_title.setText(selectedProvince.getProvinceName());
    backButton.setVisibility(View.VISIBLE);
    cityList = DataSupport.where("provinceid = ?",
        String.valueOf(selectedProvince.getId())).find(City.class);
    if (cityList.size() > 0) {
      dataList.clear();
      for (City city : cityList) {
        dataList.add(city.getCityName());
      }
      adapter.notifyDataSetChanged();
      listView.setSelection(0);
      currentLevel = LEVEL_CITY;
    } else {
      int provinceCode = selectedProvince.getProvinceCode();
      String address = "http://guolin.tech/api/china/" + provinceCode;
      queryFromServer(address, "city");
    }
  }

  private void queryDistrict() {
    tv_title.setText(selectedCity.getCityName());
    backButton.setVisibility(View.VISIBLE);
    districtList = DataSupport.where("cityid = ?",
        String.valueOf(selectedCity.getId())).find(District.class);
    if (districtList.size() > 0) {
      dataList.clear();
      for (District district : districtList) {
        dataList.add(district.getDistrictName());
      }
      adapter.notifyDataSetChanged();
      listView.setSelection(0);
      currentLevel = LEVEL_DISTRICT;
    } else {
      int provinceCode = selectedProvince.getProvinceCode();
      int cityCode = selectedCity.getCityCode();
      String address = "http://guolin.tech/api/china/"
          + provinceCode + "/" + cityCode;
      queryFromServer(address, "district");
    }
  }

  private void queryFromServer(String address, final String type) {
    HttpUtil.sendOkHttpRequest(address, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            closeProgressDialog();
            Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
          }
        });
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        String responseText = response.body().string();
        boolean result = false;
        if ("province".equals(type)) {
          result = Utility.handleProvinceResponse(responseText);
        } else if ("city".equals(type)) {
          result = Utility.handleCityResponse(responseText, selectedProvince.getId());
        } else if ("district".equals(type)) {
          result = Utility.handleDistrictResponse(responseText, selectedCity.getId());
        }
        if (result) {
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              closeProgressDialog();
              if ("province".equals(type)) {
                queryProvinces();
                closeProgressDialog();
              } else if ("city".equals(type)) {
                queryCities();
                closeProgressDialog();
              } else if ("district".equals(type)) {
                queryDistrict();
                closeProgressDialog();
              }
            }
          });
        }
      }
    });

  }

  private void showProgressDialog() {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setMessage("正在加载...");
      progressDialog.setCanceledOnTouchOutside(false);
    }
    progressDialog.show();
  }

  private void closeProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }
}
