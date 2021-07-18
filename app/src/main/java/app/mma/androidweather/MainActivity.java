package app.mma.androidweather;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.matteobattilana.weather.PrecipType;
import com.github.matteobattilana.weather.WeatherView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.mma.androidweather.data.CityDbHelper;
import app.mma.androidweather.data.CityModel;

public class MainActivity extends AppCompatActivity {

    ViewPager viewpager;
    MyPagerAdapter pagerAdapter;
    List<Fragment> weatherFragments;
    JsonObjectRequest request;
    List<CityModel> citylist;
    ProgressBar pb;
//    Handler updateHandler;
//    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewpager = (ViewPager) findViewById(R.id.view_pager);

        WeatherView weatherView = findViewById(R.id.weatherview);
        weatherView.setWeatherData(PrecipType.CLEAR);
        weatherView.setEmissionRate(80);
        weatherView.setFadeOutPercent(6);
        weatherView.setSpeed(3000);
        viewpager.setPageTransformer(true,new VerticalFlipTransformation());
        pb = (ProgressBar) findViewById(R.id.pb);
        init();
//        updateHandler = new Handler();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                loadWeatherData();
//                updateHandler.postDelayed(this, 10 * 60 * 1000);
//            }
//        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        loadWeatherData();
//        updateHandler.post(runnable);
    }



    @Override
    protected void onPause() {
        super.onPause();

//        updateHandler.removeCallbacks(runnable);
    }

    private void init() {
        CityDbHelper dbhelper = new CityDbHelper(this);
        citylist = dbhelper.getCities("selected = 1", null);
        weatherFragments = new ArrayList<>();
    }

    private void loadWeatherData() {
        String url = prepareUrl();
        request = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pb.setVisibility(View.INVISIBLE);
                Log.i("weather", "response : \n" + response);
                weatherFragments.clear();
                try {
                    int cnt = response.getInt("cnt");
                    if(cnt == 0) return;
                    JSONArray jsonlist = response.getJSONArray("list");

                    for(int i = 0; i < jsonlist.length() ; i++){
                        JSONObject res = jsonlist.getJSONObject(i);
                        String cityname = res.getString("name");

                        double temprature = res.getJSONObject("main").getDouble("temp");
                        JSONObject  jsondetails = res.getJSONArray("weather").getJSONObject(0);
                        String details = jsondetails.getString("description");
                        JSONObject sys = res.getJSONObject("sys");
                        long sunrise = sys.getLong("sunrise");
                        long sunset = sys.getLong("sunset");
                        int weatherId = jsondetails.getInt("id");

                        Bundle args = new Bundle();
                        args.putString("cityName", cityname);
                        args.putDouble("temprature", temprature);
                        args.putLong("sunrise", sunrise);
                        args.putLong("sunset", sunset);
                        args.putInt("weatherId", weatherId);
                        args.putString("details", details);
                        args.putLong("cityId", res.getLong("id"));
                        weatherFragments.add(WeatherFragment.newInstance(args));
                    }
                    updateDisplay();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("weather", "error : " + error.getMessage());
                pb.setVisibility(View.INVISIBLE);
            }
        });

        pb.setVisibility(View.VISIBLE);
        App.getRequestQueue().add(request);
    }

    private void updateDisplay() {
        if(weatherFragments == null){
            weatherFragments = new ArrayList<>();
        }
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), weatherFragments);
        viewpager.setAdapter(pagerAdapter);
    }


    private String prepareUrl(){
        StringBuilder sb = new StringBuilder("https://api.openweathermap.org/data/2.5/group?id=");
        if(citylist.size()==0)  sb.append("74477");
        for(int i= 0; i < citylist.size() ; i++){
            sb.append(String.valueOf(citylist.get(i).getId()));
            if(i < citylist.size() - 1){
                sb.append(",");
            }
        }
        sb.append("&units=metric");
        sb.append("&APPID=" + App.API_KEY);
        return sb.toString();
    }



    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }



        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
