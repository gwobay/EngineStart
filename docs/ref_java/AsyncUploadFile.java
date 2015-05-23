package com.cable.dctvcloud.demo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by erickou on 2015/4/28.
 */
public class AsyncUploadFile //extends Thread
{
//need to know the which file (local uri and remote ID, to send
    //what the id from server
//buildJsonUpLoadingRequest(String fileID,  int splitSize,  int splitIndex,  Bitmap icon)

    Activity mActivity;
    Uri fileUri;
    long mFileSize;
    final static String REQUEST_METHOD="beginUploadFile";
    final static String METHOD="uploadFile";
    String mFileId;
    String mCategory;
    String mFileName;
    static String mKEY = null;
    static String mUSERID = null;
    static String mPHONEDEVICE = null;
    static String mSN = null;

    String mFileType;
    //AsyncTask<SOAPClient, Void, String> waitForThis;
    final static String TESTKEY = "e0VS5s2nxxyXxRy7y5wFfGEWwTTZYLPWig2Ul5DJc/lKFcKZDBPibA==";
    final static String USRID = "C6153873-13CD-4E1D-8B9A-47374DC8393F";
    final static String TESTSN = "B3A4D01A-4270-4D0F-A710-1311DECFFE4D";

    void getUserProfile() {
        SharedPreferences sharedPref = mActivity.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);//getSharedPreferences(fileName, Context.MODE_PRIVATE);

        mUSERID = sharedPref.getString("registration_id", "--");//REGISTRATION_ID", "--");
        if (mUSERID==null ||mUSERID.charAt(0)=='-') mUSERID=USRID;
        mKEY = sharedPref.getString("registration_key", "--");
        if (mKEY==null||mKEY.charAt(0)=='-') mKEY=TESTKEY;
        mPHONEDEVICE = sharedPref.getString("registration_device", "--");
        if (mPHONEDEVICE==null||mPHONEDEVICE.charAt(0)=='-') mPHONEDEVICE="GT-P3100";
        mSN = sharedPref.getString("registration_SN", "--");
        if (mSN==null||mSN.charAt(0)=='-') mSN=TESTSN;

    }
    public void setFileId(String fileId)
    {
        mFileId=fileId;
    }
    public AsyncUploadFile(){
    }

    public AsyncUploadFile(Activity aa, Uri uri, String fileName, String fileType, long sz){

        mActivity=aa;fileUri=uri;mFileName=fileName;
        mFileType=fileType; mFileSize=sz;

    }

    public void setActivity(Activity aa)
    {
        mActivity=aa;
    }
    public void setUri(Uri uri)
    {
        fileUri=uri;
    }
    public void setSize(long sz)
    {
        mFileSize=sz;
    }
    SOAPClient sender;
    String rejectCode="";
    void startToSend()
    {
SOAPClient sender=new SOAPClient();
        sender=new SOAPClient(REQUEST_METHOD);
        MyJson aJson=new MyJson();

        String ext="";
        int iDot=mFileName.indexOf('.');
        String fileBase=null;
        if (iDot>0)
        {
            fileBase=mFileName.substring(0, iDot);
            ext=mFileName.substring(iDot+1);
        }
        if (mKEY==null || mUSERID==null) getUserProfile();
        String jData = aJson.buildJsonToUpLoadRequest(mKEY, mUSERID, mPHONEDEVICE, mSN,
                fileBase, mFileSize, ext, (int)(mFileSize/MyJson.SPLIT_SIZE)+1, mFileType, "");

        sender.setJsonDataString(jData);
        sender.setActivity(mActivity);
        sender.sendRequest();

        String jResp=sender.getResponse();
        if (jResp.indexOf("\"0\"") < 0)
        {
            Log.d(TAG_FAIL, "failed in uploading "+mFileName);
            return;
        }
    try {
        BufferedInputStream mCurrentInput = new BufferedInputStream(mActivity.getContentResolver().openInputStream(fileUri));
        File myDir=mActivity.getFilesDir();
        if (mCurrentInput != null) {

            int total2Send=(int)mFileSize/MyJson.SPLIT_SIZE+1;

            boolean resend=false;
            int iRead=0;
            int iTry=0;
            for (int i=0; i<total2Send; i++)
            {
                byte[] bSend=new byte[MyJson.SPLIT_SIZE+1];
                if (!resend)
                iRead=mCurrentInput.read(bSend, 0, MyJson.SPLIT_SIZE);
                if (iRead < 1) break;
                String dataString= Base64.encodeToString(bSend, Base64.DEFAULT);
                jData=new MyJson().buildJsonUpLoadingRequest(mFileId, iRead, i, dataString);
                if (i==total2Send-1){
                int k=i; //debug
                }
                sender=new SOAPClient(METHOD);
                sender.setJsonDataString(jData);
                sender.setActivity(mActivity);
                sender.sendRequest();
                resend=false;
                if (sender.getResponse().indexOf("\"0\"")<0)
                {
                    i--; resend=true;
                    iTry++;
                    if (iTry > 5) break;
                } else iTry=0;
            }
            mCurrentInput.close();

            if (iTry > 3) {
                sender = new SOAPClient("rejectFile");
            }
            else {
                sender = new SOAPClient("completeUploadFile");
            }
            jData = "{\"Changingtec\":{\"File\":{\"ID\":\"" + mFileId + "\"}}}";

            sender.setJsonDataString(jData);
            sender.setActivity(mActivity);
            sender.sendRequest();
            if (iTry > 3) Log.d(TAG_FAIL, "fail to upload "+mFileName);
            else Log.d(TAG_DONE, "finish uploading file "+mFileName);
        }
        }catch(FileNotFoundException e){
            Log.d(TAG_FAIL, "Wrong File Name "+mFileName);
        } catch (IOException e){
            Log.d(TAG_FAIL, "open stream failed  "+mFileName);
        }
    }
    final static String TAG_DONE="JOB_DONE";
    final static String TAG_FAIL="!! FAILED !!";

    public void run1()
    {
        if (fileUri != null && fileUri.getPath().length() > 1)
            startToSend();
        ((MainActivity)mActivity).stopProgressDialog();
    }
}
