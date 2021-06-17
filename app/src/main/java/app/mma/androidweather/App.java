package app.mma.androidweather;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import app.mma.androidweather.data.AssetsDatabaseHelper;


public class App extends Application {

    public static final String API_KEY = "d52a8119f8466e5143a5963f74a06bd4";
    public static final String URL_FORMAT_BY_CITY_NAME =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=" + API_KEY;

    public static final String URL_FORMAT_BY_CITY_ID =
            "https://api.openweathermap.org/data/2.5/weather?id=%d&units=metric&appid=" + API_KEY;
    public static final String URL_FORMAT_FORECAST_BY_ID =
            "https://api.openweathermap.org/data/2.5/forecast?id=%d&units=metric&appid=" + API_KEY ;

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
