package com.prod.intelligent7.engineautostart;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    static LinearLayout.LayoutParams llParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
    static LinearLayout.LayoutParams llWrapParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    static LinearLayout.LayoutParams llWWParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    static TableLayout.LayoutParams tbParams=new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    MainActivity myActivity;

    public MainActivityFragment() {
        isLandScape=false;
    }

    final static int greyOutTextColor=0x5fffffff;
    final static int normalTextColor=Color.BLACK;
    int control_width, control_height;

    Button buildOpenLogButton()
    {
        Button retB=new Button(getActivity());
        LinearLayout.LayoutParams aParams=new LinearLayout.LayoutParams(mDisplayWidth/2, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
        aParams.setMargins(2, 2, 2, 2);
        retB.setLayoutParams(aParams);
        retB.setGravity(Gravity.LEFT);
        retB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.open_log, 0, 0, 0);
        retB.setText(getResources().getString(R.string.open_log));
        int textSize=mLogBarHeight/3;
        if (textSize < 16) textSize=16;
        retB.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        retB.setTextColor(Color.BLACK);
        retB.setBackgroundColor(Color.YELLOW);//Color.GRAY);
        retB.setOnClickListener(new ButtonClickListener(OPEN_LOG));
        return retB;
    }
    Button buildClearLogButton()
    {
        Button retB=new Button(getActivity());
        LinearLayout.LayoutParams aParams=new LinearLayout.LayoutParams(mDisplayWidth/2, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
        aParams.setMargins(2, 2, 2, 2);
        retB.setLayoutParams(aParams);
        retB.setGravity(Gravity.RIGHT);
        retB.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.clean_log, 0);
        retB.setText(getResources().getString(R.string.clear_log));
        int textSize=mLogBarHeight/3;
        if (textSize < 16) textSize=16;
        retB.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        retB.setTextColor(Color.BLACK);
        retB.setBackgroundColor(Color.YELLOW);
        retB.setOnClickListener(new ButtonClickListener(CLEAN_LOG));
        return retB;
    }

    LinearLayout buildLogBar()
    {
        LinearLayout onePair=new LinearLayout(getActivity());
        onePair.setLayoutParams(llWrapParams);
        onePair.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
        //id.setGravity(1);
        onePair.addView(buildOpenLogButton());
        onePair.addView(buildClearLogButton());
        return onePair;
    }

    void constructButton(Button retB, String text)
    {
        LinearLayout.LayoutParams aParams=new LinearLayout.LayoutParams(control_width, control_height, 0.5f);
        aParams.setMargins(2, 3, 2, 3);
        retB.setLayoutParams(aParams);
        retB.setGravity(Gravity.CENTER);
        retB.setText(text);
        int textSize=control_height / 8;
        if (textSize < 28) textSize=28;
        retB.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        //retB.setTextColor(Color.RED);
        //retB.setBackgroundColor(0x847fffb4);
    }
    Button buildSIMButton()
    {
      Button retB=new Button(getActivity());
        constructButton(retB, getResources().getString(R.string.sim_setting));
        retB.setOnClickListener(new ButtonClickListener(SET_SIMM));
        return retB;
    }

    Button buildPINButton()
    {
        Button retB=new Button(getActivity());
        constructButton(retB, getResources().getString(R.string.pin_setting));
        retB.setOnClickListener(new ButtonClickListener(SET_PIN));
        return retB;
    }

    Button buildPhonesButton()
    {
        Button retB=new Button(getActivity());
        constructButton(retB,getResources().getString(R.string.phone_numbers_setting) );
        retB.setOnClickListener(new ButtonClickListener(SET_PHONES));
        return retB;
    }
    Button buildWarmerCoolerButton()
    {
        Button retB=new Button(getActivity());
        constructButton(retB,getResources().getString(R.string.warming_cooling_setting) );
        retB.setOnClickListener(new ButtonClickListener(SET_WARMER));
        return retB;
    }
    Button buildDailyOneStartButton()
    {
        Button retB=new Button(getActivity());
        constructButton(retB,getResources().getString(R.string.daily_auto_start_setting) );
        retB.setOnClickListener(new ButtonClickListener(SET_ONE_BOOT));
        return retB;
    }
    Button buildDailyMultipleButton()
    {
        Button retB=new Button(getActivity());
        constructButton(retB,getResources().getString(R.string.daily_multiple_start_setting) );
        retB.setOnClickListener(new ButtonClickListener(SET_MULTIPLE_BOOT));
        return retB;
    }
    Button buildStartNowButton()
    {
        Button retB=new Button(getActivity());
        constructButton(retB,getResources().getString(R.string.start_engine) );
        retB.setOnClickListener(new ButtonClickListener(CMD_START_NOW));
        return retB;
    }
    Button buildStopNowButton()
    {
        Button retB=new Button(getActivity());
        constructButton(retB,getResources().getString(R.string.shut_down_engine) );
        retB.setOnClickListener(new ButtonClickListener(CMD_STOP_NOW));
        return retB;
    }

    LinearLayout buildControlButtons1Pair(int whichPair, boolean vertically)
    {
        LinearLayout onePair=new LinearLayout(getActivity());
        LinearLayout.LayoutParams aParams=null;
        if (vertically) {
            aParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);
            onePair.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
        } else
        {
            aParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
          onePair.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
        }
        //aParams.setMargins(2, 0, 2, -1);
        onePair.setLayoutParams(aParams);
        onePair.setGravity(Gravity.CENTER);

        //id.setGravity(1);
        switch (whichPair)
        {
            case 0:
                onePair.addView(buildSIMButton());
                onePair.addView(buildPINButton());
                break;
            case 1:
                onePair.addView(buildPhonesButton());
                onePair.addView(buildWarmerCoolerButton());
                break;
            case 2:
                onePair.addView(buildDailyOneStartButton());
                onePair.addView(buildDailyMultipleButton());
                break;
            case 3:
                onePair.addView(buildStartNowButton());
                onePair.addView(buildStopNowButton());
                break;
        }
        return onePair;
    }

    LinearLayout buildControlButtons()
    {
        LinearLayout onePair=new LinearLayout(getActivity());
        onePair.setLayoutParams(llWrapParams);
        boolean vertically = !isLandScape;
        if (vertically) {
            onePair.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
        } else
            onePair.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);

        for (int i=0; i<4; i++)
            onePair.addView(buildControlButtons1Pair(i, !vertically));

        return onePair;

    }

    boolean isLandScape;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private int mLogBarHeight;
    void getMyScreenSize()
    {
        //Display display = getWindowManager().getDefaultDisplay();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mDisplayWidth = size.x;
        mDisplayHeight = size.y;
        isLandScape=(mDisplayWidth>mDisplayHeight);
        //If you're not in an Activity you can get the default Display via WINDOW_SERVICE:
        TypedValue tv = new TypedValue();
        int mActionBarHeight=0;
        if (getActivity().getTheme().resolveAttribute(
                android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, getActivity().getResources().getDisplayMetrics());
        }
        mDisplayHeight -= mActionBarHeight ;
        mLogBarHeight = mActionBarHeight*2/3;
        if (mLogBarHeight < 10) mLogBarHeight=10;
        mLogBarHeight=0; //now use menu item
        mDisplayHeight -= mLogBarHeight;
        int nC=2, nR=4; //# of col. row
        if (isLandScape) {nC=4; nR=2;}
        mDisplayHeight -= (8*nR);
        mDisplayWidth -= 4*nC;
        control_height=mDisplayHeight/nR;
        control_width=mDisplayWidth/nC;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getMyScreenSize();
       // ScrollView sv=new ScrollView(getActivity());
        LinearLayout retV=new LinearLayout(getActivity());
        retV.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
        //id.setGravity(1);//center_horizontal
        retV.setLayoutParams(llWrapParams);
        //retV.addView(buildLogBar()); //now in menu item
        retV.addView(buildControlButtons());
        /*
        if (isLandScape){

        } else
        {

        }
        mColumn=0;
        mRow=1;
        String pwd=getResources().getString(R.string.pass_word_key);
        mSavedCode=getArguments().getString(pwd);
        mBkpCode=getArguments().getString(BKP_PASSWORD);
        char a='1';
        String pgTitle=getResources().getString(R.string.enter_password);//"Enter Password to Log in";
        newUser = false;
        if (mSavedCode == null || mSavedCode.length() < 4)
        {
            id.addView(idLabel());
            id.addView(idLine());
            a='A';
            mRow=0;
            pgTitle=getResources().getString(R.string.setup_new_account);
            newUser=true;
        }

        id.addView(passwdLabel());

        id.addView(passwordLine());
        LinearLayout tbHome=new TableLayout(mContext);//LinearLayout(mContext);
        tbHome.setOrientation(0);//0HORIZONTAL, 1Vertical);
        //tbHome.addView(passwordLine());
        tbHome.setGravity(17);
        mTable=showNButton(3,a);
        tbHome.addView(mTable);
        id.addView(tbHome);
        //GifViewer aGif=new GifViewer(getActivity(), R.drawable.ninja_turtle);
        View rootView = null;
        int page=R.layout.gif_view_port;
        rootView=inflater.inflate(page, id, false);
        ImageView img1=(ImageView)(rootView.findViewById(R.id.gif_view));
        img1.setBackgroundResource(R.drawable.nija_frames);
        kpAnimation = (AnimationDrawable) img1.getBackground();
        // Start the animation (looped playback by default).
        kpAnimation.start();

        sv.addView(id);
        if (mRow==1)
            mCurrentText=mPasswords[0];
        else
            mCurrentText=mAlpha;
        showFocusColor(mCurrentText);
        mRoot=tbHome;
        mActivity.setTitle(pgTitle);
        return sv;

*/
       // sv.addView(retV);

       // return sv;

         return retV;
       // return inflater.inflate(R.layout.fragment_main, container, false);
    }
    void checkLog()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }
    void cleanLog()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }
    void setSimNumber()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void setPin()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void setPhones()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void selectWarmerCooler()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void setOneBoot()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void setMultipleBoot()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void startNow()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }

    void stopNow()
    {
        Toast.makeText(getActivity(), "PENDING construction of "+mCommand, Toast.LENGTH_LONG).show();
    }
    String mCommand;
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

    class ButtonClickListener implements Button.OnClickListener
    {
        String toDo;
        public ButtonClickListener(String forCommand)
        {
            toDo=forCommand;
        }
        public void onClick(View v)
        {
            mCommand=toDo;
            switch(toDo)
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
    }
}
