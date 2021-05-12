package app.mma.androidweather;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.mma.androidweather.data.CityDbHelper;
import app.mma.androidweather.data.CityModel;


public class AddCityFragment extends DialogFragment {



    private RecyclerView recyclerView;
    private SearchView searchView;
    private MyAdapter adapter;
    private AddCityInterface iactivity;
    CityDbHelper dbHelper;
    List<CityModel> citylist;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_city, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbHelper = new CityDbHelper(getContext());
        searchView = (SearchView) view.findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                citylist = dbHelper.searchCityByName(query, "20");
                Log.i(AddCityFragment.class.getSimpleName(),
                        "citylist : " + citylist.size() + " items");
                updateDisplay();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        updateDisplay();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        changeDialogSize();
    }

    private void updateDisplay(){
        if(citylist == null){
            citylist = new ArrayList<>();
        }
        adapter = new MyAdapter(citylist);
        recyclerView.setAdapter(adapter);
    }





    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<CityModel> citylist;

        public MyAdapter(List<CityModel> citylist){
            this.citylist = citylist;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.city_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final CityModel cityModel = citylist.get(position);
            holder.tv_city_name.setText(cityModel.toString());
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iactivity.addCity(cityModel.getId());
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return citylist.size();
        }
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        Button btn;
        TextView tv_city_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.btn);
            btn.setText("Add");
            btn.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.add_color));
            btn.setBackgroundResource(R.drawable.city_add_btn_bg);
            tv_city_name = (TextView) itemView.findViewById(R.id.city_name);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.iactivity = (AddCityInterface) activity;
    }

    static interface AddCityInterface{
        void addCity(long id);
    }

    private void changeDialogSize(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Window window = getDialog().getWindow();
        if(window != null){
            window.setLayout(
                    (int) (metrics.widthPixels * 0.9) ,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

}
