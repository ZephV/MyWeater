package com.example.zeph.myweater.util;


import android.util.Log;

public class LogUtil {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int EVEROR = 5;
    public static final int NOTING = 6;
    public static  int LEVE = VERBOSE;

    public static void v(String tag, String msg){
        if (LEVE <= VERBOSE){
            Log.v(tag,msg);
        }
    }

    public static void d(String tag, String msg){
        if (LEVE <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg){
        if (LEVE <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg){
        if (LEVE <= WARN){
            Log.w(tag,msg);
        }
    }

    public static void e(String tag, String msg){
        if (LEVE <= EVEROR){
            Log.e(tag,msg);
        }
    }



}
