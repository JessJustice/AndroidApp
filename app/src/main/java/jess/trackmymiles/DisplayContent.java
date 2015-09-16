package jess.trackmymiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class DisplayContent extends Activity implements View.OnClickListener {

    int from_Where_I_Am_Coming = 0;
    private DBHelper mydb;
    TextView location;
    TextView time;
    TextView mileage;
    TextView type;
    int id_To_Update = 0;

    //added for calendar
    private EditText dateEtxt;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_content);
        location = (TextView) findViewById(R.id.editTextLocation);
        time = (TextView) findViewById(R.id.editTextTime);
        mileage = (TextView) findViewById(R.id.editTextMileage);
        type = (TextView) findViewById(R.id.editType);


//       Button goBack = (Button) findViewById(R.id.goBack);
//        goBack.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                Intent i = new Intent(DisplayContent.this, MainActivity.class);
//                startActivity( i);
//            }
//        });
//
//        Button getSummary = (Button) findViewById(R.id.getSummary);
//        getSummary.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                Intent i = new Intent(DisplayContent.this, DisplayTotalMiles.class);
//                startActivity( i);
//            }
//        });
        //added for calendar
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        findViewsById();
        setDateTimeField();

        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                //means this is the view part not the add contact part.
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();
                String locatio = rs.getString(rs.getColumnIndex(DBHelper.CONTENT_COLUMN_LOCATION));
                String tim = rs.getString(rs.getColumnIndex(DBHelper.CONTENT_COLUMN_TIME));
                String mileag = rs.getString(rs.getColumnIndex(DBHelper.CONTENT_COLUMN_MILEAGE));
                String typ = rs.getString(rs.getColumnIndex(DBHelper.CONTENT_COLUMN_TYPE));
                if (!rs.isClosed()) {
                    rs.close();
                }
                Button b = (Button) findViewById(R.id.button1);
                b.setVisibility(View.INVISIBLE);

                location.setText((CharSequence) locatio);
                location.setFocusable(false);
                location.setClickable(false);

                time.setText((CharSequence) tim);
                time.setFocusable(false);
                time.setClickable(false);


                mileage.setText((CharSequence) mileag);
                mileage.setFocusable(false);
                mileage.setClickable(false);

                type.setText((CharSequence) typ);
                type.setFocusable(false);
                type.setClickable(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                getMenuInflater().inflate(R.menu.menu_display_content, menu);
            } else {
  //              getMenuInflater().inflate(R.menu.menu_main, menu); //was.main
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.Edit_Content:
                Button b = (Button) findViewById(R.id.button1);
                b.setVisibility(View.VISIBLE);
                location.setEnabled(true);
                location.setFocusableInTouchMode(true);
                location.setClickable(true);

                time.setEnabled(true);
                time.setFocusableInTouchMode(true);
                time.setClickable(true);

                mileage.setEnabled(true);
                mileage.setFocusableInTouchMode(true);
                mileage.setClickable(true);

                type.setEnabled(true);
                type.setFocusableInTouchMode(true);
                type.setClickable(true);

                return true;
            case R.id.Delete_Content:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContent)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mydb.deleteContent(id_To_Update);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), jess.trackmymiles.MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();
                return true;

            case R.id.toSummary:
//                Bundle dataBundle = new Bundle();
//                dataBundle.putInt("id", 0);
                Intent intent2 = new Intent(getApplicationContext(), DisplayTotalMiles.class);
//                intent.putExtras(dataBundle);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void run(View view) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                if (mydb.updateContent(id_To_Update, location.getText().toString(), time.getText().toString(),
                        mileage.getText().toString(), type.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), jess.trackmymiles.MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mydb.insertContent(location.getText().toString(), time.getText().toString(),
                        mileage.getText().toString(), type.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), jess.trackmymiles.MainActivity.class);
                startActivity(intent);
            }
        }
    }
    // added for calendar
    private void findViewsById(){
        dateEtxt = (EditText) findViewById(R.id.editTextTime);
        dateEtxt.setRawInputType(InputType.TYPE_NULL);
        dateEtxt.requestFocus();


    }

    //added for calendar
    private void setDateTimeField(){
        dateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    //added for calendar
    @Override
    public void onClick (View view){
        if(view == dateEtxt){
            datePickerDialog.show();
        }
    }
}
