package com.prod.intelligent7.engineautostart;


import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.Date;


public class MainActivity extends AppCompatActivity
    implements  GetTextDialogFragment.GetTextDialogListener
{

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

    public String getSim()
    {
        String retSim=null;

        return retSim;
    }

    public String getPin()
    {
        String retPin=null;

        return retPin;
    }

    void saveOneBootScheme(Date when)
    {

    }

    void saveMultipleBootScheme(Date toStart, Date toEnd, long period) //have to create service to monitor when to send command out
    {

    }


    void putInLog(String command)
    {
        //ToDo add time stamp and save it to log table with flag=SUCCESS (1) or FAILED (-1) or other code
        //also add Sim and Phone number, in case this work for multiple SIM and Phones
    }

    public void onDialogPositiveClick(DialogFragment dialog)
    {
        if (dialog.getArguments()==null) return;
        String[] returnData=dialog.getArguments().getStringArray(GetTextDialogFragment.DATA_ENTERED);
        if (returnData==null)return;


    }
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    };

    String mCommand;
    void checkLog()
    {
        Toast.makeText(this, "PENDING construction of " + mCommand, Toast.LENGTH_LONG).show();
    }
    void cleanLog()
    {
        Toast.makeText(this, "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    public static Fragment mainUI=null;
    void setSimNumber()
    {
        //GetTextDialogFragment simFragment=new GetTextDialogFragment();
        ReadSimFragment simFragment=new ReadSimFragment();
        Bundle aBundle=new Bundle();
        //aBundle.putString(GetTextDialogFragment.DATA_ENTRY_LAYOUT, R.layout.get_sim);
        aBundle.putString("PREFERENCE_FILE_NAME", getApplication().getPackageName()+"profile");
        simFragment.setArguments(aBundle);

        //simFragment.show(getSupportFragmentManager(), "getSIM");
        FragmentManager fragmentManager = getSupportFragmentManager();
        mainUI = fragmentManager.findFragmentById(R.id.main_content_fragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);//"MAIN_UI");
       // fragmentManager.findFragmentById(R.id.main_content_frame);
        fragmentTransaction.replace(R.id.container, simFragment, "MAIN_UI").commit();
        //Toast.makeText(this, "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void setPin() //make sure only 4 Digits
    //steps : first send the phone number to booster with 0000 PIN and then update the new PIN
    {
        Toast.makeText(this, "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void setPhones()
    {
        Toast.makeText(this, "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void selectWarmerCooler()
    {
        Toast.makeText(this, "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void setOneBoot()
    {
        Toast.makeText(this, "PENDING construction of " + mCommand, Toast.LENGTH_LONG).show();
    }

    void setMultipleBoot()
    {
        Toast.makeText(this, "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void startNow()
    {
        Toast.makeText(this, "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void stopNow()
    {
        Toast.makeText(this, "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }
    
    public static final String OPEN_LOG="open_log";
    public static final String CLEAN_LOG="clean_logr";
    public static final String SET_SIMM="set_simm_number";
    public static final String SET_PIN="set_pin_code";
    public static final String SET_PHONES="set_phone_numbers";
    public static final String SET_WARMER="select_warmer_cooler";
    public static final String SET_ONE_BOOT="set_one_boot";
    public static final String SET_MULTIPLE_BOOT="set_multiple_boot";
    public static final String CMD_START_NOW="cmd_start_now";
    public static final String CMD_STOP_NOW="set_stop_now";
    
    public void executeCommand(String command)
    {
        mCommand=command;
        switch(command)
        {
            case SET_SIMM:
                setSimNumber();
                break;
            case SET_PIN:
                setPin();
                break;
            case SET_WARMER:
                selectWarmerCooler();
                break;
            case SET_PHONES:
                setPhones();
                break;
            case SET_ONE_BOOT:
                setOneBoot();
                break;
            case SET_MULTIPLE_BOOT:
                setMultipleBoot();
                break;
            case CMD_START_NOW:
                startNow();
                break;
            case CMD_STOP_NOW:
                stopNow();
                break;

            case OPEN_LOG:
                checkLog();
                break;
            case CLEAN_LOG:
                cleanLog();
            default:
                break;
        }

    }

    class MyDataBaseJob {

    }



    void nullFunction()

    {

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

    }

}
