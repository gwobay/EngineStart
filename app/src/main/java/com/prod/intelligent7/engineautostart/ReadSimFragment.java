package com.prod.intelligent7.engineautostart;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReadSimFragment extends Fragment {
	public static final String BKP_PASSWORD="BKP_PASSWORD";
	
	public ReadSimFragment() {
		// TODO Auto-generated constructor stub
	}

	static LinearLayout.LayoutParams mmParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.MATCH_PARENT);
	static LinearLayout.LayoutParams mwParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	static LinearLayout.LayoutParams wwParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	static LinearLayout.LayoutParams llParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	static TableLayout.LayoutParams tbParams=new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	static LinearLayout mRoot=null;
	static Activity mActivity=null;
	static Context mContext=null;
	public void setActivity(Activity mv)
	{
		mActivity=mv;
		mContext=mv.getApplicationContext();
	}
	static TextView mAlpha=null;
	static TextView mCurrentText=null;
	static Button mOK=null;
	static int mColumn=-1;
	static int mRow=0; // 0 for id, 1 for password;
	static TextView[] mIdDigits=null;
	static TextView[] mPasswords=null;
	static TableLayout mTable=null;
	static char[] myCodes=new char[4];
	static String mSavedCode="";
	static String mBkpCode="";
	static boolean isDigitPad=false;
	static boolean newUser=false;

	void showTheRestNormal()
	{
		for (int i=0; i<mTotalColumn*mTotalRow; i++)
		{
			mIdDigits[i].setBackgroundColor(0xf600b984);
			mIdDigits[i].invalidate();
		}
	}
	void showFocusColor(TextView v)
	{
		showTheRestNormal();
		v.setBackgroundColor(0xf500ff00);
		v.invalidate();
	}
	void showNormalColor(TextView v)
	{
		v.setTextColor(Color.BLACK);
		v.setBackgroundColor(0xf5f0ffc0);
		v.invalidate();
	}

	class LL implements Button.OnClickListener
	{
		int myRow, myColumn;
		public LL(int x, int y)
		{
			myRow=x;
			myColumn=y;
		}
		public void onClick(View v)
		{
			if (mCurrentText!=null) showNormalColor(mCurrentText);
			mRow=myRow;
			mColumn=myColumn;
			showFocusColor(mIdDigits[mColumn+mRow*mTotalColumn]);
			mCurrentText=mIdDigits[mColumn+mRow*mTotalColumn];
			getDigits();
		}
	}
	
	class LP implements Button.OnClickListener
	{
		int which;
		public LP(int i)
		{
			which=i;
		}
		public void onClick(View v)
		{
			if (mCurrentText!=null) showNormalColor(mCurrentText);
			mRow=1;
			showFocusColor(mPasswords[which]);
			mCurrentText=mPasswords[which];
			mColumn=which-1;
			getDigits();
		}
	}
	
	private class ButtonInBox extends LinearLayout {
		Button mButton;
		
		public ButtonInBox(Context cx, String text, boolean isLarge)
		{
			super(cx);
			Drawable shape=null;
			//if (!isLarge) shape = getResources().getDrawable(R.drawable.shape_oval_orange, getActivity().getTheme());
			//else shape = getResources().getDrawable(R.drawable.shape_oval_orange_large, getActivity().getTheme());
			mButton=new Button(cx);
			mButton.setText(text);
			int txtSize=(isLargeScreen)?40:30;
			mButton.setTextSize(txtSize);
			//int h=shape.getMinimumHeight();
			//int w=shape.getMinimumWidth();
			//mButton.setWidth(w+4);
			//mButton.setHeight(h+4);
			if (isLargeScreen)
			mButton.setBackgroundResource(R.drawable.shape_oval_green_large);
			else
				mButton.setBackgroundResource(R.drawable.shape_oval_green);

			LayoutParams aParams=new LayoutParams(txtSize+4, txtSize+4);
			setLayoutParams(aParams);
			setGravity(17);
			addView(mButton);
		}
		
		public Button getButton()
		{
			return mButton;
		}
	}
	/*
	Pair<Integer, Integer> getOvalSize(boolean isLarge)
	{
		Drawable shape=null;
		if (!isLarge) shape = getResources().getDrawable(R.drawable.shape_oval_orange, getActivity().getTheme());
		else shape = getResources().getDrawable(R.drawable.shape_oval_orange_large, getActivity().getTheme());
		return new Pair<Integer, Integer>(shape.getMinimumHeight(),shape.getMinimumWidth());
	} */
	void setOvalAttr(Button mButton, String text, boolean isLarge)
	{
		//View rootView = getActivity().getLayoutInflater().inflate(R.layout.oval_shape, null,	false);
		
		//int rId=(isLarge)?R.id.oval_large_button:R.id.oval_button;
		//mButton = (Button)rootView.findViewById(rId);
		//TransitionDrawable drawable = (TransitionDrawable) button.getDrawable();
		//drawable.startTransition(500);
		mButton.setText(text);
		int txtSize=(isLarge)?50:40;
		mButton.setTextSize(txtSize);
		Drawable shape=null;
		//if (!isLarge) shape = getResources().getDrawable(R.drawable.shape_oval_orange);
		//else shape = getResources().getDrawable(R.drawable.shape_oval_orange_large);
		//int h=shape.getMinimumHeight();
		//int w=shape.getMinimumWidth();
		//mButton.setWidth(w+4);
		//mButton.setHeight(h+4);
		if (!isLarge) mButton.setBackgroundResource(R.drawable.shape_oval_orange);
		else mButton.setBackgroundResource(R.drawable.shape_oval_orange_large);
		
		//return button;
	}
	
	LinearLayout idLabel()
	{
		LinearLayout id=new LinearLayout(mContext);
		id.setLayoutParams(mwParams);
		id.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
	 	id.setGravity(1);
	 	TextView idLabel=new TextView(mContext);
	 	String cid=getResources().getString(R.string.sim_setting);
		idLabel.setText(cid);
		idLabel.setBackgroundColor(0x55f5f5f5);
		idLabel.setTextColor(Color.BLACK);
		int txtSize=30;
		if (isLandScape) txtSize=20;
		idLabel.setTextSize(txtSize);
		id.addView(idLabel);
		
		return id;
	}
		
	LinearLayout idLine()
	{
		LinearLayout gID=new LinearLayout(mContext);
		gID.setLayoutParams(mwParams);
		gID.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
		gID.setGravity(1);
		int txtSize=30;
		if (isLargeScreen) txtSize=40;
	 	TextView idLabel=new TextView(mContext);
		idLabel.setText("  ");
		idLabel.setTextSize(txtSize);
		gID.addView(idLabel);

		
		mIdDigits=new TextView[20];
		for (int k=0; k<mTotalRow; k++) {
			LinearLayout idRow=new LinearLayout(mContext);
			LinearLayout.LayoutParams rowParam=mwParams;
			rowParam.setMargins(5, 5, 5, 5);

			idRow.setLayoutParams(rowParam);

			idRow.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
			idRow.setGravity(1);
			for (int i = 0; i < mTotalColumn; i++) {
				int j=i+k*mTotalColumn;
				mIdDigits[j] = new TextView(mContext);
				mIdDigits[j].setWidth(txtSize * 12/ 10);
				mIdDigits[j].setTextSize(txtSize * 9 / 10);
				mIdDigits[j].setText("X");
				mIdDigits[j].setGravity(Gravity.CENTER);
				if (isLargeScreen) mIdDigits[j].setBackgroundResource(R.drawable.shape_rect_orange_large);
				else mIdDigits[j].setBackgroundResource(R.drawable.shape_rect_orange_large);
				mIdDigits[j].setClickable(true);
				mIdDigits[j].setOnClickListener(new LL(k, i));
				idRow.addView(mIdDigits[j]);
			}
			gID.addView(idRow);
		}
		return gID;
	}
	
	LinearLayout passwdLabel()
	{
		LinearLayout id=new LinearLayout(mContext);
		TextView idLabel=new TextView(mContext);
		id.setLayoutParams(llParams);
		id.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
	 	id.setGravity(1);
	 	String pwd=getResources().getString(R.string.label_get_sim);
		idLabel.setText(pwd);
		idLabel.setTextColor(Color.BLACK);
		idLabel.setTextSize(30);
		id.addView(idLabel);
		return id;
	}
	LinearLayout passwordLine()
	{
		LinearLayout id=new LinearLayout(mContext);
		id.setLayoutParams(llParams);
		id.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
	 	id.setGravity(17);//setGravity(1);

		
		mPasswords=new TextView[4];
		for (int i=0; i<4; i++)
		{
			mPasswords[i]=new TextView(mContext);
			mPasswords[i].setWidth(50);
			mPasswords[i].setTextSize(50);
			mPasswords[i].setText("0");
			mPasswords[i].setShadowLayer(20, 2, 2, Color.YELLOW);
			mPasswords[i].setInputType(16); //password
			mPasswords[i].setClickable(true);
			mPasswords[i].setOnClickListener(new LP(i));
			id.addView(mPasswords[i]);			
		}
		return id;
	}


	void getDigits()
	{
		int iPos=mColumn + mRow*mTotalColumn;
		if (iPos < mTotalColumn*mTotalRow)
		{
			mCurrentText=mIdDigits[iPos];
			showNormalColor(mCurrentText);
		}
		else
		{
			showFocusColor(mOK);
			return;
		}

		isDigitPad=true;
	}
	
	LinearLayout oneRow(char value1, int howMany, int elementWidth, int elementHeight)
	{
		LinearLayout aRow=new LinearLayout(mContext);
		//aRow.setLayoutParams(wwParams);
		aRow.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
		LinearLayout.LayoutParams rowParam=wwParams;
		rowParam.setMargins(1, 5, 1, 5);
		aRow.setLayoutParams(rowParam);
		aRow.setGravity(Gravity.CENTER_HORIZONTAL);
		boolean isLarge=true;
		//if (a < 'A' || digits < 3)
		isLarge=false;
		//Pair<Integer, Integer> aPair=getOvalSize(isLarge);
		//LinearLayout.LayoutParams rowParam=new LinearLayout.LayoutParams(3*aPair.first+2, 3*aPair.second+2);
		//aRow.setLayoutParams(rowParam);
		for (int i=0; i<howMany; i++)
		{
			//Button aBt=new Button(mContext);
			String btText="";

				btText += (char)(value1+i);

			
			//setOvalAttr(aBt, btText, isLarge);
			ButtonInBox aBt=new ButtonInBox(getActivity(),btText, isLarge);
			LinearLayout.LayoutParams aParams=new LinearLayout.LayoutParams(elementWidth, elementHeight, 1.0f);
			aParams.setMargins(2, 3, 2, 3);
			aBt.setLayoutParams(aParams);
			aBt.setGravity(Gravity.CENTER);
			aBt.getButton().setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {

					String s = ((Button) v).getText().toString().toUpperCase();

					if (mCurrentText != null) {
						mCurrentText.setText(s);
						mCurrentText.setBackgroundResource(R.drawable.transition);
						((TransitionDrawable)mCurrentText.getBackground()).startTransition (300);
					}
					mColumn++;
					getDigits();

				}
			});
			aRow.addView(aBt);
		}
		return aRow;
	}
	
	void showTransition(View v)
	{
		TransitionDrawable ttDrawable = (TransitionDrawable)
				getResources().getDrawable(R.drawable.transition);//, getActivity().getTheme());
		TextView digitField = (TextView)v;
		//Button button = (Button)v;
		//button.setText("9");
		//button.setTextSize(60);
		//button.setBackground(ttDrawable);
		digitField.setBackgroundResource(R.drawable.transition);
		ttDrawable.startTransition(300);
	}

	LinearLayout setWideKeyPadButtons() //for number keypad
	{
		LinearLayout keyPad=new LinearLayout(mContext);
		LinearLayout.LayoutParams kParams=new LinearLayout.LayoutParams(keyPadWidth*7/5,
													LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
		keyPad.setLayoutParams(kParams);
		keyPad.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
		keyPad.setGravity(Gravity.CENTER_HORIZONTAL);

		ButtonInBox aBt=new ButtonInBox(getActivity(),"OK", false);
		mOK=aBt.getButton();
		LinearLayout.LayoutParams okParams=new LinearLayout.LayoutParams(keyPadWidth/5,
													LinearLayout.LayoutParams.MATCH_PARENT, 0.1f);
		aBt.setLayoutParams(okParams);
		aBt.setGravity(Gravity.CENTER_VERTICAL);
		aBt.getButton().setTextColor(Color.WHITE);
		aBt.getButton().setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				saveData();
			}
		});
		/*
		if (isLargeScreen)
			aBt.setBackgroundResource(R.drawable.shape_oval_orange_large);
		else
			aBt.setBackgroundResource(R.drawable.shape_oval_orange);*/
		keyPad.addView(aBt);

		LinearLayout nPart=new LinearLayout(mContext);
		LinearLayout.LayoutParams nPartParam=new LinearLayout.LayoutParams(keyPadWidth,
				LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
		nPart.setLayoutParams(nPartParam);
		nPart.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
		nPart.setGravity(Gravity.FILL_HORIZONTAL);

		for (int i=0; i<2; i++)
		{
			int m=5;
			//if (a > '9') m=9;
			//char c=(char)(a+m*i);
			char c=(char)('0'+m*i);
			nPart.addView(oneRow(c, 5, keyPadWidth/5, mDisplayHeight/10 ));
		}

		keyPad.addView(nPart);

		//add last column in number key pad

		aBt=new ButtonInBox(getActivity(),"<-", false);

		aBt.setLayoutParams(okParams);
		aBt.setGravity(Gravity.CENTER_VERTICAL);
		aBt.getButton().setTextColor(Color.MAGENTA);
		aBt.getButton().setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				mColumn--;
				if (mColumn <0)
				{
					mColumn=mTotalColumn;
					mRow--;
					if (mRow< 0) { mRow=0; mColumn=0;}
				}
				int myPos=mColumn+mRow*mTotalColumn;
				mIdDigits[myPos].setText("_");
				getDigits();
			}
		});
		/* if (isLargeScreen)
			aBt.setBackgroundResource(R.drawable.shape_oval_orange_large);
		else
			aBt.setBackgroundResource(R.drawable.shape_oval_orange);*/
		keyPad.addView(aBt);

		return keyPad;
	}

	LinearLayout setKeyPadButtons() //for number keypad
	{
		LinearLayout keyPad=new LinearLayout(mContext);
		LinearLayout.LayoutParams kParams=new LinearLayout.LayoutParams(keyPadWidth,
												LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
		kParams.setMargins(3,3,3,3);
		keyPad.setLayoutParams(kParams);
		keyPad.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
	 	keyPad.setGravity(Gravity.CENTER_HORIZONTAL);

		for (int i=0; i<3; i++)
			{
				int m=3;
				//if (a > '9') m=9;
				//char c=(char)(a+m*i);
				char c=(char)('0'+(m*i+1));
				keyPad.addView(oneRow(c, 3, keyPadWidth/4, keyPadWidth/4));
			}
		 //add last row in number key pad

			LinearLayout aRow=new LinearLayout(mContext);
		aRow.setLayoutParams(mwParams);
			aRow.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
			aRow.setGravity(Gravity.FILL_HORIZONTAL);
			ButtonInBox aBt=new ButtonInBox(getActivity(),"OK", false);
			mOK=aBt.getButton();
		LinearLayout.LayoutParams aParams=new LinearLayout.LayoutParams(keyPadWidth/4, keyPadWidth/4, 0.5f);
		aParams.setMargins(2, 3, 2, 3);
		aBt.setLayoutParams(aParams);
			//setOvalAttr(aBt, "ok", false);
			aBt.getButton().setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					saveData();
				}
			});
		aBt.getButton().setTextColor(Color.WHITE);

			aRow.addView(aBt);
			aBt=new ButtonInBox(getActivity(),"0", false);
		//LinearLayout.LayoutParams aParams=new LinearLayout.LayoutParams(keyPadWidth/4, keyPadWidth/4, 0.5f);
		aParams.setMargins(2, 3, 2, 3);
		aBt.setLayoutParams(aParams);
			//setOvalAttr(aBt, "0", false);
			aBt.getButton().setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if (mCurrentText != null)
						mCurrentText.setText("0");
					mColumn++;
					getDigits();
				}
			});
			aRow.addView(aBt);
			aBt=new ButtonInBox(getActivity(),"<-", false);
			//setOvalAttr(aBt, "<-", false);
			aBt.setLayoutParams(aParams);
		aBt.getButton().setTextColor(Color.CYAN);
		aBt.getButton().setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v)
				{
					mColumn--;
					if (mColumn <0)
					{
						mColumn=mTotalColumn;
						mRow--;
						if (mRow< 0) { mRow=0; mColumn=0;}
					}
					int myPos=mColumn+mRow*mTotalColumn;
					mIdDigits[myPos].setText("_");
					getDigits();
				}
			});

			aRow.addView(aBt);
			keyPad.addView(aRow);			

		return keyPad;
	}

	void saveData()
	{
		String simCode="";
		for (int i=0; i<mIdDigits.length; i++)
		{
			String aDigit=mIdDigits[i].getText().toString();
			if (aDigit=="" || aDigit==" " || aDigit.length() > 1 || aDigit.charAt(0) > '9' || aDigit.charAt(0)<'0' ) {
				simCode += "X";
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.bad_data_entry), Toast.LENGTH_LONG).show();
				mColumn=i % mTotalColumn;
				mRow = i / mTotalColumn;
				mCurrentText=mIdDigits[i];
				return;
			}
			else
				simCode += aDigit;
		}


    	String fileName=getArguments().getString("PREFERENCE_FILE_NAME");
        SharedPreferences mSPF = getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSPF.edit();//prefs.edit();
		String pwd=getResources().getString(R.string.sim_setting);
		editor.putString("MY_SIM", simCode);
		editor.commit();

		getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
		getActivity().getSupportFragmentManager().popBackStackImmediate();

	}
	

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	 {
		 mActivity=getActivity();
		 mContext=mActivity;//.getApplicationContext();
		 getMyScreenSize();
		 ScrollView sv=new ScrollView(mContext);
		 LinearLayout myUI=new LinearLayout(mContext);
		 myUI.setLayoutParams(mmParams);
		 myUI.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
		 myUI.setGravity(1);//center_horizontal
		 myUI.setBackgroundColor(Color.WHITE);
			 mColumn=0;
			 mRow=0;
			 //String pwd=getResources().getString(R.string.label_get_pin);
			 //mSavedCode=getArguments().getString(pwd);
			 //mBkpCode=getArguments().getString(BKP_PASSWORD);
		 /*
			 char a='1';
			 String pgTitle=getResources().getString(R.string.label_get_pin);//"Enter Password to Log in";
			 newUser = false;
			 if (mSavedCode == null || mSavedCode.length() < 4)
		 	{
		 		id.addView(idLabel());
		 		id.addView(idLine());
		 		a='A';
		 		mRow=0;
		 		//pgTitle=getResources().getString(R.string.setup_new_account);
		 		newUser=true;
		 	}
		*/
		 /*
		 	id.addView(passwdLabel());

		 	id.addView(passwordLine());
		 	*/
		 myUI.addView(idLabel());
		 myUI.addView(idLine());

		 //add key pad
		 	LinearLayout keyPad=new TableLayout(mContext);//LinearLayout(mContext);
		 keyPad.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
		 	//tbHome.addView(passwordLine());
		 keyPad.setGravity(17);
			// mTable=showNButton(3,a);
			// mTable=showNButton(3,'0');
		 	//tbHome.addView(mTable);
		 	//id.addView(tbHome);
		 if (isLandScape) myUI.addView(setWideKeyPadButtons());
		 else
		 myUI.addView(setKeyPadButtons());
		 	//GifViewer aGif=new GifViewer(getActivity(), R.drawable.ninja_turtle);
		 /*
		 	View rootView = null;
		    int page=R.layout.gif_view_port;
		        rootView=inflater.inflate(page, id, false); 
			ImageView img1=(ImageView)(rootView.findViewById(R.id.gif_view));
			img1.setBackgroundResource(R.drawable.nija_frames);
			kpAnimation = (AnimationDrawable) img1.getBackground();
		 // Start the animation (looped playback by default).
			kpAnimation.start();
*/
		 	sv.addView(myUI);
		 /*
		 if (mRow==1)
		 	mCurrentText=mPasswords[0];
		 	else
		 		mCurrentText=mAlpha;
			showFocusColor(mCurrentText);
		 	mRoot=tbHome;
		 	//mActivity.setTitle(pgTitle);
		 	*/
		 mColumn=0;
		 mRow=0;
		 mCurrentText=mIdDigits[0];
	        return sv;
	 }

	int mTotalRow;
	int mTotalColumn;
	boolean isLargeScreen;
	boolean isLandScape;
	private int mDisplayWidth;
	private int mDisplayHeight;
	private int keyPadWidth;
	LinearLayout.LayoutParams keyPadParams;

	private int mLogBarHeight;
	void getMyScreenSize() //if land scape use 10-10 layout, otherwise use 4-4-4-4-4 layout
	{
		//Display display = getWindowManager().getDefaultDisplay();
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		isLargeScreen=false;
		int iMaxSize=size.x;
		mDisplayWidth = size.x;
		mDisplayHeight = size.y;
		if (size.y > iMaxSize )iMaxSize=size.y;
		if (size.y > 850) isLargeScreen=true;
		keyPadWidth=mDisplayWidth/2;
		keyPadParams=new LinearLayout.LayoutParams(keyPadWidth, mDisplayHeight/4, 0.5f);
		if (mDisplayWidth>mDisplayHeight)
		{
			isLandScape=true;
			mTotalColumn=10; mTotalRow=2;
		}
		else
		{
			isLandScape=false;
			mTotalColumn=4; mTotalRow=5;
		}
		//If you're not in an Activity you can get the default Display via WINDOW_SERVICE:
		/*
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
		//control_height=mDisplayHeight/nR;
		//control_width=mDisplayWidth/nC;
		*/
	}
}
