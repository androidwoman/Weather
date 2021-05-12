package app.mma.androidweather;


import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastRecyclerViewAdapter.MyViewHolder> {

    private JSONArray weatherData;
    ForecastRecyclerViewAdapter(JSONArray weatherData){
        this.weatherData = weatherData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            JSONObject data = weatherData.getJSONObject(position);
            holder.tv_temp.setText(
                    String.format(Locale.getDefault(), "%.0f %s",
                            data.getDouble("temp"), Html.fromHtml("&#8451;"))
            );
            holder.tv_day.setText(data.getString("day"));
            holder.tv_details.setText(data.getString("main"));
            holder.tv_weather.setWeatherIcon(data.getInt("id"), Long.MIN_VALUE, Long.MAX_VALUE);
            Log.i(WeatherFragment.TAG, "id : " + data.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherData.length();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_day, tv_details, tv_temp;
        TextViewWeather tv_weather;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_details = (TextView) itemView.findViewById(R.id.tv_details);
            tv_temp = (TextView) itemView.findViewById(R.id.tv_temp);
            tv_weather = (TextViewWeather) itemView.findViewById(R.id.tv_weather);
        }
    }
}
