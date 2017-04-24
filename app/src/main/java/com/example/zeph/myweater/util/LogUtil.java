package com.example.zeph.myweater.util;


import android.util.Log;

public class LogUtil {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int EVEROR = 5;
    public static final int NOTING = 6;
    public static  int leve = VERBOSE;

    public static void v(String tag, String msg){
        if (leve <= VERBOSE){
            Log.v(tag,msg);
        }
    }

    public static void d(String tag, String msg){
        if (leve <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg){
        if (leve <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg){
        if (leve <= WARN){
            Log.w(tag,msg);
        }
    }

    public static void e(String tag, String msg){
        if (leve <= EVEROR){
            Log.e(tag,msg);
        }
    }



}
