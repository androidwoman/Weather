package app.mma.androidweather.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.mma.androidweather.R;


public class CityDbHelper extends SQLiteOpenHelper {

    private Context context;
    private static final int DBVERSION = 2;
    private static final String DBNAME = "db_city";
    public static final String TABLE_CITY = "tb_city";
    public static final String[] all_columns = {"id", "name", "lat", "lon", "countryCode"};

    private static final String CMD_CREATE_CITY_TB = "CREATE TABLE IF NOT EXISTS '" + TABLE_CITY + "' (" +
            "'id' INTEGER PRIMARY KEY NOT NULL, " +
            "'name' TEXT , " +
            "'lat' DOUBLE, " +
            "'lon' DOUBLE, " +
            "'countryCode' TEXT, " +
            "'selected' INTEGER" +
            ")";

    public CityDbHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD_CREATE_CITY_TB);
        Log.i("dbhelper", "Table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int olderVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        onCreate(db);
    }


//    public void initContents(){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    InputStream stream = context.getResources().openRawResource(R.raw.city_list);
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//                    String line = "";
//                    SQLiteDatabase db = getWritableDatabase();
//                    while((line = reader.readLine()) != null){
//                        String[] data = line.split("\t");
//                        if(data.length < 5){
//                            continue;
//                        }
//                        long insertId = db.insert(TABLE_CITY, null,
//                                CityModel.createContentValues(
//                                        Long.valueOf(data[0]),
//                                        data[1],
//                                        Double.valueOf(data[2]),
//                                        Double.valueOf(data[3]),
//                                        data[4],
//                                        false)
//                        );
//                        Log.i("dbhelper", "city inserted : " + insertId);
//                    }
//                    db.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//    }


    public void updateCitySelected(long id, boolean selected){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("selected", selected ? 1 : 0);
        db.update(TABLE_CITY, cv, "id = " + id, null);
        db.close();
    }



    public void insertCityToDb(CityModel cityModel){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CITY, null, cityModel.getContentValuesForDb());
//        Log.i("dbhelper", "city inserted with id : " + insertId);
        db.close();
    }

    public List<CityModel> getCities(String selection, String[] selectionArgs){
        List<CityModel> citylist = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITY, all_columns, selection, selectionArgs, null, null, "countryCode, name");
        Log.d("dbhelper", "cursor returned " + cursor.getCount() + " records.");
        if(cursor.moveToFirst()){
            do{
                citylist.add(CityModel.fromCursor(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return citylist;
    }


    public List<CityModel> searchCityByName(String cityname, String limit){
        List<CityModel> citylist = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_CITY, all_columns,
                "name LIKE '" + cityname + "%'",
                null, null, null, "countryCode, name", limit);

        Log.d("dbhelper", "cursor returned " + cursor.getCount() + " records.");
        if(cursor.moveToFirst()){
            do{
                citylist.add(CityModel.fromCursor(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return citylist;
    }

}
