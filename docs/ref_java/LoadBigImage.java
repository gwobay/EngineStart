package com.cable.dctvcloud.demo;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class LoadBigImage extends Thread {
	static Activity mActivity=null;
	static InputStream mCurrentInput=null;
	static FileDescriptor mFd=null;
	static Rect mOutPadding=null;
	static boolean useFd=true;
	static int instanceCount=0;
    static boolean mLoadFromResource=false;
	static ArrayList<FinishLoadingListener> listeners=new ArrayList<FinishLoadingListener>();
	static HashMap<String, Bitmap> cache=new HashMap<String, Bitmap>();
	Uri mUri;
    int mResourceId;
    int xDimension;
    int yDimension;
    static ImageView mViewport;
    Vector<Bitmap> outBox;
	public interface FinishLoadingListener
	{
		public void onImageLoaded(ImageView parent, Bitmap bmp);
	}
	
	public void addFinishLoadingListener(FinishLoadingListener aL)
	{
		listeners.add(aL);
	}
	private LoadBigImage() {
		// TODO Auto-generated constructor stub
		mUri=null;
	}

    static Bitmap[] preLoaded=new Bitmap[3];
	public static Bitmap getCached(Uri _uri)
	{
		return cache.get(_uri.toString());
	}
	public static LoadBigImage getInstance(Activity v, ImageView aV)
	{
		Log.d(LoadBigImage.class.getSimpleName(), "Got Called "+(instanceCount+1));
		if (instanceCount > 0) return null;
		instanceCount++;
		if (v!=null)
			mActivity=v;
        mViewport=aV;
		return new LoadBigImage();		
	}

    public void setOutBox(Vector<Bitmap> aV)
    {
        outBox=aV;
    }
	public void useFileDescriptor(boolean T_F)
	{
		useFd=T_F;
	}
	
	public static FileDescriptor getFd()
	{
		return mFd;
	}
	
	@Override
	protected void finalize()
	{
		instanceCount--;
		if (instanceCount < 0) instanceCount=0;
		Log.d(LoadBigImage.class.getSimpleName(), "!Got Called "+instanceCount);		
		try {
				if (mCurrentInput != null) 
				{
						mCurrentInput.close();
				} 
		super.finalize();
		} catch (Throwable e){}
	}

    public void setResourceId(int resId)
    {
        mLoadFromResource=true;
        mResourceId=resId;
    }

    public void setImageUri(Uri aUri)
    {
        mLoadFromResource=false;
        mUri=aUri;
    }

    public void setSize(int x, int y)
    {
        xDimension=x;
        yDimension=y;
    }

	public void loadBitMap(ImageView v, int resId)
	{

        Integer[] params=new Integer[]{resId, v.getWidth(), v.getHeight()};
        Bitmap bm=convertFileBitmap(params);

			if (bm!=null) {
                onPostExecute(bm);
				if (listeners.size() > 0)
				{
					listeners.get(0).onImageLoaded(v, bm);
				}
				//v.setImageBitmap(bm);
                //v.postInvalidate();              
			}

		 finalize();
	}

	public Bitmap loadBitMap(ImageView v, Uri imgUri, int reqWidth, int reqHeight)
	{
        mLoadFromResource=false;
		Bitmap bmp=cache.get(imgUri.toString());
		if (bmp != null)
		{
			if (listeners.size() > 0)
			{
				listeners.get(0).onImageLoaded(v, bmp);
			}
			else 
			{
				v.setImageBitmap(bmp);
				v.invalidate();
			}
			//finalize();
			return bmp;
		}
		cache.clear();
		try {
			mUri=imgUri;
			mOutPadding=new Rect(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
		mCurrentInput=null;
                try {
                    mCurrentInput=mActivity.getContentResolver().openInputStream(imgUri);
                }catch(IOException e){
                    try {
                        String fileName = imgUri.getLastPathSegment();
                        mCurrentInput = new BufferedInputStream(mActivity.openFileInput(fileName));
                    } catch (IOException e1) {
                        String fileName = imgUri.getPath();
                        mCurrentInput = new BufferedInputStream(new FileInputStream(fileName));
                    }
                }
		if (mCurrentInput != null)
		mFd=((FileInputStream)mCurrentInput).getFD();
		} catch (IOException e){
			//Toast.makeText(mActivity, "file not found "+imgUri.toString(), Toast.LENGTH_SHORT).show();
			return null;
		}

		Integer[] params=new Integer[]{useFd?0:-1, reqWidth, reqHeight};
        Bitmap bm=convertFileBitmap(params);

			if (bm!=null) {
                onPostExecute(bm);
				cache.put(mUri.toString(), bm);
				if (listeners.size() > 0)
				{
					listeners.get(0).onImageLoaded(v, bm);
				}
				v.setImageBitmap(bm);
                //v.postInvalidate();
			}


		//finalize();
		return bm;
	}
	
	public static BitmapFactory.Options getImageParams(Resources inRs, int imgRid)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Resources res=inRs;
		if (res==null ) res=mActivity.getResources();
		BitmapFactory.decodeResource(res, imgRid, options);
		return options;
	}
	
	public static BitmapFactory.Options getUriImageParams(InputStream in)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		return options;
	}
	
	public static BitmapFactory.Options getFdImageParams(FileDescriptor fD)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fD, null, options);
		return options;
	}

	
	static final int NO_SCALE=1;
	public static int getScaleFactor(BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
		if (options.outHeight < reqHeight && options.outWidth < reqWidth) return NO_SCALE;		
		int scaleFactor=NO_SCALE;
		final int halfHeight = options.outHeight / 2;
        final int halfWidth = options.outWidth / 2;
		while (halfHeight > scaleFactor*reqHeight && halfWidth > scaleFactor*reqWidth)
			scaleFactor *= 2;
		return scaleFactor;
	}
	
	public static Bitmap scaleDownBitMapByResource(Resources res, int resId,
	        int reqWidth, int reqHeight)
	{
		final BitmapFactory.Options options=getImageParams(res, resId);
		options.inSampleSize = getScaleFactor(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap scaleDownBitMapByFd(FileDescriptor fd,  int reqWidth, int reqHeight)
	{
		final BitmapFactory.Options options=getFdImageParams(fd);
		options.inSampleSize = getScaleFactor(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    Bitmap bmp=BitmapFactory.decodeFileDescriptor(fd, null, options);//mOutPadding, options);
	    return bmp;
	} 
	
	public static Bitmap scaleDownBitMapByStream(InputStream in,  int reqWidth, int reqHeight)
	{
		final BitmapFactory.Options options=getUriImageParams(in);
		options.inSampleSize = getScaleFactor(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    Bitmap bmp=BitmapFactory.decodeStream(in, null, options);//mOutPadding, options);
	    return bmp;
	}





    private WeakReference<ImageView> imageViewReference;


	    protected Bitmap convertFileBitmap(Integer[] params) {
            imageViewReference=null;
            int data = 0;
            imageViewReference = new WeakReference<ImageView>(mViewport);
	    	//ParcelFileDescriptor
	        data = params[0];
	        int reqWidth=params[1];
	        int reqHeight=params[2];
	        if (data < 0)
                return scaleDownBitMapByStream(mCurrentInput, reqWidth, reqHeight);
	        else if (data == 0)
	        	return scaleDownBitMapByFd(LoadBigImage.getFd(), reqWidth, reqHeight);
	        else return scaleDownBitMapByResource(mActivity.getResources(), data, reqWidth, reqHeight);
	    }


	    protected void onPostExecute(Bitmap bitmap) {

            //outBox.add(bitmap);
	        if (imageViewReference != null && bitmap != null) {
	        	if (mUri != null){
	        		cache.clear();	        	
	        		cache.put(mUri.toString(), bitmap);
	        	}
	        	instanceCount--;
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                //imageView.setImageBitmap(bitmap);
	               // imageView.postInvalidate();
	            }  
	            
	        }
	    }


    public void run()
    {
        if (mLoadFromResource) loadBitMap(mViewport, mResourceId);
        else loadBitMap(mViewport, mUri, xDimension, yDimension);
    }
}
