package com.cable.dctvcloud.demo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.util.Calendar;

public class VideoViewUsingFiles extends Activity
        implements MediaPlayer.OnCompletionListener,
        AsyncDownloadFile.FinishDownloadListener
{

    public static final String SHOW_VIDEO_TAG="file id for showing video";
	private VideoView myVideoView;
	private int position = 0;
	private static ProgressDialog progressDialog=null;
	private MediaController mediaControls;
    private MediaPlayer mPlayer;
    String mFileName="";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mActivity=this;
        if (savedInstanceState != null)
            finish();
		// Get the layout from video_main.xml
        String fileId=getIntent().getStringExtra(SHOW_VIDEO_TAG);
        mFileName=getIntent().getStringExtra(MainActivity.FILE_NAME_TAG);
        if (mFileName==null) mFileName=" ";
        loadVideoFile(fileId);
        getMyScreenSize();
		setContentView(R.layout.activity_video);
        if (progressDialog==null) {
            // Create a progressbar
            progressDialog = new ProgressDialog(VideoViewUsingFiles.this);
            // Set progressbar title
            progressDialog.setTitle("cable.dctvcloud");
            // Set progressbar message
            progressDialog.setMessage("Loading..."+mFileName);

            progressDialog.setCancelable(true);
            // Show progressbar
            progressDialog.show();
        }

        //startVideo();
        VideoBuildingTask aTask=new VideoBuildingTask();

        Log.d("GETTING DATA", "start to at  "+ Calendar.getInstance().getTime());
        aTask.execute(fileId);
    }

    private int mDisplayWidth;
    private int mDisplayHeight;
    void getMyScreenSize()
    {
        //Display display = getWindowManager().getDefaultDisplay();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mDisplayWidth = size.x*9/10;
        mDisplayHeight = size.y*9/10;
        //If you're not in an Activity you can get the default Display via WINDOW_SERVICE:
    }


    void startVideo(Uri fileUri)
    {
// Find your VideoView in your video_main.xml layout
        myVideoView = (VideoView) findViewById(R.id.video_view);


        if (mediaControls == null) {
			mediaControls = new MediaController(VideoViewUsingFiles.this);
		}
        mediaControls.setAnchorView(myVideoView);

        //mPlayer = new MediaPlayer().create(this, R.raw.kp_grateful);

        Log.d("SHOWING VIDEO", "prepare to play at "+ Calendar.getInstance().getTime());


        try {
			myVideoView.setMediaController(mediaControls);
            //Uri vUri=Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.kp_grateful);//"https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");
			myVideoView.setVideoURI(fileUri);//vUri);//Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.testfile));

		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
            if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog.cancel();}

		}

		myVideoView.requestFocus();

		myVideoView.setOnPreparedListener(new OnPreparedListener() {
			// Close the progress bar and play the video
			public void onPrepared(MediaPlayer mp) {
				progressDialog.dismiss();
                Log.d("SHOWING VIDEO", "now play at "+ Calendar.getInstance().getTime());
				myVideoView.seekTo(position);
				if (position == 0) {
					myVideoView.start();
				} else {
					myVideoView.pause();
				}
			}
		});

        myVideoView.setOnCompletionListener(this);

    }
Activity mActivity;
    private class VideoBuildingTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected  String doInBackground(String ... fileId) {
            String id = fileId[0];
            AsyncDownloadFile aDownload = new AsyncDownloadFile(mActivity, id);
            String savedIn = aDownload.startDownload();
            if (savedIn == null)
                return null;
            return savedIn;
        }

        @Override
        protected  void onPostExecute(String savedIn)
        {
            if (savedIn == null || savedIn.length() < 2) return;
            Uri fileUri=Uri.fromFile(new File(getFilesDir().getAbsolutePath()+"/"+savedIn));

            if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog.cancel();}
            startVideo(fileUri);

        }
    }


    public void downloadJobDone(String fileName)
    {
        progressDialog.dismiss();
        progressDialog.cancel();
        Uri fileUri=Uri.fromFile(new File(getFilesDir().getAbsolutePath()+"/"+fileName));
        startVideo(fileUri);
    }
    void loadVideoFile(String fileId)
    {
        AsyncDownloadFile aDownload=new AsyncDownloadFile(this, fileId);
        aDownload.addJobDoneListener(this);
        //aDownload.start();
    }
    public void onCompletion(MediaPlayer mp) {

            myVideoView.stopPlayback();
            finish();


        }

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
		myVideoView.pause();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		position = savedInstanceState.getInt("Position");
		myVideoView.seekTo(position);
	}
    int lastPosition;
    public void toPause(View v)
    {
        myVideoView.pause();
        lastPosition=myVideoView.getCurrentPosition();
    }

    public void toContinue(View v)
    {
        myVideoView.seekTo(lastPosition);
    }

    public void toWind(View v)
    {
        myVideoView.seekTo(0);
    }
    public void toCancel(View v)
    {
        myVideoView.stopPlayback();
        finish();
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
        /* only clean in Main activity
        if (progressDialog != null){
            progressDialog.dismiss();progressDialog.cancel();
        }
        {
            try {
                File dir = getCacheDir();
                if (dir != null && dir.isDirectory()) {
                    deleteDir(dir);
                }
            } catch (Exception e) {}
        }*/
        super.onStop();
    }
}
