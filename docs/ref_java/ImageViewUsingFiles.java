package com.cable.dctvcloud.demo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ImageView;

import java.io.File;
import java.util.Vector;

public class ImageViewUsingFiles extends FragmentActivity
        implements AsyncDownloadFile.FinishDownloadListener,
                    LoadBigImage.FinishLoadingListener

        {

    public static final String SHOW_IMAGE_TAG="file id for showing image";
    public static final String IMAGE_NAME_TAG="file name for this image";
    private ImageView myImageView;
	private int position = 0;
	private static ProgressDialog progressDialog=null;
	private MediaController mediaControls;
    private MediaPlayer mPlayer;
            Activity mActivity;
            private int mDisplayWidth;
            private int mDisplayHeight;
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            finish();
        getMyScreenSize();
		// Get the layout from image_main.xml
        String fileId=getIntent().getStringExtra(SHOW_IMAGE_TAG);
        String fileName=getIntent().getStringExtra(MainActivity.FILE_NAME_TAG);
        //getImageFile(fileId);
		setContentView(R.layout.activity_show_image);
        myImageView=(ImageView)findViewById(R.id.image_view);
        if (progressDialog==null) {
            // Create a progressbar
            progressDialog = new ProgressDialog(ImageViewUsingFiles.this);
            // Set progressbar title
            progressDialog.setTitle("cable.dctvcloud");
            // Set progressbar message
            progressDialog.setMessage("Loading..."+fileName);

            progressDialog.setCancelable(true);
            // Show progressbar
            progressDialog.show();
        }
        if (fileId!=null) {
            //startImage();
            setJobParameters();
            // fileId=getIntent().getStringExtra(SHOW_IMAGE_TAG);
            new ImageBuildingTask().execute(fileId);

        }
    }

            public void byeBye(View v)
            {
                super.finish();
                if (progressDialog != null){
                    progressDialog.dismiss();
                    progressDialog.cancel();
                }
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                NavUtils.navigateUpTo(this, upIntent);
                finish();

                //finish();

            }
    @Override
         public void onStop()
    {
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }
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
            public void onImageLoaded(ImageView v, Bitmap bmp)
            {
                //v.setImageBitmap(bmp);
                v.invalidate();
            }
static Vector<Bitmap> myInventory=new Vector<Bitmap>();
            void getPhotoBitmap(ImageView v,Uri fileUri)
            {
                Bitmap oMap=LoadBigImage.getCached(fileUri);
                if (oMap != null)
                {
                    v.setImageBitmap(oMap);
                    return;
                }

                LoadBigImage lg=LoadBigImage.getInstance((Activity)v.getContext(), myImageView);
                //lg.loadBitMap(v, photo_uri, mWidth, mHeight);
                if (lg!=null)
                {
                    lg.setOutBox(myInventory);
                    lg.addFinishLoadingListener(this);
                    lg.setImageUri(fileUri);
                    lg.setSize(mDisplayWidth, mDisplayHeight);

                    lg.start();
                }
            }

    void startFillImage(Uri fileUri)
    {
// Find your ImageView in your image_main.xml layout

        //myImageView.setImageURI(fileUri);
        getPhotoBitmap(myImageView, fileUri);
        //myImageView.invalidate();

    }
    public void downloadJobDone(String fileName)
    {
        progressDialog.dismiss();
        progressDialog.cancel();
        Uri fileUri=Uri.fromFile(new File(getFilesDir().getAbsolutePath()+"/"+fileName));
        startFillImage(fileUri);
    }
    void setJobParameters()
    {
        mActivity=this;
        myImageView = (ImageView) findViewById(R.id.image_view);

        //AsyncDownloadFile aDownload=new AsyncDownloadFile(this, fileId);
        //aDownload.addJobDoneListener(this);
        //aDownload.start();
    }


    private class ImageBuildingTask extends AsyncTask<String, Void, String>
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
                Bitmap retBitmap=null;
                Uri fileUri=Uri.fromFile(new File(getFilesDir().getAbsolutePath()+"/"+savedIn));

                LoadBigImage lg=LoadBigImage.getInstance(mActivity, myImageView);
                if (lg != null) {
                    retBitmap=lg.loadBitMap(myImageView, fileUri, mDisplayWidth, mDisplayHeight);
                    if (retBitmap == null)return;
                    myImageView.setImageBitmap(retBitmap);
                    myImageView.invalidate();
                }
                if (progressDialog == null)return;
                progressDialog.dismiss();
                progressDialog.cancel();
            }
        }



    int lastPosition;
    public void showNext(View v)
    {

    }

    public void showLast(View v)
    {

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
                    View rootView = inflater.inflate(R.layout.activity_show_image, container, false);
                    return rootView;
                }
            }
}
