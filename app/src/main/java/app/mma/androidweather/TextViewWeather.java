package app.mma.androidweather;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Date;


public class TextViewWeather extends androidx.appcompat.widget.AppCompatTextView {


    public TextViewWeather(Context context) {
        super(context);
        init(context);
    }

    public TextViewWeather(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextViewWeather(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weathericons.ttf");
        setTypeface(weatherFont);
    }


    public void setWeatherIcon(int id, long sunrise, long sunset){
        String weather_icon_code = "";
        if(id == 800){ // clear
            long currentTime = new Date().getTime();
            if(currentTime >= sunrise && currentTime < sunset){
                weather_icon_code = getResources().getString(R.string.weather_sunny);
            } else {
                weather_icon_code = getResources().getString(R.string.weather_clear_night);
            }
        } else {
            if(id > 100){
                id = id / 100;
            }
            switch (id){
                case 2 :
                    weather_icon_code = getResources().getString(R.string.weather_thunder);
                    break;
                case 3 :
                    weather_icon_code = getResources().getString(R.string.weather_drizzle);
                    break;
                case 5 :
                    weather_icon_code = getResources().getString(R.string.weather_rainy);
                    break;
                case 6 :
                    weather_icon_code = getResources().getString(R.string.weather_snowy);
                    break;
                case 7 :
                    weather_icon_code = getResources().getString(R.string.weather_foggy);
                    break;
                case 8 :
                    weather_icon_code = getResources().getString(R.string.weather_cloudy);
                    break;
                default: break;
            }
        }
        setText(Html.fromHtml(weather_icon_code));
    }

}
