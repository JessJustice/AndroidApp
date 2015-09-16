package jess.trackmymiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by jessica on 4/10/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTENT_TABLE_NAME = "content";
    public static final String CONTENT_COLUMN_ID = "id";
    public static final String CONTENT_COLUMN_LOCATION = "location";
    public static final String CONTENT_COLUMN_MILEAGE= "mileage";
    public static final String CONTENT_COLUMN_TIME = "time";
    public static final String CONTENT_COLUMN_TYPE = "type";

    private SimpleDateFormat dateFormatter;

    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table content " +
                        "(id integer primary key, location text,time text,mileage text, type text)"
        );

        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS content");
        onCreate(db);
    }

    public boolean insertContent  (String location, String time, String mileage, String type)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("location", location);
        contentValues.put("time", time);
        contentValues.put("mileage", mileage);
        contentValues.put("type", type);

        db.insert("content", null, contentValues);
        return true;
    }
    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from content where id="+id+"", null );
        return res;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTENT_TABLE_NAME);
        return numRows;
    }
    public boolean updateContent (Integer id, String location, String time, String mileage, String type)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("location", location);
        contentValues.put("time", time);
        contentValues.put("mileage", mileage);
        contentValues.put("type", type);
        db.update("content", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContent (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("content",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }
    public ArrayList getAllContent()
    {
        ArrayList array_list = new ArrayList();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from content", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTENT_COLUMN_LOCATION)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList byType(String from, String to)
    {
        String fromDate = "'"+from+"'";
        String toDate = "'"+to+"'";

        ArrayList array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery
                ( "SELECT type, SUM(mileage) FROM content WHERE time between "+ fromDate+ " and "+toDate+"Group by type", null);
        res.moveToFirst();
        while (res.isAfterLast() == false){
            String type = res.getString(res.getColumnIndex(CONTENT_COLUMN_TYPE));
            if(res.getString(0).equals("")){
                type = "No type given";
            }
            String miles = res.getString(1);
            String results = type +": " + miles;
            array_list.add(results);
            res.moveToNext();
        }
        return array_list;
    }


    public int totalAllTrips()
    {
        int sum = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(mileage) FROM content ", null);
        if(cursor.moveToFirst())
            sum = cursor.getInt(0);
        else
        sum = -1;
        return sum;
    }

    public int todayMiles()
    {
        String date = getDate();
        date = "'"+date+"'";
        int sum = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(mileage) FROM content WHERE time like "+ date , null);
        if(cursor.moveToFirst())
            sum = cursor.getInt(0);
        else
            sum = -1;
        return sum;
    }

    public int getYestMiles()
    {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date.getTime());
        yesterday = "'"+yesterday+"'";
        int sum = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
        "SELECT SUM(mileage) FROM content WHERE time like "+ yesterday , null);
        if(cursor.moveToFirst())
            sum = cursor.getInt(0);
        else
            sum = -1;
        return sum;
    }

    public int getLastWeekMiles()
    {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.WEEK_OF_YEAR, -1);
        date.set(Calendar.DAY_OF_WEEK, date.getFirstDayOfWeek());
        String sunday = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date.getTime());

        Calendar otherDate = Calendar.getInstance();
        otherDate.set(Calendar.DAY_OF_WEEK, otherDate.getFirstDayOfWeek());
        otherDate.add(Calendar.DAY_OF_MONTH, -1);
        String saturday = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(otherDate.getTime());

        int sum = milesByDate(sunday, saturday);
        return sum;
    }

    public int getLastMonthMiles()
    {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH, -1);
        date.set(Calendar.DAY_OF_MONTH, 1);
        String dayOne = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date.getTime());

        Calendar otherDate = Calendar.getInstance();
        otherDate.add(Calendar.DAY_OF_MONTH, 1);
        otherDate.set(Calendar.DATE, -1);
        String lastDay = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(otherDate.getTime());

        int sum = milesByDate(dayOne, lastDay);
        return sum;
    }

    public int milesByYear()
    {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_YEAR, 1);
        String dayOne = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date.getTime());

        String lastDay = getDate();

        int sum = milesByDate(dayOne, lastDay);
        return sum;
    }

    //custom dates
    public int milesByDate(String from, String to)
    {
       String fromDate = "'"+from+"'";
       String toDate = "'"+to+"'";

        int sum = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT SUM(mileage) FROM content WHERE time between "+ fromDate+ " and "+toDate, null);
        if(cursor.moveToFirst())
            sum = cursor.getInt(0);
        else
            sum = -1;
        return sum;
    }
    public String getDate(){
        Calendar today = Calendar.getInstance();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(today.getTime());

        return date;
    }
}
