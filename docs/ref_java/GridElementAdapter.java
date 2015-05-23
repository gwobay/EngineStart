package com.cable.dctvcloud.demo;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cable.dctvcloud.demo.R;
import com.cable.dctvcloud.demo.Images;
import com.cable.dctvcloud.demo.RecyclingImageView;

import java.util.ArrayList;

/**
 * Created by erickou on 2015/4/30.
 */
public class GridElementAdapter extends BaseAdapter {

    private Context mContext;
    private int mItemHeight = 0;
    private int mNumColumns = 0;
    private int mActionBarHeight = 0;
    private GridView.LayoutParams mGridElementLayoutParams;
    private GridView.LayoutParams mImageLayoutParams;
    private ImageFetcher mImageFetcher;
    //mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
    int mImageThumbSize;//= getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
    int mImageThumbSpacing;//= getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

    private static ArrayList<GridElementInfo> mGridSource=null;

    public static boolean isSourceSet()
    {
        return (mGridSource != null);
    }

    public static void cleanUpSource()
    {
        if (mGridSource != null) mGridSource.clear();
    }
    static public void updateAdapterSource(GridElementInfo aData)
    {
        if (mGridSource == null)
            mGridSource=new ArrayList<GridElementInfo>();
        mGridSource.add(aData);
    }
    public static String getImageURL(int i)
    {
        if (mGridSource==null || i > mGridSource.size()) return null;
        return mGridSource.get(i).realURL;
    }

    public static GridElementInfo getNthItem(int n)
    {
        if (mGridSource==null || n > mGridSource.size()) return null;
       return  mGridSource.get(n);
    }
    public GridElementAdapter(Context context) {
        super();
        mContext = context;

        mGridElementLayoutParams = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mImageLayoutParams = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(
                android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, context.getResources().getDisplayMetrics());
        }
    }

    public void setDimension(int size, int spacing) {
        mImageThumbSize = size;
        mImageThumbSpacing = spacing;}

    public void addFetcher(ImageFetcher fetcher){
        mImageFetcher = fetcher;
    }

    public void setSource(ArrayList<GridElementInfo> imageList) {

        if (mGridSource != null)
            mGridSource.clear();
        mGridSource = imageList;
    }



    class ViewHolder {
        ImageView thumb;
        ImageView fileClass;
        TextView fileName;
    }


    //public ImageAdapter(Activity ac, )
    @Override
    public int getCount() {
        // If columns have yet to be determined, return no items
        if (getNumColumns() == 0) {
            return 0;
        }

        // Size + number of columns for top empty row
        return Images.imageThumbUrls.length + mNumColumns;
    }

    @Override
    public Object getItem(int position) {
        //return position < mNumColumns ?
               // null : Images.imageThumbUrls[position - mNumColumns];

        if (position < mNumColumns) return null;
        //return mGridSource.get(position - mNumColumns).thumbURL;
        return mGridSource.get(position - mNumColumns);
    }

    @Override
    public long getItemId(int position) {
        return position < mNumColumns ? 0 : position - mNumColumns;
    }

    @Override
    public int getViewTypeCount() {
        // Two types of views, the normal ImageView and the top row of empty views
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < mNumColumns) ? 1 : 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        //BEGIN_INCLUDE(load_gridview_item)
        // First check if this is the top row
        if (position < mNumColumns) {
            if (convertView == null) {
                convertView = new View(mContext);
            }
            // Set empty view with height of ActionBar
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
            return convertView;
        }

        // Now handle the main ImageView thumbnails

        ImageView imageView;
        ViewHolder holder;
        View retView;
        if (convertView == null) { // if it's not recycled, instantiate and initialize
            holder = new ViewHolder();
            ViewGroup viewProvider = (ViewGroup)LayoutInflater.from(mContext).inflate(R.layout.grid_element_content, container, false);
            //holder.thumb = (ImageView) viewProvider.findViewById(R.id.grid_thumb);
            //holder.thumb.setBackgroundResource(R.drawable.empty_photo);
            LinearLayout desp=(LinearLayout)viewProvider.findViewById(R.id.description);
            desp.setLayoutParams(new GridView.LayoutParams(-1, mItemHeight*2/5));
            holder.fileName = (TextView) (desp.findViewById(R.id.file_name));
            holder.fileName.setBackgroundColor(Color.WHITE);
            holder.fileClass = (ImageView) (desp.findViewById(R.id.file_class));

            retView=new LinearLayout(mContext);
            ((LinearLayout)retView).setOrientation(LinearLayout.VERTICAL);

            retView.setLayoutParams(mGridElementLayoutParams);

            imageView = new RecyclingImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    -1, mItemHeight*4/5));

            holder.thumb=imageView;

            //TextView fileName=new TextView(mContext);
            //fileName.setLayoutParams(new GridView.LayoutParams(-1, mItemHeight*2/5));


            ((ViewGroup)retView).addView(imageView);
            viewProvider.removeView(desp);
            ((ViewGroup)retView).addView(desp);
            holder.thumb=imageView;

            retView.setTag(holder);

        } else { // Otherwise re-use the converted view
            retView = convertView;
            holder = (ViewHolder) convertView.getTag();
            imageView = holder.thumb;
        }

        imageView=holder.thumb;
        int element_pos = position - mNumColumns;
        if (element_pos < mGridSource.size()) {
            GridElementInfo info = mGridSource.get(element_pos);
            if (info != null) {
                holder.fileName.setText(info.fileName);

                if (info.fileClass.equalsIgnoreCase("Images"))
                    holder.fileClass.setBackgroundResource(R.drawable.image);
                else if (info.fileClass.equalsIgnoreCase("Video"))
                    holder.fileClass.setBackgroundResource(R.drawable.video);
                else holder.fileClass.setBackgroundResource(R.drawable.others);


                // Finally load the image asynchronously into the ImageView, this also takes care of
                // setting a placeholder image while the background thread runs
                //mImageFetcher.loadImage(Images.imageThumbUrls[position - mNumColumns], imageView);
                mImageFetcher.loadImage(info.thumbURL, imageView);//imageView);
            }
        }
            return retView;
        //END_INCLUDE(load_gridview_item)
    }

    //@Override

    /**
     * Sets the item height. Useful for when we know the column width so the height can be set
     * to match.
     *
     * @param height
     */
    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mGridElementLayoutParams =
                new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        mImageFetcher.setImageSize(height * 4 / 5);
        notifyDataSetChanged();
    }

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public int getNumColumns() {
        return mNumColumns;
    }


    public static class GridElementInfo {
        String thumbURL;
        String fileClass;
        String fileName;
        String fileId;
        long fileSize;
        String realURL;

        public GridElementInfo(String filename, String categ, String thumb, String real) {
            fileName = filename;
            fileClass = categ;
            thumbURL = thumb;
            realURL = real;
        }

        public GridElementInfo(String filename, String categ, String id, long sz, String thumb, String real) {
            fileName = filename;
            fileClass = categ;
            fileId=id;
            fileSize=sz;
            thumbURL = thumb;
            realURL = real;
        }
    }

}
