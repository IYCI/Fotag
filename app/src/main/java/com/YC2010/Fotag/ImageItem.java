package com.YC2010.Fotag;

import android.graphics.drawable.Drawable;

import java.util.UUID;

/**
 * Created by jason on 3/26/2016.
 */
public class ImageItem {
    private UUID mUUID;
    private Integer rating = 0;
    private Drawable mDrawable;

    public ImageItem(Drawable drawable) {
        mDrawable = drawable;
        mUUID = UUID.randomUUID();
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Drawable getmDrawable() {
        return mDrawable;
    }

    public UUID getmUUID() {
        return mUUID;
    }
}
