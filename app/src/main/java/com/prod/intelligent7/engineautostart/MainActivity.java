package com.prod.intelligent7.engineautostart;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onStart() {
        super.onStart();

        ActionBar myBar=getSupportActionBar();

        if (myBar != null) {
            int height=myBar.getHeight();
            myBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
            myBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            myBar.setDisplayHomeAsUpEnabled(true);
            View myBarView= getLayoutInflater().inflate(R.layout.actionbar_title, null);
            TextView textViewTitle = (TextView) myBarView.findViewById(R.id.myActionBarTitle);
            if (height > 20)
                textViewTitle.setTextSize(height*2/3);
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);
            myBar.setCustomView(myBarView, params);
            myBar.getCustomView().invalidate();
        }

    }

    Menu mMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /*
        LayoutInflater baseInflater = //getLayoutInflater();
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        baseInflater.setFactory(new LayoutInflater.Factory() {
            public View onCreateView(String name, Context context,
                                     AttributeSet attrs) {

                if (name.equalsIgnoreCase(
                        "com.android.internal.view.menu.IconMenuItemView")) {
                    try {
                        LayoutInflater li = LayoutInflater.from(context);
                        final View view = li.createView(name, null, attrs);
                        new Handler().post(new Runnable() {
                            public void run() {
                                // set the background drawable if you want that
                                //or keep it default -- either an image, border
                                //gradient, drawable, etc.
                                //view.setBackgroundResource(R.drawable.myimage);
                                ((TextView) view).setTextSize(20);

                                // set the text color
                                //Typeface face = Typeface.createFromAsset(
                                //getAssets(),"OldeEnglish.ttf");
                                //((TextView) view).setTypeface(face);
                                ((TextView) view).setTextColor(Color.RED);
                            }
                        });
                        return view;
                    } catch (InflateException e) {
                        //Handle any inflation exception here
                    } catch (ClassNotFoundException e) {
                        //Handle any ClassNotFoundException here
                    }
                }
                return null;
            }
        }); */
        return true;
    }

    @Override
    public void openOptionsMenu()
    {
        super.openOptionsMenu();
    }

    public void showMenuItems(View v)
    {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                openOptionsMenu();
            }
        }, 0);
        //openOptionsMenu();// mMenu.findItem(R.id.action_get_recent1).setVisible(true);
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
       // if (id == R.id.action_settings) {
           // return true;
        //}

        return super.onOptionsItemSelected(item);
    }



}
