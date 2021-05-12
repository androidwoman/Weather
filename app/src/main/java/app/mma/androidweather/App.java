package app.mma.androidweather;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import app.mma.androidweather.data.AssetsDatabaseHelper;


public class App extends Application {

    public static final String API_KEY = "0a30557084e56ed7d32845a78c6ed641";
    public static final String URL_FORMAT_BY_CITY_NAME =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&APPID=" + API_KEY;

    public static final String URL_FORMAT_BY_CITY_ID =
            "http://api.openweathermap.org/data/2.5/weather?id=%d&units=metric&APPID=" + API_KEY;
    public static final String URL_FORMAT_FORECAST_BY_ID =
            "http://api.openweathermap.org/data/2.5/forecast?id=%d&units=metric&APPID=" + API_KEY ;

    private static RequestQueue requestQueue;
    private static App appInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        new AssetsDatabaseHelper(getApplicationContext()).checkDb();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static synchronized RequestQueue getRequestQueue(){
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(appInstance.getApplicationContext());
        return requestQueue;
    }
}
