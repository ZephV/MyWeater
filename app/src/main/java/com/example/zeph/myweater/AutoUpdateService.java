package com.example.zeph.myweater;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import com.example.zeph.myweater.db.Weather;
import com.example.zeph.myweater.util.HttpUtil;
import com.example.zeph.myweater.util.Utility;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

  public AutoUpdateService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    updateWeather();
    updateBingPic();
    AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
    int anHour = 8 * 60 * 60 * 1000; // 这是8小时毫秒数
    long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
    Intent i = new Intent(this, AutoUpdateService.class);
    PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
    manager.cancel(pi);
    manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    return super.onStartCommand(intent, flags, startId);
  }


  private void updateWeather() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    // 获取天气
    String weatherString = prefs.getString("my_weather", null);
    if (weatherString != null) {
      // 有缓存时直接解析天气
      Weather weather = Utility.handleWeatherResponse(weatherString);
      if (weather != null) {
        String weatherId = weather.basic.weatherId;
        String weatherURL = "http://guolin.tech/api/weather?cityid=" + weatherId
            + "&key=f1f1ad050af84e4199a7d8440e4bd906";
        HttpUtil.sendOkHttpRequest(weatherURL, new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
            e.printStackTrace();
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            String responseText = response.body().string();
            Weather weather = Utility.handleWeatherResponse(responseText);
            if (weather != null && "ok".equals(weather.status)) {
              SharedPreferences.Editor editor = PreferenceManager
                  .getDefaultSharedPreferences(AutoUpdateService.this).edit();
              editor.putString("my_weather", responseText);
              editor.apply();
            }
          }
        });
      }
    }
  }


  private void updateBingPic() {
    String requestBingPic = "http://guolin.tech/api/bing_pic";
    HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        final String bingPic = response.body().string();
        SharedPreferences.Editor editor = PreferenceManager
            .getDefaultSharedPreferences(AutoUpdateService.this).edit();
        editor.putString("bing_pic", bingPic);
        editor.apply();
      }
    });
  }
}
