
/**
 * Created by erickou on 2015/4/28.
 */

package com.cable.dctvcloud.demo;

        import android.app.Activity;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.net.Uri;

        import java.util.ArrayList;
        import java.util.Vector;

/**
 * Created by erickou on 2015/4/28.
 */
public class AsyncBuildFileList extends Thread {
//need to know the which file (local uri and remote ID, to send
    //what the id from server
//buildJsonUpLoadingRequest(String fileID,  int splitSize,  int splitIndex,  Bitmap icon)

    Activity mActivity;
    Uri fileUri;
    long mFileSize;
    final static String METHOD = "getDirectoryTree";
    final static String START_METHOD = "beginDownloadFile";
    SOAPClient sender;
    String mFileId;
    static String mKEY = null;
    static String mUSERID = null;
    static String mPHONEDEVICE = null;
    static String mSN = null;

    public interface FetchDirectoryInfoDoneListener
    {
        public void doneWithLoading(FileDirectory rootDir);
    }
    Vector < FetchDirectoryInfoDoneListener> listenerPool;

    public void addJobDoneListener(FetchDirectoryInfoDoneListener aListener) {
        if (listenerPool==null) listenerPool=new Vector < FetchDirectoryInfoDoneListener>();
        listenerPool.add(aListener);

    }

    public void sendFileId(String fileId) {
        mFileId = fileId;
    }

    public AsyncBuildFileList() {

    }


    void getUserProfile() {
        SharedPreferences sharedPref = mActivity.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);//getSharedPreferences(fileName, Context.MODE_PRIVATE);
        mUSERID = sharedPref.getString("registration_id", "--");//REGISTRATION_ID", "--");
        mKEY = sharedPref.getString("registration_key", "--");
        mPHONEDEVICE = sharedPref.getString("registration_device", "--");
        mSN = sharedPref.getString("registration_SN", "--");

    }

    public AsyncBuildFileList(Activity aa, String fileId) {

        //waitForThis=forThis;
        mActivity = aa;
        mFileId = fileId;
    }

    public AsyncBuildFileList(Activity aa, String key, String usrId, String device, String sn) {
        mActivity = aa;
        mKEY = key;
        mUSERID = usrId;
        mPHONEDEVICE = device;
        mSN = sn;
        mFileId = "";
    }

    public void setActivity(Activity aa) {
        mActivity = aa;
        init();
    }

    final static String TESTKEY = "e0VS5s2nxxyXxRy7y5wFfGEWwTTZYLPWig2Ul5DJc/lKFcKZDBPibA==";
    final static String USRID = "C6153873-13CD-4E1D-8B9A-47374DC8393F";
    final static String TESTSN = "B3A4D01A-4270-4D0F-A710-1311DECFFE4D";

    void init() {
        //if(mActivity!=null) getUserProfile();

        boolean wait2SendFile = false;
        //SOAPClient sender=null;
        sender = new SOAPClient(METHOD);
        String key = TESTKEY;
        String usrId = USRID;
        String phoneSN = TESTSN;
        String jsonDataString = null;


    }

    public void setUri(Uri uri) {
        fileUri = uri;
    }

    public void setSize(long sz) {
        mFileSize = sz;
    }

    void startBuild() {
        //init();
        String jsonDataString = new MyJson().getDirectoryRequestJason(TESTKEY, USRID, MyJson.TESTPHONE, TESTSN);
        SOAPClient sender = new SOAPClient(METHOD);
        sender.setJsonDataString(jsonDataString);
        sender.setActivity(mActivity);
        sender.setJsonDataString(jsonDataString);
        sender.setActivity(mActivity);
        sender.sendRequest();

        String jResp = sender.getResponse();
        if (jResp == null) return;
        if (jResp.indexOf("\"0\"") < 0) return;

        MyJson aJson = new MyJson(jResp);

        FileDirectory.buildNewDirectory(jResp);

        FileDirectory rootDir=FileDirectory.getRoot();
        if (rootDir.children.size() > 0) {
                if (listenerPool.size()>0) {
                    for (int i = 0; i < listenerPool.size(); i++) {
                        FetchDirectoryInfoDoneListener aListener = listenerPool.get(i);
                        aListener.doneWithLoading(rootDir);
                    }
                }
        }
    }

    public void run() {

        startBuild();
        ((MainActivity) mActivity).stopProgressDialog();
    }
}
/*
            //for directory building purpose
    //
    private class Directory
    {
        String dirName; //Videos or Images
        Vector<ForThumbnail> files;
    }

    public class ForThumbnail
    {
        int img_video; //img=0; video=1;
        String ID;
        String fileName;
        String extension;
        String thumbNailUrl;
        ForThumbnail(int ivi, String id, String nm, String xn, String thmb){
            img_video=ivi;
            ID=id;
            fileName=nm;
            extension=xn;
            thumbNailUrl=thmb;
        }
        public String getThumb()
        {
            return thumbNailUrl;
        }
    }

    public static Vector<FileStruct> rootDir=new Vector<FileStruct>();

    void buidNewDirectory(String jString) {
        int i
        String[] dirs = jString.split("@Name"); //file type under root
        if (dirs.length < 2) return;
        rootDir.clear();

        Vector<FileStruct> aDir=rootDir;
        for (int i = 1; i < dirs.length; i++) {
            int iVd = dirs[i].indexOf("Videos");
            int iImg = dirs[i].indexOf("Images");
            if (iVd < 0 && iImg < 0) continue; //forget others
            String[] files = dirs[i].split("\"ID\"");

            if (files.length < 1) continue;
            aDir=allDir;

            for (int k = 1; k < files.length; k++) {
                MyJson aJson = new MyJson("ID" + files[k]);
                String fID=aJson.scanNameForData("ID");
                if (fID==null) continue;
                String fNm=aJson.scanNameForData("Name");
                String ext=aJson.scanNameForData("Extension");
                String tmb64=aJson.scanNameForData("URL");
                String thmb=null;
                if (tmb64 != null)
                {
                    thmb=MyJson.getBase64Msg(tmb64);
                }
                //if (aDir.files == null) aDir.files
                aDir.add(new ForThumbnail(iVd>0?1:0, fID, fNm, ext, thmb));
            }
        }
    }
    public static Vector<ForThumbnail> allDir=new Vector<ForThumbnail>();

    void buidDirectory(String jString) {
        String[] dirs = jString.split("@"); //file type under root
        if (dirs.length < 2) return;
        allDir.clear();

        Vector<ForThumbnail> aDir;
        for (int i = 1; i < dirs.length; i++) {
            int iVd = dirs[i].indexOf("Videos");
            int iImg = dirs[i].indexOf("Images");
            if (iVd < 0 && iImg < 0) continue; //forget others
            String[] files = dirs[i].split("\"ID\"");

            if (files.length < 1) continue;
            aDir=allDir;

            for (int k = 1; k < files.length; k++) {
                MyJson aJson = new MyJson("ID" + files[k]);
                String fID=aJson.scanNameForData("ID");
                if (fID==null) continue;
                String fNm=aJson.scanNameForData("Name");
                String ext=aJson.scanNameForData("Extension");
                String tmb64=aJson.scanNameForData("URL");
                String thmb=null;
                if (tmb64 != null)
                {
                    thmb=MyJson.getBase64Msg(tmb64);
                }
                //if (aDir.files == null) aDir.files
                aDir.add(new ForThumbnail(iVd>0?1:0, fID, fNm, ext, thmb));
            }
        }
    }

}*/



