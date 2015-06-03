package com.prod.intelligent7.engineautostart;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReadPhoneFragment extends MySimpleFragment {
	public static final String BKP_PASSWORD="BKP_PASSWORD";

	public ReadPhoneFragment() {
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
	TextView mBlinking;
	int iBlinking;

	void showFocusColor(TextView v)
	{
		//showTheRestNormal();
		//v.setBackgroundColor(0xf500ff00);
		showBlinking(v);
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
			//showFocusColor(mIdDigits[mColumn+mRow*mTotalColumn]);
			mCurrentText=mIdDigits[mColumn+mRow*mTotalColumn];
			readDigitForNewCell();
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
			readDigitForNewCell();
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

			LayoutParams aParams=new LayoutParams(txtSize+10, txtSize+10, 0.5f);
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
	int mTextBackground;
	LinearLayout idLine()
	{
		LinearLayout gID=new LinearLayout(mContext);
		gID.setLayoutParams(mwParams);
		gID.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
		gID.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		int txtSize=30;
		if (isLargeScreen) txtSize=40;
	 	//TextView idLabel=new TextView(mContext);
		//idLabel.setText("  ");
		//idLabel.setTextSize(txtSize);
		//gID.addView(idLabel);

		
		mIdDigits=new TextView[20];
		for (int k=0; k<mTotalRow; k++) {
			LinearLayout idRow=new LinearLayout(mContext);
			LinearLayout.LayoutParams rowParam=mwParams;
			rowParam.setMargins(5, 5, 5, 5);

			idRow.setLayoutParams(rowParam);

			idRow.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
			idRow.setGravity(1);
			TextView whichPwd=new TextView(mContext);
			whichPwd.setLayoutParams(new LinearLayout.LayoutParams(mDisplayWidth /6, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
			whichPwd.setGravity(Gravity.TOP|Gravity.LEFT);
			if (k==0) whichPwd.setText(getResources().getString(R.string.enter_old_pin));
			else if (k==1) whichPwd.setText(getResources().getString(R.string.enter_new_pin));
			else if (k==2) whichPwd.setText(getResources().getString(R.string.confirm_new_pin));
			final int which1=k;
			whichPwd.setOnClickListener(new LL(k, 0));//
			/*// setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mRow=which1;
					mColumn=0;
					mCurrentText=mIdDigits[mRow*mTotalColumn];
				}
			});*/
			whichPwd.setTextSize(txtSize* 2/ 3);
			idRow.addView(whichPwd);

			for (int i = 0; i < mTotalColumn; i++) {
				int j=i+k*mTotalColumn;
				mIdDigits[j] = new TextView(mContext);
				mIdDigits[j].setWidth(txtSize * 12 / 10);
				mIdDigits[j].setTextSize(txtSize);// * 9 / 10);
				mIdDigits[j].setText("X");
				mIdDigits[j].setGravity(Gravity.CENTER);
				int showColor=0;
				if (isLargeScreen)
				{
					mTextBackground=R.drawable.shape_rect_blue_large;
					showColor=R.drawable.shape_rect_orange_large;
				}
				else  {
					mTextBackground=R.drawable.shape_rect_blue;
					showColor=R.drawable.shape_rect_orange;
				}
				mIdDigits[j].setBackgroundResource(showColor);
				mIdDigits[j].setClickable(true);
				mIdDigits[j].setOnClickListener(new LL(k, i));
				mIdDigits[j].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
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


	void readDigitForNewCell()
	{
		int iPos=mColumn + mRow*mTotalColumn;
		if (iPos<0) {mRow=0; mColumn=0; iPos=0;}
		if (iPos < mTotalColumn*mTotalRow)
		{
			mCurrentText=mIdDigits[iPos];
			//showNormalColor(mCurrentText);
			showBlinking(mCurrentText);
		}
		else
		{
			mColumn=mTotalColumn-1;
			mRow=mTotalRow-1;
			mOK.setTextColor(Color.RED);
			//mOK.setBackgroundColor(Color.RED);
			iBlinking=99;
			showBlinking(mOK);
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
						mCurrentText.setBackgroundResource(mTextBackground);//transition);
						//((TransitionDrawable)mCurrentText.getBackground()).startTransition (300);
						stopBlinking(mCurrentText);
					}
					mColumn++;
					readDigitForNewCell();

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

		ButtonInBox aBt=new ButtonInBox(getActivity(),getActivity().getResources().getString(R.string.confirm_this), false);
		mOK=aBt.getButton();
		LinearLayout.LayoutParams okParams=new LinearLayout.LayoutParams(keyPadWidth/5,
													LinearLayout.LayoutParams.MATCH_PARENT, 0.1f);
		aBt.setLayoutParams(okParams);
		aBt.setGravity(Gravity.CENTER_VERTICAL);
		aBt.getButton().setTextColor(Color.WHITE);
		aBt.getButton().setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//if (v.getAnimation()!=null) v.getAnimation().cancel();
				//mOK.setBackgroundResource(R.drawable.shape_oval_green_large);
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
			nPart.addView(oneRow(c, 5, keyPadWidth/5, mDisplayHeight/6 ));
		}

		keyPad.addView(nPart);

		//add last column in number key pad

		aBt=new ButtonInBox(getActivity(),"<-", false);

		aBt.setLayoutParams(okParams);
		aBt.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		aBt.getButton().setTextColor(Color.MAGENTA);
		aBt.getButton().setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				stopBlinking(mCurrentText);
				mColumn--;
				if (mColumn < 0) {
					mColumn = mTotalColumn;
					mRow--;
					if (mRow < 0) {
						mRow = 0;
						mColumn = 0;
					}
				}
				int myPos = mColumn + mRow * mTotalColumn;
				mIdDigits[myPos].setText("_");
				readDigitForNewCell();
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
		kParams.setMargins(3, 3, 3, 3);
		keyPad.setLayoutParams(kParams);
		keyPad.setOrientation(LinearLayout.VERTICAL);//0HORIZONTAL, 1Vertical);
	 	keyPad.setGravity(Gravity.CENTER_HORIZONTAL);

		for (int i=0; i<3; i++)
			{
				int m=3;
				//if (a > '9') m=9;
				//char c=(char)(a+m*i);
				char c=(char)('0'+(m*i+1));
				keyPad.addView(oneRow(c, 3, keyPadWidth/4*15/10, keyPadWidth/4));
			}
		 //add last row in number key pad

			LinearLayout aRow=new LinearLayout(mContext);
		aRow.setLayoutParams(mwParams);
			aRow.setOrientation(LinearLayout.HORIZONTAL);//0HORIZONTAL, 1Vertical);
			aRow.setGravity(Gravity.FILL_HORIZONTAL);
			ButtonInBox aBt=new ButtonInBox(getActivity(),getActivity().getResources().getString(R.string.confirm_this), false);
			mOK=aBt.getButton();
		LinearLayout.LayoutParams aParams=new LinearLayout.LayoutParams(keyPadWidth/4, keyPadWidth/4, 0.5f);
		aParams.setMargins(2, 3, 2, 3);
		aBt.setLayoutParams(aParams);
			//setOvalAttr(aBt, "ok", false);
			aBt.getButton().setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					//if (v.getAnimation()!=null) v.getAnimation().cancel();
					//mOK.setBackgroundResource(R.drawable.shape_oval_green);
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
					if (mCurrentText != null) {
						mCurrentText.setText("0");
						stopBlinking(mCurrentText);
					}
					mColumn++;
					readDigitForNewCell();
				}
			});
			aRow.addView(aBt);
			aBt=new ButtonInBox(getActivity(),"<-", false);
			//setOvalAttr(aBt, "<-", false);
			aBt.setLayoutParams(aParams);
		aBt.getButton().setTextColor(Color.MAGENTA);
		aBt.getButton().setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				stopBlinking(mCurrentText);
				mColumn--;
				if (mColumn < 0) {
					mColumn = mTotalColumn;
					mRow--;
					if (mRow < 0) {
						mRow = 0;
						mColumn = 0;
					}
				}
				int myPos = mColumn + mRow * mTotalColumn;
				mIdDigits[myPos].setText("_");
				readDigitForNewCell();
			}
		});

			aRow.addView(aBt);
			keyPad.addView(aRow);			

		return keyPad;
	}

	LinearLayout indicateKeyBoard()
	{
		LinearLayout devider=new LinearLayout(mContext);
		LinearLayout.LayoutParams kParams=new LinearLayout.LayoutParams(keyPadWidth,
				LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
		devider.setLayoutParams(kParams);
		devider.setOrientation(LinearLayout.HORIZONTAL);
		TextView dashed=new TextView(mContext);
		dashed.setTextSize(2);
		dashed.setText("====================");
		dashed.setGravity(Gravity.RIGHT);
		//devider.addView(dashed);

		TextView desc=new TextView(mContext);
		desc.setTextSize(9);
		desc.setText(getActivity().getResources().getString(R.string.numeric_pad));
		desc.setGravity(Gravity.CENTER_HORIZONTAL);
		devider.addView(desc);

		TextView dashed1=new TextView(mContext);
		dashed1.setTextSize(2);
		dashed1.setText("====================");
		dashed1.setGravity(Gravity.LEFT);
		//devider.addView(dashed1);

		return devider;

	}

	@Override
	public void saveData()
	{
		String old1=MainActivity.GET_PHONE_OLD;//getResources().getString(R.string.old_pin);
		String oldPhone=((MainActivity)getActivity()).getSavedValue(old1);
		((MainActivity)getActivity()).setPreferenceValue(old1, oldPhone);
		String key1=MainActivity.SET_PHONE1;
		String phone1=((EditText) mRootView.findViewById(R.id.phone1)).getText().toString();
		String key2=MainActivity.SET_PHONE2;
		String phone2=((EditText) mRootView.findViewById(R.id.phone2)).getText().toString();

		((MainActivity)getActivity()).setPreferenceValue(key1, phone1);
		((MainActivity)getActivity()).setPreferenceValue(key2, phone2);

        backToMain();
	}

	View mRootView;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	 {
		 mRootView=inflater.inflate(R.layout.phone_entry_layout, container, false);


		 return mRootView;
	 }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		if (mRootView==null) return;
		EditText v=(EditText)mRootView.findViewById(R.id.phone1);
		v.requestFocus();

	}
		int szKeySize;
	int mTotalRow;
	int mTotalColumn;
	boolean isLargeScreen;
	boolean isLandScape;
	private int mDisplayWidth;
	private int mDisplayHeight;
	private int keyPadWidth;
	LinearLayout.LayoutParams keyPadParams;

	private int mLogBarHeight;

}
