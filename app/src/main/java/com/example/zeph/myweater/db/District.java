package com.example.zeph.myweater.db;


import org.litepal.crud.DataSupport;

public class District extends DataSupport {

  private int id;

  private String weatherId;

  private String districtName;

  private int cityId;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getWeatherId() {
    return weatherId;
  }

  public void setWeatherId(String weatherId) {
    this.weatherId = weatherId;
  }

  public String getDistrictName() {
    return districtName;
  }

  public void setDistrictName(String districtName) {
    this.districtName = districtName;
  }

  public int getCityId() {
    return cityId;
  }

  public void setCityId(int cityId) {
    this.cityId = cityId;
  }
}
