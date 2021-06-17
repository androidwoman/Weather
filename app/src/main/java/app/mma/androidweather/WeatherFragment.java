package app.mma.androidweather;

import android.content.Intent;
import android.os.Bundle;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class WeatherFragment extends Fragment {
    public static final String TAG = WeatherFragment.class.getSimpleName();


    TextView tv_city, tv_temp, tv_details;
    TextViewWeather tv_weatherIcon;
    RecyclerView rv_forecast;
    private double temprature;
    private String cityName;
    private String details;
    private long sunrise;
    private long sunset;
    private int weatherId;
    private long cityId;
    private ImageButton img_btn;


    private boolean forecastLoaded = false;
    JSONArray forecastData = new JSONArray();
    public static WeatherFragment newInstance(Bundle args){
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        cityName = args.getString("cityName");
        temprature = args.getDouble("temprature");
        sunrise = args.getLong("sunrise");
        sunset = args.getLong("sunset");
        weatherId = args.getInt("weatherId");
        details = args.getString("details");
        cityId = args.getLong("cityId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_card, container, false);
        tv_city = (TextView) view.findViewById(R.id.city);
        tv_details = (TextView) view.findViewById(R.id.details);
        tv_temp = (TextView) view.findViewById(R.id.temp);
        tv_weatherIcon = (TextViewWeather) view.findViewById(R.id.weather_icon);
        rv_forecast = (RecyclerView) view.findViewById(R.id.forecast_rv);
        img_btn=view.findViewById(R.id.img_btn);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CitiesActivity.class));

            }
        });

        fill();
        return view;
    }

    private void fill() {
        tv_city.setText(cityName);
        tv_details.setText(details);
        tv_temp.setText(String.format(Locale.getDefault(), "%.0f %s",temprature, Html.fromHtml("&#8451;")));
        tv_weatherIcon.setWeatherIcon(weatherId, sunrise, sunset);
        if(!forecastLoaded){
            requestForecastData();
        }
        updateDisplay();
    }

    private void requestForecastData(){
        String url = String.format(Locale.getDefault(), App.URL_FORMAT_FORECAST_BY_ID, cityId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("weather", "response \n" + response);
                        try{
                            if(response.getString("cod").equals("200")){
                                JSONArray jsonlist = response.getJSONArray("list");
                                handleForecastData(jsonlist);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("weather", "error : " + error.getMessage() );
                    }
                });

            App.getRequestQueue().add(request);
    }

    private void handleForecastData(JSONArray jsonlist) throws JSONException{
        WeatherPack[] wps = new WeatherPack[3];
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int offset = c.get(Calendar.HOUR_OF_DAY); // 0 - 23
        offset = (offset < 12) ? 0 : 1;

        for(int i= 0; i < 8 * wps.length; i++){
            if(wps[i/8] == null){
                wps[i/8] = new WeatherPack();
            }
            JSONObject json = jsonlist.getJSONObject(i);
            double temp = json.getJSONObject("main").getDouble("temp");
            JSONObject wObj = json.getJSONArray("weather").getJSONObject(0);
            int id = wObj.getInt("id");
            String main = wObj.getString("main");
            wps[i/8].ids[i % 8] = (id == 800) ? 800 : (id/100);
            wps[i/8].temps[i%8] = temp;
            wps[i/8].mains[i%8] = main;
        }

        forecastData = new JSONArray();
        for(int i = 0; i < wps.length; i++){
//            Log.i(TAG, "wps[" + i +"].ids = " + Arrays.toString(wps[i].ids));
//            Log.i(TAG, "wps[" + i +"].temps = " + Arrays.toString(wps[i].temps));
//            Log.i(TAG, "wps[" + i +"].mains = " + Arrays.toString(wps[i].mains));

            int dayNumber = (dayOfWeek + offset + i) % 7;
            if(dayNumber == 0)
                dayNumber = 7;
//            wps[i].dayNumber = dayNumber;

            JSONObject obj = new JSONObject();
            obj.put("temp", wps[i].getMeanTemp());
            obj.put("id", wps[i].getWeatherId());
            obj.put("main", wps[i].getMain());
            obj.put("day", WeatherPack.DAYS[dayNumber - 1]);
            forecastData.put(obj);
        }

        updateDisplay();
    }

    private void updateDisplay() {
        ForecastRecyclerViewAdapter adapter = new ForecastRecyclerViewAdapter(forecastData);
        rv_forecast.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_forecast.setItemAnimator(new DefaultItemAnimator());
        rv_forecast.requestLayout();

        rv_forecast.setAdapter(adapter);
        forecastLoaded = true;
    }


    static class WeatherPack{
        int[] ids = new int[8];
        double[] temps = new double[8];
        String[] mains = new String[8];
        public static final String[] DAYS =
                {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        private int modeIndex = -1;
        public int dayNumber = 0;

        public double getMeanTemp(){
            double sum = 0.0;
            for(double x : temps){
                sum += x;
            }
            return sum / (temps.length);
        }
        public int getWeatherId(){
            HashMap<Integer, Integer> hm = new HashMap<>();
            int max = 0;
            modeIndex = 0;
            for(int i = 0 ; i < ids.length ; i++){
                int count = 0;
                if(hm.containsKey(ids[i])){
                    count = hm.get(ids[i]) + 1;
                    hm.put(ids[i], count);
                } else {
                    count = 1;
                    hm.put(ids[i], 1);
                }

                if(count > max){
                    max = count;
                    modeIndex = i;
                }
            }

            return ids[modeIndex];
        }

        public String getMain(){
            if(modeIndex != -1){
                return mains[modeIndex];
            } else {
                return "";
            }
        }

    }


}
