package com.prod.intelligent7.engineautostart;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar myBar=getActionBar();
        if (myBar != null) {
            myBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            myBar.setCustomView(R.layout.actionbar_title);
            myBar.getCustomView().invalidate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_get_sim:
                break;
            case R.id.action_get_pin:
                break;
            case R.id.action_get_phones:
                break;
            case R.id.action_get_recent1:
                break;
            case R.id.action_get_recent10:
                break;
            case R.id.action_get_last_failed:
                break;
            case R.id.action_clean_log:
                default:

                break;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
