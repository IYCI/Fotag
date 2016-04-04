package com.YC2010.Fotag;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

/**
 * Created by jason on 3/25/2016.
 */
public class Model extends Observable{
    private Boolean firstLoaded = false;
    private static Model instance;
    private int rating = 0;
    private Context mContext;
    private HashMap<UUID, ImageItem> imageMap = new HashMap<>();

    private Model() {
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public static Model getInstance(){
        if (instance == null){
            instance = new Model();
        }
        return instance;
    }

    public void updateView(){
        setChanged();
        notifyObservers();
    }

    public void setRating(int rating) {
        if (this.rating == rating){
            this.rating = 0;
        }
        else {
            this.rating = rating;
        }
        updateView();
    }

    public void setPreparedImages(){
        for (int j = 1; j <= 17; j++) {
//            Drawable drawable = mContext.getDrawable(mContext.getResources().getIdentifier("com.YC2010.Fotag:drawable/liukanshan_" + j, null, null));
            String uri = "http://www.wired.com/wp-content/uploads/2015/09/google-logo.jpg";
            Drawable drawable = new BitmapDrawable(mContext.getResources(), decodeSampledBitmapFromResource(mContext.getResources(),
                    mContext.getResources().getIdentifier("com.YC2010.Fotag:drawable/liukanshan_" + j, null, null), 256, 256));
            addImage(drawable);
        }
        firstLoaded = true;
    }

    public void addImage(Drawable drawable){
        ImageItem image = new ImageItem(drawable);
        imageMap.put(image.getmUUID(), image);
    }

    public void addImageFromURI(String uri){
//        uri = "http://www.bcgreen.com/pics/clearearth-lights.jpg";
//        uri = "https://lh3.googleusercontent.com/3YM4rLcJs7k25NZz2HYgQlLhhGu5vkPnfncS1JLv60iXetYcPAg6yDfuS_zEvTVpSQ=h900";
        //Drawable drawable = mContext.getDrawable(mContext.getResources().getIdentifier("com.YC2010.Fotag:drawable/liukanshan_1", null, null));
        new AsyncTask<String, Integer, Integer>() {
            Drawable drawable;
            @Override
            protected Integer doInBackground(String... params) {
                try {
                    drawable  = drawableFromUrl(params[0]);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                addImage(drawable);
                updateView();
            }
        }.execute(uri);
    }

    public int getRating() {
        return rating;
    }

    public ArrayList<ImageItem> getImages() {
        ArrayList<ImageItem> filterdImages = new ArrayList<>();
        for (ImageItem image : imageMap.values()){
            if (image.getRating() >= rating){
                filterdImages.add(image);
            }
        }
        return filterdImages;
    }

    public void clearImageCollection(){
        imageMap.clear();
        firstLoaded = false;
        updateView();
    }

    public void updateRatingByUUID(UUID uuid, int rating){
        ImageItem image = imageMap.get(uuid);
        image.setRating(rating);
        imageMap.put(uuid, image);
    }

    public ImageItem getImageByUUID(UUID uuid){
        return imageMap.get(uuid);
    }

    public Boolean getFirstLoaded() {
        return firstLoaded;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }
}
