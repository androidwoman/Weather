package app.mma.androidweather;

import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import app.mma.androidweather.data.CityDbHelper;
import app.mma.androidweather.data.CityModel;

public class CitiesActivity extends AppCompatActivity implements AddCityFragment.AddCityInterface{

    RecyclerView recyclerView;
    CityRecyclerViewAdapter adapter;
    CityDbHelper dbhelper;
    List<CityModel> citylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCityFragment fragment = new AddCityFragment();
                fragment.show(getSupportFragmentManager(), "addcity");
            }
        });
        dbhelper = new CityDbHelper(this);
        recyclerView = (RecyclerView) findViewById(R.id.city_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateDisplay();
    }

    private void updateDisplay(){
        citylist = dbhelper.getCities("selected = 1", null);
        adapter = new CityRecyclerViewAdapter(citylist, dbhelper);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void addCity(long id) {
        dbhelper.updateCitySelected(id, true);
        updateDisplay();
    }
}
