package jess.trackmymiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.example.AddressBook.MESSAGE";

    private ListView obj;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);
        ArrayList array_list = mydb.getAllContent();

        ArrayAdapter arrayAdapter =
                new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);

        //adding it to the list view.
        obj = (ListView)findViewById(R.id.listView1);
        obj.setAdapter(arrayAdapter);

        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override


          //  @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                int id_To_Search = arg2 + 1;
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);
                Intent intent = new Intent(getApplicationContext(),jess.trackmymiles.DisplayContent.class);
              //  Intent intent2 = new Intent(getApplicationContext(), jess.trackmymiles.DisplayTotalMiles.class);
                intent.putExtras(dataBundle);
              //  intent2.putExtras(dataBundle);
                startActivity(intent);
            }


        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu); //was .mainmenu@@@@@@@@@@@@@@@



        MenuItem item1 = menu.findItem(R.id.item1);
        Intent intent1 = new Intent(this.getApplicationContext(), jess.trackmymiles.DisplayContent.class);
        item1.setIntent(intent1);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case R.id.item1:
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getApplicationContext(),jess.trackmymiles.DisplayContent.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                return true;

            case R.id.item2:
               // Bundle dataBundle = new Bundle();
              //  dataBundle.putInt("id", 0);
                Intent intent2 = new Intent(getApplicationContext(),jess.trackmymiles.DisplayTotalMiles.class);
              //  intent.putExtras(dataBundle);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }

    }
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }

}