package com.cable.dctvcloud.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by erickou on 2015/4/28.
 */
public class AsyncDownloadFile //extends Thread
{
//need to know the which file (local uri and remote ID, to send
    //what the id from server
//buildJsonUpLoadingRequest(String fileID,  int splitSize,  int splitIndex,  Bitmap icon)

    public interface FinishDownloadListener
    {
        public void downloadJobDone(String fileUri);
    }
    Vector< FinishDownloadListener> listenerPool;

    public void addJobDoneListener(FinishDownloadListener aListener) {
        if (listenerPool==null) listenerPool=new Vector < FinishDownloadListener>();
        listenerPool.add(aListener);

    }

    void informListener(String fileUri)
    {
        if (listenerPool.size()>0) {
            for (int i = 0; i < listenerPool.size(); i++) {
                FinishDownloadListener aListener = listenerPool.get(i);
                aListener.downloadJobDone(fileUri);
            }
        }
    }


    Activity mActivity;
    Uri fileUri;
    long mFileSize;
    final static String METHOD = "downloadFile";
    final static String START_METHOD = "beginDownloadFile";
    SOAPClient sender;
    String mFileId;
    static String mKEY = null;
    static String mUSERID = null;
    static String mPHONEDEVICE = null;
    static String mSN = null;
    //AsyncTask<SOAPClient, Void, String> waitForThis;

//read data from sharePreference : key,   userid,  phonedevice, phoneSN

    public void sendFileId(String fileId) {
        mFileId = fileId;
    }

    public AsyncDownloadFile() {
    }

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

    public AsyncDownloadFile(Activity aa, String fileId) {

        //waitForThis=forThis;
        mActivity = aa;
        mFileId = fileId;
        init();
    }

    public AsyncDownloadFile(Activity aa, String key, String usrId, String device, String sn, String fileId) {
        mActivity=aa;
        mKEY = key;
        mUSERID = usrId;
        mPHONEDEVICE = device;
        mSN = sn;
        mFileId = fileId;
    }
    public void setActivity(Activity aa) {
        mActivity = aa;
        init();
    }

    void init() {
         if(mActivity!=null) getUserProfile();
    }
    public void setUri(Uri uri)
    {
        fileUri=uri;
    }
    public void setSize(long sz)
    {
        mFileSize=sz;
    }

    static String onFile="";
    public String startDownload()
    {
        String retString=null;
        sender=new SOAPClient(START_METHOD);
        MyJson aJson=new MyJson();
        String jData = aJson.buildJsonToDownLoadRequest(mKEY, mUSERID, mPHONEDEVICE, mSN, mFileId);
        sender.setJsonDataString(jData);
        sender.setActivity(mActivity);
        sender.sendRequest();

        String jResp=sender.getResponse();
        if (jResp==null || jResp.indexOf("\"0\"") < 0) return null;

        aJson=new MyJson(jResp);
        String bkID=aJson.scanNameForData("ID");
        String fileName=aJson.scanNameForData("Name");
       String fileExt=aJson.scanNameForData("Extension");
        int idx=fileName.indexOf('.');
        if (idx>0)
        {
               onFile=fileName.substring(0, idx);
            fileName=onFile;

        }
        String fileSize=aJson.scanNameForData("Size");
        String totalFiles=aJson.scanNameForData("PackageNumber");
        int total2Receive= Integer.parseInt(totalFiles);
        String fileType=aJson.scanNameForData("Class");
//start to receive
        FileOutputStream outF;
        int fileLength=0;
    try {
        outF = mActivity.openFileOutput(fileName+"."+fileExt, Context.MODE_WORLD_READABLE);
        // mActivity.getContentResolver().openInputStream(fileUri));
        if (outF != null) {

            boolean resend = false;
            int iRead = 0;
            int iTry = 0;
            for (int i = 0; i < total2Receive; i++) {
                jData = "{\"Changingtec\":{\"File\":{\"ID\":\"" + mFileId + "\", \"PackageIndex\":\"" + i + "\" }}}";
                //=new MyJson().buildJsonUpLoadingRequest(mFileId, iRead, i, dataString);

                sender = new SOAPClient(METHOD);
                sender.setJsonDataString(jData);
                sender.setActivity(mActivity);
                sender.sendRequest();
                resend = false;
                jResp = sender.getResponse();
                aJson = new MyJson(jResp);
                String whichFile = aJson.scanNameForData("PackageIndex");
                if (sender.getResponse().indexOf("\"0\"") < 0 ||
                        Integer.parseInt(whichFile) != i) {
                    i--;
                    resend = true;
                    iTry++;
                    if (iTry > 5) break;
                    continue;
                }
                bkID = aJson.scanNameForData("ID");
                //fileName=aJson.scanNameForData("Name");
                fileSize = aJson.scanNameForData("Size");
                //fileExt=aJson.scanNameForData("Extension");

                String fileData = aJson.scanNameForData("Serialize");
                if (fileData != null) {
                    byte[] dataByte = Base64.decode(fileData.getBytes(), Base64.DEFAULT);
                    if (dataByte.length != Integer.parseInt(fileSize)) {
                        String msg = " Got " + dataByte.length + " expected " + fileSize;
                        Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
                        i--;
                        resend = true;
                        iTry++;
                        if (iTry > 5) break;
                        continue;
                    }

                    iTry = 0;
                    outF.write(dataByte, 0, dataByte.length);
                    fileLength += dataByte.length;
                }
            }

            outF.close();

            if (iTry > 0) {
                Log.d(TAG_FAIL, "open BAD socket stream  "+onFile);
                return null;
            }

                sender = new SOAPClient("completeDownloadFile");

                jData = "{\"Changingtec\":{\"File\":{\"ID\":\"" + mFileId + "\"}}}";

                sender.setJsonDataString(jData);
                sender.setActivity(mActivity);
                sender.sendRequest();
        File showFile=new File(mActivity.getFilesDir(), fileName+"."+fileExt) ;
                String msg="download file : "+fileName+" OK and "+fileLength+" saved to "+showFile.toString();

           // informListener;
                    retString =(fileName+"."+fileExt);
            Log.d(TAG_DONE, msg);

        }
    }catch(FileNotFoundException e){
        Log.d(TAG_FAIL, "File Not Found  "+onFile);
    } catch (IOException e){
        Log.d(TAG_FAIL, "open stream failed  "+onFile);
    }

        return retString;
    }




final static String TAG_DONE="JOB_DONE";
final static String TAG_FAIL="!! FAILED !!";


public void run1()
    {

        startDownload();
        //((MainActivity)mActivity).stopProgressDialog();
    }
}
