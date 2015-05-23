package com.cable.dctvcloud.demo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentActivity;
import android.app.Fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Vector;

import android.database.Cursor;
import android.widget.Toast;


public class MainActivity extends FragmentActivity
       {

    static Activity applicationActivity=null;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private SelectPage mSelectPage;

    /**
     *
     */
    private CharSequence mTitle;
    boolean fromSavedInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applicationActivity=this;
        super.onCreate(savedInstanceState);
        fromSavedInstance = (savedInstanceState != null);
        //for test only
        setContentView(R.layout.activity_main);//activity_main);
        if (!fromSavedInstance) {

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new PlaceholderFragment()).commit();

        }
        //testing
        int mGridElementSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        int mGridElementSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        Point size=Utils.getScreenSize(this);

        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DisplayMetrics metricsMe=new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().
        metricsMe=getResources().getDisplayMetrics();
        int wPx=metrics.widthPixels;

    }

    //private final Vector<String>
    private class AsyncSoapConnect extends AsyncTask<SOAPClient, Void, String> {
        SOAPClient mClient;

        protected String doInBackground(SOAPClient... aClient) {
            mClient = aClient[0];
            mClient.sendRequest();
            return mClient.getResponse();
        }

        protected void onPostExecution(String jResponse) {

        }
    }

           public final static String FILE_NAME_TAG="FILE_NAME";
    final static String TESTKEY = "e0VS5s2nxxyXxRy7y5wFfGEWwTTZYLPWig2Ul5DJc/lKFcKZDBPibA==";
    final static String USRID = "C6153873-13CD-4E1D-8B9A-47374DC8393F";
    final static String TESTSN = "B3A4D01A-4270-4D0F-A710-1311DECFFE4D";
    public final static int SPLIT_SIZE = 200 * 1024;
    final static int FILL_THUMB = 1;
    Uri fileUri = null;
    String mMethodName;
    int mOption;

    public void  showImageGrids()
    {
        Intent pIntent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(pIntent, FILL_THUMB);
    }

    static void addFileToAdapter(FileDirectory aFD)
    {
        if (aFD.nodeType=='F')
        {
            GridElementAdapter.updateAdapterSource(new GridElementAdapter.GridElementInfo(aFD.name, aFD.fileClass, aFD.fileId, aFD.fileSize, aFD.fileUrl, "to_read_from_server"));
            return;

        }
        ArrayList<FileDirectory> child=aFD.children;
        for (int i=0; i<child.size(); i++)
            addFileToAdapter(child.get(i));
    }


    class JobDoneListener implements AsyncBuildFileList.FetchDirectoryInfoDoneListener
    {
        public void doneWithLoading(FileDirectory rootDir)
        {
            if (rootDir.children.size()<1) return;
            GridElementAdapter.cleanUpSource();
            addFileToAdapter(rootDir);
            showImageGrids();
        }

    }

    public void getDirectoryTree(View v)
    {
        String fileId = "c8d1329d-8c5a-40b3-926b-e4832215c37e";
        AsyncBuildFileList aThread = new AsyncBuildFileList(this, TESTKEY, USRID, "GT-P3100", TESTSN);

        aThread.addJobDoneListener(new JobDoneListener());

        aThread.start();

        //showImageGrids();
        //mMethodName="getDirectoryTree";
        //startSoapClient();
       // Intent pIntent = new Intent(this, ImageGridActivity.class);
        //pIntent.putExtra(CITIZEN_ID, mCitizenId);
        //pIntent.putExtra(PAGE_TITLE, mPageTitles[1]);
        //pIntent.putExtra(mFixKey, fixMsg);
       // startActivityForResult(pIntent, FILL_THUMB);
        //return;
    }

    public void uploadImageFile(View v) {
        mFileClass="Pictures";//"Images";
        pickFileToSend("image/*");
        return;
    }

    public void uploadVideoFile(View v) {
        mFileClass="Videos";
        pickFileToSend("video/*");
        return;
    }

   public void downloadFile(View v) {
        String fileId = "c8d1329d-8c5a-40b3-926b-e4832215c37e";
        AsyncDownloadFile dThread = new AsyncDownloadFile(this, TESTKEY, USRID, "GT-P3100", TESTSN, fileId);
        //dThread.start();
    }

    //@Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        String fileId;
        mOption = position;
        mMethodName = null;
        String title = "";
        String msg = "";


        //aClient.sendRequest();
        PlaceholderFragment nFragment = PlaceholderFragment.newInstance(position + 1);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, nFragment)
                .commit();
        if (title.length() > 1 && msg.length() > 1)
            showProgress(title, msg);
    }

    static ProgressDialog mShowProgress = null;

    void showProgress(String title, String msg) {


        //mShowProgress.show(this, title, msg);

    }

    public void stopProgressDialog() {
        if (mShowProgress != null) mShowProgress.cancel();
    }

    static final int REQ_PICK_IMAGE = 1;
    FileDescriptor mFd = null;
    long mFileSize;
    String mFileName = null;
    String mFileClass = null;

    private void pickFileToSend(String fileType) {
        mFileSize = 0;
        mFileName = null;
       Intent pickIntent = new Intent();
        pickIntent.setType(fileType);
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select  A File To Upload"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[]{takePhotoIntent}
                );
        //updatingPhoto=true;
        startActivityForResult(chooserIntent, REQ_PICK_IMAGE);

    }

    final static int WAIT_FOR_DIRECTORY_RESULT = 1;
    final static int WAIT_FOR_UPLOAD_RESULT = 2;
    final static int WAIT_FOR_DOWNLOAD_RESULT = 3;

    private void startSoapClient() {
        boolean wait2SendFile = false;
        SOAPClient aClient = null;
        aClient = new SOAPClient(mMethodName);
        String key = TESTKEY;
        String usrId = USRID;
        String phoneSN = TESTSN;
        String jsonDataString = null;
        switch (mOption) {
            case 1:
                jsonDataString = new MyJson().getDirectoryRequestJason(TESTKEY, USRID, MyJson.TESTPHONE, TESTSN);
                aClient.setJsonDataString(jsonDataString);
                break;
            case 2:

                break;
            case 3:
                //aClient.setJsonDataString(new MyJson().buildJsonToUpLoadRequest(key, usrId, phoneSN,
                // fileBase, mFileSize,ext, MyJson.SPLIT_SIZE, "Video", ""));

                break;
            default:
                break;

        }
        String toastMsg = "Sending Msg to Server :  " + jsonDataString;
        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
        /*
        aClient.setActivity(this);
        AsyncSoapConnect aTask=new AsyncSoapConnect();
        aTask.execute(aClient);
        if (wait2SendFile)
        {
            AsyncUploadFile aThread=new AsyncUploadFile(aTask, this, "fakeid", fileUri, mFileSize);
            aThread.start();
        }
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //String photo_file_path;
        Uri photo_uri = null;
        if (data != null) photo_uri = data.getData();

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_IMAGE && photo_uri != null) {
            // try {
            fileUri = photo_uri;
            String path=fileUri.getPath();
            String sPath=fileUri.getEncodedPath();
            Cursor returnCursor =
                    getContentResolver().query(fileUri, null, null, null, null);
    /*
     * Get the column indexes of the data in the Cursor,
     * move to the first row in the Cursor, get the data,
     * and display it.
     */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            mFileName = returnCursor.getString(nameIndex);
            mFileSize = returnCursor.getLong(sizeIndex);
             AsyncUploadFile uThread = new AsyncUploadFile(this, fileUri, mFileName, mFileClass, mFileSize);

            AsyncUpload aThread=new AsyncUpload();
            aThread.execute(uThread);
            //uThread.startToSend();
            showProgress("sending " + mFileName, "total " + mFileSize + " to server");
            String toastMsg = "picked file " + mFileName + " has size : " + mFileSize;
            //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            //startSoapClient();
/*
                InputStream mCurrentInput = getContentResolver().openInputStream(fileUri);
                if (mCurrentInput != null) {
                    mFileSize=mCurrentInput.available();
                    mFd = ((FileInputStream) mCurrentInput).getFD();
                    mCurrentInput.close();
                }
                */
            // }
            //catch (FileNotFoundException e){}
            // catch (IOException e){}
        }
    }

           private class AsyncUpload extends AsyncTask<AsyncUploadFile, Void, Void>
           {

               @Override
               public Void doInBackground(AsyncUploadFile... jobs)
               {
                  if (jobs.length > 0) jobs[0].startToSend();
                   return null;
               }

           }

    public void onSectionAttached(int number) {
        switch (number + 1) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mSelectPage.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
           public static boolean deleteDir(File dir) {
               if (dir != null && dir.isDirectory()) {
                   String[] children = dir.list();
                   for (int i = 0; i < children.length; i++) {
                       boolean success = deleteDir(new File(dir, children[i]));
                       if (!success) {
                           return false;
                       }
                   }
               }
               return dir.delete();
           }
           @Override
           protected void onDestroy()
           {
               ActivityManager aMgr=(ActivityManager)getSystemService(ACTIVITY_SERVICE);

               //boolean successful=aMgr.clearApplicationUserData();
               //if (successful)

               {
                   try {
                       File dir = getCacheDir();
                       if (dir != null && dir.isDirectory()) {
                           deleteDir(dir);
                       }
                   } catch (Exception e) {}
               }
               super.onDestroy();
           }


           /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.first_page, container, false);
            return rootView;
        }
    }
}





