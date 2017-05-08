package com.example.zeph.myweater.util;


import android.text.TextUtils;
import com.example.zeph.myweater.db.City;
import com.example.zeph.myweater.db.District;
import com.example.zeph.myweater.db.Province;
import com.example.zeph.myweater.db.Weather;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

  /**
   * 解析省
   */
  public static boolean handleProvinceResponse(String response) {
    if (!TextUtils.isEmpty(response)) {
      try {
        JSONArray allProvince = new JSONArray(response);
        for (int i = 0; i < allProvince.length(); i++) {
          JSONObject provinceObject = allProvince.getJSONObject(i);
          Province province = new Province();
          province.setProvinceCode(provinceObject.getInt("id"));
          province.setProvinceName(provinceObject.getString("name"));
          province.save();
        }
        return true;
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * 解析市
   */
  public static boolean handleCityResponse(String response, int provinceId) {
    if (!TextUtils.isEmpty(response)) {
      try {
        JSONArray allCity = new JSONArray(response);
        for (int i = 0; i < allCity.length(); i++) {
          JSONObject cityObject = allCity.getJSONObject(i);
          City city = new City();
          city.setCityCode(cityObject.getInt("id"));
          city.setCityName(cityObject.getString("name"));
          city.setProvinceId(provinceId);
          city.save();
        }
        return true;
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * 解析区
   */
  public static boolean handleDistrictResponse(String response, int cityId) {
    if (!TextUtils.isEmpty(response)) {
      try {
        JSONArray allDistrict = new JSONArray(response);
        for (int i = 1; i < allDistrict.length(); i++) {
          JSONObject districtObject = allDistrict.getJSONObject(i);
          District district = new District();
          district.setDistrictName(districtObject.getString("name"));
          district.setWeatherId(districtObject.getString("weather_id"));
          district.setCityId(cityId);
          district.save();
        }
        return true;
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * 解析天气
   */
  public static Weather handleWeatherResponse(String response) {
    try {
      JSONObject jsonObject = new JSONObject(response);
      JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
      String weatherContent = jsonArray.getJSONObject(0).toString();
      return new Gson().fromJson(weatherContent, Weather.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;

  }

}
