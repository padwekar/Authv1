package com.example.savi.auth.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class ImageCompressionAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private OnImageCompressed mOnImageCompressed;

    public ImageCompressionAsyncTask(Context context) {
        this(context, 0, 0);
    }

    public ImageCompressionAsyncTask(Context context, int width, int height) {
        this.mContext = context;
        mWidth = width;
        mHeight = height;
    }

    public void setOnImageCompressed(OnImageCompressed listener) {
        this.mOnImageCompressed = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        if (mWidth > 0 && mHeight > 0) {
            bitmap = new ImageUtil(mContext).decodeBitmapFromPath(params[0], mWidth, mHeight);
        } else {
            bitmap = new ImageUtil(mContext).decodeBitmapFromPath(params[0]);
        }
        Log.i(ImageCompressionAsyncTask.class.getSimpleName(), "" + bitmap);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (mOnImageCompressed != null) {
            mOnImageCompressed.onCompressedImage(result);
        }
    }

    public interface OnImageCompressed {
        public void onCompressedImage(Bitmap bitmap);
    }

}
