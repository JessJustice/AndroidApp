package jess.trackmymiles;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DisplayTotalMiles extends Activity implements View.OnClickListener  {
    //add to get sum
    TextView totMiles;
    private DBHelper mydb;

    //added for calendar
    private EditText dateEtxt;
    private EditText dateEtxt2;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog datePickerDialog2;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat dateFormatter2;

    EditText toDate;
    EditText fromDate;
    Button btnCalculate;
    TextView tvResults;

    String newFromDate = "";
    String newToDate = "";

    private ListView obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_total_miles);

        toDate = (EditText) findViewById(R.id.toDate);
        fromDate = (EditText) findViewById(R.id.fromDate);
        btnCalculate = (Button) findViewById(R.id.btnCalculate);


        mydb = new DBHelper(this);

        //set a listener
        btnCalculate.setOnClickListener((View.OnClickListener) this);

        //added for calendar
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        findViewsById();
        setDateTimeField();

        dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        findViewsById();
        setDateTimeField2();

        //to display all miles
        int sum = mydb.totalAllTrips();
        TextView totMiles = (TextView) findViewById(R.id.textView5);
        totMiles.setText(""+sum);

        //to display today's miles
        int sum2 = mydb.todayMiles();
        TextView todayMiles = (TextView) findViewById(R.id.textView7);
        todayMiles.setText(""+sum2);

        //display yesterday
        int yestSum = mydb.getYestMiles();
        TextView yestMiles = (TextView) findViewById(R.id.yesterdayDisplay);
        yestMiles.setText(""+yestSum);

        //display lastWeek
        int weekSum = mydb.getLastWeekMiles();
        TextView weekMiles = (TextView) findViewById(R.id.lastWeekDisplay);
        weekMiles.setText(""+weekSum);

        //display last month
        int monthSum = mydb.getLastMonthMiles();
        TextView monthMiles = (TextView) findViewById(R.id.lastMonthDisplay);
        monthMiles.setText(""+monthSum);

        //display this year
        int yearSum = mydb.milesByYear();
        TextView yearMiles = (TextView) findViewById(R.id.thisYearDisplay);
        yearMiles.setText(""+yearSum);


        //to display custom dates
        int sum3 = mydb.milesByDate(newFromDate, newToDate);
        TextView tvResults = (TextView) findViewById(R.id.tvResults);
        tvResults.setText(""+sum3);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_total_miles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.item1:
//                Bundle dataBundle = new Bundle();
//                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getApplicationContext(), jess.trackmymiles.DisplayContent.class);
//                intent.putExtras(dataBundle);
                startActivity(intent);
                return true;

            case R.id.item2:
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent2);
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findViewsById(){
        dateEtxt = (EditText) findViewById(R.id.fromDate);
        dateEtxt.setRawInputType(InputType.TYPE_NULL);
        dateEtxt.requestFocus();

        dateEtxt2 = (EditText) findViewById(R.id.toDate);
        dateEtxt2.setRawInputType(InputType.TYPE_NULL);
        dateEtxt2.requestFocus();
    }



    //added for calendar
    private void setDateTimeField(){
        dateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void setDateTimeField2(){
        dateEtxt2.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateEtxt2.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    //added for calendar
    @Override
    public void onClick (View view){

        if(view == dateEtxt) {
            datePickerDialog.show();
        }
        if(view == dateEtxt2) {
             datePickerDialog2.show();
        }

        //check if the fields are empty
        if (TextUtils.isEmpty(toDate.getText().toString())
                || TextUtils.isEmpty(fromDate.getText().toString())) {
            return;
        }

        //read EditText and fill variables
        newFromDate =  fromDate.getText().toString();
        newToDate = toDate.getText().toString();

        int sum3 = mydb.milesByDate(newFromDate, newToDate);
        TextView tvResults = (TextView) findViewById(R.id.tvResults);
        tvResults.setText(""+sum3);
        //display type info
        mydb = new DBHelper(this);
        ArrayList array_list = mydb.byType(newFromDate, newToDate);

        ArrayAdapter arrayAdapter =
                new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);

        //adding it to the list view.
        obj = (ListView)findViewById(R.id.listView2);
        obj.setAdapter(arrayAdapter);

    }
}
