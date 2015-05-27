package com.prod.intelligent7.engineautostart;


import android.app.Activity;
import android.app.Fragment;
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

public class ReadSimFragment extends Fragment {
	public static final String BKP_PASSWORD="BKP_PASSWORD";
	
	public ReadSimFragment() {
		// TODO Auto-generated constructor stub
	}

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
	void showFocusColor(TextView v)
	{
		v.setTextColor(Color.MAGENTA);
		v.invalidate();
	}
	void showNormalColor(TextView v)
	{
		v.setTextColor(Color.BLACK);
		v.invalidate();
	}

	class LL implements Button.OnClickListener
	{
		int which;
		public LL(int i)
		{
			which=i;
		}
		public void onClick(View v)
		{
			if (mCurrentText!=null) showNormalColor(mCurrentText);
			mRow=which/mTotalColumn;
			showFocusColor(mIdDigits[which]);
			mCurrentText=mIdDigits[which];
			mColumn=which % mTotalRow;

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
			mButton.setBackgroundResource(R.drawable.shape_oval_orange_large);
			else
				mButton.setBackgroundResource(R.drawable.shape_oval_orange);

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
		id.setLayoutParams(llParams);
		id.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
	 	id.setGravity(1);
	 	TextView idLabel=new TextView(mContext);
	 	String cid=getResources().getString(R.string.sim_setting);
		idLabel.setText(cid);
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

		/* only digits for SIMM
		mAlpha=new TextView(mContext);
		mAlpha.setWidth(txtSize * 9 / 10);
		mAlpha.setText("0");
		mAlpha.setTextSize(txtSize*9/10);
		mAlpha.setClickable(true);
		mAlpha.setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v)
				{
					mRow=0;
					getAlpha();
				}
			});
		id.addView(mAlpha);
		*/
		
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
				mIdDigits[j].setText("0");
				if (isLargeScreen) mIdDigits[j].setBackgroundResource(R.drawable.shape_rect_orange_large);
				else mIdDigits[j].setBackgroundResource(R.drawable.shape_rect_orange_large);
				mIdDigits[j].setClickable(true);
				mIdDigits[j].setOnClickListener(new LL(j));
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
	
	void getAlpha()
	{
		if (mCurrentText != null)
			showNormalColor(mCurrentText);	
		mRoot.removeView(mTable);
		mRoot.invalidate();
		mTable=showNButton(3, 'A');
		mRoot.addView(mTable);
		showFocusColor(mAlpha);
		mRoot.invalidate();
		mCurrentText=mAlpha;
		isDigitPad=false;
	}
	
	void getAlpha(String abc)
	{
		mRoot.removeView(mTable);
		mRoot.invalidate();
		mTable=showNButton(1, abc.charAt(0));
		mRoot.addView(mTable);
		mRoot.invalidate();
		isDigitPad=false;
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
	
	LinearLayout oneRow(char a, int digits)
	{
		LinearLayout aRow=new LinearLayout(mContext);
		aRow.setLayoutParams(wwParams);
		aRow.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
		LinearLayout.LayoutParams rowParam=mwParams;
		rowParam.setMargins(5,5,5,5);
		aRow.setLayoutParams(rowParam);
		aRow.setGravity(17);
		boolean isLarge=true;
		//if (a < 'A' || digits < 3)
		isLarge=false;
		//Pair<Integer, Integer> aPair=getOvalSize(isLarge);
		//LinearLayout.LayoutParams rowParam=new LinearLayout.LayoutParams(3*aPair.first+2, 3*aPair.second+2);
		//aRow.setLayoutParams(rowParam);
		for (int i=0; i<3; i++)
		{
			//Button aBt=new Button(mContext);
			String btText="";
			if (digits != 3)
				btText += (char)(a+i);
			else
			{
				btText += (char)(a+3*i);
				btText +=(char)(a+3*i+1);
				btText +=(char)(a+3*i+2);
			}
			
			//setOvalAttr(aBt, btText, isLarge);
			ButtonInBox aBt=new ButtonInBox(getActivity(),btText, isLarge);
			aBt.setMinimumWidth(60);

			aBt.setGravity(Gravity.CENTER);
			aBt.getButton().setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v)
				{
					showTransition(v);
					String s=((Button)v).getText().toString().toUpperCase();
					if (s.length()==1)
					{
						/*
						if (mRow==1) 
							{
								myCodes[mColumn % 4]=s.charAt(0);
								if (!newUser)
									s="*";
							}*/
						if (mCurrentText!=null)
						mCurrentText.setText(s);
						/*if (s.charAt(0) > '9')
						{
							mAlpha.setText(s);
							showNormalColor(mAlpha);
							showFocusColor(mIdDigits[0]);
							mRow=0;
							mColumn=-1;
						}*/
						int iPos=mColumn+mRow*mTotalColumn;
						if (iPos < mTotalRow*mTotalColumn) {
							getDigits();
							mColumn++;
						}
					}
					else
						getAlpha(s);
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
		Button button = (Button)v;
		//button.setText("9");
		//button.setTextSize(60);
		//button.setBackground(ttDrawable);
		button.setBackgroundResource(R.drawable.transition);
		ttDrawable.startTransition(300);
	}
	TableLayout showNButton(int N_row, char a) //for number keypad
	{
		TableLayout keyPad=new TableLayout(mContext);
		TableLayout.LayoutParams keyPadParam=tbParams;
		keyPadParam.setMargins(5,5,5,5);
		keyPad.setLayoutParams(keyPadParam);
		//keyPad.setOrientation(0);//0HORIZONTAL, 1Vertical);
	 	keyPad.setGravity(17);

		for (int i=0; i<3; i++)
			{
				int m=3;
				//if (a > '9') m=9;
				//char c=(char)(a+m*i);
				char c=(char)('0'+(m*i+1));
				keyPad.addView(oneRow(c, 1));
			}
		 //add last row in number key pad

			LinearLayout aRow=new LinearLayout(mContext);
			aRow.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
			aRow.setGravity(17);
			ButtonInBox aBt=new ButtonInBox(getActivity(),"OK", false);
			mOK=aBt.getButton();
			//setOvalAttr(aBt, "ok", false);
			aBt.getButton().setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					saveData();
				}
			});
			aRow.addView(aBt);
			aBt=new ButtonInBox(getActivity(),"0", false);
		LinearLayout.LayoutParams aParams=new LinearLayout.LayoutParams(40, 40, 0.5f);
		aParams.setMargins(2, 3, 2, 3);
		aBt.setLayoutParams(aParams);
			//setOvalAttr(aBt, "0", false);
			aBt.getButton().setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v)
				{
					String s0="0";
					if (mRow==1) 
					{
						myCodes[mColumn % 4]='0';
						if (!newUser) s0="*";
					}
					if (mCurrentText != null)
						mCurrentText.setText(s0);						
					getDigits();
				}
			});
			aRow.addView(aBt);
			aBt=new ButtonInBox(getActivity(),"<-", false);
			//setOvalAttr(aBt, "<-", false);
			aBt.getButton().setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v)
				{
					int myPos=mColumn+mRow*mTotalColumn;
					myPos--;
					if (myPos < 0) myPos=0;
					mColumn=myPos % mTotalColumn;
					mRow=myPos / mTotalColumn;
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
		if (!newUser)
		{
			for (int i=0; i<4; i++)
			{
				if (myCodes[i] > '9' || myCodes[i] < '0') myCodes[i]='0';
			}
			String cc=new String (myCodes);
			int ic=Integer.parseInt(cc);
			int is = Integer.parseInt(mSavedCode);
			int ib = Integer.parseInt(mBkpCode);
			if (ic != is && ic!= ib  )
				mActivity.setTitle(getResources().getString(R.string.wrong_pin));//"Wrong password, try again";
			else
			{
				//MainActivity.setNewParameter("PASSWORD", cc);
				//((LoginActivity)mActivity).done();
			}
			return;
		}
		String id=mAlpha.getText().toString();
		for (int i=0; i<9; i++)
		{
			id += mIdDigits[i].getText().toString();
		}
		String iw="";
		for (int i=0; i<4; i++)
		{
			String a=mPasswords[i].getText().toString();
			iw += a;
		}
		for (int i=0; i<4; i++)
		{
			myCodes[i]=iw.charAt(i);
			if (myCodes[i] > '9' || myCodes[i] < '0') myCodes[i]='0';			
		}
		iw=new String(myCodes);

    	String fileName=getArguments().getString("PREFERENCE_FILE_NAME");
        SharedPreferences mSPF = getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSPF.edit();//prefs.edit();
		String pwd=getResources().getString(R.string.sim_setting);
		editor.putString(pwd, id);
		// pwd=getResources().getString(R.string.pin_setting);
        //editor.putString(pwd, iw);
		editor.commit();
		getActivity().getFragmentManager().beginTransaction().remove(this).commit();
		getActivity().getFragmentManager().popBackStackImmediate();
	}
	

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	 {
		 mActivity=getActivity();
		 mContext=mActivity;//.getApplicationContext();
		 getMyScreenSize();
		 ScrollView sv=new ScrollView(mContext);
		 LinearLayout id=new LinearLayout(mContext);
		 	id.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
		 	id.setGravity(1);//center_horizontal
			 mColumn=0;
			 mRow=0;
			 String pwd=getResources().getString(R.string.label_get_pin);
			 mSavedCode=getArguments().getString(pwd);
			 mBkpCode=getArguments().getString(BKP_PASSWORD);
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
		 id.addView(idLabel());
		 id.addView(idLine());

		 //add key pad
		 	LinearLayout tbHome=new TableLayout(mContext);//LinearLayout(mContext);
		 	tbHome.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
		 	//tbHome.addView(passwordLine());
		 	tbHome.setGravity(17);
			// mTable=showNButton(3,a);
			// mTable=showNButton(3,'0');
		 	//tbHome.addView(mTable);
		 	//id.addView(tbHome);
		 id.addView(showNButton(3,'0'));
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
		 	sv.addView(id);
		 /*
		 if (mRow==1)
		 	mCurrentText=mPasswords[0];
		 	else
		 		mCurrentText=mAlpha;
			showFocusColor(mCurrentText);
		 	mRoot=tbHome;
		 	//mActivity.setTitle(pgTitle);
		 	*/
	        return sv;
	 }

	int mTotalRow;
	int mTotalColumn;
	boolean isLargeScreen;
	boolean isLandScape;
	private int mDisplayWidth;
	private int mDisplayHeight;
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
