package com.prod.intelligent7.engineautostart;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class StopEngineFragment extends MySimpleFragment {
	public static final String BKP_PASSWORD="BKP_PASSWORD";

	public StopEngineFragment() {
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

	View mRootView;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	 {
		 mRootView=inflater.inflate(R.layout.stop_engine_layout, container, false);


		 return mRootView;
	 }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		if (mRootView==null) return;
		Button v=(Button)mRootView.findViewById(R.id.stop_engine_button);
		v.requestFocus();

	}


}
